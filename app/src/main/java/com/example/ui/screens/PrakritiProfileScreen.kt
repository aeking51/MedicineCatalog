package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.RadioButtonChecked
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.AppUser
import com.example.data.model.DoshaType
import com.example.data.model.UserRole
import com.example.ui.AyurvedaUiState
import com.example.ui.theme.NaturalBackground
import com.example.ui.theme.NaturalCardBorder
import com.example.ui.theme.NaturalCardSurface
import com.example.ui.theme.NaturalEarthGold
import com.example.ui.theme.NaturalMossDark
import com.example.ui.theme.NaturalMossPrimary
import com.example.ui.theme.NaturalOliveMuted
import com.example.ui.theme.NaturalParchmentBorder
import com.example.ui.theme.NaturalParchmentContainer
import com.example.ui.theme.NaturalPittaGreen
import com.example.ui.theme.NaturalSageBorder
import com.example.ui.theme.NaturalSageContainer
import com.example.ui.theme.NaturalTerracotta
import com.example.ui.theme.NaturalTextHeading
import com.example.ui.theme.NaturalTextPrimary
import com.example.ui.theme.AppThemeMode
import com.example.ui.theme.LocalAppThemeMode

private data class ProfileThemeColors(
    val cardBg: Color,
    val cardBorder: Color,
    val headingColor: Color,
    val primaryTextColor: Color,
    val mutedTextColor: Color,
    val innerTileBg: Color,
    val innerTileBorder: Color,
    val isGlass: Boolean,
    val isDark: Boolean,
    val textShadow: Shadow? = null,
    val headerShadow: Shadow? = null
)

@Composable
private fun getProfileThemeColors(): ProfileThemeColors {
    val themeMode = LocalAppThemeMode.current
    return when (themeMode) {
        AppThemeMode.DARK -> ProfileThemeColors(
            cardBg = Color(0xFF1C221B),
            cardBorder = Color(0xFF2E382C),
            headingColor = Color(0xFFF2F7EF),
            primaryTextColor = Color(0xFFEDEFEA),
            mutedTextColor = Color(0xFFA8B4A4),
            innerTileBg = Color(0xFF252D24),
            innerTileBorder = Color(0xFF333E31),
            isGlass = false,
            isDark = true,
            textShadow = Shadow(
                color = Color(0x66000000),
                offset = Offset(0f, 1f),
                blurRadius = 2f
            ),
            headerShadow = null
        )
        AppThemeMode.GLASS -> ProfileThemeColors(
            cardBg = Color(0xEBFFFFFF),       // 92% Luminous Frosted Crystal Glass
            cardBorder = Color(0xF5FFFFFF),   // Specular Diamond White Glass Rim
            headingColor = Color(0xFF03190E), // Deep Obsidian Emerald for maximal contrast & readability
            primaryTextColor = Color(0xFF072416), // Deep Botanical Charcoal
            mutedTextColor = Color(0xFF1B432E),   // Crisp Dark Forest Pine
            innerTileBg = Color(0xCCF1F8F4),  // Frosted Mint-White Inner Surface
            innerTileBorder = Color(0xE6FFFFFF), // Specular Inner Rim
            isGlass = true,
            isDark = false,
            textShadow = Shadow(
                color = Color(0x80FFFFFF),
                offset = Offset(0f, 1f),
                blurRadius = 3f
            ),
            headerShadow = Shadow(
                color = Color(0xD9000000),
                offset = Offset(0f, 2f),
                blurRadius = 6f
            )
        )
        AppThemeMode.LIGHT -> ProfileThemeColors(
            cardBg = NaturalCardSurface,
            cardBorder = NaturalCardBorder,
            headingColor = NaturalTextHeading,
            primaryTextColor = NaturalTextPrimary,
            mutedTextColor = NaturalOliveMuted,
            innerTileBg = NaturalBackground,
            innerTileBorder = NaturalCardBorder,
            isGlass = false,
            isDark = false,
            textShadow = null,
            headerShadow = null
        )
    }
}

@Composable
fun PrakritiProfileScreen(
    uiState: AyurvedaUiState,
    onAnswerQuestion: ((Int, DoshaType) -> Unit)? = null,
    onSwitchUserClicked: () -> Unit = {},
    onNavigateToAdmin: () -> Unit = {},
    onLogout: () -> Unit = {},
    onUpdateProfile: (name: String, designation: String, phone: String, notes: String) -> Unit = { _, _, _, _ -> },
    onChangePassword: (currentPass: String, newPass: String, confirmPass: String, onResult: (Boolean, String) -> Unit) -> Unit = { _, _, _, cb -> cb(true, "") },
    onThemeSelected: (AppThemeMode) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val user = uiState.currentUser
    val canAccessAdmin = user.role == UserRole.ADMIN || user.role == UserRole.PRACTITIONER
    val themeColors = getProfileThemeColors()

    var showEditProfileDialog by remember { mutableStateOf(false) }
    var showChangePasswordDialog by remember { mutableStateOf(false) }
    var showLogoutConfirmation by remember { mutableStateOf(false) }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 840.dp)
                .background(if (themeColors.isGlass) Color.Transparent else if (themeColors.isDark) Color(0xFF131713) else NaturalBackground)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Screen Header
        item {
            Column(modifier = Modifier.padding(top = 8.dp)) {
                Text(
                    text = "ACCOUNT & SETTINGS",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.4.sp,
                    style = TextStyle(shadow = themeColors.headerShadow),
                    color = if (themeColors.isGlass) Color(0xFFD1FAE5) else themeColors.mutedTextColor
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "User Profile",
                    fontFamily = FontFamily.Serif,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    style = TextStyle(shadow = themeColors.headerShadow),
                    color = if (themeColors.isGlass) Color.White else themeColors.headingColor
                )
            }
        }

        // Primary User Profile Card
        item {
            UserProfileHeroCard(
                user = user,
                themeColors = themeColors,
                onEditProfileClick = { showEditProfileDialog = true }
            )
        }

        // Guest Limitations & Upgrade Card (Only shown in Guest Mode)
        if (user.role == UserRole.GUEST) {
            item {
                GuestLimitationsCard(
                    themeColors = themeColors,
                    onCreateAccountClick = { showLogoutConfirmation = true }
                )
            }
        }

        // Visual Theme Selection Card (Classic Light, Glass Theme, Dark Theme)
        item {
            ThemeSelectionCard(
                currentTheme = uiState.appThemeMode,
                themeColors = themeColors,
                onThemeSelected = onThemeSelected
            )
        }

        // Account Quick Actions (Edit Profile, Change Password, Admin Dashboard)
        item {
            ActionButtonsSection(
                user = user,
                canAccessAdmin = canAccessAdmin,
                themeColors = themeColors,
                onEditProfile = { showEditProfileDialog = true },
                onChangePassword = { showChangePasswordDialog = true },
                onNavigateToAdmin = onNavigateToAdmin,
                onLogout = { showLogoutConfirmation = true }
            )
        }

        // Profile Details & Clinical Information
        item {
            ProfileDetailsCard(user = user, themeColors = themeColors)
        }

        // Security & Session Card
        item {
            SecurityAndSessionCard(
                user = user,
                themeColors = themeColors,
                onChangePassword = { showChangePasswordDialog = true },
                onLogoutClick = { showLogoutConfirmation = true }
            )
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
    }

    // Modal: Edit Profile Dialog
    if (showEditProfileDialog) {
        EditProfileDialog(
            currentUser = user,
            onDismiss = { showEditProfileDialog = false },
            onSave = { name, designation, phone, notes ->
                onUpdateProfile(name, designation, phone, notes)
                showEditProfileDialog = false
            }
        )
    }

    // Modal: Change Password Dialog
    if (showChangePasswordDialog) {
        ChangePasswordDialog(
            onDismiss = { showChangePasswordDialog = false },
            onSubmitPasswordChange = { currentPass, newPass, confirmPass, callback ->
                onChangePassword(currentPass, newPass, confirmPass) { success, msg ->
                    callback(success, msg)
                    if (success) {
                        showChangePasswordDialog = false
                    }
                }
            }
        )
    }

    // Modal: Logout Confirmation
    if (showLogoutConfirmation) {
        AlertDialog(
            onDismissRequest = { showLogoutConfirmation = false },
            title = {
                Text(
                    text = "Confirm Sign Out",
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    color = NaturalTextHeading
                )
            },
            text = {
                Text(
                    text = "Are you sure you want to sign out of your account?",
                    fontSize = 13.sp,
                    color = NaturalTextPrimary
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showLogoutConfirmation = false
                        onLogout()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NaturalTerracotta,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Sign Out", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutConfirmation = false }) {
                    Text("Cancel", color = NaturalOliveMuted)
                }
            },
            shape = RoundedCornerShape(20.dp),
            containerColor = NaturalCardSurface
        )
    }
}

// -------------------------------------------------------------
// Component: User Profile Hero Card
// -------------------------------------------------------------

@Composable
private fun UserProfileHeroCard(
    user: AppUser,
    themeColors: ProfileThemeColors,
    onEditProfileClick: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("user_profile_hero_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = themeColors.cardBg),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = if (themeColors.isGlass) 4.dp else 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, themeColors.cardBorder, RoundedCornerShape(24.dp))
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Avatar Badge with User Initials & Role Indicator
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(if (themeColors.isDark) Color(0xFF2A3727) else NaturalSageContainer)
                        .border(1.5.dp, if (themeColors.isDark) Color(0xFFA7C957) else NaturalMossPrimary, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = user.name.split(" ")
                            .mapNotNull { it.firstOrNull()?.toString() }
                            .take(2)
                            .joinToString("")
                            .ifEmpty { user.role.iconEmoji },
                        fontFamily = FontFamily.Serif,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (themeColors.isDark) Color(0xFFA7C957) else NaturalMossDark
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = user.name,
                            fontFamily = FontFamily.Serif,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = themeColors.headingColor
                        )
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = user.designation.ifEmpty { user.role.displayName },
                        fontSize = 12.sp,
                        color = themeColors.mutedTextColor,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Role Pill
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (themeColors.isDark) Color(0xFF252F23) else NaturalMossPrimary.copy(alpha = 0.12f))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "${user.role.iconEmoji} ${user.role.badgeLabel}",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (themeColors.isDark) Color(0xFFDCE5D1) else NaturalMossDark
                            )
                        }

                        // Status Pill
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(NaturalPittaGreen.copy(alpha = 0.15f))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "● ACTIVE",
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = NaturalPittaGreen
                            )
                        }
                    }
                }

                if (user.role != UserRole.GUEST) {
                    IconButton(
                        onClick = onEditProfileClick,
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(themeColors.innerTileBg)
                            .border(1.dp, themeColors.cardBorder, CircleShape)
                            .testTag("profile_edit_icon_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Profile",
                            tint = if (themeColors.isDark) Color(0xFFA7C957) else NaturalMossPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// Component: Action Buttons (Edit Profile, Change Password, Admin)
// -------------------------------------------------------------

@Composable
private fun ActionButtonsSection(
    user: AppUser,
    canAccessAdmin: Boolean,
    themeColors: ProfileThemeColors,
    onEditProfile: () -> Unit,
    onChangePassword: () -> Unit,
    onNavigateToAdmin: () -> Unit,
    onLogout: () -> Unit = {}
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        if (user.role == UserRole.GUEST) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = onLogout,
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (themeColors.isDark) Color(0xFFA7C957) else NaturalMossPrimary,
                        contentColor = if (themeColors.isDark) Color(0xFF141913) else Color.White
                    ),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("guest_register_button")
                ) {
                    Text(
                        text = "✨ Create Account",
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Button(
                    onClick = onLogout,
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (themeColors.isGlass) Color(0xF5FFFFFF) else if (themeColors.isDark) Color(0xFF252D24) else Color.White,
                        contentColor = if (themeColors.isDark) Color(0xFFF2F7EF) else Color(0xFF0F382C)
                    ),
                    border = BorderStroke(
                        1.dp,
                        if (themeColors.isGlass) Color(0xF5FFFFFF) else if (themeColors.isDark) Color(0xFF333E31) else NaturalCardBorder
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = if (themeColors.isGlass) 3.dp else 1.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("guest_exit_mode_button")
                ) {
                    Text(
                        text = "Sign In / Switch",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (themeColors.isDark) Color(0xFFF2F7EF) else Color(0xFF0F382C)
                    )
                }
            }
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Edit Profile Button
                Button(
                    onClick = onEditProfile,
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (themeColors.isDark) Color(0xFFA7C957) else NaturalMossPrimary,
                        contentColor = if (themeColors.isDark) Color(0xFF141913) else Color.White
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = if (themeColors.isGlass) 3.dp else 1.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("profile_edit_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Edit Profile",
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Change Password Button
                Button(
                    onClick = onChangePassword,
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (themeColors.isGlass) Color(0xF5FFFFFF) else if (themeColors.isDark) Color(0xFF252D24) else Color.White,
                        contentColor = if (themeColors.isDark) Color(0xFFF2F7EF) else Color(0xFF0F382C)
                    ),
                    border = BorderStroke(
                        1.dp,
                        if (themeColors.isGlass) Color(0xF5FFFFFF) else if (themeColors.isDark) Color(0xFF333E31) else NaturalCardBorder
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = if (themeColors.isGlass) 3.dp else 1.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("profile_change_password_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Key,
                        contentDescription = null,
                        tint = if (themeColors.isDark) Color(0xFFA7C957) else Color(0xFFC07D1C),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Change Password",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (themeColors.isDark) Color(0xFFF2F7EF) else Color(0xFF0F382C)
                    )
                }
            }
        }

        // Admin Dashboard Link (if privileged)
        if (canAccessAdmin) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .border(
                        1.dp,
                        if (themeColors.isDark) Color(0xFF333E31) else if (themeColors.isGlass) themeColors.cardBorder else NaturalSageBorder,
                        RoundedCornerShape(14.dp)
                    ),
                color = if (themeColors.isDark) Color(0xFF252D24) else if (themeColors.isGlass) themeColors.cardBg else NaturalSageContainer.copy(alpha = 0.4f)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AdminPanelSettings,
                            contentDescription = null,
                            tint = if (themeColors.isDark) Color(0xFFA7C957) else NaturalMossDark,
                            modifier = Modifier.size(20.dp)
                        )
                        Column {
                            Text(
                                text = "Ayurvedic Clinical Console",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = themeColors.headingColor
                            )
                            Text(
                                text = "Master catalogue, remedies & team permissions",
                                fontSize = 10.5.sp,
                                color = themeColors.mutedTextColor
                            )
                        }
                    }

                    Button(
                        onClick = onNavigateToAdmin,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (themeColors.isDark) Color(0xFFA7C957) else NaturalMossDark,
                            contentColor = if (themeColors.isDark) Color(0xFF141913) else Color.White
                        ),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier
                            .height(34.dp)
                            .testTag("profile_admin_button")
                    ) {
                        Text("Open", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// Component: Theme Selection Card (Light, Glass, Dark)
// -------------------------------------------------------------

@Composable
private fun ThemeSelectionCard(
    currentTheme: AppThemeMode,
    themeColors: ProfileThemeColors,
    onThemeSelected: (AppThemeMode) -> Unit
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("profile_theme_selection_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = themeColors.cardBg),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = if (themeColors.isGlass) 4.dp else 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, themeColors.cardBorder, RoundedCornerShape(20.dp))
                .padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "INTERFACE & AMBIANCE",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = NaturalEarthGold,
                        letterSpacing = 1.2.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Visual Theme",
                        fontFamily = FontFamily.Serif,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = themeColors.headingColor
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = when (currentTheme) {
                        AppThemeMode.DARK -> Color(0xFF2A3727)
                        AppThemeMode.GLASS -> Color(0x3352B788)
                        AppThemeMode.LIGHT -> NaturalSageContainer
                    }
                ) {
                    Text(
                        text = currentTheme.displayName.uppercase(),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = when (currentTheme) {
                            AppThemeMode.DARK -> Color(0xFFA7C957)
                            AppThemeMode.GLASS -> Color(0xFF1B4332)
                            AppThemeMode.LIGHT -> NaturalMossDark
                        },
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Text(
                text = "Choose your aesthetic ambiance for clinic consultations, herbal studies, or nocturnal formulation review.",
                fontSize = 11.5.sp,
                color = themeColors.mutedTextColor,
                modifier = Modifier.padding(top = 4.dp, bottom = 14.dp)
            )

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                AppThemeMode.values().forEach { mode ->
                    val isSelected = currentTheme == mode

                    val tileBg = when {
                        isSelected && mode == AppThemeMode.DARK -> Color(0xFF252F23)
                        isSelected && mode == AppThemeMode.GLASS -> Color(0x3310B981)
                        isSelected && mode == AppThemeMode.LIGHT -> NaturalSageContainer.copy(alpha = 0.7f)
                        themeColors.isDark -> Color(0xFF161B15)
                        themeColors.isGlass -> Color(0xCCF1F8F4)
                        else -> NaturalBackground
                    }

                    val tileBorder = when {
                        isSelected && mode == AppThemeMode.DARK -> Color(0xFFA7C957)
                        isSelected && mode == AppThemeMode.GLASS -> Color(0xFF10B981)
                        isSelected && mode == AppThemeMode.LIGHT -> NaturalMossPrimary
                        themeColors.isDark -> Color(0xFF2B3329)
                        themeColors.isGlass -> Color(0xE6FFFFFF)
                        else -> NaturalCardBorder.copy(alpha = 0.7f)
                    }

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .clickable { onThemeSelected(mode) }
                            .testTag("theme_option_${mode.name.lowercase()}"),
                        shape = RoundedCornerShape(14.dp),
                        color = tileBg,
                        border = androidx.compose.foundation.BorderStroke(if (isSelected) 1.5.dp else 1.dp, tileBorder)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = when (mode) {
                                        AppThemeMode.LIGHT -> Color(0xFFF1E7D0)
                                        AppThemeMode.GLASS -> Color(0xFFD8F3DC)
                                        AppThemeMode.DARK -> Color(0xFF2F3B2C)
                                    },
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = when (mode) {
                                                AppThemeMode.LIGHT -> Icons.Default.LightMode
                                                AppThemeMode.GLASS -> Icons.Default.AutoAwesome
                                                AppThemeMode.DARK -> Icons.Default.DarkMode
                                            },
                                            contentDescription = null,
                                            tint = when (mode) {
                                                AppThemeMode.LIGHT -> NaturalTerracotta
                                                AppThemeMode.GLASS -> Color(0xFF10B981)
                                                AppThemeMode.DARK -> Color(0xFFA7C957)
                                            },
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = mode.displayName,
                                            fontSize = 13.5.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                                            color = themeColors.headingColor
                                        )
                                        if (isSelected) {
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = "• ACTIVE",
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = when (mode) {
                                                    AppThemeMode.DARK -> Color(0xFFA7C957)
                                                    AppThemeMode.GLASS -> Color(0xFF10B981)
                                                    AppThemeMode.LIGHT -> NaturalMossPrimary
                                                }
                                            )
                                        }
                                    }
                                    Text(
                                        text = mode.description,
                                        fontSize = 11.sp,
                                        color = themeColors.mutedTextColor,
                                        lineHeight = 14.sp
                                    )
                                }
                            }

                            Icon(
                                imageVector = if (isSelected) Icons.Default.RadioButtonChecked else Icons.Default.RadioButtonUnchecked,
                                contentDescription = if (isSelected) "Selected" else "Not selected",
                                tint = if (isSelected) {
                                    when (mode) {
                                        AppThemeMode.DARK -> Color(0xFFA7C957)
                                        AppThemeMode.GLASS -> Color(0xFF10B981)
                                        AppThemeMode.LIGHT -> NaturalMossPrimary
                                    }
                                } else themeColors.mutedTextColor.copy(alpha = 0.5f),
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// Component: Profile Details & Clinical Bio Card
// -------------------------------------------------------------

@Composable
private fun ProfileDetailsCard(
    user: AppUser,
    themeColors: ProfileThemeColors
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("profile_details_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = themeColors.cardBg),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = if (themeColors.isGlass) 3.dp else 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, themeColors.cardBorder, RoundedCornerShape(20.dp))
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "PERSONAL & CONTACT INFORMATION",
                fontSize = 10.5.sp,
                fontWeight = FontWeight.Bold,
                color = themeColors.mutedTextColor,
                letterSpacing = 1.sp
            )

            DetailItemRow(
                icon = Icons.Default.Person,
                label = "Full Name",
                value = user.name,
                themeColors = themeColors
            )

            HorizontalDivider(color = themeColors.cardBorder.copy(alpha = 0.5f))

            DetailItemRow(
                icon = Icons.Default.Email,
                label = "Email Address",
                value = user.email,
                themeColors = themeColors
            )

            HorizontalDivider(color = themeColors.cardBorder.copy(alpha = 0.5f))

            DetailItemRow(
                icon = Icons.Default.Phone,
                label = "Phone Number",
                value = user.phone.ifEmpty { "Not specified" },
                themeColors = themeColors
            )

            HorizontalDivider(color = themeColors.cardBorder.copy(alpha = 0.5f))

            DetailItemRow(
                icon = Icons.Default.Security,
                label = "Account Designation",
                value = user.designation.ifEmpty { "${user.role.displayName} Account" },
                themeColors = themeColors
            )

            if (user.clinicalNotes.isNotBlank()) {
                HorizontalDivider(color = themeColors.cardBorder.copy(alpha = 0.5f))
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Clinical Notes / Bio",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = themeColors.mutedTextColor
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = user.clinicalNotes,
                        fontSize = 12.sp,
                        color = themeColors.primaryTextColor,
                        lineHeight = 16.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun DetailItemRow(
    icon: ImageVector,
    label: String,
    value: String,
    themeColors: ProfileThemeColors
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(themeColors.innerTileBg)
                .border(1.dp, themeColors.cardBorder, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (themeColors.isDark) Color(0xFFA7C957) else NaturalOliveMuted,
                modifier = Modifier.size(16.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                style = TextStyle(shadow = themeColors.textShadow),
                color = themeColors.mutedTextColor
            )
            Text(
                text = value,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                style = TextStyle(shadow = themeColors.textShadow),
                color = themeColors.headingColor
            )
        }
    }
}

// -------------------------------------------------------------
// Component: Security & Session Card
// -------------------------------------------------------------

@Composable
private fun SecurityAndSessionCard(
    user: AppUser,
    themeColors: ProfileThemeColors,
    onChangePassword: () -> Unit,
    onLogoutClick: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("security_session_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = themeColors.cardBg),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = if (themeColors.isGlass) 3.dp else 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, themeColors.cardBorder, RoundedCornerShape(20.dp))
                .padding(18.dp)
        ) {
            Text(
                text = "ACCOUNT SECURITY & SESSION",
                fontSize = 10.5.sp,
                fontWeight = FontWeight.Bold,
                color = themeColors.mutedTextColor,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Password row
            if (user.role == UserRole.GUEST) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(if (themeColors.isDark) Color(0xFF2A3727) else NaturalSageContainer)
                                .border(1.dp, if (themeColors.isDark) Color(0xFF333E31) else NaturalSageBorder, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "🍃", fontSize = 14.sp)
                        }
                        Column {
                            Text(
                                text = "Guest Mode",
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = themeColors.headingColor
                            )
                            Text(
                                text = "No password required",
                                fontSize = 11.sp,
                                color = themeColors.mutedTextColor
                            )
                        }
                    }

                    Button(
                        onClick = onLogoutClick,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (themeColors.isDark) Color(0xFFA7C957) else NaturalMossPrimary,
                            contentColor = if (themeColors.isDark) Color(0xFF141913) else Color.White
                        ),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Text(
                            text = "Register",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(if (themeColors.isDark) Color(0xFF2A3727) else NaturalParchmentContainer)
                                .border(1.dp, if (themeColors.isDark) Color(0xFF333E31) else NaturalParchmentBorder, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                tint = NaturalEarthGold,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "Account Password",
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = themeColors.headingColor
                            )
                            Text(
                                text = "••••••••",
                                fontSize = 11.sp,
                                color = themeColors.mutedTextColor
                            )
                        }
                    }

                    TextButton(
                        onClick = onChangePassword,
                        modifier = Modifier.testTag("profile_change_password_textbutton")
                    ) {
                        Text(
                            text = "Update",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (themeColors.isDark) Color(0xFFA7C957) else NaturalMossPrimary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = themeColors.cardBorder.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(12.dp))

            // Sign out row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(NaturalTerracotta.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Logout,
                            contentDescription = null,
                            tint = NaturalTerracotta,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "Active Session",
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = themeColors.headingColor
                        )
                        Text(
                            text = "Signed in as ${user.email}",
                            fontSize = 11.sp,
                            color = themeColors.mutedTextColor
                        )
                    }
                }

                OutlinedButton(
                    onClick = onLogoutClick,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = NaturalTerracotta
                    ),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier
                        .height(34.dp)
                        .testTag("profile_logout_button")
                ) {
                    Text("Sign Out", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// -------------------------------------------------------------
// Dialog: Edit Profile
// -------------------------------------------------------------

@Composable
private fun EditProfileDialog(
    currentUser: AppUser,
    onDismiss: () -> Unit,
    onSave: (name: String, designation: String, phone: String, notes: String) -> Unit
) {
    var name by remember { mutableStateOf(currentUser.name) }
    var designation by remember { mutableStateOf(currentUser.designation) }
    var phone by remember { mutableStateOf(currentUser.phone) }
    var notes by remember { mutableStateOf(currentUser.clinicalNotes) }
    var nameError by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .border(1.dp, NaturalCardBorder, RoundedCornerShape(24.dp))
                .testTag("edit_profile_dialog"),
            color = NaturalCardSurface,
            shadowElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "EDIT PROFILE",
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = NaturalOliveMuted,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "Update Information",
                            fontFamily = FontFamily.Serif,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = NaturalTextHeading
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = NaturalOliveMuted
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Full Name Input
                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                        if (it.isNotBlank()) nameError = false
                    },
                    label = { Text("Full Name *") },
                    isError = nameError,
                    supportingText = if (nameError) {
                        { Text("Name cannot be empty", color = NaturalTerracotta) }
                    } else null,
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("profile_name_input"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NaturalMossPrimary,
                        unfocusedBorderColor = NaturalCardBorder
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Designation Input
                OutlinedTextField(
                    value = designation,
                    onValueChange = { designation = it },
                    label = { Text("Designation / Title") },
                    placeholder = { Text("e.g. Senior Ayurvedic Vaidya") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("profile_designation_input"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NaturalMossPrimary,
                        unfocusedBorderColor = NaturalCardBorder
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Phone Input
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Contact Phone Number") },
                    placeholder = { Text("e.g. +91 98450 12345") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("profile_phone_input"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NaturalMossPrimary,
                        unfocusedBorderColor = NaturalCardBorder
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Clinical Notes / Bio Input
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Clinical Notes / Bio") },
                    placeholder = { Text("Specializations, clinic timings, or personal notes...") },
                    minLines = 3,
                    maxLines = 5,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("profile_notes_input"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NaturalMossPrimary,
                        unfocusedBorderColor = NaturalCardBorder
                    )
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = NaturalOliveMuted)
                    ) {
                        Text("Cancel")
                    }

                    Button(
                        onClick = {
                            if (name.trim().isEmpty()) {
                                nameError = true
                            } else {
                                onSave(name.trim(), designation.trim(), phone.trim(), notes.trim())
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp)
                            .testTag("profile_save_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = NaturalMossPrimary,
                            contentColor = Color.White
                        )
                    ) {
                        Text("Save Changes", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// Dialog: Change Password
// -------------------------------------------------------------

@Composable
private fun ChangePasswordDialog(
    onDismiss: () -> Unit,
    onSubmitPasswordChange: (currentPass: String, newPass: String, confirmPass: String, callback: (Boolean, String) -> Unit) -> Unit
) {
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var newPassVisible by remember { mutableStateOf(false) }
    var confirmPassVisible by remember { mutableStateOf(false) }

    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isSubmitting by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .border(1.dp, NaturalCardBorder, RoundedCornerShape(24.dp))
                .testTag("change_password_dialog"),
            color = NaturalCardSurface,
            shadowElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "SECURITY CREDENTIALS",
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = NaturalOliveMuted,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "Change Password",
                            fontFamily = FontFamily.Serif,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = NaturalTextHeading
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = NaturalOliveMuted
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Set a new password for your account. You do not need to enter your current password.",
                    fontSize = 12.sp,
                    color = NaturalOliveMuted
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Error Banner
                if (errorMessage != null) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .border(1.dp, NaturalTerracotta.copy(alpha = 0.5f), RoundedCornerShape(10.dp)),
                        color = NaturalTerracotta.copy(alpha = 0.1f)
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = NaturalTerracotta,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = errorMessage ?: "",
                                fontSize = 11.5.sp,
                                color = NaturalTerracotta,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }

                // New Password Field
                OutlinedTextField(
                    value = newPassword,
                    onValueChange = {
                        newPassword = it
                        errorMessage = null
                    },
                    label = { Text("New Password *") },
                    supportingText = { Text("Must be at least 6 characters", fontSize = 10.sp, color = NaturalOliveMuted) },
                    singleLine = true,
                    visualTransformation = if (newPassVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    trailingIcon = {
                        IconButton(onClick = { newPassVisible = !newPassVisible }) {
                            Icon(
                                imageVector = if (newPassVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = if (newPassVisible) "Hide password" else "Show password",
                                tint = NaturalOliveMuted,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("password_new_input"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NaturalMossPrimary,
                        unfocusedBorderColor = NaturalCardBorder
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Confirm Password Field
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = {
                        confirmPassword = it
                        errorMessage = null
                    },
                    label = { Text("Confirm New Password *") },
                    singleLine = true,
                    visualTransformation = if (confirmPassVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    trailingIcon = {
                        IconButton(onClick = { confirmPassVisible = !confirmPassVisible }) {
                            Icon(
                                imageVector = if (confirmPassVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = if (confirmPassVisible) "Hide password" else "Show password",
                                tint = NaturalOliveMuted,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("password_confirm_input"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NaturalMossPrimary,
                        unfocusedBorderColor = NaturalCardBorder
                    )
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = NaturalOliveMuted)
                    ) {
                        Text("Cancel")
                    }

                    Button(
                        onClick = {
                            if (newPassword.trim().length < 6) {
                                errorMessage = "New password must be at least 6 characters."
                                return@Button
                            }
                            if (newPassword.trim() != confirmPassword.trim()) {
                                errorMessage = "New password and confirmation do not match."
                                return@Button
                            }

                            isSubmitting = true
                            errorMessage = null
                            onSubmitPasswordChange(
                                "",
                                newPassword.trim(),
                                confirmPassword.trim()
                            ) { success, msg ->
                                isSubmitting = false
                                if (!success) {
                                    errorMessage = msg
                                }
                            }
                        },
                        enabled = !isSubmitting,
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp)
                            .testTag("password_submit_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = NaturalMossPrimary,
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            text = if (isSubmitting) "Updating..." else "Update Password",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

/**
 * Guest Limitations & Account Upgrade Card
 * Clearly details active read-only privileges and locked features,
 * with a single-click bridge to create a full account or sign in.
 */
@Composable
private fun GuestLimitationsCard(
    themeColors: ProfileThemeColors,
    onCreateAccountClick: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("guest_limitations_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = themeColors.cardBg),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = if (themeColors.isGlass) 3.dp else 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.2.dp, if (themeColors.isDark) Color(0xFF3B4834) else if (themeColors.isGlass) themeColors.cardBorder else NaturalSageBorder, RoundedCornerShape(20.dp))
                .padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(if (themeColors.isDark) Color(0xFF2E3D2B) else NaturalSageContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "🍃", fontSize = 18.sp)
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Guest Access Privileges",
                            fontFamily = FontFamily.Serif,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = themeColors.headingColor
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(NaturalEarthGold.copy(alpha = 0.2f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "LIMITED",
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold,
                                color = NaturalEarthGold
                            )
                        }
                    }
                    Text(
                        text = "Read-only access to classical formulations",
                        fontSize = 11.sp,
                        color = themeColors.mutedTextColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "✓ UNLOCKED FOR GUESTS",
                fontSize = 10.sp,
                fontWeight = FontWeight.ExtraBold,
                color = if (themeColors.isDark) Color(0xFFA7C957) else NaturalMossDark,
                letterSpacing = 0.8.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "• Browse classical formulation monographs & therapeutic indications\n• Search herbs by Sanskrit botanical source, rasas, and actions\n• Filter by dosha balance (Vata, Pitta, Kapha) & preparation form",
                fontSize = 11.sp,
                color = themeColors.primaryTextColor,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "🔒 RESTRICTED IN GUEST MODE",
                fontSize = 10.sp,
                fontWeight = FontWeight.ExtraBold,
                color = NaturalTerracotta,
                letterSpacing = 0.8.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "• Ayurvedic Practitioner Consultations & Dosha Diagnosis\n• Personal Daily Regimen & Adherence Logging\n• Formulation Management & Inventory Control Console\n• Persistent Profile Sync across devices",
                fontSize = 11.sp,
                color = themeColors.mutedTextColor,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            Button(
                onClick = onCreateAccountClick,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (themeColors.isDark) Color(0xFFA7C957) else NaturalMossPrimary,
                    contentColor = if (themeColors.isDark) Color(0xFF141913) else Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(42.dp)
                    .testTag("guest_upgrade_account_button")
            ) {
                Text(
                    text = "Create Free Account / Sign In for Full Access",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
