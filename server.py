#!/usr/bin/env python3
"""
Sitaram Ayurveda Medicine Catalogue & Web Admin Panel Gateway Server.
Serves static assets on port 3000 and exposes full Supabase CUD REST API.
"""

import http.server
import socketserver
import json
import os
import sys
import urllib.parse
import mimetypes
import base64
import time

# Add root directory to sys.path
BASE_DIR = os.path.dirname(os.path.abspath(__file__))
if BASE_DIR not in sys.path:
    sys.path.insert(0, BASE_DIR)

from backend import supabase_db as db

# Server port should be DEFAULT_APP_PORT (3000) because Nginx listens on PORT (8080)
PORT = int(os.environ.get("DEFAULT_APP_PORT", 3000))
PUBLIC_DIR = os.path.join(BASE_DIR, "public")
UPLOAD_DIR = os.path.join(PUBLIC_DIR, "images", "uploads")
os.makedirs(UPLOAD_DIR, exist_ok=True)


class SitaramAdminHandler(http.server.BaseHTTPRequestHandler):

    def extract_client_headers(self):
        headers_extra = {}
        sb_key = self.headers.get("X-Supabase-Key") or self.headers.get("apikey")
        sb_url = self.headers.get("X-Supabase-Url")
        auth_header = self.headers.get("Authorization", "")
        if not sb_key and auth_header.startswith("Bearer "):
            token = auth_header[7:].strip()
            if token.startswith("ey") or len(token) > 20:
                sb_key = token

        if sb_key:
            headers_extra["X-Supabase-Key"] = sb_key
            # Auto-save valid key to config if currently empty
            curr_url, curr_key = db.get_supabase_credentials()
            if not curr_key and (sb_key.startswith("eyJ") or len(sb_key) > 20):
                db.set_supabase_credentials(sb_url or curr_url, sb_key)

        if sb_url:
            headers_extra["X-Supabase-Url"] = sb_url

        return headers_extra

    def send_json(self, status_code, data, extra_headers=None):
        payload = json.dumps(data, indent=2).encode("utf-8")
        self.send_response(status_code)
        self.send_header("Content-Type", "application/json; charset=utf-8")
        self.send_header("Content-Length", str(len(payload)))
        self.send_header("Access-Control-Allow-Origin", "*")
        self.send_header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS")
        self.send_header("Access-Control-Allow-Headers", "Content-Type, Authorization, X-Supabase-Key, X-Supabase-Url, apikey, X-Requested-With")
        self.send_header("Cache-Control", "no-store, no-cache, must-revalidate")
        if extra_headers:
            for k, v in extra_headers.items():
                self.send_header(k, v)
        self.end_headers()
        self.wfile.write(payload)

    def do_OPTIONS(self):
        self.send_response(204)
        self.send_header("Access-Control-Allow-Origin", "*")
        self.send_header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS")
        self.send_header("Access-Control-Allow-Headers", "Content-Type, Authorization, X-Supabase-Key, X-Supabase-Url, apikey, X-Requested-With")
        self.send_header("Access-Control-Max-Age", "86400")
        self.end_headers()

    def read_json_body(self):
        try:
            content_len = int(self.headers.get("Content-Length", 0))
            if content_len > 0:
                raw = self.rfile.read(content_len).decode("utf-8")
                return json.loads(raw)
        except Exception as e:
            print(f"[Server] JSON parse error: {e}")
        return {}

    def serve_static(self, rel_path):
        clean_path = rel_path.lstrip("/").split("?")[0].split("#")[0]
        if not clean_path or clean_path in ["admin", "admin.html", "index", "index.html"]:
            filepath = os.path.join(PUBLIC_DIR, "admin.html")
        else:
            filepath = os.path.join(PUBLIC_DIR, clean_path)

        if not os.path.exists(filepath) or os.path.isdir(filepath):
            # Fallback to admin.html for SPA routes
            filepath = os.path.join(PUBLIC_DIR, "admin.html")

        mime_type, _ = mimetypes.guess_type(filepath)
        if not mime_type:
            mime_type = "application/octet-stream"
        if mime_type.startswith("text/") or mime_type in ["application/javascript", "application/json"]:
            mime_type += "; charset=utf-8"

        try:
            with open(filepath, "rb") as f:
                content = f.read()
            self.send_response(200)
            self.send_header("Content-Type", mime_type)
            self.send_header("Content-Length", str(len(content)))
            self.send_header("Access-Control-Allow-Origin", "*")
            self.send_header("Cache-Control", "no-cache, must-revalidate")
            self.end_headers()
            self.wfile.write(content)
        except Exception as e:
            self.send_json(500, {"error": f"Error serving file: {str(e)}"})

    def do_HEAD(self):
        self.do_GET()

    def do_GET(self):
        parsed = urllib.parse.urlparse(self.path)
        path = parsed.path
        query = urllib.parse.parse_qs(parsed.query)
        headers_extra = self.extract_client_headers()

        # Health check
        if path in ["/api/health", "/health"]:
            self.send_json(200, {"status": "ok", "app": "Sitaram Ayurveda Catalogue API", "time": time.time()})
            return

        # Supabase connectivity status
        if path == "/api/supabase/status":
            url = query.get("url", [None])[0] or headers_extra.get("X-Supabase-Url")
            key = query.get("key", [None])[0] or headers_extra.get("X-Supabase-Key")
            status = db.check_connection(url=url, key=key)
            self.send_json(200 if status.get("success") else 200, status)
            return

        # Products list
        if path == "/api/products":
            res = db.get_all_products(headers_extra=headers_extra)
            # Apply search or category filter if requested
            data = res.get("data", [])
            q_search = query.get("q", [None])[0]
            q_cat = query.get("category", [None])[0]

            if q_cat and q_cat.lower() != "all":
                data = [p for p in data if (p.get("category_name") or p.get("category") or "").lower() == q_cat.lower()]
            if q_search:
                qs = q_search.lower()
                data = [p for p in data if qs in (p.get("name") or "").lower() or qs in (p.get("code") or "").lower() or qs in (p.get("indications") or "").lower()]

            self.send_json(200, {
                "success": True,
                "count": len(data),
                "data": data,
                "source": res.get("source"),
                "warning": res.get("warning")
            })
            return

        # Single product
        if path.startswith("/api/products/"):
            ident = path.split("/api/products/")[1].strip()
            if ident and ident not in ["update", "delete"]:
                res = db.get_product(ident, headers_extra=headers_extra)
                self.send_json(200 if res.get("success") else 404, res)
                return

        # Categories list
        if path == "/api/categories":
            res = db.get_all_categories(headers_extra=headers_extra)
            self.send_json(200, {
                "success": True,
                "count": len(res.get("data", [])),
                "data": res.get("data", []),
                "source": res.get("source"),
                "warning": res.get("warning")
            })
            return

        # Dashboard metrics / stats
        if path == "/api/stats":
            p_res = db.get_all_products(headers_extra=headers_extra)
            c_res = db.get_all_categories(headers_extra=headers_extra)
            products = p_res.get("data", [])
            categories = c_res.get("data", [])

            active_count = len([p for p in products if (p.get("status") or "").lower() == "active"])
            low_stock_count = len([p for p in products if int(p.get("stock") or 0) < 15])
            featured_count = len([p for p in products if p.get("featured")])

            self.send_json(200, {
                "success": True,
                "stats": {
                    "total_formulations": len(products),
                    "active_published": active_count,
                    "low_stock_alerts": low_stock_count,
                    "featured_medicines": featured_count,
                    "total_categories": len(categories)
                }
            })
            return

        # Export catalogue
        if path == "/api/export":
            p_res = db.get_all_products(headers_extra=headers_extra)
            c_res = db.get_all_categories(headers_extra=headers_extra)
            export_payload = {
                "system": "Sitaram Ayurveda Catalogue",
                "exported_at": time.strftime("%Y-%m-%dT%H:%M:%SZ", time.gmtime()),
                "total_products": len(p_res.get("data", [])),
                "categories": c_res.get("data", []),
                "products": p_res.get("data", [])
            }
            self.send_json(200, export_payload)
            return

        # Serve static assets
        self.serve_static(path)

    def do_POST(self):
        parsed = urllib.parse.urlparse(self.path)
        path = parsed.path
        body = self.read_json_body()
        headers_extra = self.extract_client_headers()

        # Supabase config
        if path == "/api/supabase/configure":
            url = body.get("url")
            key = body.get("key")
            if not url or not key:
                self.send_json(400, {"success": False, "error": "Both URL and API Key are required."})
                return
            saved = db.set_supabase_credentials(url, key)
            test = db.check_connection(url=url, key=key)
            self.send_json(200, {
                "success": saved,
                "connection": test,
                "message": "Supabase credentials updated successfully."
            })
            return

        # Create Product
        if path == "/api/products":
            res = db.create_product(body, headers_extra=headers_extra)
            status_code = 201 if res.get("success") else 400
            self.send_json(status_code, res)
            return

        # Update Product (via POST /api/products/update or /api/products/<id>)
        if path == "/api/products/update":
            ident = body.get("id") or body.get("code")
            if not ident:
                self.send_json(400, {"success": False, "error": "Product id or code is required for update."})
                return
            res = db.update_product(ident, body, headers_extra=headers_extra)
            status_code = 200 if res.get("success") else 400
            self.send_json(status_code, res)
            return

        # Delete Product (via POST /api/products/delete)
        if path == "/api/products/delete":
            ident = body.get("id") or body.get("code")
            if not ident:
                self.send_json(400, {"success": False, "error": "Product id or code is required for deletion."})
                return
            res = db.delete_product(ident, headers_extra=headers_extra)
            status_code = 200 if res.get("success") else 400
            self.send_json(status_code, res)
            return

        # Create Category
        if path == "/api/categories":
            res = db.create_category(body, headers_extra=headers_extra)
            status_code = 201 if res.get("success") else 400
            self.send_json(status_code, res)
            return

        # Update Category
        if path == "/api/categories/update":
            ident = body.get("id") or body.get("code")
            if not ident:
                self.send_json(400, {"success": False, "error": "Category id or code is required for update."})
                return
            res = db.update_category(ident, body, headers_extra=headers_extra)
            status_code = 200 if res.get("success") else 400
            self.send_json(status_code, res)
            return

        # Delete Category
        if path == "/api/categories/delete":
            ident = body.get("id") or body.get("code")
            if not ident:
                self.send_json(400, {"success": False, "error": "Category id or code is required for deletion."})
                return
            res = db.delete_category(ident, headers_extra=headers_extra)
            status_code = 200 if res.get("success") else (409 if res.get("conflict") else 400)
            self.send_json(status_code, res)
            return

        # Image Upload (Base64 data URL)
        if path == "/api/upload":
            image_data = body.get("data")
            filename = body.get("filename") or f"herb_{int(time.time())}.jpg"
            if not image_data:
                self.send_json(400, {"success": False, "error": "No image data received."})
                return

            try:
                if "," in image_data:
                    image_data = image_data.split(",", 1)[1]
                decoded = base64.b64decode(image_data)
                target_file = os.path.join(UPLOAD_DIR, filename)
                with open(target_file, "wb") as f:
                    f.write(decoded)

                public_url = f"/images/uploads/{filename}"
                self.send_json(200, {
                    "success": True,
                    "url": public_url,
                    "message": "Image uploaded successfully."
                })
            except Exception as e:
                self.send_json(500, {"success": False, "error": f"Image processing failed: {str(e)}"})
            return

        self.send_json(404, {"success": False, "error": f"Route not found: {path}"})

    def do_PUT(self):
        parsed = urllib.parse.urlparse(self.path)
        path = parsed.path
        body = self.read_json_body()
        headers_extra = self.extract_client_headers()

        if path.startswith("/api/products/"):
            ident = path.split("/api/products/")[1].strip()
            res = db.update_product(ident, body, headers_extra=headers_extra)
            self.send_json(200 if res.get("success") else 400, res)
            return

        if path.startswith("/api/categories/"):
            ident = path.split("/api/categories/")[1].strip()
            res = db.update_category(ident, body, headers_extra=headers_extra)
            self.send_json(200 if res.get("success") else 400, res)
            return

        self.send_json(404, {"error": "Endpoint not found."})

    def do_DELETE(self):
        parsed = urllib.parse.urlparse(self.path)
        path = parsed.path
        headers_extra = self.extract_client_headers()

        if path.startswith("/api/products/"):
            ident = path.split("/api/products/")[1].strip()
            res = db.delete_product(ident, headers_extra=headers_extra)
            self.send_json(200 if res.get("success") else 400, res)
            return

        if path.startswith("/api/categories/"):
            ident = path.split("/api/categories/")[1].strip()
            res = db.delete_category(ident, headers_extra=headers_extra)
            status_code = 200 if res.get("success") else (409 if res.get("conflict") else 400)
            self.send_json(status_code, res)
            return

        self.send_json(404, {"error": "Endpoint not found."})

    def log_message(self, format, *args):
        # Concise logging
        print(f"[Gateway] {self.command} {self.path} - {args[1] if len(args) > 1 else ''}")


class ThreadingServer(socketserver.ThreadingMixIn, socketserver.TCPServer):
    allow_reuse_address = True
    daemon_threads = True


def run_server():
    while True:
        try:
            with ThreadingServer(("", PORT), SitaramAdminHandler) as server:
                print(f"Sitaram Ayurveda Admin Gateway running on port {PORT}")
                server.serve_forever()
        except KeyboardInterrupt:
            print("Server shutting down by user request.")
            break
        except Exception as e:
            print(f"[Gateway Error] Server crashed with: {e}. Restarting in 1s...")
            time.sleep(1)


if __name__ == "__main__":
    run_server()
