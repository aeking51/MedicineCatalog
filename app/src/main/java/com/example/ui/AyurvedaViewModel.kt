package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.AppUser
import com.example.data.model.AuditLogEntry
import com.example.data.model.AyurvedaMedicine
import com.example.data.model.DailyDoseLog
import com.example.data.model.DailyHabit
import com.example.data.model.DoshaType
import com.example.data.model.FormulationCategory
import com.example.data.model.PrakritiScore
import com.example.data.model.UserRole
import com.example.data.model.UserStatus
import com.example.data.repository.AyurvedaRepository
import com.example.data.repository.FirestoreRepository
import com.example.data.repository.FirebaseAuthRepository
import com.example.data.repository.SupabaseRepository
import com.example.data.local.ThemePreferences
import com.example.ui.theme.AppThemeMode
import com.example.ui.theme.GlassMotionProfile
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

enum class AppTab {
    HOME,
    LIBRARY,
    INSIGHTS,
    PROFILE,
    ADMIN
}

enum class AuthMode {
    LOGIN,
    SIGNUP,
    FORGOT_PASSWORD,
    OTP_RESET
}

object DailySpotlightHelper {
    /**
     * Deterministically calculates today's featured medicine from the catalogue based on the calendar day.
     * Each day of the year rotates to a new formulation seamlessly.
     * Supports dayOffset for exploring previous or upcoming days.
     */
    fun getDailyMedicine(medicines: List<AyurvedaMedicine>, dayOffset: Int = 0): AyurvedaMedicine {
        if (medicines.isEmpty()) return AyurvedaRepository.allMedicines.first()
        val calendar = java.util.Calendar.getInstance()
        if (dayOffset != 0) {
            calendar.add(java.util.Calendar.DAY_OF_YEAR, dayOffset)
        }
        val dayIndex = calendar.get(java.util.Calendar.YEAR) * 366 + calendar.get(java.util.Calendar.DAY_OF_YEAR)
        // Stable ordering by ID ensures predictable daily sequence regardless of fetch order
        val stableList = medicines.sortedBy { it.id }
        val index = Math.floorMod(dayIndex, stableList.size)
        return stableList[index]
    }

    fun getTodaySpotlight(medicines: List<AyurvedaMedicine>): AyurvedaMedicine {
        return getDailyMedicine(medicines, 0)
    }

    fun getDayLabel(dayOffset: Int = 0): String {
        val calendar = java.util.Calendar.getInstance()
        if (dayOffset != 0) {
            calendar.add(java.util.Calendar.DAY_OF_YEAR, dayOffset)
        }
        val sdf = java.text.SimpleDateFormat("MMM d", java.util.Locale.getDefault())
        return sdf.format(calendar.time)
    }

    fun getBadgeText(dayOffset: Int = 0): String {
        return when {
            dayOffset == 0 -> "DAILY HIGHLIGHT"
            dayOffset > 0 -> "+${dayOffset}D PREVIEW"
            else -> "${dayOffset}D ARCHIVE"
        }
    }
}

data class AyurvedaUiState(
    val currentTab: AppTab = AppTab.HOME,
    val searchQuery: String = "",
    val searchHistory: List<String> = listOf("Triphala", "Ashwagandha"),
    val selectedCategory: FormulationCategory = FormulationCategory.ALL,
    val selectedDosha: DoshaType? = null,
    val allMedicines: List<AyurvedaMedicine> = AyurvedaRepository.allMedicines,
    val isCatalogueLoading: Boolean = false,
    val selectedMedicine: AyurvedaMedicine? = null,
    val dailyVitalityMedicine: AyurvedaMedicine = DailySpotlightHelper.getDailyMedicine(AyurvedaRepository.allMedicines),
    val isDailyVitalityLogged: Boolean = false,
    val dailyDoses: List<DailyDoseLog> = AyurvedaRepository.defaultDailyDoses,
    val dailyHabits: List<DailyHabit> = AyurvedaRepository.defaultHabits,
    val pittaPercent: Int = 72,
    val vataPercent: Int = 65,
    val kaphaPercent: Int = 78,
    val hydrationLiters: Float = 1.2f,
    val hydrationTargetLiters: Float = 2.5f,
    val prakritiAnswers: Map<Int, DoshaType> = mapOf(1 to DoshaType.PITTA, 2 to DoshaType.PITTA, 3 to DoshaType.VATA, 4 to DoshaType.PITTA),
    val prakritiScore: PrakritiScore = PrakritiScore(vataScore = 1, pittaScore = 3, kaphaScore = 0, dominantDosha = DoshaType.PITTA),
    val snackbarMessage: String? = null,
    // Role-based User & Admin State
    val currentUser: AppUser = AyurvedaRepository.defaultUsers.first(), // Starts as Admin Jerin MR
    val allUsers: List<AppUser> = AyurvedaRepository.defaultUsers,
    val userRoleFilter: UserRole? = null,
    val userStatusFilter: UserStatus? = null,
    val userSearchQuery: String = "",
    val editingUser: AppUser? = null,
    val userPendingDeletion: AppUser? = null,
    val auditLogs: List<AuditLogEntry> = AyurvedaRepository.defaultAuditLogs,
    val isAddMedicineDialogOpen: Boolean = false,
    val isAddUserDialogOpen: Boolean = false,
    val isSwitchUserDialogOpen: Boolean = false,
    val selectedUserForDetail: AppUser? = null,
    // Authentication State
    val isAuthenticated: Boolean = true,
    val authMode: AuthMode = AuthMode.LOGIN,
    val authErrorMessage: String? = null,
    val authSuccessMessage: String? = null,
    val pendingResetEmail: String = "",
    val generatedOtpCode: String? = null,
    // Cloud Firestore Persistence State
    val isCloudSyncEnabled: Boolean = FirestoreRepository.isCloudConnected,
    val cloudSyncStatus: String = if (FirestoreRepository.isCloudConnected) "Cloud Firestore Connected" else "Local Storage (Cloud Ready)",
    // Appearance & Theme Mode (LIGHT, GLASS, DARK)
    val appThemeMode: AppThemeMode = AppThemeMode.GLASS,
    val glassMotionProfile: GlassMotionProfile = GlassMotionProfile.DEFAULT
)

class AyurvedaViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(
        AyurvedaUiState(
            appThemeMode = ThemePreferences.getThemeMode(),
            glassMotionProfile = ThemePreferences.getMotionProfile()
        )
    )
    val uiState: StateFlow<AyurvedaUiState> = _uiState.asStateFlow()
    private var catalogueFetchJob: Job? = null

    init {
        val savedTheme = ThemePreferences.getThemeMode()
        val savedMotion = GlassMotionProfile.DEFAULT
        ThemePreferences.setMotionProfile(savedMotion)
        val dailySpotlight = DailySpotlightHelper.getDailyMedicine(AyurvedaRepository.allMedicines)
        _uiState.update { 
            it.copy(
                appThemeMode = savedTheme, 
                glassMotionProfile = savedMotion,
                dailyVitalityMedicine = dailySpotlight
            ) 
        }
        fetchCatalogueFromDatabase(debounce = 0L)
        initCloudSync()
    }

    private fun initCloudSync() {
        if (!FirestoreRepository.isCloudConnected) return

        // Seed initial collections to Cloud Firestore if newly connecting
        FirestoreRepository.seedInitialDataIfEmpty(
            defaultUsers = AyurvedaRepository.defaultUsers,
            defaultMedicines = AyurvedaRepository.allMedicines,
            defaultLogs = AyurvedaRepository.defaultAuditLogs
        )

        // Observe cloud users in real-time
        FirestoreRepository.observeUsers(
            onSuccess = { cloudUsers ->
                if (cloudUsers.isNotEmpty()) {
                    _uiState.update { current ->
                        val updatedCurrent = cloudUsers.find { it.id == current.currentUser.id } ?: current.currentUser
                        current.copy(
                            allUsers = cloudUsers,
                            currentUser = updatedCurrent,
                            isCloudSyncEnabled = true,
                            cloudSyncStatus = "Cloud Firestore Connected"
                        )
                    }
                }
            }
        )

        // Observe cloud medicines in real-time
        FirestoreRepository.observeMedicines(
            onSuccess = { cloudMedicines ->
                if (cloudMedicines.isNotEmpty()) {
                    _uiState.update { current ->
                        val dailyMed = DailySpotlightHelper.getDailyMedicine(cloudMedicines)
                        current.copy(
                            allMedicines = cloudMedicines,
                            dailyVitalityMedicine = dailyMed
                        )
                    }
                }
            }
        )

        // Observe cloud regulatory audit logs in real-time
        FirestoreRepository.observeAuditLogs(
            onSuccess = { cloudLogs ->
                if (cloudLogs.isNotEmpty()) {
                    _uiState.update { it.copy(auditLogs = cloudLogs) }
                }
            }
        )

        // Observe cloud user roles collection in real-time
        FirestoreRepository.observeUserRoles(
            onSuccess = { roleMap ->
                if (roleMap.isNotEmpty()) {
                    _uiState.update { current ->
                        val updatedUsers = current.allUsers.map { u ->
                            val cloudRole = roleMap[u.id]
                            if (cloudRole != null) u.copy(role = cloudRole) else u
                        }
                        val updatedCurrent = updatedUsers.find { it.id == current.currentUser.id } ?: current.currentUser
                        current.copy(allUsers = updatedUsers, currentUser = updatedCurrent)
                    }
                }
            }
        )

        // Auto-link active Firebase Auth session if present
        val fbUser = FirebaseAuthRepository.currentFirebaseUser
        if (fbUser != null) {
            val matched = _uiState.value.allUsers.find {
                it.id == fbUser.uid || it.email.equals(fbUser.email, ignoreCase = true)
            }
            if (matched != null) {
                _uiState.update { it.copy(currentUser = matched, isAuthenticated = true) }
            }
        }
    }

    fun setTab(tab: AppTab) {
        _uiState.update { it.copy(currentTab = tab) }
        if (tab == AppTab.LIBRARY) {
            fetchCatalogueFromDatabase(debounce = 300L)
        }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        if (query.isNotEmpty() && query.length % 2 == 1) {
            fetchCatalogueFromDatabase(debounce = 250L)
        }
    }

    fun addSearchQueryToHistory(query: String) {
        val trimmed = query.trim()
        if (trimmed.isBlank()) return
        _uiState.update { state ->
            val updated = listOf(trimmed) + state.searchHistory.filterNot { it.equals(trimmed, ignoreCase = true) }
            state.copy(searchHistory = updated.take(10))
        }
    }

    fun removeSearchQueryFromHistory(query: String) {
        _uiState.update { state ->
            state.copy(searchHistory = state.searchHistory.filterNot { it.equals(query, ignoreCase = true) })
        }
    }

    fun clearSearchHistory() {
        _uiState.update { it.copy(searchHistory = emptyList()) }
    }

    fun performSearch(query: String) {
        val trimmed = query.trim()
        if (trimmed.isNotBlank()) {
            addSearchQueryToHistory(trimmed)
        }
        _uiState.update { it.copy(searchQuery = trimmed) }
        fetchCatalogueFromDatabase(debounce = 100L)
    }

    fun onCategorySelected(category: FormulationCategory) {
        _uiState.update { it.copy(selectedCategory = category) }
        fetchCatalogueFromDatabase(debounce = 300L)
    }

    fun onDoshaSelected(dosha: DoshaType?) {
        _uiState.update {
            it.copy(selectedDosha = if (it.selectedDosha == dosha) null else dosha)
        }
        fetchCatalogueFromDatabase(debounce = 300L)
    }

    fun refreshCatalogue() {
        fetchCatalogueFromDatabase(debounce = 450L)
    }

    fun fetchCatalogueFromDatabase(debounce: Long = 300L) {
        catalogueFetchJob?.cancel()
        _uiState.update { it.copy(isCatalogueLoading = true) }
        catalogueFetchJob = viewModelScope.launch {
            if (debounce > 0L) {
                delay(debounce)
            }
            try {
                val cloudMedicines = withContext(Dispatchers.IO) {
                    SupabaseRepository.fetchMedicinesSuspend()
                }
                if (cloudMedicines.isNotEmpty()) {
                    _uiState.update { current ->
                        val dailyMed = DailySpotlightHelper.getDailyMedicine(cloudMedicines)
                        current.copy(
                            allMedicines = cloudMedicines,
                            dailyVitalityMedicine = dailyMed,
                            isCloudSyncEnabled = true,
                            cloudSyncStatus = "Supabase Cloud Connected (${cloudMedicines.size} formulations)"
                        )
                    }
                    Log.d("AyurvedaViewModel", "Loaded ${cloudMedicines.size} formulations from Supabase cloud.")
                }
            } catch (e: Exception) {
                Log.e("AyurvedaViewModel", "Error syncing catalogue from Supabase: ${e.message}", e)
            } finally {
                _uiState.update { it.copy(isCatalogueLoading = false) }
            }
        }
    }

    fun selectMedicine(medicine: AyurvedaMedicine?) {
        _uiState.update { it.copy(selectedMedicine = medicine) }
    }

    // Role-Based User Management
    fun switchUser(user: AppUser) {
        // Observe cloud doses for the switched user
        FirestoreRepository.observeUserDoses(user.id, onSuccess = { cloudDoses ->
            if (cloudDoses.isNotEmpty()) {
                _uiState.update { current ->
                    if (current.currentUser.id == user.id) {
                        current.copy(dailyDoses = cloudDoses)
                    } else current
                }
            }
        })

        _uiState.update { state ->
            val nextTab = if (user.role == UserRole.PATIENT && state.currentTab == AppTab.ADMIN) {
                AppTab.HOME
            } else {
                state.currentTab
            }
            val newLog = AuditLogEntry(
                id = "log_${System.currentTimeMillis()}",
                timestamp = "Just now",
                actorName = "${user.name} (${user.role.badgeLabel})",
                actionType = "USER_SWITCH",
                targetItem = user.name,
                details = "Switched active session to ${user.name} (${user.role.displayName})."
            )
            state.copy(
                currentUser = user,
                currentTab = nextTab,
                isSwitchUserDialogOpen = false,
                auditLogs = listOf(newLog) + state.auditLogs,
                snackbarMessage = "Logged in as ${user.name} (${user.role.displayName})"
            )
        }
    }

    fun updateUserRole(userId: String, newRole: UserRole) {
        val currentState = _uiState.value
        val isChiefAdmin = currentState.currentUser.role == UserRole.ADMIN
        // Strict RBAC: Only Admin can elevate another user to Practitioner or Admin privilege
        if ((newRole == UserRole.ADMIN || newRole == UserRole.PRACTITIONER) && !isChiefAdmin) {
            _uiState.update {
                it.copy(snackbarMessage = "Security Policy: Only an Administrator can elevate users to Practitioner or Admin privileges.")
            }
            return
        }

        _uiState.update { state ->
            val updatedUsers = state.allUsers.map { user ->
                if (user.id == userId) user.copy(role = newRole) else user
            }
            val targetUser = state.allUsers.find { it.id == userId }?.copy(role = newRole)
            targetUser?.let {
                FirestoreRepository.saveUser(it)
                FirestoreRepository.saveUserRole(it.id, it.email, newRole, state.currentUser.name)
            }
            val updatedCurrent = if (state.currentUser.id == userId) state.currentUser.copy(role = newRole) else state.currentUser
            val isElevatedToAdmin = newRole == UserRole.ADMIN
            val action = if (isElevatedToAdmin) "ADMIN_ELEVATION" else "ROLE_CHANGE"
            val newLog = AuditLogEntry(
                id = "log_${System.currentTimeMillis()}",
                timestamp = "Just now",
                actorName = "${state.currentUser.name} (${state.currentUser.role.badgeLabel})",
                actionType = action,
                targetItem = targetUser?.name ?: userId,
                details = if (isElevatedToAdmin) {
                    "Elevated ${targetUser?.name ?: userId} to Administrator privileges."
                } else {
                    "User role changed to ${newRole.badgeLabel} by ${state.currentUser.name}."
                }
            )
            FirestoreRepository.saveAuditLog(newLog)
            state.copy(
                allUsers = updatedUsers,
                currentUser = updatedCurrent,
                selectedUserForDetail = state.selectedUserForDetail?.let { if (it.id == userId) it.copy(role = newRole) else it },
                auditLogs = listOf(newLog) + state.auditLogs,
                snackbarMessage = if (isElevatedToAdmin) {
                    "Elevated ${targetUser?.name ?: "user"} to Chief Administrator"
                } else {
                    "Updated role for ${targetUser?.name ?: "user"} to ${newRole.displayName}"
                }
            )
        }
    }

    fun updateUserStatus(userId: String, newStatus: UserStatus) {
        _uiState.update { state ->
            val updatedUsers = state.allUsers.map { user ->
                if (user.id == userId) user.copy(status = newStatus) else user
            }
            val targetUser = state.allUsers.find { it.id == userId }?.copy(status = newStatus)
            targetUser?.let { FirestoreRepository.saveUser(it) }
            val newLog = AuditLogEntry(
                id = "log_${System.currentTimeMillis()}",
                timestamp = "Just now",
                actorName = "${state.currentUser.name} (${state.currentUser.role.badgeLabel})",
                actionType = "STATUS_UPDATE",
                targetItem = targetUser?.name ?: userId,
                details = "Account status set to ${newStatus.label}.",
                isWarning = newStatus == UserStatus.SUSPENDED
            )
            FirestoreRepository.saveAuditLog(newLog)
            state.copy(
                allUsers = updatedUsers,
                selectedUserForDetail = state.selectedUserForDetail?.let { if (it.id == userId) it.copy(status = newStatus) else it },
                auditLogs = listOf(newLog) + state.auditLogs,
                snackbarMessage = "Status for ${targetUser?.name} changed to ${newStatus.label}"
            )
        }
    }

    fun addNewUser(user: AppUser) {
        val currentState = _uiState.value
        val isChiefAdmin = currentState.currentUser.role == UserRole.ADMIN
        // Admin privilege enforcement: Only admin can assign Practitioner or Admin role
        if ((user.role == UserRole.ADMIN || user.role == UserRole.PRACTITIONER) && !isChiefAdmin) {
            _uiState.update {
                it.copy(snackbarMessage = "Security Policy: Only an Administrator can assign Practitioner or Admin privileges.")
            }
            return
        }

        _uiState.update { state ->
            val isElevatedToAdmin = user.role == UserRole.ADMIN
            val newLog = AuditLogEntry(
                id = "log_${System.currentTimeMillis()}",
                timestamp = "Just now",
                actorName = "${state.currentUser.name} (${state.currentUser.role.badgeLabel})",
                actionType = if (isElevatedToAdmin) "ADMIN_ELEVATION" else "USER_REGISTERED",
                targetItem = user.name,
                details = "Registered user ${user.name} (${user.role.badgeLabel}) with Prakriti ${user.prakriti.displayName}."
            )
            FirestoreRepository.saveUser(user)
            FirestoreRepository.saveAuditLog(newLog)
            state.copy(
                allUsers = listOf(user) + state.allUsers,
                isAddUserDialogOpen = false,
                auditLogs = listOf(newLog) + state.auditLogs,
                snackbarMessage = "Registered new user ${user.name}"
            )
        }
    }

    fun updateUser(updatedUser: AppUser) {
        val currentState = _uiState.value
        val isChiefAdmin = currentState.currentUser.role == UserRole.ADMIN
        val existingUser = currentState.allUsers.find { it.id == updatedUser.id }

        // Enforce: only admin can elevate another user to practitioner or admin
        val isElevating = (updatedUser.role == UserRole.ADMIN || updatedUser.role == UserRole.PRACTITIONER) && existingUser?.role != updatedUser.role
        if (isElevating && !isChiefAdmin) {
            _uiState.update {
                it.copy(snackbarMessage = "Security Policy: Only an Administrator can elevate users to Practitioner or Admin privileges.")
            }
            return
        }

        _uiState.update { state ->
            val updatedUsers = state.allUsers.map { if (it.id == updatedUser.id) updatedUser else it }
            val updatedCurrent = if (state.currentUser.id == updatedUser.id) updatedUser else state.currentUser
            val isNewlyElevated = updatedUser.role == UserRole.ADMIN && existingUser?.role != UserRole.ADMIN
            val newLog = AuditLogEntry(
                id = "log_${System.currentTimeMillis()}",
                timestamp = "Just now",
                actorName = "${state.currentUser.name} (${state.currentUser.role.badgeLabel})",
                actionType = if (isNewlyElevated) "ADMIN_ELEVATION" else "USER_UPDATED",
                targetItem = updatedUser.name,
                details = if (isNewlyElevated) {
                    "Elevated ${updatedUser.name} to Chief Administrator with full system control."
                } else {
                    "Updated profile details for ${updatedUser.name} (${updatedUser.role.badgeLabel})."
                }
            )
            FirestoreRepository.saveUser(updatedUser)
            FirestoreRepository.saveAuditLog(newLog)
            state.copy(
                allUsers = updatedUsers,
                currentUser = updatedCurrent,
                selectedUserForDetail = if (state.selectedUserForDetail?.id == updatedUser.id) updatedUser else state.selectedUserForDetail,
                editingUser = null,
                auditLogs = listOf(newLog) + state.auditLogs,
                snackbarMessage = "Profile updated for ${updatedUser.name}"
            )
        }
    }

    fun deleteUser(userId: String) {
        val currentState = _uiState.value
        val targetUser = currentState.allUsers.find { it.id == userId } ?: return

        // Safety Protection: Cannot delete root Admin Jerin MR
        if (targetUser.name.contains("Jerin", ignoreCase = true) ||
            targetUser.email.equals("sys.jerin@gmail.com", ignoreCase = true) ||
            targetUser.id == "user_admin_jerin"
        ) {
            _uiState.update {
                it.copy(
                    userPendingDeletion = null,
                    snackbarMessage = "Protected Account: Root Administrator Jerin MR cannot be deleted."
                )
            }
            return
        }

        // Safety Protection: Cannot delete currently logged in account
        if (targetUser.id == currentState.currentUser.id) {
            _uiState.update {
                it.copy(
                    userPendingDeletion = null,
                    snackbarMessage = "Security Restriction: You cannot delete your currently active session account."
                )
            }
            return
        }

        _uiState.update { state ->
            val remainingUsers = state.allUsers.filterNot { it.id == userId }
            val newLog = AuditLogEntry(
                id = "log_${System.currentTimeMillis()}",
                timestamp = "Just now",
                actorName = "${state.currentUser.name} (${state.currentUser.role.badgeLabel})",
                actionType = "USER_DELETED",
                targetItem = targetUser.name,
                details = "Account for ${targetUser.name} (${targetUser.role.badgeLabel}) permanently removed from directory.",
                isWarning = true
            )
            FirestoreRepository.deleteUser(userId)
            FirestoreRepository.saveAuditLog(newLog)
            state.copy(
                allUsers = remainingUsers,
                userPendingDeletion = null,
                selectedUserForDetail = if (state.selectedUserForDetail?.id == userId) null else state.selectedUserForDetail,
                auditLogs = listOf(newLog) + state.auditLogs,
                snackbarMessage = "User ${targetUser.name} permanently removed from directory"
            )
        }
    }

    fun resetUserPassword(userId: String) {
        _uiState.update { state ->
            val targetUser = state.allUsers.find { it.id == userId }
            val updatedUsers = state.allUsers.map {
                if (it.id == userId) it.copy(password = "ayur123") else it
            }
            targetUser?.let {
                FirestoreRepository.saveUser(it.copy(password = "ayur123"))
            }
            val newLog = AuditLogEntry(
                id = "log_${System.currentTimeMillis()}",
                timestamp = "Just now",
                actorName = "${state.currentUser.name} (${state.currentUser.role.badgeLabel})",
                actionType = "PASSWORD_RESET",
                targetItem = targetUser?.name ?: userId,
                details = "Credentials reset to default (ayur123) for ${targetUser?.name}."
            )
            FirestoreRepository.saveAuditLog(newLog)
            state.copy(
                allUsers = updatedUsers,
                auditLogs = listOf(newLog) + state.auditLogs,
                snackbarMessage = "Password reset to default (ayur123) for ${targetUser?.name ?: "user"}"
            )
        }
    }

    fun onUserSearchQueryChanged(query: String) {
        _uiState.update { it.copy(userSearchQuery = query) }
    }

    fun setUserRoleFilter(role: UserRole?) {
        _uiState.update { it.copy(userRoleFilter = role) }
    }

    fun setUserStatusFilter(status: UserStatus?) {
        _uiState.update { it.copy(userStatusFilter = status) }
    }

    fun setEditingUser(user: AppUser?) {
        _uiState.update { it.copy(editingUser = user) }
    }

    fun setUserPendingDeletion(user: AppUser?) {
        _uiState.update { it.copy(userPendingDeletion = user) }
    }

    fun selectUserForDetail(user: AppUser?) {
        _uiState.update { it.copy(selectedUserForDetail = user) }
    }

    fun setAddMedicineDialogOpen(open: Boolean) {
        _uiState.update { it.copy(isAddMedicineDialogOpen = open) }
    }

    fun setAddUserDialogOpen(open: Boolean) {
        _uiState.update { it.copy(isAddUserDialogOpen = open) }
    }

    fun setSwitchUserDialogOpen(open: Boolean) {
        _uiState.update { it.copy(isSwitchUserDialogOpen = open) }
    }

    // Inventory & Medicine Management
    fun addNewMedicine(medicine: AyurvedaMedicine) {
        _uiState.update { state ->
            val newLog = AuditLogEntry(
                id = "log_${System.currentTimeMillis()}",
                timestamp = "Just now",
                actorName = "${state.currentUser.name} (${state.currentUser.role.badgeLabel})",
                actionType = "FORMULATION_ADDED",
                targetItem = medicine.name,
                details = "Added new classical formulation (${medicine.category.displayName}) to master catalogue. Stock: ${medicine.stockUnits} units."
            )
            FirestoreRepository.saveMedicine(medicine)
            FirestoreRepository.saveAuditLog(newLog)
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    SupabaseRepository.saveMedicineSuspend(medicine)
                } catch (e: Exception) {
                    Log.w("AyurvedaViewModel", "Supabase save error: ${e.message}")
                }
            }
            state.copy(
                allMedicines = listOf(medicine) + state.allMedicines,
                isAddMedicineDialogOpen = false,
                auditLogs = listOf(newLog) + state.auditLogs,
                snackbarMessage = "Added ${medicine.name} to catalogue"
            )
        }
    }

    fun updateMedicineStock(medicineId: String, newStock: Int) {
        _uiState.update { state ->
            val clamped = newStock.coerceAtLeast(0)
            val updatedMedicines = state.allMedicines.map { med ->
                if (med.id == medicineId) {
                    med.copy(stockUnits = clamped, isLowStock = clamped < 15)
                } else med
            }
            val target = state.allMedicines.find { it.id == medicineId }
            val newLog = AuditLogEntry(
                id = "log_${System.currentTimeMillis()}",
                timestamp = "Just now",
                actorName = "${state.currentUser.name} (${state.currentUser.role.badgeLabel})",
                actionType = "STOCK_UPDATE",
                targetItem = target?.name ?: medicineId,
                details = "Adjusted inventory stock to $clamped units."
            )
            FirestoreRepository.updateMedicineStock(medicineId, clamped, clamped < 15)
            FirestoreRepository.saveAuditLog(newLog)
            state.copy(
                allMedicines = updatedMedicines,
                auditLogs = listOf(newLog) + state.auditLogs,
                snackbarMessage = "Updated stock for ${target?.name ?: "medicine"}: $clamped units"
            )
        }
    }

    fun toggleMedicineVitality(medicineId: String) {
        _uiState.update { state ->
            val target = state.allMedicines.find { it.id == medicineId }
            if (target != null) {
                val updated = state.allMedicines.map {
                    it.copy(isDailyVitality = it.id == medicineId)
                }
                state.copy(
                    allMedicines = updated,
                    dailyVitalityMedicine = target,
                    snackbarMessage = "${target.name} set as featured Daily Vitality herb"
                )
            } else state
        }
    }

    fun updateMedicinePhoto(medicineId: String, newPhotoUrl: String) {
        _uiState.update { state ->
            val updatedMedicines = state.allMedicines.map { med ->
                if (med.id == medicineId) {
                    med.copy(photoUrl = newPhotoUrl)
                } else med
            }
            val target = state.allMedicines.find { it.id == medicineId }
            val updatedSelected = if (state.selectedMedicine?.id == medicineId) {
                state.selectedMedicine?.copy(photoUrl = newPhotoUrl)
            } else state.selectedMedicine

            if (target != null) {
                FirestoreRepository.saveMedicine(target.copy(photoUrl = newPhotoUrl))
            }
            state.copy(
                allMedicines = updatedMedicines,
                selectedMedicine = updatedSelected,
                snackbarMessage = "Updated photo for ${target?.name ?: "formulation"}"
            )
        }
    }

    fun deleteMedicine(medicineId: String) {
        _uiState.update { state ->
            val target = state.allMedicines.find { it.id == medicineId }
            val newLog = AuditLogEntry(
                id = "log_${System.currentTimeMillis()}",
                timestamp = "Just now",
                actorName = "${state.currentUser.name} (${state.currentUser.role.badgeLabel})",
                actionType = "FORMULATION_ARCHIVED",
                targetItem = target?.name ?: medicineId,
                details = "Formulation archived from active dispensary.",
                isWarning = true
            )
            FirestoreRepository.deleteMedicine(medicineId)
            FirestoreRepository.saveAuditLog(newLog)
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    SupabaseRepository.deleteMedicineSuspend(medicineId)
                } catch (e: Exception) {
                    Log.w("AyurvedaViewModel", "Supabase delete error: ${e.message}")
                }
            }
            state.copy(
                allMedicines = state.allMedicines.filter { it.id != medicineId },
                selectedMedicine = if (state.selectedMedicine?.id == medicineId) null else state.selectedMedicine,
                auditLogs = listOf(newLog) + state.auditLogs,
                snackbarMessage = "Removed ${target?.name ?: "medicine"} from catalogue"
            )
        }
    }

    fun prescribeMedicineToPatient(medicine: AyurvedaMedicine, patientId: String) {
        _uiState.update { state ->
            val patient = state.allUsers.find { it.id == patientId }
            val newLog = AuditLogEntry(
                id = "log_${System.currentTimeMillis()}",
                timestamp = "Just now",
                actorName = "${state.currentUser.name} (${state.currentUser.role.badgeLabel})",
                actionType = "PRESCRIPTION_ISSUED",
                targetItem = medicine.name,
                details = "Prescribed ${medicine.dosage.summary} to ${patient?.name ?: "patient"}."
            )
            state.copy(
                auditLogs = listOf(newLog) + state.auditLogs,
                snackbarMessage = "Prescribed ${medicine.name} to ${patient?.name ?: "patient"}"
            )
        }
    }

    // Daily Health Routines
    fun logDailyVitalityDose() {
        _uiState.update { state ->
            val newLogged = !state.isDailyVitalityLogged
            val msg = if (newLogged) "Logged: 500mg ${state.dailyVitalityMedicine.name} taken" else "${state.dailyVitalityMedicine.name} dose unlogged"
            val updatedDoses = state.dailyDoses.map { dose ->
                if (dose.medicineId == state.dailyVitalityMedicine.id) {
                    dose.copy(isLogged = newLogged, loggedAtTime = if (newLogged) "Just now" else null)
                } else dose
            }
            FirestoreRepository.saveUserDoses(state.currentUser.id, updatedDoses)
            state.copy(
                isDailyVitalityLogged = newLogged,
                dailyDoses = updatedDoses,
                snackbarMessage = msg
            )
        }
    }

    fun toggleDose(doseId: String) {
        _uiState.update { state ->
            val updated = state.dailyDoses.map { dose ->
                if (dose.id == doseId) {
                    val nextState = !dose.isLogged
                    dose.copy(isLogged = nextState, loggedAtTime = if (nextState) "Just now" else null)
                } else dose
            }
            val vitalityLogged = updated.find { it.medicineId == state.dailyVitalityMedicine.id }?.isLogged ?: state.isDailyVitalityLogged
            FirestoreRepository.saveUserDoses(state.currentUser.id, updated)
            state.copy(dailyDoses = updated, isDailyVitalityLogged = vitalityLogged)
        }
    }

    fun toggleHabit(habitId: String) {
        _uiState.update { state ->
            val updated = state.dailyHabits.map { habit ->
                if (habit.id == habitId) {
                    habit.copy(isCompleted = !habit.isCompleted)
                } else habit
            }
            val habit = state.dailyHabits.find { it.id == habitId }
            val msg = if (habit?.isCompleted == false) "Completed ${habit.title}!" else null
            state.copy(dailyHabits = updated, snackbarMessage = msg)
        }
    }

    fun addHydration(amountLiters: Float) {
        _uiState.update { state ->
            val newAmount = (state.hydrationLiters + amountLiters).coerceIn(0f, 5f)
            val rounded = (Math.round(newAmount * 10f) / 10f)
            state.copy(hydrationLiters = rounded)
        }
    }

    fun updateDoshaLevel(dosha: DoshaType, percent: Int) {
        _uiState.update { state ->
            when (dosha) {
                DoshaType.PITTA -> state.copy(pittaPercent = percent)
                DoshaType.VATA -> state.copy(vataPercent = percent)
                DoshaType.KAPHA -> state.copy(kaphaPercent = percent)
                DoshaType.TRIDOSHIC -> state
            }
        }
    }

    fun addMedicineToDailyRoutine(medicine: AyurvedaMedicine) {
        _uiState.update { state ->
            val exists = state.dailyDoses.any { it.medicineId == medicine.id }
            if (exists) {
                state.copy(snackbarMessage = "${medicine.name} is already in your Daily Routine")
            } else {
                val newDose = DailyDoseLog(
                    id = "custom_${System.currentTimeMillis()}",
                    medicineId = medicine.id,
                    medicineName = medicine.name,
                    doseLabel = medicine.dosage.summary.split("•").firstOrNull()?.trim() ?: "1 dose",
                    timing = medicine.dosage.summary.split("•").getOrNull(1)?.trim() ?: medicine.dosage.timing,
                    iconEmoji = "🌿",
                    isLogged = false
                )
                val updatedDoses = state.dailyDoses + newDose
                FirestoreRepository.saveUserDoses(state.currentUser.id, updatedDoses)
                state.copy(
                    dailyDoses = updatedDoses,
                    snackbarMessage = "Added ${medicine.name} to Daily Routine"
                )
            }
        }
    }

    fun answerPrakriti(questionId: Int, doshaChoice: DoshaType) {
        _uiState.update { state ->
            val newAnswers = state.prakritiAnswers + (questionId to doshaChoice)
            val vataCount = newAnswers.values.count { it == DoshaType.VATA }
            val pittaCount = newAnswers.values.count { it == DoshaType.PITTA }
            val kaphaCount = newAnswers.values.count { it == DoshaType.KAPHA }

            val dominant = when {
                pittaCount >= vataCount && pittaCount >= kaphaCount -> DoshaType.PITTA
                vataCount >= pittaCount && vataCount >= kaphaCount -> DoshaType.VATA
                else -> DoshaType.KAPHA
            }
            val updatedUser = state.currentUser.copy(prakriti = dominant)
            val updatedUsers = state.allUsers.map { if (it.id == updatedUser.id) updatedUser else it }
            FirestoreRepository.saveUser(updatedUser)
            state.copy(
                currentUser = updatedUser,
                allUsers = updatedUsers,
                prakritiAnswers = newAnswers,
                prakritiScore = PrakritiScore(vataCount, pittaCount, kaphaCount, dominant)
            )
        }
    }

    fun clearSnackbar() {
        _uiState.update { it.copy(snackbarMessage = null) }
    }

    // -------------------------------------------------------------
    // Authentication Operations
    // -------------------------------------------------------------

    fun setAuthMode(mode: AuthMode) {
        _uiState.update { it.copy(authMode = mode, authErrorMessage = null, authSuccessMessage = null) }
    }

    fun login(email: String, pass: String): Boolean {
        if (email.isBlank() || pass.isBlank()) {
            _uiState.update { it.copy(authErrorMessage = "Please enter both email and password.") }
            return false
        }
        val trimmedEmail = email.trim()

        if (FirebaseAuthRepository.isAuthAvailable) {
            FirebaseAuthRepository.signInWithEmail(
                email = trimmedEmail,
                pass = pass,
                onSuccess = { fbUser ->
                    var user = _uiState.value.allUsers.firstOrNull {
                        it.id == fbUser.uid || it.email.equals(trimmedEmail, ignoreCase = true)
                    }

                    if (user == null) {
                        user = AppUser(
                            id = fbUser.uid,
                            name = fbUser.displayName ?: trimmedEmail.substringBefore("@").replaceFirstChar { it.uppercase() },
                            email = trimmedEmail,
                            role = UserRole.PATIENT,
                            prakriti = DoshaType.PITTA,
                            status = UserStatus.ACTIVE,
                            designation = "Wellness Seeker",
                            registeredDate = "Today",
                            lastActive = "Just now",
                            password = pass
                        )
                        FirestoreRepository.saveUser(user)
                        FirestoreRepository.saveUserRole(user.id, user.email, user.role, "FIREBASE_SIGNIN_SYNC")
                    }

                    if (user.status == UserStatus.SUSPENDED) {
                        FirebaseAuthRepository.signOut()
                        _uiState.update { it.copy(authErrorMessage = "This account is suspended. Contact clinical administrator.") }
                        return@signInWithEmail
                    }

                    val loginLog = AuditLogEntry(
                        id = "audit_${System.currentTimeMillis()}",
                        timestamp = "Just now",
                        actorName = user.name,
                        actionType = "FIREBASE_LOGIN_SUCCESS",
                        targetItem = user.role.displayName,
                        details = "Authenticated via Firebase Auth (${user.role.badgeLabel})."
                    )
                    FirestoreRepository.saveAuditLog(loginLog)

                    _uiState.update { state ->
                        state.copy(
                            currentUser = user,
                            isAuthenticated = true,
                            authErrorMessage = null,
                            authSuccessMessage = "Welcome back, ${user.name}!",
                            auditLogs = listOf(loginLog) + state.auditLogs
                        )
                    }
                },
                onError = { errorMsg ->
                    // Fallback to local accounts (e.g. pre-seeded administrative profiles)
                    val localUser = _uiState.value.allUsers.firstOrNull { it.email.equals(trimmedEmail, ignoreCase = true) }
                    if (localUser != null && (pass.isEmpty() || localUser.password == pass)) {
                        if (localUser.status == UserStatus.SUSPENDED) {
                            _uiState.update { it.copy(authErrorMessage = "This account is suspended. Contact clinical administrator.") }
                        } else {
                            val loginLog = AuditLogEntry(
                                id = "audit_${System.currentTimeMillis()}",
                                timestamp = "Just now",
                                actorName = localUser.name,
                                actionType = "LOGIN_SUCCESS",
                                targetItem = localUser.role.displayName,
                                details = "Logged in successfully to AyurGuide portal."
                            )
                            FirestoreRepository.saveAuditLog(loginLog)
                            _uiState.update { state ->
                                state.copy(
                                    currentUser = localUser,
                                    isAuthenticated = true,
                                    authErrorMessage = null,
                                    authSuccessMessage = "Welcome back, ${localUser.name}!",
                                    auditLogs = listOf(loginLog) + state.auditLogs
                                )
                            }
                        }
                    } else {
                        _uiState.update { it.copy(authErrorMessage = errorMsg) }
                    }
                }
            )
            return true
        }

        // Local / Offline fallback
        val user = _uiState.value.allUsers.firstOrNull { it.email.equals(trimmedEmail, ignoreCase = true) }
        if (user == null) {
            _uiState.update { it.copy(authErrorMessage = "No account found with this email. Please check or sign up.") }
            return false
        }
        if (user.status == UserStatus.SUSPENDED) {
            _uiState.update { it.copy(authErrorMessage = "This account is suspended. Contact clinical administrator.") }
            return false
        }
        if (pass.isNotEmpty() && user.password != pass) {
            _uiState.update { it.copy(authErrorMessage = "Incorrect password. Tap 'Forgot Password?' to reset.") }
            return false
        }

        val loginLog = AuditLogEntry(
            id = "audit_${System.currentTimeMillis()}",
            timestamp = "Just now",
            actorName = user.name,
            actionType = "LOGIN_SUCCESS",
            targetItem = user.role.displayName,
            details = "Logged in successfully to AyurGuide portal."
        )

        FirestoreRepository.saveAuditLog(loginLog)

        _uiState.update { state ->
            state.copy(
                currentUser = user,
                isAuthenticated = true,
                authErrorMessage = null,
                authSuccessMessage = "Welcome back, ${user.name}!",
                auditLogs = listOf(loginLog) + state.auditLogs
            )
        }
        return true
    }

    fun quickLoginAs(user: AppUser) {
        switchUser(user)
        _uiState.update {
            it.copy(
                isAuthenticated = true,
                authErrorMessage = null,
                authSuccessMessage = "Logged in as ${user.name} (${user.role.displayName})"
            )
        }
    }

    fun loginAsGuest() {
        val guestUser = AppUser(
            id = "guest_user",
            name = "Guest Explorer",
            email = "guest@ayurguide.local",
            role = UserRole.GUEST,
            prakriti = DoshaType.VATA,
            status = UserStatus.ACTIVE,
            designation = "Guest Access (Limited)",
            phone = "N/A",
            registeredDate = "Today",
            lastActive = "Active now",
            adherencePercent = 0,
            clinicalNotes = "Limited read-only guest session."
        )

        val guestLog = AuditLogEntry(
            id = "audit_${System.currentTimeMillis()}",
            timestamp = "Just now",
            actorName = guestUser.name,
            actionType = "GUEST_ACCESS",
            targetItem = "Ayurvedic Library",
            details = "Logged in as Guest with limited read-only permissions."
        )
        FirestoreRepository.saveAuditLog(guestLog)

        _uiState.update { state ->
            state.copy(
                currentUser = guestUser,
                isAuthenticated = true,
                currentTab = AppTab.HOME,
                authErrorMessage = null,
                authSuccessMessage = "Welcome! You are browsing with limited guest access.",
                snackbarMessage = "Browsing with limited guest access • Sign in anytime for clinical features",
                auditLogs = listOf(guestLog) + state.auditLogs
            )
        }
    }

    fun signup(
        name: String,
        email: String,
        pass: String,
        role: UserRole = UserRole.PATIENT,
        prakriti: DoshaType,
        designation: String = ""
    ): Boolean {
        if (name.isBlank() || email.isBlank() || pass.isBlank()) {
            _uiState.update { it.copy(authErrorMessage = "Please fill in all required fields.") }
            return false
        }
        val trimmedEmail = email.trim()
        if (_uiState.value.allUsers.any { it.email.equals(trimmedEmail, ignoreCase = true) }) {
            _uiState.update { it.copy(authErrorMessage = "An account with this email already exists.") }
            return false
        }

        // Whenever a new user creates an account, default role is strictly Wellness Seeker (UserRole.PATIENT)
        val defaultRole = UserRole.PATIENT

        if (FirebaseAuthRepository.isAuthAvailable) {
            FirebaseAuthRepository.signUpWithEmail(
                email = trimmedEmail,
                pass = pass,
                onSuccess = { fbUser ->
                    val newUser = AppUser(
                        id = fbUser.uid,
                        name = name.trim(),
                        email = trimmedEmail,
                        role = defaultRole,
                        prakriti = prakriti,
                        status = UserStatus.ACTIVE,
                        designation = designation.ifBlank { "Wellness Seeker" },
                        registeredDate = "Today",
                        lastActive = "Just now",
                        password = pass
                    )

                    val regLog = AuditLogEntry(
                        id = "audit_${System.currentTimeMillis()}",
                        timestamp = "Just now",
                        actorName = newUser.name,
                        actionType = "FIREBASE_REGISTER",
                        targetItem = "${defaultRole.badgeLabel} ACCOUNT",
                        details = "New ${defaultRole.displayName} registered via Firebase Auth with ${prakriti.displayName} constitution."
                    )

                    FirestoreRepository.saveUser(newUser)
                    FirestoreRepository.saveUserRole(newUser.id, newUser.email, defaultRole, "FIREBASE_SELF_REGISTRATION")
                    FirestoreRepository.saveAuditLog(regLog)

                    _uiState.update { state ->
                        state.copy(
                            allUsers = state.allUsers + newUser,
                            currentUser = newUser,
                            isAuthenticated = true,
                            authErrorMessage = null,
                            authSuccessMessage = "Firebase Account created! Welcome, ${newUser.name}.",
                            auditLogs = listOf(regLog) + state.auditLogs
                        )
                    }
                },
                onError = { errorMsg ->
                    _uiState.update { it.copy(authErrorMessage = errorMsg) }
                }
            )
            return true
        }

        // Fallback for local testing / offline mode
        val newUser = AppUser(
            id = "user_${System.currentTimeMillis()}",
            name = name.trim(),
            email = trimmedEmail,
            role = defaultRole,
            prakriti = prakriti,
            status = UserStatus.ACTIVE,
            designation = designation.ifBlank { "Wellness Seeker" },
            registeredDate = "Today",
            lastActive = "Just now",
            password = pass
        )

        val regLog = AuditLogEntry(
            id = "audit_${System.currentTimeMillis()}",
            timestamp = "Just now",
            actorName = newUser.name,
            actionType = "USER_REGISTERED",
            targetItem = "${defaultRole.badgeLabel} ACCOUNT",
            details = "New ${defaultRole.displayName} registered with ${prakriti.displayName} constitution."
        )

        FirestoreRepository.saveUser(newUser)
        FirestoreRepository.saveUserRole(newUser.id, newUser.email, defaultRole, "LOCAL_REGISTRATION")
        FirestoreRepository.saveAuditLog(regLog)

        _uiState.update { state ->
            state.copy(
                allUsers = state.allUsers + newUser,
                currentUser = newUser,
                isAuthenticated = true,
                authErrorMessage = null,
                authSuccessMessage = "Account created! Welcome, ${newUser.name}.",
                auditLogs = listOf(regLog) + state.auditLogs
            )
        }
        return true
    }

    fun requestPasswordReset(email: String): Boolean {
        val trimmed = email.trim()
        if (trimmed.isBlank()) {
            _uiState.update { it.copy(authErrorMessage = "Please enter your registered email address.") }
            return false
        }
        val user = _uiState.value.allUsers.firstOrNull { it.email.equals(trimmed, ignoreCase = true) }
        if (user == null) {
            _uiState.update { it.copy(authErrorMessage = "No account found registered with $trimmed.") }
            return false
        }

        if (FirebaseAuthRepository.isAuthAvailable) {
            FirebaseAuthRepository.sendPasswordResetEmail(
                email = trimmed,
                onSuccess = {},
                onError = {}
            )
        }

        val otp = (100000..999999).random().toString()
        _uiState.update {
            it.copy(
                pendingResetEmail = trimmed,
                generatedOtpCode = otp,
                authMode = AuthMode.OTP_RESET,
                authErrorMessage = null,
                authSuccessMessage = "Password reset instructions dispatched. Verification OTP code: $otp"
            )
        }
        return true
    }

    fun completePasswordReset(email: String, otpInput: String, newPassword: String): Boolean {
        if (otpInput.trim() != _uiState.value.generatedOtpCode) {
            _uiState.update { it.copy(authErrorMessage = "Invalid OTP code. Please enter the 6-digit verification code.") }
            return false
        }
        if (newPassword.length < 4) {
            _uiState.update { it.copy(authErrorMessage = "Password must be at least 4 characters.") }
            return false
        }

        val resetLog = AuditLogEntry(
            id = "audit_${System.currentTimeMillis()}",
            timestamp = "Just now",
            actorName = email,
            actionType = "PASSWORD_RESET",
            targetItem = "CREDENTIALS",
            details = "Password reset verified via OTP."
        )

        val targetUser = _uiState.value.allUsers.find { it.email.equals(email, ignoreCase = true) }
        targetUser?.let {
            FirestoreRepository.saveUser(it.copy(password = newPassword))
        }
        FirestoreRepository.saveAuditLog(resetLog)

        _uiState.update { state ->
            val updatedUsers = state.allUsers.map { u ->
                if (u.email.equals(email, ignoreCase = true)) u.copy(password = newPassword) else u
            }
            val updatedCurrent = if (state.currentUser.email.equals(email, ignoreCase = true)) {
                state.currentUser.copy(password = newPassword)
            } else state.currentUser

            state.copy(
                allUsers = updatedUsers,
                currentUser = updatedCurrent,
                authMode = AuthMode.LOGIN,
                generatedOtpCode = null,
                pendingResetEmail = "",
                authErrorMessage = null,
                authSuccessMessage = "Password reset successfully! Please sign in with your new password.",
                auditLogs = listOf(resetLog) + state.auditLogs
            )
        }
        return true
    }

    fun setAppThemeMode(themeMode: AppThemeMode) {
        ThemePreferences.setThemeMode(themeMode)
        _uiState.update { it.copy(appThemeMode = themeMode) }
    }

    fun setGlassMotionProfile(profile: GlassMotionProfile) {
        ThemePreferences.setMotionProfile(profile)
        _uiState.update { 
            it.copy(
                glassMotionProfile = profile,
                snackbarMessage = "Liquid Glass motion updated: ${profile.title}"
            ) 
        }
    }

    fun updateCurrentUserProfile(
        name: String,
        designation: String,
        phone: String,
        clinicalNotes: String = ""
    ) {
        val trimmedName = name.trim()
        if (_uiState.value.currentUser.role == UserRole.GUEST) {
            _uiState.update { it.copy(snackbarMessage = "Profile saving is restricted in Guest Mode. Please sign in or register.") }
            return
        }
        if (trimmedName.isEmpty()) {
            _uiState.update { it.copy(snackbarMessage = "Profile name cannot be empty.") }
            return
        }

        _uiState.update { state ->
            val updatedUser = state.currentUser.copy(
                name = trimmedName,
                designation = designation.trim(),
                phone = phone.trim(),
                clinicalNotes = if (clinicalNotes.isNotBlank()) clinicalNotes.trim() else state.currentUser.clinicalNotes
            )
            val updatedUsers = state.allUsers.map { u ->
                if (u.id == updatedUser.id) updatedUser else u
            }

            FirestoreRepository.saveUser(updatedUser)

            val auditLog = AuditLogEntry(
                id = "audit_${System.currentTimeMillis()}",
                timestamp = "Just now",
                actorName = updatedUser.name,
                actionType = "PROFILE_UPDATE",
                targetItem = "USER_ACCOUNT",
                details = "User updated profile information (Name: $trimmedName, Title: ${designation.trim()})."
            )
            FirestoreRepository.saveAuditLog(auditLog)

            state.copy(
                currentUser = updatedUser,
                allUsers = updatedUsers,
                snackbarMessage = "Profile updated successfully!",
                auditLogs = listOf(auditLog) + state.auditLogs
            )
        }
    }

    fun changeCurrentUserPassword(
        currentPasswordInput: String = "",
        newPasswordInput: String,
        confirmPasswordInput: String,
        onResult: (Boolean, String) -> Unit
    ) {
        val curUser = _uiState.value.currentUser
        if (curUser.role == UserRole.GUEST) {
            onResult(false, "Password management is not available in Guest Mode. Please create a permanent account.")
            return
        }
        val curPass = currentPasswordInput.trim()
        val newPass = newPasswordInput.trim()
        val confPass = confirmPasswordInput.trim()

        if (newPass.length < 6) {
            onResult(false, "New password must be at least 6 characters.")
            return
        }
        if (newPass != confPass) {
            onResult(false, "New password and confirmation do not match.")
            return
        }
        if (curPass.isNotEmpty() && newPass == curPass) {
            onResult(false, "New password must be different from current password.")
            return
        }

        FirebaseAuthRepository.updateUserPassword(
            newPass = newPass,
            onSuccess = {
                val updatedUser = curUser.copy(password = newPass)
                FirestoreRepository.saveUser(updatedUser)

                val auditLog = AuditLogEntry(
                    id = "audit_${System.currentTimeMillis()}",
                    timestamp = "Just now",
                    actorName = updatedUser.name,
                    actionType = "PASSWORD_CHANGE",
                    targetItem = "CREDENTIALS",
                    details = "User successfully updated account password."
                )
                FirestoreRepository.saveAuditLog(auditLog)

                _uiState.update { state ->
                    val updatedUsers = state.allUsers.map { u ->
                        if (u.id == updatedUser.id) updatedUser else u
                    }
                    state.copy(
                        currentUser = updatedUser,
                        allUsers = updatedUsers,
                        snackbarMessage = "Password changed successfully!",
                        auditLogs = listOf(auditLog) + state.auditLogs
                    )
                }
                onResult(true, "Password changed successfully!")
            },
            onError = { errMsg ->
                onResult(false, errMsg)
            }
        )
    }

    fun logout() {
        FirebaseAuthRepository.signOut()
        val logoutLog = AuditLogEntry(
            id = "audit_${System.currentTimeMillis()}",
            timestamp = "Just now",
            actorName = _uiState.value.currentUser.name,
            actionType = "LOGOUT",
            targetItem = "SESSION",
            details = "User signed out."
        )
        FirestoreRepository.saveAuditLog(logoutLog)
        _uiState.update { state ->
            state.copy(
                isAuthenticated = false,
                authMode = AuthMode.LOGIN,
                authErrorMessage = null,
                authSuccessMessage = null,
                currentTab = AppTab.HOME,
                auditLogs = listOf(logoutLog) + state.auditLogs
            )
        }
    }

    fun clearAuthMessages() {
        _uiState.update { it.copy(authErrorMessage = null, authSuccessMessage = null) }
    }
}

