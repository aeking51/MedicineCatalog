package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.AdminPanelSettings
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.MedicalServices
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.AppUser
import com.example.data.model.AuditLogEntry
import com.example.data.model.AyurvedaIngredient
import com.example.data.model.AyurvedaMedicine
import com.example.data.model.DosageInfo
import com.example.data.model.DoshaType
import com.example.data.model.DravyagunaProfile
import com.example.data.model.FormulationCategory
import com.example.data.model.UserRole
import com.example.data.model.UserStatus
import com.example.ui.AyurvedaUiState
import com.example.ui.components.ExportCataloguePdfDialog
import com.example.ui.components.PrintableMedicineQrDialog
import com.example.ui.theme.AyurTheme
import com.example.ui.theme.NaturalBackground
import com.example.ui.theme.NaturalCardBorder
import com.example.ui.theme.NaturalCardSurface
import com.example.ui.theme.NaturalEarthGold
import com.example.ui.theme.NaturalMossDark
import com.example.ui.theme.NaturalMossPrimary
import com.example.ui.theme.NaturalOliveMuted
import com.example.ui.theme.NaturalParchmentBorder
import com.example.ui.theme.NaturalParchmentContainer
import com.example.ui.theme.NaturalSageBorder
import com.example.ui.theme.NaturalSageContainer
import com.example.ui.theme.NaturalTerracotta
import com.example.ui.theme.NaturalTextHeading
import com.example.ui.theme.NaturalTextPrimary

enum class AdminTab(val title: String, val icon: String) {
    OVERVIEW("Overview", "📊"),
    INVENTORY("Formulations", "🌿"),
    USERS("User Directory", "👥"),
    AUDIT("Safety & Audits", "🛡️")
}

@Composable
fun AdminDashboardScreen(
    uiState: AyurvedaUiState,
    onSwitchUser: (AppUser) -> Unit = {},
    onUpdateUserRole: (String, UserRole) -> Unit,
    onUpdateUserStatus: (String, UserStatus) -> Unit,
    onAddNewUser: (AppUser) -> Unit,
    onSetUserRoleFilter: (UserRole?) -> Unit,
    onSelectUserForDetail: (AppUser?) -> Unit,
    onOpenAddMedicineDialog: (Boolean) -> Unit,
    onOpenAddUserDialog: (Boolean) -> Unit,
    onOpenSwitchUserDialog: (Boolean) -> Unit = {},
    onAddNewMedicine: (AyurvedaMedicine) -> Unit,
    onUpdateStock: (String, Int) -> Unit,
    onToggleVitality: (String) -> Unit,
    onDeleteMedicine: (String) -> Unit,
    onSelectMedicine: (AyurvedaMedicine) -> Unit,
    onPrescribeToPatient: (AyurvedaMedicine, String) -> Unit,
    onUpdateUser: (AppUser) -> Unit = {},
    onDeleteUser: (String) -> Unit = {},
    onResetUserPassword: (String) -> Unit = {},
    onUserSearchQueryChanged: (String) -> Unit = {},
    onSetUserStatusFilter: (UserStatus?) -> Unit = {},
    onSetEditingUser: (AppUser?) -> Unit = {},
    onSetUserPendingDeletion: (AppUser?) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var currentAdminTab by remember { mutableStateOf(AdminTab.OVERVIEW) }
    var qrMedicineForDialog by remember { mutableStateOf<AyurvedaMedicine?>(null) }
    var showExportPdfDialog by remember { mutableStateOf(false) }
    val currentUser = uiState.currentUser
    val isChiefAdmin = currentUser.role == UserRole.ADMIN

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 1100.dp)
                .background(NaturalBackground)
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Admin Banner
        item {
            AdminHeaderCard(
                currentUser = currentUser,
                cloudStatus = uiState.cloudSyncStatus,
                isCloudConnected = uiState.isCloudSyncEnabled
            )
        }

        // 2. Sub-Tab Selector
        item {
            AdminSubTabRow(
                selectedTab = currentAdminTab,
                onTabSelected = { currentAdminTab = it }
            )
        }

        // 3. Tab Contents
        when (currentAdminTab) {
            AdminTab.OVERVIEW -> {
                item {
                    AdminKpiSection(
                        totalMedicines = uiState.allMedicines.size,
                        totalUsers = uiState.allUsers.size,
                        auditLogCount = uiState.auditLogs.size
                    )
                }

                item {
                    QuickActionAdminRow(
                        onAddFormulation = { onOpenAddMedicineDialog(true) },
                        onAddUser = { onOpenAddUserDialog(true) },
                        onViewLogs = { currentAdminTab = AdminTab.AUDIT }
                    )
                }

                item {
                    RecentAuditPreviewCard(
                        logs = uiState.auditLogs.take(3),
                        onViewAll = { currentAdminTab = AdminTab.AUDIT }
                    )
                }
            }

            AdminTab.INVENTORY -> {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "CLASSICAL FORMULATION REPOSITORY",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.3.sp,
                                color = NaturalOliveMuted
                            )
                            Text(
                                text = "${uiState.allMedicines.size} Classical Remedies Managed",
                                fontSize = 12.sp,
                                color = NaturalTextPrimary.copy(alpha = 0.7f)
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = { showExportPdfDialog = true },
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, NaturalEarthGold),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = NaturalMossDark),
                                modifier = Modifier.testTag("admin_export_pdf_button")
                            ) {
                                Icon(
                                    Icons.Default.PictureAsPdf,
                                    contentDescription = null,
                                    modifier = Modifier.size(15.dp),
                                    tint = NaturalEarthGold
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Export PDF", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            }

                            Button(
                                onClick = { onOpenAddMedicineDialog(true) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = NaturalMossPrimary,
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.testTag("admin_add_herb_button")
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("+ Add Herb", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }

                items(uiState.allMedicines, key = { it.id }) { medicine ->
                    AdminMedicineCard(
                        medicine = medicine,
                        isChiefAdmin = isChiefAdmin,
                        onToggleVitality = { onToggleVitality(medicine.id) },
                        onDelete = { onDeleteMedicine(medicine.id) },
                        onCardClick = { onSelectMedicine(medicine) },
                        onShowQr = { qrMedicineForDialog = medicine }
                    )
                }
            }

            AdminTab.USERS -> {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp))
                            .background(NaturalCardSurface)
                            .border(1.dp, NaturalCardBorder, RoundedCornerShape(20.dp))
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "USER ACCESS & RBAC DIRECTORY",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.3.sp,
                                    color = NaturalOliveMuted
                                )
                                Text(
                                    text = "${uiState.allUsers.size} Registered Accounts",
                                    fontFamily = FontFamily.Serif,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NaturalTextHeading
                                )
                            }

                            Button(
                                onClick = { onOpenAddUserDialog(true) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = NaturalMossPrimary,
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.testTag("admin_add_user_button")
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("+ New User", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            val adminCount = uiState.allUsers.count { it.role == UserRole.ADMIN }
                            val doctorCount = uiState.allUsers.count { it.role == UserRole.PRACTITIONER }
                            val patientCount = uiState.allUsers.count { it.role == UserRole.PATIENT }

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(NaturalBackground)
                                    .padding(vertical = 8.dp, horizontal = 10.dp)
                            ) {
                                Column {
                                    Text(text = "⚡ Admins", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = NaturalEarthGold)
                                    Text(text = "$adminCount active", fontSize = 11.sp, fontWeight = FontWeight.Medium, color = NaturalTextHeading)
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(NaturalBackground)
                                    .padding(vertical = 8.dp, horizontal = 10.dp)
                            ) {
                                Column {
                                    Text(text = "⚕️ Vaidyas", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = NaturalMossPrimary)
                                    Text(text = "$doctorCount doctors", fontSize = 11.sp, fontWeight = FontWeight.Medium, color = NaturalTextHeading)
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(NaturalBackground)
                                    .padding(vertical = 8.dp, horizontal = 10.dp)
                            ) {
                                Column {
                                    Text(text = "🌿 Seekers", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = NaturalTerracotta)
                                    Text(text = "$patientCount patients", fontSize = 11.sp, fontWeight = FontWeight.Medium, color = NaturalTextHeading)
                                }
                            }
                        }
                    }
                }

                item {
                    UserSearchField(
                        query = uiState.userSearchQuery,
                        onQueryChange = onUserSearchQueryChanged
                    )
                }

                item {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        RoleFilterRow(
                            selectedRole = uiState.userRoleFilter,
                            onSelectRole = onSetUserRoleFilter,
                            users = uiState.allUsers
                        )

                        StatusFilterRow(
                            selectedStatus = uiState.userStatusFilter,
                            onSelectStatus = onSetUserStatusFilter,
                            users = uiState.allUsers
                        )
                    }
                }

                val query = uiState.userSearchQuery.trim().lowercase()
                val filteredUsers = uiState.allUsers.filter { user ->
                    val matchesRole = uiState.userRoleFilter == null || user.role == uiState.userRoleFilter
                    val matchesStatus = uiState.userStatusFilter == null || user.status == uiState.userStatusFilter
                    val matchesQuery = query.isEmpty() ||
                        user.name.lowercase().contains(query) ||
                        user.email.lowercase().contains(query) ||
                        user.phone.lowercase().contains(query) ||
                        user.designation.lowercase().contains(query) ||
                        user.clinicalNotes.lowercase().contains(query)
                    matchesRole && matchesStatus && matchesQuery
                }

                if (filteredUsers.isEmpty()) {
                    item {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(18.dp))
                                .border(1.dp, NaturalCardBorder, RoundedCornerShape(18.dp)),
                            color = NaturalCardSurface
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(text = "🔍", fontSize = 32.sp)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "No matching users found",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NaturalTextHeading
                                )
                                Text(
                                    text = "Try adjusting your search query or reset the role/status filter criteria.",
                                    fontSize = 11.sp,
                                    color = NaturalOliveMuted,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                                Spacer(modifier = Modifier.height(14.dp))
                                OutlinedButton(
                                    onClick = {
                                        onUserSearchQueryChanged("")
                                        onSetUserRoleFilter(null)
                                        onSetUserStatusFilter(null)
                                    },
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text("Reset Filters & Search", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                } else {
                    items(filteredUsers, key = { it.id }) { user ->
                        AdminUserCard(
                            user = user,
                            isCurrentUser = user.id == currentUser.id,
                            onCardClick = { onSelectUserForDetail(user) },
                            onEditUser = { onSetEditingUser(user) },
                            onDeleteUser = { onSetUserPendingDeletion(user) },
                            onSwitchToUser = { onSwitchUser(user) },
                            onPromoteRole = { newRole -> onUpdateUserRole(user.id, newRole) },
                            onToggleStatus = { newStatus -> onUpdateUserStatus(user.id, newStatus) }
                        )
                    }
                }
            }

            AdminTab.AUDIT -> {
                item {
                    Column {
                        Text(
                            text = "PHARMACOPOEIA SAFETY & AUDIT LOGS",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.3.sp,
                            color = NaturalOliveMuted
                        )
                        Text(
                            text = "Verifiable ledger of batch validations, roles & clinical alerts",
                            fontSize = 12.sp,
                            color = NaturalTextPrimary.copy(alpha = 0.7f)
                        )
                    }
                }

                items(uiState.auditLogs, key = { it.id }) { log ->
                    AuditLogRow(log = log)
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
    }

    // Dialogs
    if (uiState.isAddMedicineDialogOpen) {
        AddMedicineDialog(
            onDismiss = { onOpenAddMedicineDialog(false) },
            onConfirmAdd = onAddNewMedicine
        )
    }

    if (uiState.isAddUserDialogOpen) {
        AddUserDialog(
            isChiefAdmin = isChiefAdmin,
            onDismiss = { onOpenAddUserDialog(false) },
            onConfirmAdd = onAddNewUser
        )
    }

    uiState.selectedUserForDetail?.let { user ->
        UserDetailRoleDialog(
            user = user,
            isChiefAdmin = isChiefAdmin,
            onDismiss = { onSelectUserForDetail(null) },
            onRoleChange = { newRole -> onUpdateUserRole(user.id, newRole) },
            onStatusChange = { newStatus -> onUpdateUserStatus(user.id, newStatus) },
            onEditProfile = {
                onSelectUserForDetail(null)
                onSetEditingUser(user)
            },
            onDeleteUser = {
                onSelectUserForDetail(null)
                onSetUserPendingDeletion(user)
            },
            onResetPassword = {
                onResetUserPassword(user.id)
            },
            onSwitchToUser = {
                onSwitchUser(user)
                onSelectUserForDetail(null)
            }
        )
    }

    uiState.editingUser?.let { user ->
        EditUserDialog(
            user = user,
            isChiefAdmin = isChiefAdmin,
            onDismiss = { onSetEditingUser(null) },
            onConfirmSave = { updated ->
                onUpdateUser(updated)
            }
        )
    }

    uiState.userPendingDeletion?.let { user ->
        DeleteUserConfirmDialog(
            user = user,
            isCurrentUser = user.id == currentUser.id,
            onDismiss = { onSetUserPendingDeletion(null) },
            onConfirmDelete = {
                onDeleteUser(user.id)
            }
        )
    }

    // Printable QR Monograph Dialog
    qrMedicineForDialog?.let { medicine ->
        PrintableMedicineQrDialog(
            medicine = medicine,
            onDismiss = { qrMedicineForDialog = null }
        )
    }

    // Professional Catalogue & Stock PDF Report Export Dialog
    if (showExportPdfDialog) {
        ExportCataloguePdfDialog(
            allMedicines = uiState.allMedicines,
            filteredMedicines = uiState.allMedicines,
            activeFilterDescription = "Admin Master Inventory Audit",
            onDismiss = { showExportPdfDialog = false }
        )
    }
}

// -------------------------------------------------------------
// UI Sub-components
// -------------------------------------------------------------

@Composable
private fun AdminHeaderCard(
    currentUser: AppUser,
    cloudStatus: String = "Cloud Ready",
    isCloudConnected: Boolean = false,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .border(1.dp, NaturalCardBorder, RoundedCornerShape(24.dp)),
        color = NaturalCardSurface,
        shadowElevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(
                                when (currentUser.role) {
                                    UserRole.ADMIN -> NaturalTerracotta.copy(alpha = 0.18f)
                                    UserRole.PRACTITIONER -> NaturalSageContainer
                                    UserRole.PATIENT -> NaturalParchmentContainer
                                    UserRole.GUEST -> NaturalSageContainer.copy(alpha = 0.5f)
                                }
                            )
                            .border(
                                1.5.dp,
                                when (currentUser.role) {
                                    UserRole.ADMIN -> NaturalTerracotta
                                    UserRole.PRACTITIONER -> NaturalMossPrimary
                                    UserRole.PATIENT -> NaturalOliveMuted
                                    UserRole.GUEST -> NaturalMossPrimary
                                },
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = currentUser.role.iconEmoji,
                            fontSize = 20.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = currentUser.name,
                                fontFamily = FontFamily.Serif,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = NaturalTextHeading
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            RoleBadge(role = currentUser.role)
                        }

                        Text(
                            text = currentUser.designation.ifEmpty { currentUser.email },
                            fontSize = 11.sp,
                            color = NaturalOliveMuted
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(NaturalBackground)
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Access: ${currentUser.role.description}",
                        fontSize = 10.sp,
                        color = NaturalTextPrimary.copy(alpha = 0.8f),
                        lineHeight = 14.sp,
                        modifier = Modifier.weight(1f)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .clip(CircleShape)
                                .background(if (isCloudConnected) Color(0xFF2E7D32) else NaturalEarthGold)
                        )
                        Text(
                            text = if (isCloudConnected) "Firestore Online" else "Local / Firestore Ready",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isCloudConnected) Color(0xFF2E7D32) else NaturalEarthGold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RoleBadge(role: UserRole) {
    val (bgColor, textColor, borderCol) = when (role) {
        UserRole.ADMIN -> Triple(
            NaturalTerracotta.copy(alpha = 0.15f),
            NaturalTerracotta,
            NaturalTerracotta.copy(alpha = 0.5f)
        )
        UserRole.PRACTITIONER -> Triple(
            NaturalSageContainer,
            NaturalMossDark,
            NaturalSageBorder
        )
        UserRole.PATIENT -> Triple(
            NaturalParchmentContainer,
            NaturalEarthGold,
            NaturalParchmentBorder
        )
        UserRole.GUEST -> Triple(
            NaturalSageContainer.copy(alpha = 0.5f),
            NaturalMossDark,
            NaturalSageBorder
        )
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .border(1.dp, borderCol, RoundedCornerShape(8.dp))
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(
            text = "${role.iconEmoji} ${role.badgeLabel}",
            fontSize = 9.sp,
            fontWeight = FontWeight.ExtraBold,
            color = textColor,
            letterSpacing = 0.4.sp
        )
    }
}

@Composable
fun StatusBadge(status: UserStatus) {
    val (bg, txt) = when (status) {
        UserStatus.ACTIVE -> Pair(NaturalSageContainer, NaturalMossDark)
        UserStatus.SUSPENDED -> Pair(NaturalTerracotta.copy(alpha = 0.15f), NaturalTerracotta)
        UserStatus.PENDING -> Pair(NaturalParchmentContainer, NaturalEarthGold)
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(bg)
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(
            text = status.label.uppercase(),
            fontSize = 8.sp,
            fontWeight = FontWeight.Bold,
            color = txt,
            letterSpacing = 0.5.sp
        )
    }
}

@Composable
private fun AdminSubTabRow(
    selectedTab: AdminTab,
    onTabSelected: (AdminTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        AdminTab.values().forEach { tab ->
            val isSelected = tab == selectedTab
            Surface(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .border(
                        1.dp,
                        if (isSelected) NaturalMossPrimary else NaturalCardBorder,
                        RoundedCornerShape(16.dp)
                    )
                    .clickable { onTabSelected(tab) }
                    .testTag("admin_tab_${tab.name.lowercase()}"),
                color = if (isSelected) NaturalMossPrimary else NaturalCardSurface
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = tab.icon, fontSize = 13.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = tab.title,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) Color.White else NaturalTextHeading
                    )
                }
            }
        }
    }
}

@Composable
private fun AdminKpiSection(
    totalMedicines: Int,
    totalUsers: Int,
    auditLogCount: Int
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Formulations KPI
            KpiCard(
                title = "FORMULATIONS",
                metric = "$totalMedicines",
                subtext = "Classical AFI Monographs",
                isWarning = false,
                icon = "🌿",
                modifier = Modifier.weight(1f)
            )

            // User Accounts KPI
            KpiCard(
                title = "REGISTERED USERS",
                metric = "$totalUsers",
                subtext = "Role-Based Access",
                isWarning = false,
                icon = "👥",
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Classical Categories KPI
            KpiCard(
                title = "CATEGORIES",
                metric = "12 Classes",
                subtext = "Arishta, Taila, Vati & more",
                isWarning = false,
                icon = "📜",
                modifier = Modifier.weight(1f)
            )

            // Safety Audit KPI
            KpiCard(
                title = "SYSTEM AUDIT",
                metric = "$auditLogCount",
                subtext = "Security & Admin Logs",
                isWarning = false,
                icon = "🛡️",
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun KpiCard(
    title: String,
    metric: String,
    subtext: String,
    isWarning: Boolean,
    icon: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .border(
                1.dp,
                if (isWarning) NaturalTerracotta.copy(alpha = 0.4f) else NaturalCardBorder,
                RoundedCornerShape(18.dp)
            ),
        color = NaturalCardSurface
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.1.sp,
                    color = NaturalOliveMuted
                )
                Text(text = icon, fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = metric,
                fontFamily = FontFamily.Serif,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = NaturalTextHeading
            )

            Text(
                text = subtext,
                fontSize = 10.sp,
                color = if (isWarning) NaturalTerracotta else NaturalMossDark,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun QuickActionAdminRow(
    onAddFormulation: () -> Unit,
    onAddUser: () -> Unit,
    onViewLogs: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedButton(
            onClick = onAddFormulation,
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = NaturalCardSurface,
                contentColor = NaturalMossPrimary
            )
        ) {
            Text("+ Herb", fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }

        OutlinedButton(
            onClick = onAddUser,
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = NaturalCardSurface,
                contentColor = NaturalMossPrimary
            )
        ) {
            Text("+ User", fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }

        OutlinedButton(
            onClick = onViewLogs,
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = NaturalCardSurface,
                contentColor = NaturalMossPrimary
            )
        ) {
            Text("🛡️ Audits", fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun RecentAuditPreviewCard(
    logs: List<AuditLogEntry>,
    onViewAll: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .border(1.dp, NaturalCardBorder, RoundedCornerShape(20.dp)),
        color = NaturalCardSurface
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "RECENT REGULATORY & AUDIT EVENTS",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp,
                    color = NaturalOliveMuted
                )

                TextButton(
                    onClick = onViewAll,
                    colors = ButtonDefaults.textButtonColors(contentColor = NaturalMossPrimary),
                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "View All",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = NaturalMossPrimary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = NaturalMossPrimary,
                        modifier = Modifier.size(12.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            logs.forEach { log ->
                AuditLogRow(log = log, compact = true)
                Spacer(modifier = Modifier.height(6.dp))
            }
        }
    }
}

@Composable
private fun AuditLogRow(log: AuditLogEntry, compact: Boolean = false) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .border(
                1.dp,
                if (log.isWarning) NaturalTerracotta.copy(alpha = 0.3f) else NaturalCardBorder,
                RoundedCornerShape(14.dp)
            ),
        color = if (log.isWarning) NaturalTerracotta.copy(alpha = 0.05f) else NaturalBackground
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Text(
                text = if (log.isWarning) "⚠️" else "🛡️",
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = log.targetItem,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = NaturalTextHeading
                    )
                    Text(
                        text = log.timestamp,
                        fontSize = 10.sp,
                        color = NaturalOliveMuted
                    )
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = log.details,
                    fontSize = 11.sp,
                    color = NaturalTextPrimary.copy(alpha = 0.85f),
                    lineHeight = 15.sp
                )

                if (!compact) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Actor: ${log.actorName} • Action: ${log.actionType}",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Medium,
                        color = NaturalOliveMuted
                    )
                }
            }
        }
    }
}

@Composable
private fun AdminMedicineCard(
    medicine: AyurvedaMedicine,
    isChiefAdmin: Boolean,
    onToggleVitality: () -> Unit,
    onDelete: () -> Unit,
    onCardClick: () -> Unit,
    onShowQr: () -> Unit = {}
) {
    val interactionSource = androidx.compose.runtime.remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()
    val scale by animateFloatAsState(
        targetValue = if (isHovered) 1.018f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "admin_med_row_hover_scale"
    )

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .hoverable(interactionSource)
            .clip(RoundedCornerShape(20.dp))
            .border(
                1.dp,
                if (isHovered) NaturalEarthGold.copy(alpha = 0.6f) else NaturalCardBorder,
                RoundedCornerShape(20.dp)
            )
            .clickable(onClick = onCardClick),
        color = NaturalCardSurface
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = medicine.sanskritName,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = NaturalEarthGold
                    )
                    Text(
                        text = medicine.name,
                        fontFamily = FontFamily.Serif,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = NaturalTextHeading
                    )
                    Text(
                        text = "Batch: ${medicine.batchNumber} • ${medicine.category.displayName}",
                        fontSize = 10.sp,
                        color = NaturalOliveMuted
                    )
                }

                // Printable QR Code & Monograph Action Button
                IconButton(
                    onClick = onShowQr,
                    modifier = Modifier
                        .size(32.dp)
                        .testTag("admin_qr_code_btn_${medicine.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.QrCode,
                        contentDescription = "Printable QR Monograph for ${medicine.name}",
                        tint = NaturalMossDark,
                        modifier = Modifier.size(19.dp)
                    )
                }

                // Daily Vitality Featured Toggle
                IconButton(
                    onClick = onToggleVitality,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = if (medicine.isDailyVitality) Icons.Default.Star else Icons.Outlined.StarBorder,
                        contentDescription = "Toggle Vitality",
                        tint = if (medicine.isDailyVitality) NaturalEarthGold else NaturalOliveMuted
                    )
                }

                if (isChiefAdmin) {
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Archive formulation",
                            tint = NaturalOliveMuted,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Classical Reference & Effective Packing
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(NaturalBackground)
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Ref: ${medicine.classicalReference.ifBlank { "AFI Classical" }}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = NaturalOliveMuted
                )
                Text(
                    text = "Packing: ${medicine.effectivePacking}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = NaturalMossDark
                )
            }
        }
    }
}

@Composable
private fun RoleFilterRow(
    selectedRole: UserRole?,
    onSelectRole: (UserRole?) -> Unit,
    users: List<AppUser>
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        FilterChip(
            selected = selectedRole == null,
            onClick = { onSelectRole(null) },
            label = { Text("All Roles (${users.size})", fontSize = 11.sp) },
            colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = NaturalMossPrimary,
                selectedLabelColor = Color.White
            )
        )

        UserRole.values().forEach { role ->
            val count = users.count { it.role == role }
            FilterChip(
                selected = selectedRole == role,
                onClick = { onSelectRole(if (selectedRole == role) null else role) },
                label = { Text("${role.iconEmoji} ${role.badgeLabel} ($count)", fontSize = 11.sp) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = NaturalMossPrimary,
                    selectedLabelColor = Color.White
                )
            )
        }
    }
}

@Composable
private fun StatusFilterRow(
    selectedStatus: UserStatus?,
    onSelectStatus: (UserStatus?) -> Unit,
    users: List<AppUser>,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        val allSelected = selectedStatus == null
        Surface(
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .border(
                    1.dp,
                    if (allSelected) NaturalMossPrimary else NaturalCardBorder,
                    RoundedCornerShape(10.dp)
                )
                .clickable { onSelectStatus(null) },
            color = if (allSelected) NaturalSageContainer else NaturalCardSurface
        ) {
            Text(
                text = "All Statuses (${users.size})",
                fontSize = 10.sp,
                fontWeight = if (allSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (allSelected) NaturalMossDark else NaturalOliveMuted,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
            )
        }

        UserStatus.values().forEach { status ->
            val isChosen = selectedStatus == status
            val count = users.count { it.status == status }
            Surface(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .border(
                        1.dp,
                        if (isChosen) NaturalMossPrimary else NaturalCardBorder,
                        RoundedCornerShape(10.dp)
                    )
                    .clickable { onSelectStatus(if (isChosen) null else status) },
                color = if (isChosen) NaturalSageContainer else NaturalCardSurface
            ) {
                Text(
                    text = "${status.label} ($count)",
                    fontSize = 10.sp,
                    fontWeight = if (isChosen) FontWeight.Bold else FontWeight.Medium,
                    color = if (isChosen) NaturalMossDark else NaturalOliveMuted,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                )
            }
        }
    }
}

@Composable
private fun UserSearchField(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(NaturalCardSurface)
            .border(1.dp, NaturalCardBorder, RoundedCornerShape(12.dp))
            .padding(horizontal = 12.dp, vertical = 9.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search Users",
                tint = NaturalOliveMuted,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Box(modifier = Modifier.weight(1f)) {
                if (query.isEmpty()) {
                    Text(
                        text = "Search users by name, email, phone, notes...",
                        fontSize = 11.sp,
                        color = NaturalOliveMuted.copy(alpha = 0.7f)
                    )
                }
                BasicTextField(
                    value = query,
                    onValueChange = onQueryChange,
                    singleLine = true,
                    textStyle = TextStyle(
                        fontSize = 12.sp,
                        color = NaturalTextHeading,
                        fontWeight = FontWeight.Medium
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("user_search_input")
                )
            }
            if (query.isNotEmpty()) {
                IconButton(
                    onClick = { onQueryChange("") },
                    modifier = Modifier.size(20.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Clear search",
                        tint = NaturalOliveMuted,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun AdminUserCard(
    user: AppUser,
    isCurrentUser: Boolean,
    onCardClick: () -> Unit,
    onEditUser: () -> Unit,
    onDeleteUser: () -> Unit,
    onSwitchToUser: () -> Unit = {},
    onPromoteRole: (UserRole) -> Unit,
    onToggleStatus: (UserStatus) -> Unit
) {
    val isRootAdmin = user.name.contains("Jerin", ignoreCase = true) ||
        user.email.equals("sys.jerin@gmail.com", ignoreCase = true) ||
        user.id == "user_admin_jerin"

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .border(
                1.dp,
                if (isCurrentUser) NaturalMossPrimary else NaturalCardBorder,
                RoundedCornerShape(20.dp)
            )
            .clickable(onClick = onCardClick)
            .testTag("user_card_${user.id}"),
        color = NaturalCardSurface
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header row: Avatar + Name + Role Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(
                                when (user.role) {
                                    UserRole.ADMIN -> NaturalEarthGold.copy(alpha = 0.2f)
                                    UserRole.PRACTITIONER -> NaturalSageContainer
                                    UserRole.PATIENT -> NaturalTerracotta.copy(alpha = 0.15f)
                                    UserRole.GUEST -> NaturalSageContainer.copy(alpha = 0.6f)
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = user.name.take(1).uppercase(),
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = when (user.role) {
                                UserRole.ADMIN -> NaturalEarthGold
                                UserRole.PRACTITIONER -> NaturalMossPrimary
                                UserRole.PATIENT -> NaturalTerracotta
                                UserRole.GUEST -> NaturalMossPrimary
                            }
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = user.name,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = NaturalTextHeading
                            )
                            if (isCurrentUser) {
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "(You)",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NaturalMossPrimary
                                )
                            }
                        }
                        Text(
                            text = "${user.designation} • ${user.email}",
                            fontSize = 10.sp,
                            color = NaturalOliveMuted
                        )
                    }
                }

                RoleBadge(role = user.role)
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Details strip: Constitution, Adherence, Status, Phone
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(NaturalBackground)
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Constitution: ",
                        fontSize = 10.sp,
                        color = NaturalOliveMuted
                    )
                    Text(
                        text = "${user.prakriti.symbol} ${user.prakriti.displayName}",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = NaturalTextHeading
                    )
                    if (user.phone.isNotBlank()) {
                        Text(
                            text = " • 📞 ${user.phone}",
                            fontSize = 9.sp,
                            color = NaturalOliveMuted
                        )
                    }
                }

                StatusBadge(status = user.status)
            }

            if (user.clinicalNotes.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "📝 \"${user.clinicalNotes}\"",
                    fontSize = 10.sp,
                    color = NaturalOliveMuted,
                    maxLines = 1
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Actions row: Edit, Delete, Role & Quick Switcher
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Secondary actions: Edit + Delete
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Edit Profile button
                    OutlinedButton(
                        onClick = onEditUser,
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = NaturalMossPrimary
                        ),
                        modifier = Modifier.height(30.dp).testTag("user_edit_${user.id}")
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit Profile", modifier = Modifier.size(13.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Edit", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }

                    // Delete button (or protected indicator)
                    if (isRootAdmin) {
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(NaturalSageContainer.copy(alpha = 0.5f))
                                .border(1.dp, NaturalEarthGold.copy(alpha = 0.4f), RoundedCornerShape(8.dp)),
                            color = Color.Transparent
                        ) {
                            Text(
                                text = "🛡️ Protected Admin",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = NaturalEarthGold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                            )
                        }
                    } else if (!isCurrentUser) {
                        OutlinedButton(
                            onClick = onDeleteUser,
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = NaturalTerracotta
                            ),
                            modifier = Modifier.height(30.dp).testTag("user_delete_${user.id}")
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete User", modifier = Modifier.size(13.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Delete", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // Primary actions: Manage Role & Status
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = onCardClick,
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = NaturalSageContainer,
                            contentColor = NaturalMossDark
                        ),
                        modifier = Modifier.height(30.dp).testTag("user_manage_${user.id}")
                    ) {
                        Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Role & Status", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// Interactive Dialogs
// -------------------------------------------------------------

@Composable
private fun UserDetailRoleDialog(
    user: AppUser,
    isChiefAdmin: Boolean,
    onDismiss: () -> Unit,
    onRoleChange: (UserRole) -> Unit,
    onStatusChange: (UserStatus) -> Unit,
    onEditProfile: () -> Unit,
    onDeleteUser: () -> Unit,
    onResetPassword: () -> Unit,
    onSwitchToUser: () -> Unit = {}
) {
    var selectedRole by remember { mutableStateOf(user.role) }
    var selectedStatus by remember { mutableStateOf(user.status) }
    var rbacWarning by remember { mutableStateOf<String?>(null) }
    var passwordResetSuccess by remember { mutableStateOf(false) }

    val isRootAdmin = user.name.contains("Jerin", ignoreCase = true) ||
        user.email.equals("sys.jerin@gmail.com", ignoreCase = true) ||
        user.id == "user_admin_jerin"

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .border(1.dp, NaturalCardBorder, RoundedCornerShape(24.dp)),
            color = NaturalCardSurface
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "USER ACCESS & ROLE MANAGEMENT",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.1.sp,
                            color = NaturalOliveMuted
                        )
                        Text(
                            text = user.name,
                            fontFamily = FontFamily.Serif,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = NaturalTextHeading
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = NaturalOliveMuted)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // User details strip
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(NaturalBackground)
                        .padding(10.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "📧 Email: ${user.email}",
                            fontSize = 11.sp,
                            color = NaturalTextHeading
                        )
                        if (user.phone.isNotBlank()) {
                            Text(
                                text = "📞 Phone: ${user.phone}",
                                fontSize = 11.sp,
                                color = NaturalTextHeading
                            )
                        }
                        Text(
                            text = "🏷️ Designation: ${user.designation}",
                            fontSize = 11.sp,
                            color = NaturalTextHeading
                        )
                        Text(
                            text = "🌿 Prakriti: ${user.prakriti.symbol} ${user.prakriti.displayName}",
                            fontSize = 11.sp,
                            color = NaturalTextHeading
                        )
                        if (user.clinicalNotes.isNotBlank()) {
                            Text(
                                text = "📝 Notes: ${user.clinicalNotes}",
                                fontSize = 11.sp,
                                color = NaturalOliveMuted
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "ASSIGN APPLICATION ROLE:",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = NaturalOliveMuted
                )

                if (rbacWarning != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "⚠️ $rbacWarning",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = NaturalTerracotta
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                UserRole.values().forEach { role ->
                    val isChosen = selectedRole == role
                    val isElevation = role == UserRole.ADMIN || role == UserRole.PRACTITIONER
                    val canSelect = !isElevation || isChiefAdmin

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .border(
                                1.dp,
                                if (isChosen) NaturalMossPrimary else NaturalCardBorder,
                                RoundedCornerShape(12.dp)
                            )
                            .clickable {
                                if (isElevation && !isChiefAdmin) {
                                    rbacWarning = "Only Administrator can elevate a user to Practitioner or Admin."
                                } else {
                                    rbacWarning = null
                                    selectedRole = role
                                    onRoleChange(role)
                                }
                            },
                        color = if (isChosen) NaturalSageContainer else if (!canSelect) NaturalBackground.copy(alpha = 0.5f) else NaturalBackground
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = role.iconEmoji, fontSize = 16.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = role.displayName,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = NaturalTextHeading
                                    )
                                    if (isElevation && !isChiefAdmin) {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "🔒 Requires Admin",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = NaturalTerracotta
                                        )
                                    }
                                }
                                Text(
                                    text = role.description,
                                    fontSize = 9.sp,
                                    color = NaturalOliveMuted,
                                    lineHeight = 12.sp
                                )
                            }
                            if (isChosen) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = NaturalMossPrimary, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "ACCOUNT STATUS:",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = NaturalOliveMuted
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    UserStatus.values().forEach { status ->
                        val isChosen = selectedStatus == status
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .border(
                                    1.dp,
                                    if (isChosen) NaturalMossPrimary else NaturalCardBorder,
                                    RoundedCornerShape(10.dp)
                                )
                                .clickable {
                                    selectedStatus = status
                                    onStatusChange(status)
                                },
                            color = if (isChosen) NaturalSageContainer else NaturalBackground
                        ) {
                            Box(
                                modifier = Modifier.padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = status.label,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NaturalTextHeading
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // CRUD Actions: Edit, Reset Password, Login, Delete
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = onEditProfile,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = NaturalSageContainer,
                            contentColor = NaturalMossDark
                        )
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Edit Full Profile & Details", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = {
                            onResetPassword()
                            passwordResetSuccess = true
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = NaturalMossPrimary
                        )
                    ) {
                        Icon(Icons.Default.VpnKey, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (passwordResetSuccess) "Temporary Password Sent! (ayurguide123)" else "Reset User Password",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    if (!isRootAdmin) {
                        OutlinedButton(
                            onClick = onDeleteUser,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = NaturalTerracotta
                            )
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Delete Account from Sanctuary", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AddMedicineDialog(
    onDismiss: () -> Unit,
    onConfirmAdd: (AyurvedaMedicine) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var sanskritName by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(FormulationCategory.CHURNA) }
    var primaryBenefit by remember { mutableStateOf("") }
    var standardDose by remember { mutableStateOf("500mg - 1g with warm water") }
    var photoUrl by remember { mutableStateOf("") }
    var packing by remember { mutableStateOf("450 ml") }
    var classicalReference by remember { mutableStateOf("Ashtamgahrudayam") }
    var mainIngredientsText by remember { mutableStateOf("") }
    var usageInstructionsText by remember { mutableStateOf("") }
    var stockUnits by remember { mutableIntStateOf(50) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .border(1.dp, NaturalCardBorder, RoundedCornerShape(24.dp)),
            color = NaturalCardSurface
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "NEW CLASSICAL PRODUCT",
                        fontFamily = FontFamily.Serif,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = NaturalTextHeading
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = NaturalOliveMuted)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                AdminInputField(label = "Product Name *", value = name, onValueChange = { name = it }, placeholder = "e.g. Abhayarishtam")
                Spacer(modifier = Modifier.height(8.dp))
                AdminInputField(label = "Sanskrit Name", value = sanskritName, onValueChange = { sanskritName = it }, placeholder = "e.g. अभयारिष्टम्")
                Spacer(modifier = Modifier.height(8.dp))
                AdminInputField(label = "Product Photo Image URL", value = photoUrl, onValueChange = { photoUrl = it }, placeholder = "https://example.com/product_photo.jpg")
                Spacer(modifier = Modifier.height(8.dp))
                AdminInputField(label = "Standard Packing *", value = packing, onValueChange = { packing = it }, placeholder = "e.g. 450 ml or 100 Nos.")
                Spacer(modifier = Modifier.height(8.dp))
                AdminInputField(label = "Classical Reference", value = classicalReference, onValueChange = { classicalReference = it }, placeholder = "e.g. Ashtamgahrudayam / Sahasrayogam")
                Spacer(modifier = Modifier.height(8.dp))
                AdminInputField(label = "Main Ingredients Text", value = mainIngredientsText, onValueChange = { mainIngredientsText = it }, placeholder = "e.g. Haritaki, Draksha, Vidanga, Dhataki...")
                Spacer(modifier = Modifier.height(8.dp))
                AdminInputField(label = "Usage / Dosage Instructions", value = usageInstructionsText, onValueChange = { usageInstructionsText = it }, placeholder = "e.g. 15 to 25 ml twice daily after food")
                Spacer(modifier = Modifier.height(8.dp))
                AdminInputField(label = "Primary Therapeutic Benefit / Indications", value = primaryBenefit, onValueChange = { primaryBenefit = it }, placeholder = "e.g. Piles, constipation, digestive disorders")

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        if (name.isNotBlank()) {
                            val newMed = AyurvedaMedicine(
                                id = "custom_${System.currentTimeMillis()}",
                                name = name.trim(),
                                sanskritName = sanskritName.ifBlank { name }.trim(),
                                category = selectedCategory,
                                tagPill = "CLASSICAL FORMULATION",
                                shortDescription = primaryBenefit.ifBlank { "Traditional Ayurvedic classical formulation." },
                                primaryBenefit = primaryBenefit.ifBlank { "Holistic vitality and dosha pacification." },
                                doshaImpact = "Tridoshic Balancer",
                                targetDoshas = listOf(DoshaType.TRIDOSHIC),
                                photoUrl = photoUrl.trim(),
                                packing = packing.trim(),
                                classicalReference = classicalReference.trim(),
                                mainIngredientsText = mainIngredientsText.trim(),
                                usageInstructionsText = usageInstructionsText.trim(),
                                constituents = listOf("Herbal Extractives", "Natural Biotics"),
                                ingredients = listOf(
                                    AyurvedaIngredient(
                                        name = if (mainIngredientsText.isNotBlank()) mainIngredientsText.take(30) else name.trim(),
                                        sanskritName = sanskritName.ifBlank { name }.trim(),
                                        botanicalName = "Botanical Herb",
                                        partUsed = "Classical Herbal Compound",
                                        classicalRole = "Rasayana & Dravyaguna agent"
                                    )
                                ),
                                dravyaguna = DravyagunaProfile(
                                    rasa = listOf("Madhura (Sweet)", "Tikta (Bitter)"),
                                    virya = "Sheeta (Cooling)",
                                    vipaka = "Madhura (Nourishing)",
                                    guna = listOf("Guru (Heavy)", "Snigdha (Unctuous)")
                                ),
                                dosage = DosageInfo(
                                    summary = usageInstructionsText.ifBlank { standardDose },
                                    standardDose = usageInstructionsText.ifBlank { standardDose },
                                    frequency = "Twice Daily",
                                    timing = "After Meals",
                                    anupana = "Warm Water"
                                ),
                                indications = if (primaryBenefit.isNotBlank()) listOf(primaryBenefit) else listOf("General debility", "Metabolic harmony"),
                                contraindications = listOf("Use under Vaidya supervision"),
                                pathyaWholesome = listOf("Light warm nourishing food"),
                                apathyaAvoid = listOf("Excessive cold, stale foods"),
                                stockUnits = stockUnits,
                                batchNumber = "AYUR-2026-B${(10..99).random()}"
                            )
                            onConfirmAdd(newMed)
                        }
                    },
                    enabled = name.isNotBlank(),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NaturalMossPrimary,
                        contentColor = Color.White
                    )
                ) {
                    Text("Add Product to Master Catalogue", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun AddUserDialog(
    isChiefAdmin: Boolean,
    onDismiss: () -> Unit,
    onConfirmAdd: (AppUser) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf(UserRole.PATIENT) }
    var selectedPrakriti by remember { mutableStateOf(DoshaType.PITTA) }
    var designation by remember { mutableStateOf("") }
    var clinicalNotes by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .border(1.dp, NaturalCardBorder, RoundedCornerShape(24.dp)),
            color = NaturalCardSurface
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "REGISTER NEW USER",
                        fontFamily = FontFamily.Serif,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = NaturalTextHeading
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = NaturalOliveMuted)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                AdminInputField(label = "Full Name *", value = name, onValueChange = { name = it }, placeholder = "e.g. Dr. Anand Joshi")
                Spacer(modifier = Modifier.height(8.dp))
                AdminInputField(label = "Email Address *", value = email, onValueChange = { email = it }, placeholder = "e.g. anand.j@ayurguide.org")
                Spacer(modifier = Modifier.height(8.dp))
                AdminInputField(label = "Phone Number", value = phone, onValueChange = { phone = it }, placeholder = "e.g. +91 98765 43210")
                Spacer(modifier = Modifier.height(8.dp))
                AdminInputField(label = "Designation / Title", value = designation, onValueChange = { designation = it }, placeholder = "e.g. Ayurvedic Vaidya / Patient")
                Spacer(modifier = Modifier.height(8.dp))
                AdminInputField(label = "Clinical Notes / Specialization", value = clinicalNotes, onValueChange = { clinicalNotes = it }, placeholder = "e.g. Panchakarma specialist / Vata anxiety history")

                Spacer(modifier = Modifier.height(10.dp))

                Text(text = "ROLE:", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = NaturalOliveMuted)
                if (!isChiefAdmin) {
                    Text(
                        text = "🔒 Default role is Wellness Seeker. Only Administrator can assign Practitioner or Admin roles.",
                        fontSize = 9.sp,
                        color = NaturalOliveMuted,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val selectableRoles = if (isChiefAdmin) {
                        UserRole.values().toList()
                    } else {
                        listOf(UserRole.PATIENT)
                    }

                    selectableRoles.forEach { role ->
                        val isChosen = selectedRole == role
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .border(1.dp, if (isChosen) NaturalMossPrimary else NaturalCardBorder, RoundedCornerShape(8.dp))
                                .clickable { selectedRole = role },
                            color = if (isChosen) NaturalSageContainer else NaturalBackground
                        ) {
                            Box(modifier = Modifier.padding(vertical = 6.dp), contentAlignment = Alignment.Center) {
                                Text(
                                    text = "${role.iconEmoji} ${role.badgeLabel}",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NaturalTextHeading
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(text = "CONSTITUTION (PRAKRITI):", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = NaturalOliveMuted)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(DoshaType.VATA, DoshaType.PITTA, DoshaType.KAPHA, DoshaType.TRIDOSHIC).forEach { dosha ->
                        val isChosen = selectedPrakriti == dosha
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .border(1.dp, if (isChosen) NaturalMossPrimary else NaturalCardBorder, RoundedCornerShape(8.dp))
                                .clickable { selectedPrakriti = dosha },
                            color = if (isChosen) NaturalSageContainer else NaturalBackground
                        ) {
                            Box(modifier = Modifier.padding(vertical = 6.dp), contentAlignment = Alignment.Center) {
                                Text(
                                    text = "${dosha.symbol} ${dosha.displayName.take(5)}",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NaturalTextHeading
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        if (name.isNotBlank() && email.isNotBlank()) {
                            val newUser = AppUser(
                                id = "user_${System.currentTimeMillis()}",
                                name = name.trim(),
                                email = email.trim(),
                                phone = phone.trim(),
                                role = selectedRole,
                                prakriti = selectedPrakriti,
                                status = UserStatus.ACTIVE,
                                designation = designation.ifBlank { selectedRole.displayName }.trim(),
                                clinicalNotes = clinicalNotes.trim(),
                                registeredDate = "Today",
                                lastActive = "Just now"
                            )
                            onConfirmAdd(newUser)
                        }
                    },
                    enabled = name.isNotBlank() && email.isNotBlank(),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NaturalMossPrimary,
                        contentColor = Color.White
                    )
                ) {
                    Text("Register & Grant Access", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun EditUserDialog(
    user: AppUser,
    isChiefAdmin: Boolean,
    onDismiss: () -> Unit,
    onConfirmSave: (AppUser) -> Unit
) {
    var name by remember { mutableStateOf(user.name) }
    var email by remember { mutableStateOf(user.email) }
    var phone by remember { mutableStateOf(user.phone) }
    var designation by remember { mutableStateOf(user.designation) }
    var clinicalNotes by remember { mutableStateOf(user.clinicalNotes) }
    var selectedRole by remember { mutableStateOf(user.role) }
    var selectedPrakriti by remember { mutableStateOf(user.prakriti) }
    var selectedStatus by remember { mutableStateOf(user.status) }
    var rbacWarning by remember { mutableStateOf<String?>(null) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .border(1.dp, NaturalCardBorder, RoundedCornerShape(24.dp)),
            color = NaturalCardSurface
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "EDIT USER PROFILE",
                            fontFamily = FontFamily.Serif,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = NaturalTextHeading
                        )
                        Text(
                            text = "ID: ${user.id}",
                            fontSize = 10.sp,
                            color = NaturalOliveMuted
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = NaturalOliveMuted)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                AdminInputField(label = "Full Name", value = name, onValueChange = { name = it }, placeholder = "Full name")
                Spacer(modifier = Modifier.height(8.dp))
                AdminInputField(label = "Email Address", value = email, onValueChange = { email = it }, placeholder = "Email address")
                Spacer(modifier = Modifier.height(8.dp))
                AdminInputField(label = "Phone Number", value = phone, onValueChange = { phone = it }, placeholder = "+91 98765 43210")
                Spacer(modifier = Modifier.height(8.dp))
                AdminInputField(label = "Designation / Title", value = designation, onValueChange = { designation = it }, placeholder = "e.g. Ayurvedic Vaidya / Seeker")
                Spacer(modifier = Modifier.height(8.dp))
                AdminInputField(label = "Clinical Notes / Specialization", value = clinicalNotes, onValueChange = { clinicalNotes = it }, placeholder = "Clinical history, specialty, notes")

                Spacer(modifier = Modifier.height(10.dp))

                Text(text = "ROLE ASSIGNMENT:", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = NaturalOliveMuted)
                if (rbacWarning != null) {
                    Text(text = "⚠️ $rbacWarning", fontSize = 9.sp, color = NaturalTerracotta)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    UserRole.values().forEach { role ->
                        val isChosen = selectedRole == role
                        val isElevation = (role == UserRole.ADMIN || role == UserRole.PRACTITIONER) && role != user.role
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .border(1.dp, if (isChosen) NaturalMossPrimary else NaturalCardBorder, RoundedCornerShape(8.dp))
                                .clickable {
                                    if (isElevation && !isChiefAdmin) {
                                        rbacWarning = "Only Administrator can elevate a user to Practitioner or Admin."
                                    } else {
                                        rbacWarning = null
                                        selectedRole = role
                                    }
                                },
                            color = if (isChosen) NaturalSageContainer else NaturalBackground
                        ) {
                            Box(modifier = Modifier.padding(vertical = 6.dp), contentAlignment = Alignment.Center) {
                                Text(
                                    text = "${role.iconEmoji} ${role.badgeLabel}",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NaturalTextHeading
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(text = "CONSTITUTION (PRAKRITI):", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = NaturalOliveMuted)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(DoshaType.VATA, DoshaType.PITTA, DoshaType.KAPHA, DoshaType.TRIDOSHIC).forEach { dosha ->
                        val isChosen = selectedPrakriti == dosha
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .border(1.dp, if (isChosen) NaturalMossPrimary else NaturalCardBorder, RoundedCornerShape(8.dp))
                                .clickable { selectedPrakriti = dosha },
                            color = if (isChosen) NaturalSageContainer else NaturalBackground
                        ) {
                            Box(modifier = Modifier.padding(vertical = 6.dp), contentAlignment = Alignment.Center) {
                                Text(
                                    text = "${dosha.symbol} ${dosha.displayName.take(5)}",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NaturalTextHeading
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(text = "ACCOUNT STATUS:", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = NaturalOliveMuted)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    UserStatus.values().forEach { status ->
                        val isChosen = selectedStatus == status
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .border(1.dp, if (isChosen) NaturalMossPrimary else NaturalCardBorder, RoundedCornerShape(8.dp))
                                .clickable { selectedStatus = status },
                            color = if (isChosen) NaturalSageContainer else NaturalBackground
                        ) {
                            Box(modifier = Modifier.padding(vertical = 6.dp), contentAlignment = Alignment.Center) {
                                Text(
                                    text = status.label,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NaturalTextHeading
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        if (name.isNotBlank() && email.isNotBlank()) {
                            val updatedUser = user.copy(
                                name = name.trim(),
                                email = email.trim(),
                                phone = phone.trim(),
                                designation = designation.ifBlank { selectedRole.displayName }.trim(),
                                clinicalNotes = clinicalNotes.trim(),
                                role = selectedRole,
                                prakriti = selectedPrakriti,
                                status = selectedStatus
                            )
                            onConfirmSave(updatedUser)
                        }
                    },
                    enabled = name.isNotBlank() && email.isNotBlank(),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NaturalMossPrimary,
                        contentColor = Color.White
                    )
                ) {
                    Text("Save User Changes", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun DeleteUserConfirmDialog(
    user: AppUser,
    isCurrentUser: Boolean,
    onDismiss: () -> Unit,
    onConfirmDelete: () -> Unit
) {
    val isRootAdmin = user.name.contains("Jerin", ignoreCase = true) ||
        user.email.equals("sys.jerin@gmail.com", ignoreCase = true) ||
        user.id == "user_admin_jerin"

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = null,
                    tint = if (isRootAdmin || isCurrentUser) NaturalOliveMuted else NaturalTerracotta,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isRootAdmin) "Root Admin Protected" else if (isCurrentUser) "Cannot Delete Self" else "Confirm User Deletion",
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                if (isRootAdmin) {
                    Text(
                        text = "User '${user.name}' (${user.email}) is the Root Chief Administrator and cannot be removed under any circumstances.",
                        fontSize = 12.sp,
                        color = NaturalTextHeading
                    )
                } else if (isCurrentUser) {
                    Text(
                        text = "You cannot delete your own logged-in administrator account.",
                        fontSize = 12.sp,
                        color = NaturalTextHeading
                    )
                } else {
                    Text(
                        text = "Are you sure you want to permanently delete '${user.name}' (${user.email})?",
                        fontSize = 12.sp,
                        color = NaturalTextHeading,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Role: ${user.role.displayName} • Prakriti: ${user.prakriti.displayName}\nThis action will immediately revoke application access and record an audit log event.",
                        fontSize = 11.sp,
                        color = NaturalOliveMuted
                    )
                }
            }
        },
        confirmButton = {
            if (!isRootAdmin && !isCurrentUser) {
                Button(
                    onClick = onConfirmDelete,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NaturalTerracotta,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("confirm_delete_user_button")
                ) {
                    Text("Delete Account", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(if (isRootAdmin || isCurrentUser) "Close" else "Cancel", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        },
        shape = RoundedCornerShape(20.dp),
        containerColor = NaturalCardSurface
    )
}

@Composable
private fun AdminInputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String
) {
    Column {
        Text(text = label, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = NaturalOliveMuted)
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(NaturalBackground)
                .border(1.dp, NaturalCardBorder, RoundedCornerShape(10.dp))
                .padding(horizontal = 12.dp, vertical = 10.dp)
        ) {
            if (value.isEmpty()) {
                Text(text = placeholder, fontSize = 11.sp, color = NaturalOliveMuted.copy(alpha = 0.6f))
            }
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                singleLine = true,
                textStyle = TextStyle(fontSize = 12.sp, color = NaturalTextHeading, fontWeight = FontWeight.Normal),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

