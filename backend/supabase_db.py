"""
Supabase Database Integration Layer for Sitaram Ayurveda Medicine Catalogue.
Direct PostgREST API client managing Products and Categories tables.
"""

import json
import os
import re
import urllib.request
import urllib.parse
import urllib.error
import time

CONFIG_PATH = os.path.join(os.path.dirname(__file__), "..", "data", "supabase_config.json")
LOCAL_DATA_PATH = os.path.join(os.path.dirname(__file__), "..", "data", "sitaram_export.json")

DEFAULT_SUPABASE_URL = "https://ksnsfilauqzxsegpjpdt.supabase.co"
DEFAULT_SUPABASE_KEY = ""

_cached_url = None
_cached_key = None


def get_supabase_credentials():
    global _cached_url, _cached_key
    if _cached_url is not None and _cached_key is not None:
        return _cached_url, _cached_key

    # Check env vars first
    env_url = os.environ.get("SUPABASE_URL")
    env_key = os.environ.get("SUPABASE_KEY") or os.environ.get("SUPABASE_ANON_KEY") or os.environ.get("SUPABASE_SERVICE_ROLE_KEY")

    if env_url and env_key:
        _cached_url = env_url.rstrip("/")
        _cached_key = env_key.strip()
        return _cached_url, _cached_key

    # Check config file
    if os.path.exists(CONFIG_PATH):
        try:
            with open(CONFIG_PATH, "r", encoding="utf-8") as f:
                cfg = json.load(f)
                _cached_url = (cfg.get("url") or DEFAULT_SUPABASE_URL).rstrip("/")
                _cached_key = (cfg.get("key") or "").strip()
                return _cached_url, _cached_key
        except Exception as e:
            print(f"[SupabaseDB] Error loading config: {e}")

    _cached_url = DEFAULT_SUPABASE_URL
    _cached_key = DEFAULT_SUPABASE_KEY
    return _cached_url, _cached_key


def set_supabase_credentials(url: str, key: str):
    global _cached_url, _cached_key
    clean_url = (url or DEFAULT_SUPABASE_URL).strip().rstrip("/")
    clean_key = (key or "").strip()
    _cached_url = clean_url
    _cached_key = clean_key

    os.makedirs(os.path.dirname(CONFIG_PATH), exist_ok=True)
    try:
        with open(CONFIG_PATH, "w", encoding="utf-8") as f:
            json.dump({"url": clean_url, "key": clean_key}, f, indent=2)
        print(f"[SupabaseDB] Credentials updated. URL: {clean_url}")
        return True
    except Exception as e:
        print(f"[SupabaseDB] Failed to save config: {e}")
        return False


def _get_local_cache():
    if os.path.exists(LOCAL_DATA_PATH):
        try:
            with open(LOCAL_DATA_PATH, "r", encoding="utf-8") as f:
                return json.load(f)
        except Exception:
            pass
    return {"products": [], "categories": []}


def _save_local_cache(data):
    try:
        os.makedirs(os.path.dirname(LOCAL_DATA_PATH), exist_ok=True)
        with open(LOCAL_DATA_PATH, "w", encoding="utf-8") as f:
            json.dump(data, f, indent=2)
    except Exception as e:
        print(f"[SupabaseDB] Failed to update local cache: {e}")


def supabase_api_call(endpoint: str, method: str = "GET", data: dict = None, params: dict = None, headers_extra: dict = None):
    """
    Executes a direct PostgREST request to Supabase.
    """
    url, key = get_supabase_credentials()

    # Allow request-level overrides from Admin headers
    if headers_extra:
        if headers_extra.get("X-Supabase-Url"):
            url = headers_extra["X-Supabase-Url"].rstrip("/")
        if headers_extra.get("X-Supabase-Key"):
            key = headers_extra["X-Supabase-Key"].strip()

    if not url or not key:
        return {
            "success": False,
            "error": "Supabase credentials not configured. Please supply a valid Supabase URL and API Key in Settings.",
            "unconfigured": True
        }

    clean_endpoint = endpoint.lstrip("/")
    req_url = f"{url}/rest/v1/{clean_endpoint}"
    if params:
        query_string = urllib.parse.urlencode(params)
        req_url += f"?{query_string}"

    headers = {
        "apikey": key,
        "Authorization": f"Bearer {key}",
        "Content-Type": "application/json",
        "Accept": "application/json",
        "Prefer": "return=representation"
    }

    if headers_extra:
        for k, v in headers_extra.items():
            if k.lower() not in ["x-supabase-key", "x-supabase-url"]:
                headers[k] = v

    body_bytes = None
    if data is not None:
        body_bytes = json.dumps(data).encode("utf-8")

    req = urllib.request.Request(req_url, data=body_bytes, headers=headers, method=method)

    try:
        with urllib.request.urlopen(req, timeout=12) as response:
            status_code = response.getcode()
            resp_body = response.read().decode("utf-8")
            parsed_data = None
            if resp_body:
                try:
                    parsed_data = json.loads(resp_body)
                except Exception:
                    parsed_data = resp_body
            return {
                "success": True,
                "status_code": status_code,
                "data": parsed_data
            }
    except urllib.error.HTTPError as e:
        err_msg = ""
        try:
            err_body = e.read().decode("utf-8")
            err_json = json.loads(err_body)
            err_msg = err_json.get("message") or err_json.get("error") or err_json.get("details") or err_body
        except Exception:
            err_msg = str(e)
        return {
            "success": False,
            "status_code": e.code,
            "error": f"Supabase error ({e.code}): {err_msg}",
            "raw": err_msg
        }
    except urllib.error.URLError as e:
        return {
            "success": False,
            "error": f"Network error connecting to Supabase: {str(e.reason)}",
            "offline": True
        }
    except Exception as e:
        return {
            "success": False,
            "error": f"Unexpected error during Supabase operation: {str(e)}"
        }


# ====================================================================
# PRODUCT OPERATIONS (CUD & Read)
# ====================================================================

def get_all_products(headers_extra=None):
    res = supabase_api_call("products", method="GET", params={"select": "*", "order": "id.asc"}, headers_extra=headers_extra)
    if res.get("success") and isinstance(res.get("data"), list):
        # Update local cache
        cache = _get_local_cache()
        cache["products"] = res["data"]
        _save_local_cache(cache)
        return {"success": True, "data": res["data"], "source": "supabase"}
    
    # Return local cache if Supabase read fails
    cache = _get_local_cache()
    return {"success": True, "data": cache.get("products", []), "source": "local_fallback", "warning": res.get("error")}


def get_product(identifier, headers_extra=None):
    ident_str = str(identifier).strip()
    url, key = get_supabase_credentials()
    if headers_extra and headers_extra.get("X-Supabase-Key"):
        key = headers_extra["X-Supabase-Key"]

    if key:
        # Check if integer ID or string code
        if ident_str.isdigit():
            endpoint = f"products?id=eq.{ident_str}&select=*"
        else:
            endpoint = f"products?code=eq.{urllib.parse.quote(ident_str)}&select=*"

        res = supabase_api_call(endpoint, method="GET", headers_extra=headers_extra)
        if res.get("success") and res.get("data") and len(res["data"]) > 0:
            return {"success": True, "data": res["data"][0]}

    # Local cache check
    cache = _get_local_cache()
    for p in cache.get("products", []):
        if str(p.get("id")) == ident_str or str(p.get("code")) == ident_str:
            return {"success": True, "data": p}

    return {"success": False, "error": f"Product '{identifier}' not found."}


def create_product(product_data: dict, headers_extra=None):
    name = (product_data.get("name") or "").strip()
    code = (product_data.get("code") or "").strip()

    if not name:
        return {"success": False, "error": "Product name is required."}
    if not code:
        code = f"SA-{int(time.time()) % 100000:05d}"

    # Verify duplicate code first
    check = get_product(code, headers_extra=headers_extra)
    if check.get("success"):
        return {"success": False, "error": f"A product with code '{code}' already exists."}

    # Normalize fields according to Supabase schema
    payload = {
        "name": name,
        "code": code,
        "category_name": product_data.get("category") or product_data.get("category_name") or "Arishtam",
        "classical_reference": product_data.get("classicalReference") or product_data.get("classical_reference") or product_data.get("classicalRef") or "",
        "dosage": product_data.get("usage") or product_data.get("dosageSummary") or product_data.get("dosage") or "",
        "indications": product_data.get("indications") or product_data.get("benefit") or "",
        "description": product_data.get("description") or product_data.get("shortDescription") or product_data.get("monograph") or "",
        "image_url": product_data.get("imageUrl") or product_data.get("image_url") or "",
        "stock": int(product_data.get("stock") or 25),
        "status": product_data.get("status") or "Active",
        "featured": bool(product_data.get("featured", False)),
        "packings": product_data.get("packings") if isinstance(product_data.get("packings"), list) else [],
        "ingredients": product_data.get("ingredients") if isinstance(product_data.get("ingredients"), list) else []
    }

    if product_data.get("category_id"):
        try:
            payload["category_id"] = int(product_data["category_id"])
        except Exception:
            pass

    if "category_id" not in payload:
        cache = _get_local_cache()
        for c in cache.get("categories", []):
            if c.get("name") == payload["category_name"] or c.get("code") == payload["category_name"]:
                if c.get("id"):
                    payload["category_id"] = c.get("id")
                    break

    url, key = get_supabase_credentials()
    if headers_extra and headers_extra.get("X-Supabase-Key"):
        key = headers_extra["X-Supabase-Key"]

    if key:
        res = supabase_api_call("products", method="POST", data=payload, headers_extra=headers_extra)
        if res.get("success"):
            created = res.get("data")
            created_item = created[0] if isinstance(created, list) and len(created) > 0 else payload
            cache = _get_local_cache()
            products = cache.get("products", [])
            products.append(created_item)
            cache["products"] = products
            _save_local_cache(cache)
            return {"success": True, "data": created_item, "message": f"Product '{name}' ({code}) saved directly to Supabase."}
        return {"success": False, "error": res.get("error") or "Failed to insert product into Supabase."}
    else:
        # Fallback local save if key not yet provided, with helpful guidance
        cache = _get_local_cache()
        products = cache.get("products", [])
        payload["id"] = len(products) + 1
        products.append(payload)
        cache["products"] = products
        _save_local_cache(cache)
        return {
            "success": True,
            "data": payload,
            "message": f"Product '{name}' ({code}) created in catalogue. (Note: Configure Supabase API Key in Settings to persist to cloud database)."
        }


def update_product(identifier, update_data: dict, headers_extra=None):
    ident_str = str(identifier).strip()
    if not ident_str:
        return {"success": False, "error": "Product identifier is required for update."}

    # Build fields to update
    payload = {}
    field_mappings = {
        "name": "name",
        "code": "code",
        "category": "category_name",
        "category_name": "category_name",
        "classicalReference": "classical_reference",
        "classical_reference": "classical_reference",
        "classicalRef": "classical_reference",
        "usage": "dosage",
        "dosageSummary": "dosage",
        "dosage": "dosage",
        "indications": "indications",
        "benefit": "indications",
        "description": "description",
        "shortDescription": "description",
        "monograph": "description",
        "imageUrl": "image_url",
        "image_url": "image_url",
        "stock": "stock",
        "status": "status",
        "featured": "featured",
        "packings": "packings",
        "ingredients": "ingredients"
    }

    for in_key, out_key in field_mappings.items():
        if in_key in update_data:
            val = update_data[in_key]
            if out_key == "stock":
                try: val = int(val)
                except Exception: val = 25
            elif out_key == "featured":
                val = bool(val)
            payload[out_key] = val

    if "category_id" in update_data:
        try: payload["category_id"] = int(update_data["category_id"])
        except Exception: pass
    elif "category_name" in payload:
        cache = _get_local_cache()
        for c in cache.get("categories", []):
            if c.get("name") == payload["category_name"] or c.get("code") == payload["category_name"]:
                if c.get("id"):
                    payload["category_id"] = c.get("id")
                    break

    if not payload:
        return {"success": False, "error": "No update fields provided."}

    url, key = get_supabase_credentials()
    if headers_extra and headers_extra.get("X-Supabase-Key"):
        key = headers_extra["X-Supabase-Key"]

    if key:
        # Filter by id or code
        if ident_str.isdigit():
            endpoint = f"products?id=eq.{ident_str}"
        else:
            endpoint = f"products?or=(code.eq.{urllib.parse.quote(ident_str)},id.eq.{urllib.parse.quote(ident_str)})"

        res = supabase_api_call(endpoint, method="PATCH", data=payload, headers_extra=headers_extra)
        if res.get("success"):
            updated = res.get("data")
            updated_item = updated[0] if isinstance(updated, list) and len(updated) > 0 else payload
            cache = _get_local_cache()
            products = cache.get("products", [])
            for i, p in enumerate(products):
                if str(p.get("id")) == ident_str or str(p.get("code")) == ident_str:
                    products[i].update(payload)
                    break
            cache["products"] = products
            _save_local_cache(cache)
            return {"success": True, "data": updated_item, "message": f"Product updated successfully in Supabase."}

        return {"success": False, "error": res.get("error") or "Failed to update product in Supabase."}
    else:
        cache = _get_local_cache()
        products = cache.get("products", [])
        found = False
        for i, p in enumerate(products):
            if str(p.get("id")) == ident_str or str(p.get("code")) == ident_str:
                products[i].update(payload)
                found = True
                break
        if not found:
            return {"success": False, "error": f"Product '{identifier}' not found."}
        cache["products"] = products
        _save_local_cache(cache)
        return {"success": True, "data": payload, "message": f"Product updated in catalogue. (Note: Configure Supabase API Key to persist changes to cloud database)."}


def delete_product(identifier, headers_extra=None):
    ident_str = str(identifier).strip()
    if not ident_str:
        return {"success": False, "error": "Product identifier is required for deletion."}

    url, key = get_supabase_credentials()
    if headers_extra and headers_extra.get("X-Supabase-Key"):
        key = headers_extra["X-Supabase-Key"]

    if key:
        if ident_str.isdigit():
            endpoint = f"products?id=eq.{ident_str}"
        else:
            endpoint = f"products?or=(code.eq.{urllib.parse.quote(ident_str)},id.eq.{urllib.parse.quote(ident_str)})"

        res = supabase_api_call(endpoint, method="DELETE", headers_extra=headers_extra)
        if res.get("success"):
            cache = _get_local_cache()
            cache["products"] = [p for p in cache.get("products", []) if str(p.get("id")) != ident_str and str(p.get("code")) != ident_str]
            _save_local_cache(cache)
            return {"success": True, "message": f"Product '{identifier}' deleted successfully from Supabase."}

        return {"success": False, "error": res.get("error") or "Failed to delete product from Supabase."}
    else:
        cache = _get_local_cache()
        orig_len = len(cache.get("products", []))
        cache["products"] = [p for p in cache.get("products", []) if str(p.get("id")) != ident_str and str(p.get("code")) != ident_str]
        if len(cache["products"]) == orig_len:
            return {"success": False, "error": f"Product '{identifier}' not found."}
        _save_local_cache(cache)
        return {"success": True, "message": f"Product '{identifier}' removed from catalogue."}


# ====================================================================
# CATEGORY OPERATIONS (CUD & Read)
# ====================================================================

def get_all_categories(headers_extra=None):
    res = supabase_api_call("categories", method="GET", params={"select": "*", "order": "id.asc"}, headers_extra=headers_extra)
    if res.get("success") and isinstance(res.get("data"), list):
        cache = _get_local_cache()
        cache["categories"] = res["data"]
        _save_local_cache(cache)
        return {"success": True, "data": res["data"], "source": "supabase"}

    cache = _get_local_cache()
    return {"success": True, "data": cache.get("categories", []), "source": "local_fallback", "warning": res.get("error")}


def create_category(category_data: dict, headers_extra=None):
    name = (category_data.get("name") or "").strip()
    code = (category_data.get("code") or "").strip().upper()

    if not name:
        return {"success": False, "error": "Category name is required."}
    if not code:
        code = re.sub(r'[^A-Z0-9]', '', name.upper())[:10] or "CAT"

    payload = {
        "name": name,
        "code": code,
        "title": category_data.get("subtitle") or category_data.get("title") or name,
        "description": category_data.get("description") or "",
        "icon": category_data.get("icon") or "🏷️",
        "status": category_data.get("status") or "Active"
    }

    url, key = get_supabase_credentials()
    if headers_extra and headers_extra.get("X-Supabase-Key"):
        key = headers_extra["X-Supabase-Key"]

    if key:
        res = supabase_api_call("categories", method="POST", data=payload, headers_extra=headers_extra)
        if res.get("success"):
            created = res.get("data")
            created_item = created[0] if isinstance(created, list) and len(created) > 0 else payload
            cache = _get_local_cache()
            cats = cache.get("categories", [])
            cats.append(created_item)
            cache["categories"] = cats
            _save_local_cache(cache)
            return {"success": True, "data": created_item, "message": f"Category '{name}' created successfully in Supabase."}

        return {"success": False, "error": res.get("error") or "Failed to create category in Supabase."}
    else:
        cache = _get_local_cache()
        cats = cache.get("categories", [])
        payload["id"] = len(cats) + 1
        cats.append(payload)
        cache["categories"] = cats
        _save_local_cache(cache)
        return {"success": True, "data": payload, "message": f"Category '{name}' created in catalogue."}


def update_category(identifier, update_data: dict, headers_extra=None):
    ident_str = str(identifier).strip()
    if not ident_str:
        return {"success": False, "error": "Category identifier is required."}

    payload = {}
    if "name" in update_data: payload["name"] = update_data["name"].strip()
    if "code" in update_data: payload["code"] = update_data["code"].strip().upper()
    if "subtitle" in update_data or "title" in update_data:
        payload["title"] = (update_data.get("subtitle") or update_data.get("title") or "").strip()
    if "description" in update_data: payload["description"] = update_data["description"].strip()
    if "icon" in update_data: payload["icon"] = update_data["icon"].strip()
    if "status" in update_data: payload["status"] = update_data["status"]

    if not payload:
        return {"success": False, "error": "No update fields provided."}

    url, key = get_supabase_credentials()
    if headers_extra and headers_extra.get("X-Supabase-Key"):
        key = headers_extra["X-Supabase-Key"]

    if key:
        if ident_str.isdigit():
            endpoint = f"categories?id=eq.{ident_str}"
        else:
            endpoint = f"categories?or=(code.eq.{urllib.parse.quote(ident_str)},name.eq.{urllib.parse.quote(ident_str)})"

        res = supabase_api_call(endpoint, method="PATCH", data=payload, headers_extra=headers_extra)
        if res.get("success"):
            updated = res.get("data")
            updated_item = updated[0] if isinstance(updated, list) and len(updated) > 0 else payload
            cache = _get_local_cache()
            cats = cache.get("categories", [])
            for i, c in enumerate(cats):
                if str(c.get("id")) == ident_str or str(c.get("code")) == ident_str or str(c.get("name")) == ident_str:
                    cats[i].update(payload)
                    break
            cache["categories"] = cats
            _save_local_cache(cache)
            return {"success": True, "data": updated_item, "message": "Category updated successfully in Supabase."}

        return {"success": False, "error": res.get("error") or "Failed to update category in Supabase."}
    else:
        cache = _get_local_cache()
        cats = cache.get("categories", [])
        found = False
        for i, c in enumerate(cats):
            if str(c.get("id")) == ident_str or str(c.get("code")) == ident_str or str(c.get("name")) == ident_str:
                cats[i].update(payload)
                found = True
                break
        if not found:
            return {"success": False, "error": f"Category '{identifier}' not found."}
        cache["categories"] = cats
        _save_local_cache(cache)
        return {"success": True, "data": payload, "message": "Category updated in catalogue."}


def delete_category(identifier, headers_extra=None):
    ident_str = str(identifier).strip()
    if not ident_str:
        return {"success": False, "error": "Category identifier is required."}

    # Safety Check: Check if products belong to this category before deleting!
    cache = _get_local_cache()
    cat_obj = None
    for c in cache.get("categories", []):
        if str(c.get("id")) == ident_str or str(c.get("code")) == ident_str or str(c.get("name")) == ident_str:
            cat_obj = c
            break

    cat_name = cat_obj.get("name") if cat_obj else ident_str
    cat_code = cat_obj.get("code") if cat_obj else ident_str

    # Query products assigned to this category
    assigned_count = len([p for p in cache.get("products", []) if p.get("category_name") in [cat_name, cat_code] or p.get("category") in [cat_name, cat_code]])

    if assigned_count > 0:
        return {
            "success": False,
            "status_code": 409,
            "error": f"Cannot delete category '{cat_name}' because {assigned_count} active medicine formulation(s) are assigned to it. Please reassign those medicines first.",
            "conflict": True
        }

    url, key = get_supabase_credentials()
    if headers_extra and headers_extra.get("X-Supabase-Key"):
        key = headers_extra["X-Supabase-Key"]

    if key:
        if ident_str.isdigit():
            endpoint = f"categories?id=eq.{ident_str}"
        else:
            endpoint = f"categories?or=(code.eq.{urllib.parse.quote(ident_str)},name.eq.{urllib.parse.quote(ident_str)})"

        res = supabase_api_call(endpoint, method="DELETE", headers_extra=headers_extra)
        if res.get("success"):
            cache["categories"] = [c for c in cache.get("categories", []) if str(c.get("id")) != ident_str and str(c.get("code")) != ident_str and str(c.get("name")) != ident_str]
            _save_local_cache(cache)
            return {"success": True, "message": f"Category '{cat_name}' deleted successfully from Supabase."}

        return {"success": False, "error": res.get("error") or "Failed to delete category from Supabase."}
    else:
        orig_len = len(cache.get("categories", []))
        cache["categories"] = [c for c in cache.get("categories", []) if str(c.get("id")) != ident_str and str(c.get("code")) != ident_str and str(c.get("name")) != ident_str]
        if len(cache["categories"]) == orig_len:
            return {"success": False, "error": f"Category '{identifier}' not found."}
        _save_local_cache(cache)
        return {"success": True, "message": f"Category '{cat_name}' deleted from catalogue."}


def check_connection(url=None, key=None):
    test_headers = {}
    if url: test_headers["X-Supabase-Url"] = url
    if key: test_headers["X-Supabase-Key"] = key

    t0 = time.time()
    res = supabase_api_call("products?select=id&limit=1", method="GET", headers_extra=test_headers)
    latency_ms = round((time.time() - t0) * 1000)

    if res.get("success"):
        return {
            "success": True,
            "latency_ms": latency_ms,
            "message": "Connected successfully to Supabase cloud PostgreSQL database!",
            "url": url or get_supabase_credentials()[0]
        }
    return {
        "success": False,
        "latency_ms": latency_ms,
        "error": res.get("error"),
        "unconfigured": res.get("unconfigured", False),
        "url": url or get_supabase_credentials()[0]
    }
