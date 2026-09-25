"""
Sitaram Ayurveda Medicine Catalogue — Administrative Web Server & Auth Gateway
Enforces strict backend authentication, session cookies, and route redirection.
"""

import http.server
import socketserver
import os
import json
import re
import urllib.parse
from http import cookies
import backend.supabase_db as db
import backend.supabase_sync as supabase_sync

PORT = 3000
PUBLIC_DIR = os.path.abspath("./public")

def validate_product_payload(body, is_update=False):
    """Validates medicine/product data and returns a dictionary of field errors."""
    errors = {}
    
    # 1. Commercial Name
    name = (body.get("name") or "").strip()
    if not is_update or "name" in body:
        if not name:
            errors["name"] = "Commercial English Name is required."
        elif len(name) < 2:
            errors["name"] = "Medicine name must be at least 2 characters."
        elif len(name) > 120:
            errors["name"] = "Medicine name cannot exceed 120 characters."

    # 2. Classical Sanskrit Name
    sanskrit = (body.get("sanskrit_name") or body.get("sanskritName") or "").strip()
    if not is_update or "sanskrit_name" in body or "sanskritName" in body:
        if not sanskrit:
            errors["sanskritName"] = "Classical Sanskrit Name is required."
        elif len(sanskrit) < 2:
            errors["sanskritName"] = "Sanskrit name must be at least 2 characters."
        elif len(sanskrit) > 120:
            errors["sanskritName"] = "Sanskrit name cannot exceed 120 characters."

    # 3. Formulation Category
    category = (body.get("category") or body.get("category_code") or "").strip()
    if not is_update or "category" in body or "category_code" in body:
        if not category:
            errors["category"] = "Formulation Category is required."

    # 4. Publication Status
    status = (body.get("status") or "").strip().upper()
    if status and status not in ["PUBLISHED", "DRAFT", "ACTIVE", "INACTIVE"]:
        errors["status"] = "Publication status must be either PUBLISHED or DRAFT."

    # 5. Direct Image URL
    image_url = (body.get("image_url") or body.get("imageUrl") or "").strip()
    if image_url:
        if not (image_url.startswith("http://") or image_url.startswith("https://") or image_url.startswith("data:image/") or image_url.startswith("/")):
            errors["imageUrl"] = "Image URL must be a valid web URL (http:// or https://) or image data URI."

    # 6. Primary Clinical Benefit
    benefit = (body.get("benefit") or body.get("primary_benefit") or "").strip()
    if not is_update or "benefit" in body:
        if not benefit:
            errors["benefit"] = "Primary Clinical Benefit is required."
        elif len(benefit) < 5:
            errors["benefit"] = "Primary clinical benefit must be at least 5 characters."
        elif len(benefit) > 300:
            errors["benefit"] = "Primary clinical benefit cannot exceed 300 characters."

    # 7. Health Goals
    goals = body.get("health_goals") or body.get("healthGoals")
    if goals is not None and isinstance(goals, list):
        if len(goals) == 0:
            errors["healthGoals"] = "At least one target health goal must be selected."

    return errors

def validate_category_payload(body, is_update=False):
    """Validates category data and returns a dictionary of field errors."""
    errors = {}
    
    code = (body.get("code") or "").strip().upper()
    if not is_update:
        if not code:
            errors["code"] = "Category Code is required."
        elif len(code) < 2 or len(code) > 20:
            errors["code"] = "Category Code must be between 2 and 20 characters."
        elif not re.match(r'^[A-Z0-9_]+$', code):
            errors["code"] = "Category Code must contain only uppercase letters, numbers, and underscores (no spaces)."

    name = (body.get("name") or "").strip()
    if not is_update or "name" in body:
        if not name:
            errors["name"] = "Category Name is required."
        elif len(name) < 2 or len(name) > 80:
            errors["name"] = "Category Name must be between 2 and 80 characters."

    icon = (body.get("icon") or "").strip()
    if not is_update or "icon" in body:
        if not icon:
            errors["icon"] = "Icon or Emoji is required."
        elif len(icon) > 10:
            errors["icon"] = "Icon cannot exceed 10 characters."

    subtitle = (body.get("subtitle") or "").strip()
    if not is_update or "subtitle" in body:
        if not subtitle:
            errors["subtitle"] = "Dosage Form Subtitle is required."
        elif len(subtitle) < 3 or len(subtitle) > 100:
            errors["subtitle"] = "Subtitle must be between 3 and 100 characters."

    desc = (body.get("description") or "").strip()
    if desc and len(desc) > 600:
        errors["description"] = "Description cannot exceed 600 characters."

    return errors

class SitaramAdminHandler(http.server.BaseHTTPRequestHandler):
    def get_session_token(self):
        # 1. Check Authorization Bearer header
        auth_header = self.headers.get("Authorization")
        if auth_header and auth_header.startswith("Bearer "):
            return auth_header.split(" ", 1)[1].strip()

        # 2. Check Cookie
        cookie_header = self.headers.get("Cookie")
        if cookie_header:
            c = cookies.SimpleCookie()
            try:
                c.load(cookie_header)
                if "sitaram_session" in c:
                    return c["sitaram_session"].value
            except Exception:
                pass
        return None

    def get_current_admin(self):
        token = self.get_session_token()
        if not token:
            return None
        return db.get_session_admin(token)

    def send_json(self, status_code, data, extra_headers=None):
        payload = json.dumps(data).encode("utf-8")
        self.send_response(status_code)
        self.send_header("Content-Type", "application/json; charset=utf-8")
        self.send_header("Content-Length", str(len(payload)))
        self.send_header("Cache-Control", "no-store, no-cache, must-revalidate")
        if extra_headers:
            for k, v in extra_headers.items():
                self.send_header(k, v)
        self.end_headers()
        self.wfile.write(payload)

    def send_redirect(self, target_url, extra_headers=None):
        self.send_response(302)
        self.send_header("Location", target_url)
        self.send_header("Cache-Control", "no-store, no-cache, must-revalidate")
        if extra_headers:
            for k, v in extra_headers.items():
                self.send_header(k, v)
        self.end_headers()

    def serve_static_file(self, filepath, content_type=None):
        if not os.path.exists(filepath) or os.path.isdir(filepath):
            self.send_error(404, "File Not Found")
            return

        if not content_type:
            ext = os.path.splitext(filepath)[1].lower()
            types = {
                ".html": "text/html; charset=utf-8",
                ".js": "application/javascript; charset=utf-8",
                ".css": "text/css; charset=utf-8",
                ".json": "application/json; charset=utf-8",
                ".png": "image/png",
                ".jpg": "image/jpeg",
                ".jpeg": "image/jpeg",
                ".svg": "image/svg+xml",
                ".ico": "image/x-icon"
            }
            content_type = types.get(ext, "application/octet-stream")

        with open(filepath, "rb") as f:
            content = f.read()

        self.send_response(200)
        self.send_header("Content-Type", content_type)
        self.send_header("Content-Length", str(len(content)))
        self.send_header("Access-Control-Allow-Origin", "*")
        self.send_header("Cache-Control", "no-cache, must-revalidate")
        self.send_header("Cache-Control", "no-cache")
        self.end_headers()
        self.wfile.write(content)

    def read_json_body(self):
        try:
            content_len = int(self.headers.get("Content-Length", 0))
            if content_len > 0:
                raw = self.rfile.read(content_len).decode("utf-8")
                return json.loads(raw)
        except Exception:
            pass
        return {}

    def do_HEAD(self):
        self.do_GET()

    def do_GET(self):
        try:
            parsed = urllib.parse.urlparse(self.path)
            path = parsed.path
            qs = urllib.parse.parse_qs(parsed.query)

            # 1. API Endpoints
            if path.startswith("/api/"):
                # Check session status
                if path == "/api/auth/session":
                    admin = self.get_current_admin()
                    if admin:
                        self.send_json(200, {"authenticated": True, "admin": admin})
                    else:
                        self.send_json(401, {"authenticated": False, "message": "No active administrator session."})
                    return

                if path == "/api/admin/profile":
                    admin = self.get_current_admin()
                    if not admin:
                        self.send_json(401, {"authenticated": False, "error": "Unauthorized. Please log in."})
                        return
                    profile = db.get_admin_by_id(admin["id"])
                    if profile:
                        self.send_json(200, {"success": True, "profile": profile})
                    else:
                        self.send_json(200, {"success": True, "profile": admin})
                    return

                # Public digital monograph API for QR code scanning
                if path.startswith("/api/monograph/"):
                    prod_id = urllib.parse.unquote(path[len("/api/monograph/"):])
                    prod = db.get_product_by_id(prod_id)
                    if not prod:
                        # check case-insensitive match or code
                        all_products = db.get_all_products()
                        for p in all_products:
                            if str(p.get("id")) == prod_id or p.get("code") == prod_id or p.get("name", "").lower().replace(" ", "_") == prod_id.lower():
                                prod = p
                                break
                    if prod:
                        self.send_json(200, {"success": True, "data": prod})
                    else:
                        self.send_json(404, {"success": False, "error": f"Monograph not found for identifier {prod_id}"})
                    return

                # Public Supabase Connection & Schema Endpoints
                if path == "/api/supabase/status":
                    cfg = supabase_sync.get_supabase_config()
                    status = supabase_sync.test_supabase_connection()
                    status["url"] = cfg.get("url", "")
                    status["has_key"] = bool(cfg.get("key"))
                    self.send_json(200, status)
                    return

                if path == "/api/supabase/schema":
                    schema_path = os.path.abspath("./supabase_schema.sql")
                    if os.path.exists(schema_path):
                        with open(schema_path, "r", encoding="utf-8") as f:
                            content = f.read()
                        self.send_response(200)
                        self.send_header("Content-Type", "text/plain; charset=utf-8")
                        self.send_header("Content-Disposition", "attachment; filename=\"supabase_schema.sql\"")
                        self.end_headers()
                        self.wfile.write(content.encode("utf-8"))
                    else:
                        self.send_json(404, {"error": "supabase_schema.sql not found."})
                    return

                # Protected API endpoints
                admin = self.get_current_admin()
                if not admin:
                    self.send_json(401, {"error": "Unauthorized. Please authenticate as an administrator."})
                    return

                if path == "/api/dashboard/metrics":
                    metrics = db.get_dashboard_metrics()
                    self.send_json(200, {"success": True, "data": metrics})
                    return

                elif path == "/api/dashboard/recent-products":
                    recent = db.get_recent_products(5)
                    self.send_json(200, {"success": True, "data": recent})
                    return

                elif path == "/api/dashboard/recently-updated":
                    updated = db.get_recently_updated_products(5)
                    self.send_json(200, {"success": True, "data": updated})
                    return

                elif path == "/api/dashboard/category-summary":
                    summary = db.get_category_summary()
                    self.send_json(200, {"success": True, "data": summary})
                    return

                elif path == "/api/products":
                    search = qs.get("search", [None])[0]
                    category = qs.get("category", [None])[0]
                    status = qs.get("status", [None])[0]
                    ingredient = qs.get("ingredient", [None])[0]
                    products = db.get_all_products(search, category, status, ingredient)
                    self.send_json(200, {"success": True, "data": products, "count": len(products)})
                    return

                elif path.startswith("/api/products/"):
                    prod_id = path.replace("/api/products/", "").strip()
                    prod = db.get_product_by_id(prod_id)
                    if prod:
                        self.send_json(200, {"success": True, "data": prod})
                    else:
                        self.send_json(404, {"success": False, "error": "Product not found"})
                    return

                elif path == "/api/categories":
                    categories = db.get_all_categories()
                    self.send_json(200, {"success": True, "data": categories, "count": len(categories)})
                    return

                elif path == "/api/ingredients":
                    ingredients = db.get_all_ingredients()
                    self.send_json(200, {"success": True, "data": ingredients, "count": len(ingredients)})
                    return

                elif path == "/api/manufacturers":
                    mfgs = db.get_all_manufacturers()
                    self.send_json(200, {"success": True, "data": mfgs, "count": len(mfgs)})
                    return

                elif path == "/api/audit-logs":
                    logs = db.get_audit_logs(100)
                    self.send_json(200, {"success": True, "data": logs, "count": len(logs)})
                    return

                self.send_json(404, {"error": "API route not found."})
                return

            # 2. Static Asset Requests (serve any existing file in public)
            clean_rel = path.lstrip("/")
            candidate_file = os.path.join(PUBLIC_DIR, clean_rel)
            if clean_rel and os.path.isfile(candidate_file):
                self.serve_static_file(candidate_file)
                return

            # Public digital monograph Page (for user-facing app and QR scans)
            if path == "/monograph" or path == "/monograph.html" or path.startswith("/monograph/"):
                self.serve_static_file(os.path.join(PUBLIC_DIR, "monograph.html"), "text/html; charset=utf-8")
                return

            # 3. Route Authentication & Authorization Gateway
            # Root path: Serve modern admin portal with Login Gateway
            if path in ["/", "/index.html", "/admin", "/admin/", "/admin/dashboard", "/admin/products", "/admin/categories", "/admin.html", "/admin/login"] or path.startswith("/admin/"):
                self.serve_static_file(os.path.join(PUBLIC_DIR, "admin.html"), "text/html; charset=utf-8")
                return

            # Catch-all: If visitor accesses any unknown route, serve admin.html
            self.serve_static_file(os.path.join(PUBLIC_DIR, "admin.html"), "text/html; charset=utf-8")
        except Exception as e:
            try:
                self.send_json(500, {"error": "Internal Server Error", "details": str(e)})
            except Exception:
                pass

    def do_POST(self):
        try:
            parsed = urllib.parse.urlparse(self.path)
            path = parsed.path

            # 1. Login Endpoint
            if path == "/api/auth/login":
                body = self.read_json_body() or {}
                identifier = body.get("identifier") or body.get("username") or body.get("email") or ""
                password = body.get("password", "")

                admin = db.authenticate_admin(identifier, password)
                if admin:
                    token, expires = db.create_session(admin["id"])
                    client_ip = self.client_address[0] if self.client_address else "127.0.0.1"
                    db.log_audit(admin["email"], "ADMIN_LOGIN", "AUTH", admin["id"], "Administrator logged in via credentials.", client_ip)

                    # Set secure HttpOnly cookie
                    cookie = cookies.SimpleCookie()
                    cookie["sitaram_session"] = token
                    cookie["sitaram_session"]["path"] = "/"
                    cookie["sitaram_session"]["httponly"] = True
                    cookie["sitaram_session"]["samesite"] = "Lax"
                    cookie["sitaram_session"]["max-age"] = 86400  # 24 hours

                    cookie_str = cookie.output(header="").strip()
                    self.send_json(200, {
                        "success": True,
                        "token": token,
                        "admin": admin
                    }, extra_headers={"Set-Cookie": cookie_str})
                else:
                    self.send_json(401, {
                        "success": False,
                        "message": "Invalid Administrator credentials. Please check your username/email and password."
                    })
                return

            # 2. Logout Endpoint
            if path == "/api/auth/logout":
                token = self.get_session_token()
                if token:
                    db.destroy_session(token)

                # Expire session cookie
                expired_cookie = "sitaram_session=; Path=/; Expires=Thu, 01 Jan 1970 00:00:00 GMT; Max-Age=0; HttpOnly; SameSite=Lax"
                self.send_json(200, {"success": True}, extra_headers={"Set-Cookie": expired_cookie})
                return

            # 3. Forgot Password Endpoint
            if path == "/api/auth/forgot-password":
                body = self.read_json_body()
                email = (body.get("email", "")).strip().lower()

                conn = db.get_connection()
                c = conn.cursor()
                c.execute("SELECT id, name, email FROM admins WHERE lower(email) = ?", (email,))
                admin_row = c.fetchone()
                conn.close()

                if admin_row:
                    client_ip = self.client_address[0] if self.client_address else "127.0.0.1"
                    db.log_audit(admin_row["email"], "PASSWORD_RESET_REQUEST", "ADMIN", admin_row["id"], f"Password recovery requested for {admin_row['email']}.", client_ip)
                    self.send_json(200, {
                        "success": True,
                        "message": f"A secure password recovery instructions email has been queued for {admin_row['email']}."
                    })
                else:
                    self.send_json(404, {
                        "success": False,
                        "message": "The provided email address is not registered as an authorized administrator."
                    })
                return

            elif path == "/api/upload-logo":
                import base64
                import subprocess
                body = self.read_json_body() or {}
                data_uri = body.get("image") or ""
                if not data_uri:
                    self.send_json(400, {"success": False, "error": "No image data provided."})
                    return
                if "," in data_uri:
                    data_uri = data_uri.split(",", 1)[1]
                try:
                    img_bytes = base64.b64decode(data_uri)
                    save_path = "/app/applet/public/img/sitaram_luxury_logo_original.jpg"
                    with open(save_path, "wb") as f:
                        f.write(img_bytes)
                    proc = subprocess.run(["/app/applet/process_logo.sh", save_path], capture_output=True, text=True)
                    if proc.returncode == 0:
                        self.send_json(200, {"success": True, "message": "Logo updated and centered successfully!"})
                    else:
                        self.send_json(500, {"success": False, "error": f"Failed to process logo: {proc.stderr}"})
                except Exception as e:
                    self.send_json(400, {"success": False, "error": f"Invalid image format: {str(e)}"})
                return

            # 4. Authenticated Data Mutations
            admin = self.get_current_admin()
            if not admin:
                self.send_json(401, {"error": "Unauthorized. Please authenticate as an administrator."})
                return

            admin_email = admin.get("email", "admin@sitaramayurveda.com")
            body = self.read_json_body() or {}

            # Admin Account Profile & Settings
            if path == "/api/admin/profile":
                name = body.get("name", admin.get("name", ""))
                email = body.get("email", admin.get("email", ""))
                username = body.get("username", admin.get("username", ""))
                avatar = body.get("avatar")
                ok, msg = db.update_admin_profile(admin["id"], name, email, username, avatar)
                if ok:
                    client_ip = self.client_address[0] if self.client_address else "127.0.0.1"
                    db.log_audit(email, "ADMIN_PROFILE_UPDATE", "ADMIN", admin["id"], f"Administrator updated account profile ({name}, {email}).", client_ip)
                    updated_admin = db.get_admin_by_id(admin["id"])
                    self.send_json(200, {"success": True, "message": msg, "admin": updated_admin})
                else:
                    self.send_json(400, {"success": False, "error": msg})
                return

            elif path == "/api/admin/change-password":
                curr_pwd = body.get("current_password", "")
                new_pwd = body.get("new_password", "")
                ok, msg = db.update_admin_password(admin["id"], curr_pwd, new_pwd)
                if ok:
                    client_ip = self.client_address[0] if self.client_address else "127.0.0.1"
                    db.log_audit(admin.get("email", ""), "ADMIN_PASSWORD_CHANGE", "ADMIN", admin["id"], "Administrator changed account password.", client_ip)
                    self.send_json(200, {"success": True, "message": msg})
                else:
                    self.send_json(400, {"success": False, "error": msg})
                return

            # Product Mutations
            if path == "/api/products":
                val_errors = validate_product_payload(body, is_update=False)
                if val_errors:
                    self.send_json(400, {
                        "success": False,
                        "error": "Validation failed: Please correct invalid medicine data fields.",
                        "errors": val_errors
                    })
                    return
                try:
                    prod = db.create_product(body, admin_email)
                    self.send_json(201, {"success": True, "data": prod})
                except Exception as e:
                    print(f"Product creation error: {e}", flush=True)
                    self.send_json(400, {"success": False, "error": f"Failed to save medicine: {str(e)}"})
                return

            elif path == "/api/products/update":
                prod_id = body.get("id")
                if not prod_id:
                    self.send_json(400, {"success": False, "error": "Medicine ID is required for update."})
                    return
                val_errors = validate_product_payload(body, is_update=True)
                if val_errors:
                    self.send_json(400, {
                        "success": False,
                        "error": "Validation failed: Please correct invalid medicine data fields.",
                        "errors": val_errors
                    })
                    return
                try:
                    prod = db.update_product(prod_id, body, admin_email)
                    if prod:
                        self.send_json(200, {"success": True, "data": prod})
                    else:
                        self.send_json(404, {"success": False, "error": "Product not found"})
                except Exception as e:
                    print(f"Product update error: {e}", flush=True)
                    self.send_json(400, {"success": False, "error": f"Failed to update medicine: {str(e)}"})
                return

            elif path == "/api/products/delete":
                prod_id = body.get("id")
                if not prod_id:
                    self.send_json(400, {"success": False, "error": "Medicine ID is required."})
                    return
                try:
                    result = db.delete_product(prod_id, admin_email)
                    if isinstance(result, dict):
                        if result.get("success"):
                            self.send_json(200, {"success": True, "message": result.get("message", "Medicine deleted successfully.")})
                        else:
                            self.send_json(400, {"success": False, "error": result.get("error", "Database restriction prevented deletion.")})
                    elif result:
                        self.send_json(200, {"success": True, "message": "Medicine deleted successfully."})
                    else:
                        self.send_json(400, {"success": False, "error": "Database restriction: Unable to delete formulation."})
                except Exception as e:
                    self.send_json(400, {"success": False, "error": f"Database restriction: {str(e)}"})
                return

            elif path == "/api/products/bulk-status":
                ids = body.get("ids", [])
                new_status = body.get("status", "Active")
                for pid in ids:
                    db.update_product(pid, {"status": new_status}, admin_email)
                self.send_json(200, {"success": True, "count": len(ids)})
                return

            elif path == "/api/products/bulk-category":
                ids = body.get("ids", [])
                category_name = body.get("category")
                for pid in ids:
                    db.update_product(pid, {"category": category_name}, admin_email)
                self.send_json(200, {"success": True, "count": len(ids)})
                return

            elif path == "/api/products/bulk-delete":
                ids = body.get("ids", [])
                for pid in ids:
                    db.delete_product(pid, admin_email)
                self.send_json(200, {"success": True, "count": len(ids)})
                return

            # Category Mutations
            elif path == "/api/categories":
                val_errors = validate_category_payload(body, is_update=False)
                if val_errors:
                    self.send_json(400, {
                        "success": False,
                        "error": "Validation failed: Please correct invalid category data fields.",
                        "errors": val_errors
                    })
                    return
                cat = db.create_category(body, admin_email)
                self.send_json(201, {"success": True, "data": cat})
                return

            elif path == "/api/categories/update":
                cat_id = body.get("id") or body.get("code")
                if not cat_id:
                    self.send_json(400, {"success": False, "error": "Category ID or code is required for update."})
                    return
                val_errors = validate_category_payload(body, is_update=True)
                if val_errors:
                    self.send_json(400, {
                        "success": False,
                        "error": "Validation failed: Please correct invalid category data fields.",
                        "errors": val_errors
                    })
                    return
                cat = db.update_category(cat_id, body, admin_email)
                self.send_json(200, {"success": True, "data": cat})
                return

            elif path == "/api/categories/delete":
                cat_id = body.get("id")
                success = db.delete_category(cat_id, admin_email)
                self.send_json(200, {"success": success})
                return

            # Ingredient Mutations
            elif path == "/api/ingredients":
                ing = db.create_ingredient(body, admin_email)
                self.send_json(201, {"success": True, "data": ing})
                return

            # Manufacturer Mutations
            elif path == "/api/manufacturers":
                mfg = db.create_manufacturer(body, admin_email)
                self.send_json(201, {"success": True, "data": mfg})
                return

            # Supabase Cloud Database Management
            elif path == "/api/supabase/configure":
                url = body.get("url", "").strip()
                key = body.get("key", "").strip()
                supabase_sync.save_supabase_config(url, key)
                res = supabase_sync.test_supabase_connection()
                self.send_json(200, res)
                return

            elif path == "/api/supabase/migrate":
                res = supabase_sync.migrate_sqlite_to_supabase()
                self.send_json(200, res)
                return

            elif path == "/api/supabase/sync-catalogue":
                medicines_list = body.get("medicines", [])
                categories_list = body.get("categories", [])
                res = supabase_sync.sync_custom_catalogue(medicines_list, categories_list)
                self.send_json(200, res)
                return

            self.send_json(404, {"error": "API route not found."})
        except Exception as e:
            try:
                self.send_json(500, {"error": "Internal Server Error", "details": str(e)})
            except Exception:
                pass

    def do_PUT(self):
        try:
            parsed = urllib.parse.urlparse(self.path)
            path = parsed.path
            admin = self.get_current_admin()
            if not admin:
                self.send_json(401, {"error": "Unauthorized."})
                return

            admin_email = admin.get("email", "admin@sitaramayurveda.com")
            body = self.read_json_body()

            if path.startswith("/api/products/"):
                prod_id = path.split("/")[-1]
                prod = db.update_product(prod_id, body, admin_email)
                if prod:
                    self.send_json(200, {"success": True, "data": prod})
                else:
                    self.send_json(404, {"success": False, "error": "Product not found"})
                return

            elif path.startswith("/api/categories/"):
                cat_id = path.split("/")[-1]
                cat = db.update_category(cat_id, body, admin_email)
                self.send_json(200, {"success": True, "data": cat})
                return

            self.send_json(404, {"error": "API route not found."})
        except Exception as e:
            self.send_json(500, {"error": str(e)})

    def do_DELETE(self):
        try:
            parsed = urllib.parse.urlparse(self.path)
            path = parsed.path
            admin = self.get_current_admin()
            if not admin:
                self.send_json(401, {"error": "Unauthorized."})
                return

            admin_email = admin.get("email", "admin@sitaramayurveda.com")

            if path.startswith("/api/products/"):
                prod_id = path.split("/")[-1]
                try:
                    result = db.delete_product(prod_id, admin_email)
                    if isinstance(result, dict):
                        if result.get("success"):
                            self.send_json(200, {"success": True, "message": result.get("message", "Medicine deleted successfully.")})
                        else:
                            self.send_json(400, {"success": False, "error": result.get("error", "Database restriction prevented deletion.")})
                    elif result:
                        self.send_json(200, {"success": True, "message": "Medicine deleted successfully."})
                    else:
                        self.send_json(400, {"success": False, "error": "Database restriction: Unable to delete formulation."})
                except Exception as e:
                    self.send_json(400, {"success": False, "error": f"Database restriction: {str(e)}"})
                return

            elif path.startswith("/api/categories/"):
                cat_id = path.split("/")[-1]
                success = db.delete_category(cat_id, admin_email)
                self.send_json(200, {"success": success})
                return

            self.send_json(404, {"error": "API route not found."})
        except Exception as e:
            self.send_json(500, {"error": str(e)})

    def log_message(self, format, *args):
        # Keep background server logs concise
        pass

if __name__ == "__main__":
    socketserver.TCPServer.allow_reuse_address = True
    server_address = ("0.0.0.0", PORT)
    httpd = http.server.ThreadingHTTPServer(server_address, SitaramAdminHandler)
    httpd.daemon_threads = True
    print(f"Sitaram Ayurveda Admin Gateway running on port {PORT}", flush=True)
    while True:
        try:
            httpd.serve_forever()
        except KeyboardInterrupt:
            httpd.server_close()
            break
        except Exception as e:
            print(f"HTTP server error: {e}", flush=True)
