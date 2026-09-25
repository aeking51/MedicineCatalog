package com.example.data.repository

import android.util.Log
import com.example.data.model.AppUser
import com.example.data.model.AuditLogEntry
import com.example.data.model.AyurvedaIngredient
import com.example.data.model.AyurvedaMedicine
import com.example.data.model.DailyDoseLog
import com.example.data.model.DosageInfo
import com.example.data.model.DoshaType
import com.example.data.model.DravyagunaProfile
import com.example.data.model.FormulationCategory
import com.example.data.model.UserRole
import com.example.data.model.UserStatus
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

object FirestoreRepository {

    private const val TAG = "FirestoreRepository"
    private const val COLLECTION_USERS = "users"
    private const val COLLECTION_USER_ROLES = "user_roles"
    private const val COLLECTION_MEDICINES = "medicines"
    private const val COLLECTION_AUDIT_LOGS = "audit_logs"
    private const val COLLECTION_DOSES = "user_doses"

    private var activeUsersListener: ListenerRegistration? = null
    private var activeUserRolesListener: ListenerRegistration? = null
    private var activeMedicinesListener: ListenerRegistration? = null
    private var activeAuditListener: ListenerRegistration? = null

    // Safe access to Firestore instance (returns null if google-services.json not configured)
    val firestore: FirebaseFirestore? by lazy {
        try {
            FirebaseFirestore.getInstance().apply {
                firestoreSettings = com.google.firebase.firestore.FirebaseFirestoreSettings.Builder()
                    .setPersistenceEnabled(true)
                    .build()
            }
        } catch (t: Throwable) {
            Log.w(TAG, "Firestore initialization skipped: ${t.message}")
            null
        }
    }

    val isCloudConnected: Boolean
        get() = firestore != null

    // --- USERS ---

    fun observeUsers(
        onSuccess: (List<AppUser>) -> Unit,
        onError: (Exception) -> Unit = {}
    ) {
        val db = firestore ?: return
        activeUsersListener?.remove()
        activeUsersListener = db.collection(COLLECTION_USERS)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e(TAG, "Failed to observe users: ${error.message}")
                    onError(error)
                    return@addSnapshotListener
                }
                if (snapshot != null && !snapshot.isEmpty) {
                    val users = snapshot.documents.mapNotNull { doc ->
                        doc.data?.toAppUser(doc.id)
                    }
                    onSuccess(users)
                }
            }
    }

    fun saveUser(user: AppUser, onComplete: ((Boolean) -> Unit)? = null) {
        val db = firestore
        if (db == null) {
            onComplete?.invoke(false)
            return
        }
        db.collection(COLLECTION_USERS)
            .document(user.id)
            .set(user.toMap(), SetOptions.merge())
            .addOnSuccessListener {
                Log.d(TAG, "User ${user.name} saved to Firestore")
                saveUserRole(user.id, user.email, user.role, "SYSTEM")
                onComplete?.invoke(true)
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error saving user: ${e.message}")
                onComplete?.invoke(false)
            }
    }

    fun saveUserRole(
        userId: String,
        email: String,
        role: UserRole,
        elevatedBy: String,
        onComplete: ((Boolean) -> Unit)? = null
    ) {
        val db = firestore ?: run {
            onComplete?.invoke(false)
            return
        }
        val roleData = mapOf(
            "userId" to userId,
            "email" to email,
            "role" to role.name,
            "roleTitle" to role.displayName,
            "badgeLabel" to role.badgeLabel,
            "roleDescription" to role.description,
            "updatedBy" to elevatedBy,
            "updatedAt" to System.currentTimeMillis()
        )
        db.collection(COLLECTION_USER_ROLES)
            .document(userId)
            .set(roleData, SetOptions.merge())
            .addOnSuccessListener {
                Log.d(TAG, "Role ${role.name} recorded in $COLLECTION_USER_ROLES for $userId")
                onComplete?.invoke(true)
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Failed to save user role in Firestore: ${e.message}")
                onComplete?.invoke(false)
            }
    }

    fun observeUserRoles(
        onSuccess: (Map<String, UserRole>) -> Unit,
        onError: (Exception) -> Unit = {}
    ) {
        val db = firestore ?: return
        activeUserRolesListener?.remove()
        activeUserRolesListener = db.collection(COLLECTION_USER_ROLES)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e(TAG, "Failed to observe user roles: ${error.message}")
                    onError(error)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val rolesMap = mutableMapOf<String, UserRole>()
                    for (doc in snapshot.documents) {
                        val roleStr = doc.getString("role")
                        val role = runCatching { UserRole.valueOf(roleStr ?: "") }.getOrNull()
                        if (role != null) {
                            rolesMap[doc.id] = role
                        }
                    }
                    onSuccess(rolesMap)
                }
            }
    }

    fun deleteUser(userId: String, onComplete: ((Boolean) -> Unit)? = null) {
        val db = firestore
        if (db == null) {
            onComplete?.invoke(false)
            return
        }
        db.collection(COLLECTION_USERS)
            .document(userId)
            .delete()
            .addOnSuccessListener {
                db.collection(COLLECTION_USER_ROLES).document(userId).delete()
                onComplete?.invoke(true)
            }
            .addOnFailureListener { onComplete?.invoke(false) }
    }

    // --- MEDICINES / INVENTORY ---

    fun observeMedicines(
        onSuccess: (List<AyurvedaMedicine>) -> Unit,
        onError: (Exception) -> Unit = {}
    ) {
        val db = firestore ?: return
        activeMedicinesListener?.remove()
        activeMedicinesListener = db.collection(COLLECTION_MEDICINES)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e(TAG, "Failed to observe medicines: ${error.message}")
                    onError(error)
                    return@addSnapshotListener
                }
                if (snapshot != null && !snapshot.isEmpty) {
                    val medicines = snapshot.documents.mapNotNull { doc ->
                        doc.data?.toAyurvedaMedicine(doc.id)
                    }
                    onSuccess(medicines)
                }
            }
    }

    fun saveMedicine(medicine: AyurvedaMedicine, onComplete: ((Boolean) -> Unit)? = null) {
        val db = firestore
        if (db == null) {
            onComplete?.invoke(false)
            return
        }
        db.collection(COLLECTION_MEDICINES)
            .document(medicine.id)
            .set(medicine.toMap(), SetOptions.merge())
            .addOnSuccessListener { onComplete?.invoke(true) }
            .addOnFailureListener { onComplete?.invoke(false) }
    }

    fun updateMedicineStock(medicineId: String, newStock: Int, isLowStock: Boolean) {
        val db = firestore ?: return
        db.collection(COLLECTION_MEDICINES)
            .document(medicineId)
            .update(
                mapOf(
                    "stockUnits" to newStock,
                    "isLowStock" to isLowStock
                )
            )
            .addOnFailureListener { e ->
                Log.e(TAG, "Failed to update stock for $medicineId: ${e.message}")
            }
    }

    fun deleteMedicine(medicineId: String, onComplete: ((Boolean) -> Unit)? = null) {
        val db = firestore
        if (db == null) {
            onComplete?.invoke(false)
            return
        }
        db.collection(COLLECTION_MEDICINES)
            .document(medicineId)
            .delete()
            .addOnSuccessListener { onComplete?.invoke(true) }
            .addOnFailureListener { onComplete?.invoke(false) }
    }

    suspend fun fetchMedicinesSuspend(): List<AyurvedaMedicine> = suspendCancellableCoroutine { continuation ->
        val db = firestore
        if (db == null) {
            continuation.resume(emptyList())
            return@suspendCancellableCoroutine
        }
        db.collection(COLLECTION_MEDICINES)
            .get()
            .addOnSuccessListener { snapshot ->
                val medicines = snapshot.documents.mapNotNull { doc ->
                    doc.data?.toAyurvedaMedicine(doc.id)
                }
                continuation.resume(medicines)
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "fetchMedicinesSuspend failed: ${e.message}")
                continuation.resume(emptyList())
            }
    }

    suspend fun saveMedicineSuspend(medicine: AyurvedaMedicine): Boolean = suspendCancellableCoroutine { continuation ->
        val db = firestore
        if (db == null) {
            continuation.resume(false)
            return@suspendCancellableCoroutine
        }
        db.collection(COLLECTION_MEDICINES)
            .document(medicine.id)
            .set(medicine.toMap(), SetOptions.merge())
            .addOnSuccessListener { continuation.resume(true) }
            .addOnFailureListener { e ->
                Log.e(TAG, "saveMedicineSuspend failed: ${e.message}")
                continuation.resume(false)
            }
    }

    suspend fun deleteMedicineSuspend(medicineId: String): Boolean = suspendCancellableCoroutine { continuation ->
        val db = firestore
        if (db == null) {
            continuation.resume(false)
            return@suspendCancellableCoroutine
        }
        db.collection(COLLECTION_MEDICINES)
            .document(medicineId)
            .delete()
            .addOnSuccessListener { continuation.resume(true) }
            .addOnFailureListener { e ->
                Log.e(TAG, "deleteMedicineSuspend failed: ${e.message}")
                continuation.resume(false)
            }
    }

    // --- AUDIT LOGS ---

    fun observeAuditLogs(
        onSuccess: (List<AuditLogEntry>) -> Unit,
        onError: (Exception) -> Unit = {}
    ) {
        val db = firestore ?: return
        activeAuditListener?.remove()
        activeAuditListener = db.collection(COLLECTION_AUDIT_LOGS)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e(TAG, "Failed to observe audit logs: ${error.message}")
                    onError(error)
                    return@addSnapshotListener
                }
                if (snapshot != null && !snapshot.isEmpty) {
                    val logs = snapshot.documents.mapNotNull { doc ->
                        doc.data?.toAuditLogEntry(doc.id)
                    }
                    onSuccess(logs)
                }
            }
    }

    fun saveAuditLog(entry: AuditLogEntry) {
        val db = firestore ?: return
        db.collection(COLLECTION_AUDIT_LOGS)
            .document(entry.id)
            .set(entry.toMap(), SetOptions.merge())
            .addOnFailureListener { e ->
                Log.e(TAG, "Failed to save audit log: ${e.message}")
            }
    }

    // --- DAILY DOSES ---

    fun observeUserDoses(
        userId: String,
        onSuccess: (List<DailyDoseLog>) -> Unit
    ) {
        val db = firestore ?: return
        db.collection(COLLECTION_DOSES)
            .document(userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener
                val rawList = snapshot?.get("doses") as? List<Map<String, Any?>>
                if (rawList != null) {
                    val doses = rawList.mapNotNull { it.toDailyDoseLog() }
                    onSuccess(doses)
                }
            }
    }

    fun saveUserDoses(userId: String, doses: List<DailyDoseLog>) {
        val db = firestore ?: return
        val dosesMapList = doses.map { it.toMap() }
        db.collection(COLLECTION_DOSES)
            .document(userId)
            .set(mapOf("doses" to dosesMapList, "lastUpdated" to System.currentTimeMillis()), SetOptions.merge())
    }

    // --- SEED INITIAL CLOUD REPOSITORY ---

    fun seedInitialDataIfEmpty(
        defaultUsers: List<AppUser>,
        defaultMedicines: List<AyurvedaMedicine>,
        defaultLogs: List<AuditLogEntry>
    ) {
        val db = firestore ?: return
        db.collection(COLLECTION_MEDICINES).limit(1).get().addOnSuccessListener { snapshot ->
            if (snapshot.isEmpty) {
                Log.d(TAG, "Seeding default medicines to Firestore...")
                defaultMedicines.forEach { saveMedicine(it) }
            }
        }
        db.collection(COLLECTION_USERS).limit(1).get().addOnSuccessListener { snapshot ->
            if (snapshot.isEmpty) {
                Log.d(TAG, "Seeding default users and roles to Firestore...")
                defaultUsers.forEach { user ->
                    saveUser(user)
                    saveUserRole(user.id, user.email, user.role, "INITIAL_SYSTEM_SEED")
                }
            }
        }
        db.collection(COLLECTION_AUDIT_LOGS).limit(1).get().addOnSuccessListener { snapshot ->
            if (snapshot.isEmpty) {
                Log.d(TAG, "Seeding default audit logs to Firestore...")
                defaultLogs.forEach { saveAuditLog(it) }
            }
        }
    }

    // --- MAPPERS ---

    private fun AppUser.toMap(): Map<String, Any?> = mapOf(
        "id" to id,
        "name" to name,
        "email" to email,
        "role" to role.name,
        "prakriti" to prakriti.name,
        "status" to status.name,
        "designation" to designation,
        "phone" to phone,
        "registeredDate" to registeredDate,
        "lastActive" to lastActive,
        "adherencePercent" to adherencePercent,
        "assignedPractitioner" to assignedPractitioner,
        "clinicalNotes" to clinicalNotes,
        "password" to password
    )

    private fun Map<String, Any?>.toAppUser(docId: String): AppUser {
        return AppUser(
            id = (this["id"] as? String) ?: docId,
            name = (this["name"] as? String) ?: "User",
            email = (this["email"] as? String) ?: "",
            role = runCatching { UserRole.valueOf(this["role"] as? String ?: "") }.getOrDefault(UserRole.PATIENT),
            prakriti = runCatching { DoshaType.valueOf(this["prakriti"] as? String ?: "") }.getOrDefault(DoshaType.PITTA),
            status = runCatching { UserStatus.valueOf(this["status"] as? String ?: "") }.getOrDefault(UserStatus.ACTIVE),
            designation = (this["designation"] as? String) ?: "",
            phone = (this["phone"] as? String) ?: "",
            registeredDate = (this["registeredDate"] as? String) ?: "",
            lastActive = (this["lastActive"] as? String) ?: "Just now",
            adherencePercent = (this["adherencePercent"] as? Number)?.toInt() ?: 80,
            assignedPractitioner = this["assignedPractitioner"] as? String,
            clinicalNotes = (this["clinicalNotes"] as? String) ?: "",
            password = (this["password"] as? String) ?: "ayur123"
        )
    }

    private fun AuditLogEntry.toMap(): Map<String, Any?> = mapOf(
        "id" to id,
        "timestamp" to timestamp,
        "actorName" to actorName,
        "actionType" to actionType,
        "targetItem" to targetItem,
        "details" to details,
        "isWarning" to isWarning
    )

    private fun Map<String, Any?>.toAuditLogEntry(docId: String): AuditLogEntry {
        return AuditLogEntry(
            id = (this["id"] as? String) ?: docId,
            timestamp = (this["timestamp"] as? String) ?: "",
            actorName = (this["actorName"] as? String) ?: "",
            actionType = (this["actionType"] as? String) ?: "",
            targetItem = (this["targetItem"] as? String) ?: "",
            details = (this["details"] as? String) ?: "",
            isWarning = (this["isWarning"] as? Boolean) ?: false
        )
    }

    private fun DailyDoseLog.toMap(): Map<String, Any?> = mapOf(
        "id" to id,
        "medicineId" to medicineId,
        "medicineName" to medicineName,
        "doseLabel" to doseLabel,
        "timing" to timing,
        "iconEmoji" to iconEmoji,
        "isLogged" to isLogged,
        "loggedAtTime" to loggedAtTime
    )

    private fun Map<String, Any?>.toDailyDoseLog(): DailyDoseLog {
        return DailyDoseLog(
            id = (this["id"] as? String) ?: "",
            medicineId = (this["medicineId"] as? String) ?: "",
            medicineName = (this["medicineName"] as? String) ?: "",
            doseLabel = (this["doseLabel"] as? String) ?: "",
            timing = (this["timing"] as? String) ?: "",
            iconEmoji = (this["iconEmoji"] as? String) ?: "🍵",
            isLogged = (this["isLogged"] as? Boolean) ?: false,
            loggedAtTime = this["loggedAtTime"] as? String
        )
    }

    private fun AyurvedaMedicine.toMap(): Map<String, Any?> = mapOf(
        "id" to id,
        "name" to name,
        "sanskritName" to sanskritName,
        "category" to category.name,
        "tagPill" to tagPill,
        "shortDescription" to shortDescription,
        "primaryBenefit" to primaryBenefit,
        "dosageInstructions" to effectiveDosageInstructions,
        "benefits" to effectiveBenefits,
        "doshaImpact" to doshaImpact,
        "targetDoshas" to targetDoshas.map { it.name },
        "constituents" to constituents,
        "ingredients" to ingredients.map {
            mapOf(
                "name" to it.name,
                "sanskritName" to it.sanskritName,
                "botanicalName" to it.botanicalName,
                "partUsed" to it.partUsed,
                "classicalRole" to it.classicalRole
            )
        },
        "dravyaguna" to mapOf(
            "rasa" to dravyaguna.rasa,
            "virya" to dravyaguna.virya,
            "vipaka" to dravyaguna.vipaka,
            "guna" to dravyaguna.guna
        ),
        "dosage" to mapOf(
            "summary" to dosage.summary,
            "standardDose" to dosage.standardDose,
            "frequency" to dosage.frequency,
            "timing" to dosage.timing,
            "anupana" to dosage.anupana,
            "caution" to dosage.caution
        ),
        "indications" to indications,
        "contraindications" to contraindications,
        "pathyaWholesome" to pathyaWholesome,
        "apathyaAvoid" to apathyaAvoid,
        "isDailyVitality" to isDailyVitality,
        "stockUnits" to stockUnits,
        "batchNumber" to batchNumber,
        "isLowStock" to isLowStock
    )

    @Suppress("UNCHECKED_CAST")
    private fun Map<String, Any?>.toAyurvedaMedicine(docId: String): AyurvedaMedicine {
        val targetDoshaNames = (this["targetDoshas"] as? List<String>) ?: emptyList()
        val targetDoshaEnums = targetDoshaNames.mapNotNull { name ->
            runCatching { DoshaType.valueOf(name) }.getOrNull()
        }

        val rawIngredients = (this["ingredients"] as? List<Map<String, Any?>>) ?: emptyList()
        val ingredientsList = rawIngredients.map { ingMap ->
            AyurvedaIngredient(
                name = (ingMap["name"] as? String) ?: "",
                sanskritName = (ingMap["sanskritName"] as? String) ?: "",
                botanicalName = (ingMap["botanicalName"] as? String) ?: "",
                partUsed = (ingMap["partUsed"] as? String) ?: "",
                classicalRole = (ingMap["classicalRole"] as? String) ?: ""
            )
        }

        val dravMap = this["dravyaguna"] as? Map<String, Any?>
        val dravProfile = DravyagunaProfile(
            rasa = (dravMap?.get("rasa") as? List<String>) ?: listOf("Tikta"),
            virya = (dravMap?.get("virya") as? String) ?: "Ushna",
            vipaka = (dravMap?.get("vipaka") as? String) ?: "Madhura",
            guna = (dravMap?.get("guna") as? List<String>) ?: listOf("Laghu")
        )

        val dosageMap = this["dosage"] as? Map<String, Any?>
        val dosageObj = DosageInfo(
            summary = (dosageMap?.get("summary") as? String) ?: "500mg daily",
            standardDose = (dosageMap?.get("standardDose") as? String) ?: "500mg",
            frequency = (dosageMap?.get("frequency") as? String) ?: "Twice daily",
            timing = (dosageMap?.get("timing") as? String) ?: "After meals",
            anupana = (dosageMap?.get("anupana") as? String) ?: "Warm water",
            caution = (dosageMap?.get("caution") as? String) ?: ""
        )

        val catName = (this["category"] as? String) ?: "CHURNA"
        val categoryEnum = runCatching { FormulationCategory.valueOf(catName) }.getOrDefault(FormulationCategory.CHURNA)

        val rawBenefits = (this["benefits"] as? List<String>) ?: emptyList()
        val dosageInstructionsStr = (this["dosageInstructions"] as? String) ?: ""

        return AyurvedaMedicine(
            id = (this["id"] as? String) ?: docId,
            name = (this["name"] as? String) ?: "Medicine",
            ingredients = ingredientsList,
            dosageInstructions = dosageInstructionsStr,
            benefits = rawBenefits,
            sanskritName = (this["sanskritName"] as? String) ?: "",
            category = categoryEnum,
            tagPill = (this["tagPill"] as? String) ?: "HERBAL",
            shortDescription = (this["shortDescription"] as? String) ?: "",
            primaryBenefit = (this["primaryBenefit"] as? String) ?: "",
            doshaImpact = (this["doshaImpact"] as? String) ?: "",
            targetDoshas = if (targetDoshaEnums.isNotEmpty()) targetDoshaEnums else listOf(DoshaType.TRIDOSHIC),
            constituents = (this["constituents"] as? List<String>) ?: emptyList(),
            dravyaguna = dravProfile,
            dosage = dosageObj,
            indications = (this["indications"] as? List<String>) ?: emptyList(),
            contraindications = (this["contraindications"] as? List<String>) ?: emptyList(),
            pathyaWholesome = (this["pathyaWholesome"] as? List<String>) ?: emptyList(),
            apathyaAvoid = (this["apathyaAvoid"] as? List<String>) ?: emptyList(),
            isDailyVitality = (this["isDailyVitality"] as? Boolean) ?: false,
            stockUnits = (this["stockUnits"] as? Number)?.toInt() ?: 45,
            batchNumber = (this["batchNumber"] as? String) ?: "AYUR-2026-B12",
            isLowStock = (this["isLowStock"] as? Boolean) ?: false
        )
    }
}
