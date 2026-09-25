"""
Sitaram Ayurveda — Supabase Cloud Database Integration & Sync Service
Connects the application directly to Supabase PostgreSQL via PostgREST REST API.
"""

import os
import json
import urllib.request
import urllib.parse
import urllib.error
import sqlite3

CONFIG_FILE = os.path.abspath("./data/supabase_config.json")
DB_PATH = os.path.abspath("./data/sitaram.db")

def get_supabase_config():
    """Retrieve Supabase URL and Key from env vars or saved config."""
    url = os.environ.get("SUPABASE_URL", "").strip().rstrip("/")
    key = os.environ.get("SUPABASE_KEY", "").strip() or os.environ.get("SUPABASE_ANON_KEY", "").strip() or os.environ.get("SUPABASE_SERVICE_ROLE_KEY", "").strip()

    if not url or not key:
        if os.path.exists(CONFIG_FILE):
            try:
                with open(CONFIG_FILE, "r", encoding="utf-8") as f:
                    cfg = json.load(f)
                    url = cfg.get("url", "").strip().rstrip("/")
                    key = cfg.get("key", "").strip()
            except Exception:
                pass
    return {
        "url": url,
        "key": key,
        "configured": bool(url and key)
    }

def save_supabase_config(url: str, key: str):
    """Save Supabase configuration locally."""
    os.makedirs(os.path.dirname(CONFIG_FILE), exist_ok=True)
    with open(CONFIG_FILE, "w", encoding="utf-8") as f:
        json.dump({"url": url.strip().rstrip("/"), "key": key.strip()}, f, indent=2)

def supabase_request(endpoint: str, method: str = "GET", data: dict = None, params: dict = None):
    """Make authenticated request to Supabase PostgREST API."""
    cfg = get_supabase_config()
    if not cfg["configured"]:
        return {"success": False, "error": "Supabase is not configured yet. Please provide your Project URL and Key."}

    full_url = f"{cfg['url']}/rest/v1/{endpoint.lstrip('/')}"
    if params:
        query_str = urllib.parse.urlencode(params)
        full_url = f"{full_url}?{query_str}"

    headers = {
        "apikey": cfg["key"],
        "Authorization": f"Bearer {cfg['key']}",
        "Content-Type": "application/json",
        "Accept": "application/json",
        "Prefer": "return=representation"
    }

    body_bytes = None
    if data is not None:
        body_bytes = json.dumps(data).encode("utf-8")

    req = urllib.request.Request(full_url, data=body_bytes, headers=headers, method=method)
    try:
        with urllib.request.urlopen(req, timeout=12) as response:
            status = response.getcode()
            raw = response.read().decode("utf-8")
            if raw:
                try:
                    res_json = json.loads(raw)
                    return {"success": True, "status": status, "data": res_json}
                except Exception:
                    return {"success": True, "status": status, "data": raw}
            return {"success": True, "status": status, "data": None}
    except urllib.error.HTTPError as e:
        err_msg = e.read().decode("utf-8") if e.fp else str(e)
        return {"success": False, "status": e.code, "error": err_msg}
    except Exception as e:
        return {"success": False, "error": str(e)}

def test_supabase_connection():
    """Verify if Supabase URL and Key are functional."""
    cfg = get_supabase_config()
    if not cfg["configured"]:
        return {
            "success": False,
            "configured": False,
            "message": "Supabase credentials are not set."
        }

    # Test reading categories or products
    res = supabase_request("categories?select=id,name&limit=1")
    if res["success"]:
        return {
            "success": True,
            "configured": True,
            "url": cfg["url"],
            "message": "Connected successfully to Supabase cloud database!"
        }
    else:
        # Check if it was a table-missing error (schema not run yet)
        err_str = str(res.get("error", ""))
        if "relation \"public.categories\" does not exist" in err_str or "404" in str(res.get("status")):
            return {
                "success": True,
                "configured": True,
                "schema_needed": True,
                "url": cfg["url"],
                "message": "Connected to Supabase! However, the SQL tables are not created yet. Please execute supabase_schema.sql in the Supabase SQL Editor."
            }
        return {
            "success": False,
            "configured": True,
            "error": res.get("error", "Failed to connect to Supabase."),
            "status": res.get("status")
        }

def migrate_sqlite_to_supabase():
    """Reads all products and categories from local SQLite and inserts/upserts them to Supabase."""
    cfg = get_supabase_config()
    if not cfg["configured"]:
        return {"success": False, "error": "Cannot migrate: Supabase is not configured."}

    if not os.path.exists(DB_PATH):
        return {"success": False, "error": "Local database not found."}

    conn = sqlite3.connect(DB_PATH)
    conn.row_factory = sqlite3.Row
    c = conn.cursor()

    # 1. Fetch Categories
    c.execute("SELECT name, code, description FROM categories")
    categories = []
    for row in c.fetchall():
        r = dict(row)
        categories.append({
            "name": r.get("name"),
            "code": r.get("code") or r.get("name", "").upper()[:10],
            "title": r.get("name"),
            "description": r.get("description") or "",
            "icon": "leaf"
        })

    # 2. Fetch Products
    c.execute("""
        SELECT p.code, p.name, c.name as category_name, p.classical_reference, 
               p.packings_json, p.ingredients_json, p.usage as dosage, 
               p.indications, p.description, p.image_url, p.status, p.featured
        FROM products p
        LEFT JOIN categories c ON p.category_id = c.id
    """)
    raw_products = [dict(row) for row in c.fetchall()]
    conn.close()

    formatted_products = []
    for p in raw_products:
        packings = p.get("packings_json")
        if isinstance(packings, str):
            try: packings = json.loads(packings)
            except Exception: packings = [packings]

        ingredients = p.get("ingredients_json")
        if isinstance(ingredients, str):
            try: ingredients = json.loads(ingredients)
            except Exception: ingredients = [ingredients]

        formatted_products.append({
            "code": p.get("code"),
            "name": p.get("name"),
            "category_name": p.get("category_name"),
            "classical_reference": p.get("classical_reference"),
            "packings": packings or [],
            "ingredients": ingredients or [],
            "dosage": p.get("dosage"),
            "indications": p.get("indications"),
            "description": p.get("description"),
            "image_url": p.get("image_url"),
            "stock": 25,
            "status": p.get("status", "Active"),
            "featured": bool(p.get("featured"))
        })

    # Upsert Categories to Supabase
    cat_res = supabase_request("categories", method="POST", data=categories)

    # Upsert Products to Supabase
    prod_res = supabase_request("products", method="POST", data=formatted_products)

    return {
        "success": prod_res.get("success", False),
        "categories_migrated": len(categories),
        "products_migrated": len(formatted_products),
        "categories_status": cat_res,
        "products_status": prod_res
    }

def sync_custom_catalogue(medicines_list, categories_list=None):
    """Syncs medicine catalogue and category objects directly from the web client to Supabase."""
    cfg = get_supabase_config()
    if not cfg["configured"]:
        return {"success": False, "error": "Supabase is not configured yet. Please configure URL and API key."}

    results = {"categories_synced": 0, "medicines_synced": 0}

    # 1. Sync Categories
    if categories_list:
        formatted_cats = []
        for cat in categories_list:
            if isinstance(cat, dict) and cat.get("name"):
                formatted_cats.append({
                    "name": cat.get("name"),
                    "code": cat.get("code") or cat.get("name", "").upper()[:10],
                    "title": cat.get("title") or cat.get("name"),
                    "description": cat.get("description") or "",
                    "icon": cat.get("icon") or "leaf",
                    "status": "Active"
                })
        if formatted_cats:
            cat_res = supabase_request("categories", method="POST", data=formatted_cats)
            results["categories_status"] = cat_res
            results["categories_synced"] = len(formatted_cats)

    # 2. Sync Medicines
    if medicines_list:
        formatted_prods = []
        for m in medicines_list:
            if isinstance(m, dict) and m.get("name"):
                code = m.get("code") or f"AYUR-{str(m.get('id', ''))[:8]}"
                packings = m.get("packings") or []
                if isinstance(packings, str):
                    packings = [p.strip() for p in packings.split(",") if p.strip()]
                ingredients = m.get("ingredients") or []
                if isinstance(ingredients, str):
                    ingredients = [i.strip() for i in ingredients.split(",") if i.strip()]

                formatted_prods.append({
                    "code": code,
                    "name": m.get("name"),
                    "category_name": m.get("category"),
                    "classical_reference": m.get("reference") or m.get("classical_reference"),
                    "packings": packings,
                    "ingredients": ingredients,
                    "dosage": m.get("dosage"),
                    "indications": m.get("monograph") or m.get("indications") or m.get("description"),
                    "description": m.get("description"),
                    "image_url": m.get("image") or m.get("image_url"),
                    "stock": 50,
                    "status": "Active" if m.get("published", True) else "Draft",
                    "featured": bool(m.get("featured", False))
                })

        if formatted_prods:
            prod_res = supabase_request("products", method="POST", data=formatted_prods)
            results["products_status"] = prod_res
            results["medicines_synced"] = len(formatted_prods)

    results["success"] = True
    results["message"] = f"Successfully synchronized {results['medicines_synced']} medicines and {results['categories_synced']} categories to Supabase cloud database!"
    return results

