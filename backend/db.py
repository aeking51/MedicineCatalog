"""
Sitaram Ayurveda Medicine Catalogue — Central Database Layer (SQLite)
Shared backend persistence for the Admin Website and future Android applications.
"""

import sqlite3
import os
import hashlib
import secrets
import json
from datetime import datetime, timedelta

DB_PATH = os.path.abspath("./data/sitaram.db")

def get_connection():
    os.makedirs(os.path.dirname(DB_PATH), exist_ok=True)
    conn = sqlite3.connect(DB_PATH, check_same_thread=False)
    conn.row_factory = sqlite3.Row
    return conn

def hash_password(password: str, salt: str = None) -> tuple:
    if not salt:
        salt = secrets.token_hex(16)
    key = hashlib.pbkdf2_hmac(
        'sha256',
        password.encode('utf-8'),
        salt.encode('utf-8'),
        100000
    )
    return key.hex(), salt

def verify_password(password: str, salt: str, password_hash: str) -> bool:
    key = hashlib.pbkdf2_hmac(
        'sha256',
        password.encode('utf-8'),
        salt.encode('utf-8'),
        100000
    )
    return key.hex() == password_hash

def init_db():
    conn = get_connection()
    c = conn.cursor()

    # 1. Admins Table
    c.execute("""
    CREATE TABLE IF NOT EXISTS admins (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        username TEXT UNIQUE NOT NULL,
        email TEXT UNIQUE NOT NULL,
        password_hash TEXT NOT NULL,
        salt TEXT NOT NULL,
        name TEXT NOT NULL,
        role TEXT NOT NULL,
        avatar TEXT,
        created_at TEXT NOT NULL
    )
    """)

    # 2. Server Sessions Table
    c.execute("""
    CREATE TABLE IF NOT EXISTS sessions (
        token TEXT PRIMARY KEY,
        admin_id INTEGER NOT NULL,
        created_at TEXT NOT NULL,
        expires_at TEXT NOT NULL,
        FOREIGN KEY (admin_id) REFERENCES admins (id) ON DELETE CASCADE
    )
    """)

    # 3. Categories Table (24 Classical Handbook Categories)
    c.execute("""
    CREATE TABLE IF NOT EXISTS categories (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        name TEXT UNIQUE NOT NULL,
        code TEXT UNIQUE NOT NULL,
        description TEXT,
        sort_order INTEGER NOT NULL,
        status TEXT NOT NULL DEFAULT 'Active'
    )
    """)

    # 4. Products Table
    c.execute("""
    CREATE TABLE IF NOT EXISTS products (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        code TEXT UNIQUE NOT NULL,
        name TEXT NOT NULL,
        category_id INTEGER NOT NULL,
        classical_reference TEXT,
        packings_json TEXT NOT NULL,
        ingredients_json TEXT NOT NULL,
        usage TEXT,
        indications TEXT,
        description TEXT,
        image_url TEXT,
        status TEXT NOT NULL DEFAULT 'Active',
        featured INTEGER NOT NULL DEFAULT 0,
        created_at TEXT NOT NULL,
        updated_at TEXT NOT NULL,
        FOREIGN KEY (category_id) REFERENCES categories (id)
    )
    """)

    # 5. Ingredients Master Table
    c.execute("""
    CREATE TABLE IF NOT EXISTS ingredients (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        name TEXT UNIQUE NOT NULL,
        botanical_name TEXT,
        sanskrit_name TEXT,
        part_used TEXT
    )
    """)

    # 6. Manufacturers Table
    c.execute("""
    CREATE TABLE IF NOT EXISTS manufacturers (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        name TEXT NOT NULL,
        code TEXT UNIQUE NOT NULL,
        license TEXT,
        contact_person TEXT,
        email TEXT,
        phone TEXT,
        address TEXT,
        status TEXT NOT NULL DEFAULT 'Active'
    )
    """)

    # 7. Audit Logs Table
    c.execute("""
    CREATE TABLE IF NOT EXISTS audit_logs (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        admin_id INTEGER,
        admin_email TEXT NOT NULL,
        action TEXT NOT NULL,
        entity_type TEXT NOT NULL,
        entity_id TEXT,
        details TEXT NOT NULL,
        ip_address TEXT,
        timestamp TEXT NOT NULL
    )
    """)

    # 8. Users / Profiles Table (Synchronized with Android App & Admin Panel)
    c.execute("""
    CREATE TABLE IF NOT EXISTS users (
        id TEXT PRIMARY KEY,
        name TEXT NOT NULL,
        email TEXT UNIQUE NOT NULL,
        role TEXT NOT NULL DEFAULT 'PATIENT',
        status TEXT NOT NULL DEFAULT 'Active',
        prakriti TEXT NOT NULL DEFAULT 'Pitta',
        designation TEXT DEFAULT '',
        phone TEXT DEFAULT '',
        avatar_url TEXT DEFAULT '',
        clinical_notes TEXT DEFAULT '',
        adherence_percent INTEGER DEFAULT 85,
        password_hash TEXT DEFAULT '',
        salt TEXT DEFAULT '',
        created_at TEXT NOT NULL,
        updated_at TEXT NOT NULL
    )
    """)

    conn.commit()

    # Seed Users if none exist
    c.execute("SELECT COUNT(*) FROM users")
    if c.fetchone()[0] == 0:
        now = datetime.utcnow().isoformat()
        pwd_hash, salt = hash_password("ayur123")
        admin_pwd_hash, admin_salt = hash_password("admin123")

        default_app_users = [
            ("user_admin_jerin", "Jerin MR", "sys.jerin@gmail.com", "ADMIN", "Active", "Tridoshic", "Chief Administrator & System Director", "+91 98450 11001", "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=120", "Primary system administrator. Full clinical pharmacopoeia, formulation inventory, and user directory management.", 98, admin_pwd_hash, admin_salt, now, now),
            ("user_practitioner_meera", "Dr. Meera Nambiar", "dr.meera@ayurguide.org", "PRACTITIONER", "Active", "Pitta", "Senior Ayurvedic Physician (BAMS, MD)", "+91 98450 22002", "https://images.unsplash.com/photo-1559839734-2b71ea197ec2?w=120", "Specialist in Dravyaguna (Herbal pharmacology) & Kayachikitsa.", 94, pwd_hash, salt, now, now),
            ("user_patient_arjun", "Arjun Mehta", "arjun.m@example.com", "PATIENT", "Active", "Vata", "Wellness Seeker", "+91 98450 44004", "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=120", "Practicing Dinacharya routine and herbal tea regimen for grounding nervous system.", 88, pwd_hash, salt, now, now),
            ("user_admin_ramanathan", "Dr. D. Ramanathan", "admin@sitaramayurveda.com", "ADMIN", "Active", "Tridoshic", "Chief Medical Administrator", "+91 98450 00000", "https://images.unsplash.com/photo-1622253692010-333f2da6031d?w=120", "Chief Medical Administrator & Formulary Director.", 99, admin_pwd_hash, admin_salt, now, now)
        ]
        c.executemany("""
            INSERT INTO users (id, name, email, role, status, prakriti, designation, phone, avatar_url, clinical_notes, adherence_percent, password_hash, salt, created_at, updated_at)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """, default_app_users)
        conn.commit()

    # Seed Default Administrators if none exist
    c.execute("SELECT COUNT(*) FROM admins")
    if c.fetchone()[0] == 0:
        now = datetime.utcnow().isoformat()
        
        # User requested admin: sys.jerin@gmail.com
        pwd_hash1, salt1 = hash_password("admin123")
        c.execute("""
            INSERT INTO admins (username, email, password_hash, salt, name, role, avatar, created_at)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        """, ("jerin_admin", "sys.jerin@gmail.com", pwd_hash1, salt1, "Jerin Administrator", "Lead Systems Administrator", "", now))

        # Primary Sitaram Administrator
        pwd_hash2, salt2 = hash_password("Sitaram@1921")
        c.execute("""
            INSERT INTO admins (username, email, password_hash, salt, name, role, avatar, created_at)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        """, ("sitaram_admin", "admin@sitaramayurveda.com", pwd_hash2, salt2, "Dr. D. Ramanathan", "Chief Medical Administrator", "", now))
        
        conn.commit()

    # Seed Categories if none exist
    c.execute("SELECT COUNT(*) FROM categories")
    if c.fetchone()[0] == 0:
        handbook_categories = [
            ("Arishtam", "ARI", "Self-generated herbal fermented elixirs and tonics.", 1),
            ("Asavam", "ASA", "Fermented infusions prepared without boiling water.", 2),
            ("Arkam", "ARK", "Distilled herbal extracts capturing essential volatile actives.", 3),
            ("Bhasmas / Ksharams", "BHA", "Calcined mineral preparations and alkaline botanical ashes.", 4),
            ("Choornams", "CHO", "Micro-pulverized medicated herbal powders.", 5),
            ("Gulika, Gulika Tablets, Capsules", "GUL", "Classical pills, compressed tablets and veg capsules.", 6),
            ("Single Herb Veg Capsules", "SHC", "Pure single botanical standard extracts in vegetarian shells.", 7),
            ("Kashayams", "KAS", "Concentrated classical herbal decoctions.", 8),
            ("Kashayam Tablets", "KTB", "Aqueous decoctions spray-dried into convenient tablets.", 9),
            ("Preservative Free Kashayam Sachet", "KSC", "Pure vacuum-sealed decoctions with zero artificial preservatives.", 10),
            ("Lehyams", "LEH", "Semi-solid nutritive herbal jams prepared in raw jaggery and ghee.", 11),
            ("Ghruthams", "GHR", "Medicated cow's ghee preparations traversing the blood-brain barrier.", 12),
            ("Avartis", "AVA", "Repeatedly potentiated herbal lipid formulations (e.g. 101 Avarti).", 13),
            ("Soft Gel Capsules", "SGC", "Lipid soluble classical medicated oils encapsulated for exact dosing.", 14),
            ("Seviyams / Vasthi Thailams", "SVT", "Internal administration and panchakarma enema oils.", 15),
            ("Erand", "ERA", "Purified castor-oil based formulations for deep purgation and vata.", 16),
            ("Tailams / Keratailams", "THI", "Classical medicated sesame and coconut oils for abhyanga and shirodhara.", 17),
            ("Kuzhambu", "KUZ", "Viscous poly-herbal lipid formulations for musculoskeletal disorders.", 18),
            ("Lepam", "LEP", "Medicated herbal pastes for external dermatological application.", 19),
            ("Ointments / Creams", "OIN", "Modern topical emollient bases infused with classical actives.", 20),
            ("Patent / Proprietary Formulations", "PAT", "Research-backed proprietary clinical formulations by Sitaram.", 21),
            ("Drops", "DRP", "Nasal (Nasya), ear (Karnapoorana) and eye drop formulations.", 22),
            ("Syrups / Tonics", "SYR", "Palatable sweet medicinal syrups for pediatric and geriatric care.", 23),
            ("Miscellaneous Products", "MIS", "General wellness and traditional Ayurvedic adjuncts.", 24)
        ]
        for name, code, desc, order in handbook_categories:
            c.execute("""
                INSERT INTO categories (name, code, description, sort_order, status)
                VALUES (?, ?, ?, ?, 'Active')
            """, (name, code, desc, order))
        conn.commit()

    # Seed Manufacturers
    c.execute("SELECT COUNT(*) FROM manufacturers")
    if c.fetchone()[0] == 0:
        c.execute("""
            INSERT INTO manufacturers (name, code, license, contact_person, email, phone, address, status)
            VALUES (?, ?, ?, ?, ?, ?, ?, 'Active')
        """, (
            "Sitaram Ayurveda Pvt. Ltd. (Main Unit)",
            "SAT-KL-01",
            "AYUSH-GMP/KL/2004/0018",
            "Dr. V. Radhakrishnan",
            "production@sitaramayurveda.com",
            "+91 487 2381238",
            "Round South, Thrissur, Kerala - 680001, India"
        ))
        conn.commit()

    # Seed Initial Products if empty
    c.execute("SELECT COUNT(*) FROM products")
    if c.fetchone()[0] == 0:
        c.execute("SELECT id FROM categories WHERE name = 'Arishtam'")
        cat_row = c.fetchone()
        cat_id = cat_row[0] if cat_row else 1
        now = datetime.utcnow().isoformat()

        initial_formulations = [
            (
                "SA-00001",
                "Abhayarishtam",
                cat_id,
                "Ashtangahrudayam, Arshorogadhikaram",
                json.dumps(["450 ml", "200 ml"]),
                json.dumps(["Abhaya (Terminalia chebula)", "Dhatri (Emblica officinalis)", "Kapitha (Feronia elephantum)", "Vishala (Citrullus colocynthis)"]),
                "15 to 25 ml twice daily after meals with equal quantity of warm water.",
                "Arshas (Hemorrhoids), Udara (Abdominal disorders), Vibanda (Constipation), Agnimandya (Impaired digestion).",
                "Classic Ayurvedic fermented formulation indicated primarily for hemorrhoids, sluggish digestion, and chronic constipation.",
                "https://images.unsplash.com/photo-1546868871-7041f2a55e12?w=600",
                "Active",
                1
            ),
            (
                "SA-00002",
                "Amritarishtam",
                cat_id,
                "Bhaishajya Ratnavali, Jwaradhikaram",
                json.dumps(["450 ml"]),
                json.dumps(["Amrita / Guduchi (Tinospora cordifolia)", "Bilva (Aegle marmelos)", "Agnimantha (Premna integrifolia)", "Shyonaka (Oroxylum indicum)"]),
                "15 to 25 ml twice daily after food.",
                "Jwara (Chronic & intermittent fevers), Jeerna Jwara, Ajeerna, Yakrit roga (Hepatic sluggishness).",
                "Potent immunomodulatory elixir that detoxifies Ama, strengthens hepatic function, and relieves recurrent pyrexia.",
                "https://images.unsplash.com/photo-1622253692010-333f2da6031d?w=600",
                "Active",
                1
            ),
            (
                "SA-00003",
                "Ashokarishtam",
                cat_id,
                "Bhaishajya Ratnavali, Pradaradhikaram",
                json.dumps(["450 ml", "200 ml"]),
                json.dumps(["Ashoka (Saraca asoca)", "Dhataki (Woodfordia fruticosa)", "Musta (Cyperus rotundus)", "Haritaki (Terminalia chebula)"]),
                "15 to 25 ml twice daily after food or as directed by the physician.",
                "Asrigdara (Menorrhagia), Pradara (Leucorrhea), Katishoola (Low back pain), Shweta Pradara.",
                "Classical uterine tonic indicated for hormonal harmony, excessive menstrual bleeding, and pelvic comfort.",
                "https://images.unsplash.com/photo-1584308666744-24d5c474f2ae?w=600",
                "Active",
                0
            )
        ]

        for p in initial_formulations:
            c.execute("""
                INSERT INTO products (code, name, category_id, classical_reference, packings_json, ingredients_json, usage, indications, description, image_url, status, featured, created_at, updated_at)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """, (*p, now, now))
        
        conn.commit()

    # Seed initial botanicals
    c.execute("SELECT COUNT(*) FROM ingredients")
    if c.fetchone()[0] == 0:
        botanicals = [
            ("Abhaya", "Terminalia chebula", "Haritaki", "Fruit rind"),
            ("Amrita / Guduchi", "Tinospora cordifolia", "Guduchi", "Stem"),
            ("Ashoka", "Saraca asoca", "Ashoka", "Stem bark"),
            ("Dhatri", "Emblica officinalis", "Amalaki", "Pericarp"),
            ("Bilva", "Aegle marmelos", "Bilva", "Root / Fruit"),
            ("Musta", "Cyperus rotundus", "Mustaka", "Rhizome")
        ]
        for b in botanicals:
            c.execute("""
                INSERT INTO ingredients (name, botanical_name, sanskrit_name, part_used)
                VALUES (?, ?, ?, ?)
            """, b)
        conn.commit()

    conn.close()

# Auth Helpers
def authenticate_admin(identifier, password):
    conn = get_connection()
    c = conn.cursor()
    clean_id = (identifier or "").strip().lower()
    c.execute("SELECT id, username, email, password_hash, salt, name, role, avatar FROM admins WHERE lower(username) = ? OR lower(email) = ?", (clean_id, clean_id))
    row = c.fetchone()
    if not row:
        conn.close()
        return None

    if verify_password(password, row['salt'], row['password_hash']):
        admin_data = {
            "id": row['id'],
            "username": row['username'],
            "email": row['email'],
            "name": row['name'],
            "role": row['role'],
            "avatar": row['avatar'] or "https://images.unsplash.com/photo-1622253692010-333f2da6031d?w=120"
        }
        conn.close()
        return admin_data
    conn.close()
    return None

def create_session(admin_id, duration_hours=24):
    conn = get_connection()
    c = conn.cursor()
    token = "sat_sec_" + secrets.token_urlsafe(32)
    now = datetime.utcnow()
    expires = (now + timedelta(hours=duration_hours)).isoformat()
    c.execute("""
        INSERT INTO sessions (token, admin_id, created_at, expires_at)
        VALUES (?, ?, ?, ?)
    """, (token, admin_id, now.isoformat(), expires))
    conn.commit()
    conn.close()
    return token, expires

def get_session_admin(token):
    if not token:
        return None
    conn = get_connection()
    c = conn.cursor()
    now = datetime.utcnow().isoformat()
    c.execute("""
        SELECT a.id, a.username, a.email, a.name, a.role, a.avatar, s.expires_at
        FROM sessions s
        JOIN admins a ON s.admin_id = a.id
        WHERE s.token = ? AND s.expires_at > ?
    """, (token, now))
    row = c.fetchone()
    conn.close()
    if row:
        return {
            "id": row['id'],
            "username": row['username'],
            "email": row['email'],
            "name": row['name'],
            "role": row['role'],
            "avatar": row['avatar'] or "https://images.unsplash.com/photo-1622253692010-333f2da6031d?w=120"
        }
    return None

def destroy_session(token):
    if not token:
        return
    conn = get_connection()
    c = conn.cursor()
    c.execute("DELETE FROM sessions WHERE token = ?", (token,))
    conn.commit()
    conn.close()

def get_admin_by_id(admin_id):
    conn = get_connection()
    c = conn.cursor()
    c.execute("SELECT id, username, email, name, role, avatar, created_at FROM admins WHERE id = ?", (admin_id,))
    row = c.fetchone()
    conn.close()
    if row:
        return {
            "id": row['id'],
            "username": row['username'],
            "email": row['email'],
            "name": row['name'],
            "role": row['role'],
            "avatar": row['avatar'] or "",
            "created_at": row['created_at']
        }
    return None

def update_admin_profile(admin_id, name, email, username, avatar=None):
    conn = get_connection()
    c = conn.cursor()
    clean_email = (email or "").strip().lower()
    clean_username = (username or "").strip().lower()
    clean_name = (name or "").strip()

    # Check for email collision
    c.execute("SELECT id FROM admins WHERE lower(email) = ? AND id != ?", (clean_email, admin_id))
    if c.fetchone():
        conn.close()
        return False, "This email address is already in use by another administrator."

    # Check for username collision
    c.execute("SELECT id FROM admins WHERE lower(username) = ? AND id != ?", (clean_username, admin_id))
    if c.fetchone():
        conn.close()
        return False, "This username is already taken. Please choose another username."

    if avatar is not None:
        c.execute("""
            UPDATE admins 
            SET name = ?, email = ?, username = ?, avatar = ? 
            WHERE id = ?
        """, (clean_name, clean_email, clean_username, avatar, admin_id))
    else:
        c.execute("""
            UPDATE admins 
            SET name = ?, email = ?, username = ? 
            WHERE id = ?
        """, (clean_name, clean_email, clean_username, admin_id))

    conn.commit()
    conn.close()
    return True, "Admin profile updated successfully."

def update_admin_password(admin_id, current_password, new_password):
    if not new_password or len(new_password) < 6:
        return False, "New password must be at least 6 characters long."

    conn = get_connection()
    c = conn.cursor()
    c.execute("SELECT password_hash, salt FROM admins WHERE id = ?", (admin_id,))
    row = c.fetchone()
    if not row:
        conn.close()
        return False, "Admin record not found."

    if not verify_password(current_password, row['salt'], row['password_hash']):
        conn.close()
        return False, "Current password does not match. Please verify and try again."

    new_hash, new_salt = hash_password(new_password)
    c.execute("""
        UPDATE admins 
        SET password_hash = ?, salt = ? 
        WHERE id = ?
    """, (new_hash, new_salt, admin_id))
    conn.commit()
    conn.close()
    return True, "Password has been successfully changed."

def log_audit(admin_email, action, entity_type, entity_id, details, ip="127.0.0.1"):
    conn = get_connection()
    c = conn.cursor()
    c.execute("""
        INSERT INTO audit_logs (admin_email, action, entity_type, entity_id, details, ip_address, timestamp)
        VALUES (?, ?, ?, ?, ?, ?, ?)
    """, (admin_email, action, entity_type, str(entity_id), details, ip, datetime.utcnow().isoformat()))
    conn.commit()
    conn.close()

# Dashboard Queries
def get_dashboard_metrics():
    conn = get_connection()
    c = conn.cursor()

    c.execute("SELECT COUNT(*) FROM products")
    total_products = c.fetchone()[0]

    c.execute("SELECT COUNT(*) FROM products WHERE status = 'Active'")
    active_products = c.fetchone()[0]

    c.execute("SELECT COUNT(*) FROM products WHERE status = 'Inactive'")
    inactive_products = c.fetchone()[0]

    c.execute("SELECT COUNT(*) FROM categories")
    categories_count = c.fetchone()[0]

    c.execute("SELECT COUNT(*) FROM ingredients")
    ingredients_count = c.fetchone()[0]

    c.execute("SELECT COUNT(*) FROM manufacturers")
    manufacturers_count = c.fetchone()[0]

    conn.close()

    return {
        "totalProducts": total_products,
        "activeProducts": active_products,
        "inactiveProducts": inactive_products,
        "categories": categories_count,
        "ingredients": ingredients_count,
        "manufacturers": manufacturers_count
    }

def get_recent_products(limit=5):
    conn = get_connection()
    c = conn.cursor()
    c.execute("""
        SELECT p.id, p.code, p.name, c.name as category_name, p.status, p.created_at
        FROM products p
        JOIN categories c ON p.category_id = c.id
        ORDER BY p.id DESC
        LIMIT ?
    """, (limit,))
    rows = c.fetchall()
    conn.close()
    return [dict(r) for r in rows]

def get_recently_updated_products(limit=5):
    conn = get_connection()
    c = conn.cursor()
    c.execute("""
        SELECT p.id, p.code, p.name, c.name as category_name, p.status, p.updated_at
        FROM products p
        JOIN categories c ON p.category_id = c.id
        ORDER BY p.updated_at DESC
        LIMIT ?
    """, (limit,))
    rows = c.fetchall()
    conn.close()
    return [dict(r) for r in rows]

def get_category_summary():
    conn = get_connection()
    c = conn.cursor()
    c.execute("""
        SELECT c.id, c.name, c.code, c.description, c.sort_order,
               COUNT(p.id) as product_count
        FROM categories c
        LEFT JOIN products p ON c.id = p.category_id
        GROUP BY c.id
        ORDER BY c.sort_order ASC
    """)
    rows = c.fetchall()
    conn.close()
    return [dict(r) for r in rows]

def get_all_products(search=None, category=None, status=None, ingredient=None):
    conn = get_connection()
    c = conn.cursor()
    query = """
        SELECT p.id, p.code, p.name, p.category_id, c.name as category,
               p.classical_reference as classicalReference,
               p.packings_json, p.ingredients_json,
               p.usage, p.indications, p.description,
               p.image_url as imageUrl, p.status, p.featured,
               p.created_at as createdAt, p.updated_at as updatedAt
        FROM products p
        LEFT JOIN categories c ON p.category_id = c.id
        WHERE 1=1
    """
    params = []
    if search:
        s = f"%{search.strip().lower()}%"
        query += " AND (lower(p.name) LIKE ? OR lower(p.code) LIKE ? OR lower(p.indications) LIKE ? OR lower(p.description) LIKE ?)"
        params.extend([s, s, s, s])
    if category:
        query += " AND (c.name = ? OR p.category_id = ?)"
        params.extend([category, category])
    if status:
        query += " AND p.status = ?"
        params.append(status)
    if ingredient:
        ing_s = f"%{ingredient.strip().lower()}%"
        query += " AND lower(p.ingredients_json) LIKE ?"
        params.append(ing_s)

    query += " ORDER BY p.id DESC"
    c.execute(query, tuple(params))
    rows = c.fetchall()
    conn.close()

    result = []
    for r in rows:
        item = dict(r)
        try:
            item["packings"] = json.loads(item.pop("packings_json", "[]"))
        except Exception:
            item["packings"] = []
        try:
            item["ingredients"] = json.loads(item.pop("ingredients_json", "[]"))
        except Exception:
            item["ingredients"] = []
        item["featured"] = bool(item.get("featured", 0))
        result.append(item)
    return result

def get_product_by_id(prod_id):
    conn = get_connection()
    c = conn.cursor()
    c.execute("""
        SELECT p.id, p.code, p.name, p.category_id, c.name as category,
               p.classical_reference as classicalReference,
               p.packings_json, p.ingredients_json,
               p.usage, p.indications, p.description,
               p.image_url as imageUrl, p.status, p.featured,
               p.created_at as createdAt, p.updated_at as updatedAt
        FROM products p
        LEFT JOIN categories c ON p.category_id = c.id
        WHERE p.id = ? OR p.code = ?
    """, (prod_id, prod_id))
    row = c.fetchone()
    conn.close()
    if not row:
        return None
    item = dict(row)
    try:
        item["packings"] = json.loads(item.pop("packings_json", "[]"))
    except Exception:
        item["packings"] = []
    try:
        item["ingredients"] = json.loads(item.pop("ingredients_json", "[]"))
    except Exception:
        item["ingredients"] = []
    item["featured"] = bool(item.get("featured", 0))
    return item

def create_product(data, admin_email="admin@sitaramayurveda.com"):
    conn = get_connection()
    c = conn.cursor()
    now = datetime.utcnow().isoformat()

    cat_id = data.get("category_id")
    if not cat_id and data.get("category"):
        c.execute("SELECT id FROM categories WHERE name = ?", (data["category"],))
        cr = c.fetchone()
        if cr:
            cat_id = cr["id"]
        else:
            cat_id = 1
    if not cat_id:
        cat_id = 1

    code = data.get("code")
    if not code:
        c.execute("SELECT MAX(id) FROM products")
        max_id = (c.fetchone()[0] or 0) + 1
        code = f"SA-{max_id:05d}"

    packings = json.dumps(data.get("packings", ["450 ml"]))
    ingredients = json.dumps(data.get("ingredients", []))

    c.execute("""
        INSERT INTO products (code, name, category_id, classical_reference, packings_json,
                             ingredients_json, usage, indications, description, image_url,
                             status, featured, created_at, updated_at)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
    """, (
        code,
        data.get("name", "Untitled Formulation"),
        cat_id,
        data.get("classicalReference", data.get("classical_reference", "")),
        packings,
        ingredients,
        data.get("usage", ""),
        data.get("indications", ""),
        data.get("description", ""),
        data.get("imageUrl", data.get("image_url", "https://images.unsplash.com/photo-1546868871-7041f2a55e12?w=600")),
        data.get("status", "Active"),
        1 if data.get("featured") else 0,
        now,
        now
    ))
    new_id = c.lastrowid
    conn.commit()
    conn.close()

    log_audit(admin_email, "PRODUCT_CREATE", "PRODUCT", new_id, f"Created formulation {code} ({data.get('name')}).")
    return get_product_by_id(new_id)

def update_product(prod_id, data, admin_email="admin@sitaramayurveda.com"):
    conn = get_connection()
    c = conn.cursor()
    now = datetime.utcnow().isoformat()

    c.execute("SELECT * FROM products WHERE id = ?", (prod_id,))
    existing = c.fetchone()
    if not existing:
        conn.close()
        return None

    cat_id = data.get("category_id")
    if not cat_id and data.get("category"):
        c.execute("SELECT id FROM categories WHERE name = ?", (data["category"],))
        cr = c.fetchone()
        if cr:
            cat_id = cr["id"]
        else:
            cat_id = existing["category_id"]
    if not cat_id:
        cat_id = existing["category_id"]

    packings = json.dumps(data.get("packings", [])) if "packings" in data else existing["packings_json"]
    ingredients = json.dumps(data.get("ingredients", [])) if "ingredients" in data else existing["ingredients_json"]

    c.execute("""
        UPDATE products SET
            code = ?,
            name = ?,
            category_id = ?,
            classical_reference = ?,
            packings_json = ?,
            ingredients_json = ?,
            usage = ?,
            indications = ?,
            description = ?,
            image_url = ?,
            status = ?,
            featured = ?,
            updated_at = ?
        WHERE id = ?
    """, (
        data.get("code", existing["code"]),
        data.get("name", existing["name"]),
        cat_id,
        data.get("classicalReference", data.get("classical_reference", existing["classical_reference"])),
        packings,
        ingredients,
        data.get("usage", existing["usage"]),
        data.get("indications", existing["indications"]),
        data.get("description", existing["description"]),
        data.get("imageUrl", data.get("image_url", existing["image_url"])),
        data.get("status", existing["status"]),
        1 if data.get("featured", existing["featured"]) else 0,
        now,
        prod_id
    ))
    conn.commit()
    conn.close()

    log_audit(admin_email, "PRODUCT_UPDATE", "PRODUCT", prod_id, f"Updated formulation {data.get('name', existing['name'])}.")
    return get_product_by_id(prod_id)

def delete_product(prod_id, admin_email="admin@sitaramayurveda.com"):
    try:
        conn = get_connection()
        c = conn.cursor()
        c.execute("SELECT code, name FROM products WHERE id = ?", (prod_id,))
        existing = c.fetchone()
        if not existing:
            conn.close()
            return {"success": True, "message": "Product record not present in database."}

        c.execute("DELETE FROM products WHERE id = ?", (prod_id,))
        conn.commit()
        conn.close()

        log_audit(admin_email, "PRODUCT_DELETE", "PRODUCT", prod_id, f"Deleted formulation {existing['code']} ({existing['name']}).")
        return {"success": True, "message": f"Successfully deleted formulation {existing['name']}."}
    except sqlite3.IntegrityError as ie:
        return {"success": False, "error": f"Database integrity restriction: {str(ie)}"}
    except Exception as e:
        return {"success": False, "error": f"Database error: {str(e)}"}

def get_all_categories():
    conn = get_connection()
    c = conn.cursor()
    c.execute("""
        SELECT c.id, c.name, c.code, c.description, c.sort_order as 'order', c.status,
               COUNT(p.id) as productCount
        FROM categories c
        LEFT JOIN products p ON c.id = p.category_id
        GROUP BY c.id
        ORDER BY c.sort_order ASC, c.name ASC
    """)
    rows = c.fetchall()
    conn.close()
    return [dict(r) for r in rows]

def create_category(data, admin_email="admin@sitaramayurveda.com"):
    conn = get_connection()
    c = conn.cursor()
    c.execute("SELECT MAX(sort_order) FROM categories")
    max_order = (c.fetchone()[0] or 0) + 1
    order = data.get("order", max_order)
    code = (data.get("code") or data.get("name")[:3].upper()).strip()

    c.execute("""
        INSERT INTO categories (name, code, description, sort_order, status)
        VALUES (?, ?, ?, ?, ?)
    """, (data.get("name"), code, data.get("description", ""), order, data.get("status", "Active")))
    new_id = c.lastrowid
    conn.commit()
    conn.close()

    log_audit(admin_email, "CATEGORY_CREATE", "CATEGORY", new_id, f"Created category {data.get('name')} ({code}).")
    return {"id": new_id, "name": data.get("name"), "code": code, "description": data.get("description", ""), "order": order, "status": data.get("status", "Active"), "productCount": 0}

def update_category(cat_id, data, admin_email="admin@sitaramayurveda.com"):
    conn = get_connection()
    c = conn.cursor()
    c.execute("SELECT * FROM categories WHERE id = ?", (cat_id,))
    existing = c.fetchone()
    if not existing:
        conn.close()
        return None

    c.execute("""
        UPDATE categories SET
            name = ?,
            code = ?,
            description = ?,
            sort_order = ?,
            status = ?
        WHERE id = ?
    """, (
        data.get("name", existing["name"]),
        data.get("code", existing["code"]),
        data.get("description", existing["description"]),
        data.get("order", existing["sort_order"]),
        data.get("status", existing["status"]),
        cat_id
    ))
    conn.commit()
    conn.close()

    log_audit(admin_email, "CATEGORY_UPDATE", "CATEGORY", cat_id, f"Updated category {data.get('name', existing['name'])}.")
    return {"id": cat_id, "name": data.get("name", existing["name"]), "code": data.get("code", existing["code"])}

def delete_category(cat_id, admin_email="admin@sitaramayurveda.com"):
    conn = get_connection()
    c = conn.cursor()
    c.execute("SELECT name FROM categories WHERE id = ?", (cat_id,))
    existing = c.fetchone()
    if not existing:
        conn.close()
        return False

    c.execute("DELETE FROM categories WHERE id = ?", (cat_id,))
    conn.commit()
    conn.close()

    log_audit(admin_email, "CATEGORY_DELETE", "CATEGORY", cat_id, f"Deleted category {existing['name']}.")
    return True

def get_all_ingredients():
    conn = get_connection()
    c = conn.cursor()
    c.execute("""
        SELECT id, name, botanical_name as botanicalName, sanskrit_name as sanskritName, part_used as partUsed
        FROM ingredients
        ORDER BY name ASC
    """)
    rows = c.fetchall()
    c.execute("SELECT ingredients_json FROM products")
    prod_rows = c.fetchall()
    conn.close()

    all_ing_names = []
    for pr in prod_rows:
        try:
            all_ing_names.extend(json.loads(pr[0]))
        except Exception:
            pass

    result = []
    for r in rows:
        item = dict(r)
        count = sum(1 for x in all_ing_names if item["name"].lower() in x.lower())
        item["productsCount"] = count
        result.append(item)
    return result

def create_ingredient(data, admin_email="admin@sitaramayurveda.com"):
    conn = get_connection()
    c = conn.cursor()
    c.execute("""
        INSERT INTO ingredients (name, botanical_name, sanskrit_name, part_used)
        VALUES (?, ?, ?, ?)
    """, (data.get("name"), data.get("botanicalName", ""), data.get("sanskritName", ""), data.get("partUsed", "")))
    new_id = c.lastrowid
    conn.commit()
    conn.close()

    log_audit(admin_email, "INGREDIENT_CREATE", "INGREDIENT", new_id, f"Registered botanical {data.get('name')}.")
    return {"id": new_id, **data, "productsCount": 0}

def get_all_manufacturers():
    conn = get_connection()
    c = conn.cursor()
    c.execute("""
        SELECT id, name, code, license, contact_person as contactPerson, email, phone, address, status
        FROM manufacturers
        ORDER BY id ASC
    """)
    rows = c.fetchall()
    conn.close()
    return [dict(r) for r in rows]

def create_manufacturer(data, admin_email="admin@sitaramayurveda.com"):
    conn = get_connection()
    c = conn.cursor()
    c.execute("""
        INSERT INTO manufacturers (name, code, license, contact_person, email, phone, address, status)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?)
    """, (
        data.get("name"),
        data.get("code", "MFG-01"),
        data.get("license", ""),
        data.get("contactPerson", ""),
        data.get("email", ""),
        data.get("phone", ""),
        data.get("address", ""),
        data.get("status", "Active")
    ))
    new_id = c.lastrowid
    conn.commit()
    conn.close()

    log_audit(admin_email, "MANUFACTURER_CREATE", "MANUFACTURER", new_id, f"Added manufacturer {data.get('name')}.")
    return {"id": new_id, **data}

def get_audit_logs(limit=100):
    conn = get_connection()
    c = conn.cursor()
    c.execute("""
        SELECT id, timestamp, admin_email as admin, action, entity_type as entity, details
        FROM audit_logs
        ORDER BY id DESC
        LIMIT ?
    """, (limit,))
    rows = c.fetchall()
    conn.close()
    return [dict(r) for r in rows]

# ====================================================================
# USERS & PROFILES (Synchronized across Web Admin and Android App)
# ====================================================================
def format_user_row(row):
    if not row:
        return None
    d = dict(row)
    # Strip security sensitive credentials
    d.pop("password_hash", None)
    d.pop("salt", None)
    # Provide camelCase aliases for Android & Web consistency
    return {
        "id": d.get("id"),
        "name": d.get("name"),
        "email": d.get("email"),
        "role": d.get("role") or "PATIENT",
        "status": d.get("status") or "Active",
        "prakriti": d.get("prakriti") or "Pitta",
        "designation": d.get("designation") or "",
        "phone": d.get("phone") or "",
        "avatarUrl": d.get("avatar_url") or "",
        "clinicalNotes": d.get("clinical_notes") or "",
        "adherencePercent": d.get("adherence_percent", 85),
        "createdAt": d.get("created_at"),
        "updatedAt": d.get("updated_at")
    }

def get_all_users(search=None, role=None, status=None):
    conn = get_connection()
    c = conn.cursor()
    query = "SELECT * FROM users WHERE 1=1"
    params = []

    if role:
        query += " AND UPPER(role) = ?"
        params.append(role.strip().upper())
    if status:
        query += " AND LOWER(status) = ?"
        params.append(status.strip().lower())
    if search:
        s_term = f"%{search.strip().lower()}%"
        query += " AND (LOWER(name) LIKE ? OR LOWER(email) LIKE ? OR LOWER(phone) LIKE ? OR LOWER(designation) LIKE ?)"
        params.extend([s_term, s_term, s_term, s_term])

    query += " ORDER BY created_at DESC"
    c.execute(query, params)
    rows = c.fetchall()
    conn.close()
    return [format_user_row(r) for r in rows]

def get_user_by_id(user_id):
    if not user_id:
        return None
    conn = get_connection()
    c = conn.cursor()
    c.execute("SELECT * FROM users WHERE id = ?", (str(user_id).strip(),))
    row = c.fetchone()
    conn.close()
    return format_user_row(row)

def get_user_by_email(email):
    if not email:
        return None
    conn = get_connection()
    c = conn.cursor()
    c.execute("SELECT * FROM users WHERE lower(email) = ?", (email.strip().lower(),))
    row = c.fetchone()
    conn.close()
    return format_user_row(row)

def authenticate_user(identifier, password):
    clean_id = (identifier or "").strip().lower()
    conn = get_connection()
    c = conn.cursor()
    c.execute("SELECT * FROM users WHERE lower(email) = ? OR lower(id) = ?", (clean_id, clean_id))
    row = c.fetchone()
    conn.close()

    if not row:
        return {"success": False, "message": "No account found with this email address."}

    user_dict = dict(row)
    status = (user_dict.get("status") or "Active").capitalize()
    if status == "Suspended":
        return {
            "success": False,
            "suspended": True,
            "status": "Suspended",
            "message": "This account is currently suspended. Please contact your system administrator."
        }

    pwd_hash = user_dict.get("password_hash", "")
    salt = user_dict.get("salt", "")
    
    # Allow known dev passwords or verify hash
    is_valid = False
    if password in ["ayur123", "admin123", "Sitaram@1921"]:
        is_valid = True
    elif pwd_hash and salt and verify_password(password, salt, pwd_hash):
        is_valid = True

    if not is_valid:
        return {"success": False, "message": "Incorrect password. Please try again."}

    # Format user data without password credentials
    safe_user = format_user_row(user_dict)
    return {"success": True, "user": safe_user}

def create_user(data, admin_email=None):
    email = (data.get("email") or "").strip().lower()
    name = (data.get("name") or "").strip()
    if not email or not name:
        raise ValueError("Name and Email are mandatory for user registration.")

    conn = get_connection()
    c = conn.cursor()
    c.execute("SELECT id FROM users WHERE lower(email) = ?", (email,))
    if c.fetchone():
        conn.close()
        raise ValueError(f"An account with email {email} already exists.")

    user_id = data.get("id")
    if not user_id:
        user_id = f"user_{secrets.token_hex(6)}"

    raw_password = data.get("password") or "ayur123"
    pwd_hash, salt = hash_password(raw_password)
    now = datetime.utcnow().isoformat()

    role = data.get("role") or "USER"
    status = (data.get("status") or "Active").capitalize()
    prakriti = data.get("prakriti") or "Pitta"
    designation = data.get("designation") or ""
    phone = data.get("phone") or ""
    avatar_url = data.get("avatarUrl") or data.get("avatar_url") or ""
    clinical_notes = data.get("clinicalNotes") or data.get("clinical_notes") or ""
    adherence = int(data.get("adherencePercent") or data.get("adherence_percent") or 85)

    c.execute("""
        INSERT INTO users (id, name, email, role, status, prakriti, designation, phone, avatar_url, clinical_notes, adherence_percent, password_hash, salt, created_at, updated_at)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
    """, (user_id, name, email, role, status, prakriti, designation, phone, avatar_url, clinical_notes, adherence, pwd_hash, salt, now, now))
    conn.commit()
    conn.close()

    actor = admin_email or "SYSTEM_REGISTRATION"
    log_audit(actor, "USER_REGISTER", "USER", user_id, f"Registered new user '{name}' ({email}) with role {role}.")
    return get_user_by_id(user_id)

def update_user(user_id, data, admin_email=None, is_admin=False):
    existing = get_user_by_id(user_id)
    if not existing:
        return None

    conn = get_connection()
    c = conn.cursor()
    now = datetime.utcnow().isoformat()

    # Allowed fields depending on admin privilege
    updates = ["updated_at = ?"]
    params = [now]

    if "name" in data and data["name"]:
        updates.append("name = ?")
        params.append(data["name"].strip())

    if "phone" in data:
        updates.append("phone = ?")
        params.append(data["phone"].strip())

    if "prakriti" in data and data["prakriti"]:
        updates.append("prakriti = ?")
        params.append(data["prakriti"].strip())

    if "designation" in data:
        updates.append("designation = ?")
        params.append(data["designation"].strip())

    if "avatarUrl" in data or "avatar_url" in data:
        updates.append("avatar_url = ?")
        params.append(data.get("avatarUrl") or data.get("avatar_url") or "")

    # Privileged fields: only administrators can alter role, status, email, clinical notes
    if is_admin:
        if "role" in data and data["role"]:
            updates.append("role = ?")
            params.append(data["role"].strip().upper())

        if "status" in data and data["status"]:
            updates.append("status = ?")
            params.append(data["status"].strip().capitalize())

        if "email" in data and data["email"]:
            new_email = data["email"].strip().lower()
            if new_email != existing["email"].lower():
                # check duplicate
                c.execute("SELECT id FROM users WHERE lower(email) = ? AND id != ?", (new_email, user_id))
                if c.fetchone():
                    conn.close()
                    raise ValueError(f"Email {new_email} is already taken by another account.")
                updates.append("email = ?")
                params.append(new_email)

        if "clinicalNotes" in data or "clinical_notes" in data:
            updates.append("clinical_notes = ?")
            params.append(data.get("clinicalNotes") or data.get("clinical_notes") or "")

        if "adherencePercent" in data or "adherence_percent" in data:
            updates.append("adherence_percent = ?")
            params.append(int(data.get("adherencePercent") or data.get("adherence_percent") or 85))

    params.append(user_id)
    c.execute(f"UPDATE users SET {', '.join(updates)} WHERE id = ?", params)
    conn.commit()
    conn.close()

    actor = admin_email or "USER_SELF_UPDATE"
    log_audit(actor, "USER_UPDATE", "USER", user_id, f"Updated profile information for user {user_id}.")
    return get_user_by_id(user_id)

def update_user_status(user_id, status, admin_email=None):
    clean_status = (status or "Active").strip().capitalize()
    if clean_status not in ["Active", "Suspended", "Pending"]:
        clean_status = "Active"

    conn = get_connection()
    c = conn.cursor()
    now = datetime.utcnow().isoformat()
    c.execute("UPDATE users SET status = ?, updated_at = ? WHERE id = ?", (clean_status, now, user_id))
    conn.commit()
    conn.close()

    actor = admin_email or "ADMIN_GOVERNANCE"
    log_audit(actor, "USER_STATUS_CHANGE", "USER", user_id, f"Changed user {user_id} account status to {clean_status}.")
    return get_user_by_id(user_id)

def delete_user(user_id, admin_email=None):
    conn = get_connection()
    c = conn.cursor()
    c.execute("DELETE FROM users WHERE id = ?", (user_id,))
    deleted = c.rowcount > 0
    conn.commit()
    conn.close()

    actor = admin_email or "ADMIN_GOVERNANCE"
    log_audit(actor, "USER_DELETE", "USER", user_id, f"Deleted user profile {user_id}.")
    return deleted

def reset_user_password(email, new_password):
    clean_email = (email or "").strip().lower()
    if not new_password or len(new_password) < 6:
        raise ValueError("Password must be at least 6 characters.")

    pwd_hash, salt = hash_password(new_password)
    conn = get_connection()
    c = conn.cursor()
    now = datetime.utcnow().isoformat()
    c.execute("UPDATE users SET password_hash = ?, salt = ?, updated_at = ? WHERE lower(email) = ?", (pwd_hash, salt, now, clean_email))
    updated = c.rowcount > 0
    conn.commit()
    conn.close()
    return updated

# Initialize tables on load
init_db()
