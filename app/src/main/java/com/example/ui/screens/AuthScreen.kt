package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.AppUser
import com.example.data.model.DoshaType
import com.example.data.model.UserRole
import com.example.ui.AuthMode
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
import com.example.ui.theme.NaturalSageBorder
import com.example.ui.theme.NaturalSageContainer
import com.example.ui.theme.NaturalTerracotta
import com.example.ui.theme.NaturalTextHeading
import com.example.ui.theme.NaturalTextPrimary

@Composable
fun AuthScreen(
    uiState: AyurvedaUiState,
    onSetAuthMode: (AuthMode) -> Unit,
    onLogin: (String, String) -> Unit,
    onQuickLoginAs: (AppUser) -> Unit,
    onGuestLogin: () -> Unit = {},
    onSignup: (String, String, String, UserRole, DoshaType, String) -> Unit,
    onRequestReset: (String) -> Unit,
    onCompleteReset: (String, String, String) -> Unit,
    onDismissError: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(NaturalBackground)
            .statusBarsPadding()
            .verticalScroll(scrollState)
            .padding(horizontal = 24.dp, vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // App Identity & Sacred Crest
        AuthHeaderSection(
            mode = uiState.authMode,
            onBackToLogin = { onSetAuthMode(AuthMode.LOGIN) }
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Error / Success Feedback Banners
        AnimatedVisibility(
            visible = uiState.authErrorMessage != null,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            uiState.authErrorMessage?.let { err ->
                AuthNotificationCard(
                    text = err,
                    isError = true,
                    onDismiss = onDismissError
                )
            }
        }

        AnimatedVisibility(
            visible = uiState.authSuccessMessage != null,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            uiState.authSuccessMessage?.let { msg ->
                AuthNotificationCard(
                    text = msg,
                    isError = false,
                    onDismiss = onDismissError
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Main Auth Card Container
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(26.dp))
                .border(1.dp, NaturalCardBorder, RoundedCornerShape(26.dp)),
            color = NaturalCardSurface,
            shadowElevation = 3.dp
        ) {
            Column(modifier = Modifier.padding(22.dp)) {
                when (uiState.authMode) {
                    AuthMode.LOGIN -> {
                        LoginFormContent(
                            allUsers = uiState.allUsers,
                            onLogin = onLogin,
                            onQuickLoginAs = onQuickLoginAs,
                            onGuestLogin = onGuestLogin,
                            onSwitchToSignup = { onSetAuthMode(AuthMode.SIGNUP) },
                            onSwitchToForgotPassword = { onSetAuthMode(AuthMode.FORGOT_PASSWORD) }
                        )
                    }

                    AuthMode.SIGNUP -> {
                        SignupFormContent(
                            onSignup = onSignup,
                            onSwitchToLogin = { onSetAuthMode(AuthMode.LOGIN) }
                        )
                    }

                    AuthMode.FORGOT_PASSWORD -> {
                        ForgotPasswordRequestContent(
                            onRequestReset = onRequestReset,
                            onBackToLogin = { onSetAuthMode(AuthMode.LOGIN) }
                        )
                    }

                    AuthMode.OTP_RESET -> {
                        ResetPasswordVerifyContent(
                            pendingEmail = uiState.pendingResetEmail,
                            generatedOtp = uiState.generatedOtpCode ?: "",
                            onCompleteReset = onCompleteReset,
                            onResendOtp = { onRequestReset(uiState.pendingResetEmail) },
                            onBackToLogin = { onSetAuthMode(AuthMode.LOGIN) }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Natural Philosophy Footer
        Text(
            text = "Ayurveda • The Science of Life & Longevity",
            fontSize = 11.sp,
            color = NaturalOliveMuted,
            letterSpacing = 0.8.sp
        )
        Spacer(modifier = Modifier.height(16.dp))
    }
}

// -------------------------------------------------------------
// Auth Header
// -------------------------------------------------------------

@Composable
private fun AuthHeaderSection(
    mode: AuthMode,
    onBackToLogin: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        if (mode == AuthMode.FORGOT_PASSWORD || mode == AuthMode.OTP_RESET) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = onBackToLogin,
                    colors = ButtonDefaults.textButtonColors(contentColor = NaturalMossPrimary),
                    contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back to Login",
                        tint = NaturalMossDark,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Back to Sign In",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = NaturalMossPrimary
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Emblem
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(NaturalSageContainer)
                .border(2.dp, NaturalEarthGold.copy(alpha = 0.8f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.sitaram_luxury_logo),
                contentDescription = "Sitaram Ayurveda Official Logo",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .size(68.dp)
                    .clip(CircleShape)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "AYURGUIDE",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 2.2.sp,
            color = NaturalOliveMuted
        )

        Text(
            text = "Sanctuary of Healing",
            fontFamily = FontFamily.Serif,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = NaturalTextHeading
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "सर्वे सन्तु निरामयाः • May All Be Free From Ailment",
            fontSize = 10.sp,
            color = NaturalEarthGold,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 0.5.sp
        )
    }
}

// -------------------------------------------------------------
// 1. Login Form Content
// -------------------------------------------------------------

@Composable
private fun LoginFormContent(
    allUsers: List<AppUser>,
    onLogin: (String, String) -> Unit,
    onQuickLoginAs: (AppUser) -> Unit,
    onGuestLogin: () -> Unit = {},
    onSwitchToSignup: () -> Unit,
    onSwitchToForgotPassword: () -> Unit
) {
    var email by remember { mutableStateOf("arjun.m@example.com") }
    var password by remember { mutableStateOf("ayur123") }
    var passwordVisible by remember { mutableStateOf(false) }
    var rememberMe by remember { mutableStateOf(true) }

    Column {
        // Sign In / Sign Up Mode Switch Tabs
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(NaturalBackground)
                .padding(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(NaturalCardSurface)
                    .border(1.dp, NaturalCardBorder, RoundedCornerShape(10.dp))
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Sign In",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = NaturalTextHeading
                )
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .clickable(onClick = onSwitchToSignup)
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Create Account",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = NaturalOliveMuted
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Email Field
        Text(
            text = "EMAIL ADDRESS",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.1.sp,
            color = NaturalOliveMuted
        )
        Spacer(modifier = Modifier.height(6.dp))
        AuthInputField(
            value = email,
            onValueChange = { email = it },
            placeholder = "e.g. arjun.m@example.com",
            leadingIcon = {
                Icon(Icons.Default.Email, contentDescription = null, tint = NaturalOliveMuted, modifier = Modifier.size(18.dp))
            },
            keyboardType = KeyboardType.Email,
            testTag = "login_email_input"
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Password Field
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "PASSWORD",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.1.sp,
                color = NaturalOliveMuted
            )

            TextButton(
                onClick = onSwitchToForgotPassword,
                colors = ButtonDefaults.textButtonColors(contentColor = NaturalMossPrimary),
                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp),
                modifier = Modifier.testTag("login_forgot_password_btn")
            ) {
                Text(
                    text = "Forgot Password?",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = NaturalMossPrimary
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))
        AuthInputField(
            value = password,
            onValueChange = { password = it },
            placeholder = "Enter your password",
            leadingIcon = {
                Icon(Icons.Default.Lock, contentDescription = null, tint = NaturalOliveMuted, modifier = Modifier.size(18.dp))
            },
            trailingIcon = {
                IconButton(
                    onClick = { passwordVisible = !passwordVisible },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = "Toggle password",
                        tint = NaturalOliveMuted,
                        modifier = Modifier.size(18.dp)
                    )
                }
            },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardType = KeyboardType.Password,
            testTag = "login_password_input"
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Remember Me Row
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable { rememberMe = !rememberMe }
        ) {
            Checkbox(
                checked = rememberMe,
                onCheckedChange = { rememberMe = it },
                colors = CheckboxDefaults.colors(
                    checkedColor = NaturalMossPrimary,
                    checkmarkColor = Color.White
                )
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "Remember this device for 30 days",
                fontSize = 11.sp,
                color = NaturalTextPrimary.copy(alpha = 0.8f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Sign In Button
        Button(
            onClick = { onLogin(email, password) },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("auth_login_button"),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = NaturalMossPrimary,
                contentColor = Color.White
            )
        ) {
            Text(
                text = "Sign In to Sanctuary",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Divider
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(1.dp)
                    .background(NaturalCardBorder)
            )
            Text(
                text = "OR CONTINUE WITHOUT ACCOUNT",
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = NaturalOliveMuted,
                letterSpacing = 1.1.sp,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(1.dp)
                    .background(NaturalCardBorder)
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Guest Access Card with Limited Access Clarifications
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .border(1.2.dp, NaturalSageBorder, RoundedCornerShape(16.dp))
                .testTag("guest_access_container"),
            color = NaturalSageContainer.copy(alpha = 0.55f)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(NaturalMossPrimary.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "🍃", fontSize = 17.sp)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Guest Access",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = NaturalTextHeading
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(NaturalEarthGold.copy(alpha = 0.2f))
                                    .padding(horizontal = 5.dp, vertical = 1.5.dp)
                            ) {
                                Text(
                                    text = "LIMITED ACCESS",
                                    fontSize = 7.5.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = NaturalEarthGold,
                                    letterSpacing = 0.5.sp
                                )
                            }
                        }
                        Text(
                            text = "Explore classical herbs & remedies immediately",
                            fontSize = 10.5.sp,
                            color = NaturalOliveMuted
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Permissions Comparison Row (Included vs Restricted)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Included
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(NaturalBackground)
                            .border(0.8.dp, NaturalCardBorder, RoundedCornerShape(10.dp))
                            .padding(8.dp)
                    ) {
                        Column {
                            Text(
                                text = "✓ INCLUDED",
                                fontSize = 8.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = NaturalMossDark,
                                letterSpacing = 0.8.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "• Classical Formulations\n• Dosha Indications\n• Botanical Monograph",
                                fontSize = 9.5.sp,
                                color = NaturalTextPrimary.copy(alpha = 0.85f),
                                lineHeight = 13.5.sp
                            )
                        }
                    }

                    // Restricted
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(NaturalBackground)
                            .border(0.8.dp, NaturalCardBorder, RoundedCornerShape(10.dp))
                            .padding(8.dp)
                    ) {
                        Column {
                            Text(
                                text = "🔒 RESTRICTED",
                                fontSize = 8.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = NaturalTerracotta,
                                letterSpacing = 0.8.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "• Clinical Consults\n• Admin Dashboard\n• Personal Regimens",
                                fontSize = 9.5.sp,
                                color = NaturalTextPrimary.copy(alpha = 0.85f),
                                lineHeight = 13.5.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedButton(
                    onClick = onGuestLogin,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(42.dp)
                        .testTag("auth_guest_login_button"),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.2.dp, NaturalMossPrimary),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = NaturalBackground,
                        contentColor = NaturalMossDark
                    )
                ) {
                    Text(
                        text = "Continue as Guest",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.4.sp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "→", fontSize = 13.sp)
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 2. Signup Form Content
// -------------------------------------------------------------

@Composable
private fun SignupFormContent(
    onSignup: (String, String, String, UserRole, DoshaType, String) -> Unit,
    onSwitchToLogin: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var selectedPrakriti by remember { mutableStateOf(DoshaType.PITTA) }
    var designation by remember { mutableStateOf("") }
    var termsAgreed by remember { mutableStateOf(true) }

    Column {
        // Sign In / Sign Up Mode Switch Tabs
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(NaturalBackground)
                .padding(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .clickable(onClick = onSwitchToLogin)
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Sign In",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = NaturalOliveMuted
                )
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(NaturalCardSurface)
                    .border(1.dp, NaturalCardBorder, RoundedCornerShape(10.dp))
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Create Account",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = NaturalTextHeading
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Full Name
        Text(text = "FULL LEGAL NAME", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = NaturalOliveMuted)
        Spacer(modifier = Modifier.height(4.dp))
        AuthInputField(
            value = name,
            onValueChange = { name = it },
            placeholder = "e.g. Dr. Kavita Roy / Devansh Sen",
            leadingIcon = {
                Icon(Icons.Default.Person, contentDescription = null, tint = NaturalOliveMuted, modifier = Modifier.size(18.dp))
            },
            testTag = "signup_name_input"
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Email
        Text(text = "EMAIL ADDRESS", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = NaturalOliveMuted)
        Spacer(modifier = Modifier.height(4.dp))
        AuthInputField(
            value = email,
            onValueChange = { email = it },
            placeholder = "e.g. yourname@example.com",
            leadingIcon = {
                Icon(Icons.Default.Email, contentDescription = null, tint = NaturalOliveMuted, modifier = Modifier.size(18.dp))
            },
            keyboardType = KeyboardType.Email,
            testTag = "signup_email_input"
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Password & Confirm
        Text(text = "PASSWORD", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = NaturalOliveMuted)
        Spacer(modifier = Modifier.height(4.dp))
        AuthInputField(
            value = password,
            onValueChange = { password = it },
            placeholder = "Minimum 4 characters",
            leadingIcon = {
                Icon(Icons.Default.Lock, contentDescription = null, tint = NaturalOliveMuted, modifier = Modifier.size(18.dp))
            },
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }, modifier = Modifier.size(24.dp)) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = null,
                        tint = NaturalOliveMuted,
                        modifier = Modifier.size(18.dp)
                    )
                }
            },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardType = KeyboardType.Password,
            testTag = "signup_password_input"
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(text = "CONFIRM PASSWORD", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = NaturalOliveMuted)
        Spacer(modifier = Modifier.height(4.dp))
        AuthInputField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            placeholder = "Repeat your password",
            leadingIcon = {
                Icon(Icons.Default.Lock, contentDescription = null, tint = NaturalOliveMuted, modifier = Modifier.size(18.dp))
            },
            visualTransformation = PasswordVisualTransformation(),
            keyboardType = KeyboardType.Password,
            testTag = "signup_confirm_password_input"
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Default Account Role: Wellness Seeker
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .border(1.dp, NaturalCardBorder, RoundedCornerShape(12.dp)),
            color = NaturalBackground
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(NaturalSageContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "🧘", fontSize = 18.sp)
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Account Role: Wellness Seeker",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = NaturalTextHeading
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        RoleBadge(role = UserRole.PATIENT)
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "New accounts default to Wellness Seeker. Practitioner & Admin access is designated by Sanctuary Admin.",
                        fontSize = 9.sp,
                        color = NaturalOliveMuted,
                        lineHeight = 13.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Constitution / Prakriti Selector
        Text(text = "PRIMARY PRAKRITI CONSTITUTION", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = NaturalOliveMuted)
        Spacer(modifier = Modifier.height(6.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            DoshaType.values().forEach { dosha ->
                val isSelected = selectedPrakriti == dosha
                Surface(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .border(
                            1.dp,
                            if (isSelected) NaturalMossPrimary else NaturalCardBorder,
                            RoundedCornerShape(12.dp)
                        )
                        .clickable { selectedPrakriti = dosha },
                    color = if (isSelected) NaturalSageContainer else NaturalBackground
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = dosha.symbol, fontSize = 12.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = dosha.displayName,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = NaturalTextHeading
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Title / Designation
        Text(text = "TITLE / AFFILIATION (OPTIONAL)", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = NaturalOliveMuted)
        Spacer(modifier = Modifier.height(4.dp))
        AuthInputField(
            value = designation,
            onValueChange = { designation = it },
            placeholder = "e.g. Wellness Seeker / Holistic Lifestyle",
            testTag = "signup_designation_input"
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Terms agreement
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable { termsAgreed = !termsAgreed }
        ) {
            Checkbox(
                checked = termsAgreed,
                onCheckedChange = { termsAgreed = it },
                colors = CheckboxDefaults.colors(
                    checkedColor = NaturalMossPrimary,
                    checkmarkColor = Color.White
                )
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "I accept Ayurvedic guidelines and Terms of Practice",
                fontSize = 10.sp,
                color = NaturalTextPrimary.copy(alpha = 0.85f)
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Create Account Button
        val canSubmit = name.isNotBlank() && email.isNotBlank() && password.isNotBlank() && password == confirmPassword && termsAgreed
        Button(
            onClick = {
                onSignup(name, email, password, UserRole.PATIENT, selectedPrakriti, designation)
            },
            enabled = canSubmit,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("auth_signup_button"),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = NaturalMossPrimary,
                contentColor = Color.White
            )
        ) {
            Text(
                text = "Create Ayurvedic Account",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// -------------------------------------------------------------
// 3. Forgot Password Request Content
// -------------------------------------------------------------

@Composable
private fun ForgotPasswordRequestContent(
    onRequestReset: (String) -> Unit,
    onBackToLogin: () -> Unit
) {
    var email by remember { mutableStateOf("") }

    Column {
        Text(
            text = "RECOVER CREDENTIALS",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.3.sp,
            color = NaturalOliveMuted
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Reset Your Account Password",
            fontFamily = FontFamily.Serif,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = NaturalTextHeading
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Enter your registered email address below. We will send a secure 6-digit verification code to reset your account password.",
            fontSize = 11.sp,
            color = NaturalTextPrimary.copy(alpha = 0.8f),
            lineHeight = 16.sp
        )

        Spacer(modifier = Modifier.height(18.dp))

        Text(
            text = "REGISTERED EMAIL",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.1.sp,
            color = NaturalOliveMuted
        )
        Spacer(modifier = Modifier.height(6.dp))
        AuthInputField(
            value = email,
            onValueChange = { email = it },
            placeholder = "e.g. arjun.m@example.com",
            leadingIcon = {
                Icon(Icons.Default.Email, contentDescription = null, tint = NaturalOliveMuted, modifier = Modifier.size(18.dp))
            },
            keyboardType = KeyboardType.Email,
            testTag = "forgot_email_input"
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = { onRequestReset(email) },
            enabled = email.isNotBlank(),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("send_recovery_code_button"),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = NaturalMossPrimary,
                contentColor = Color.White
            )
        ) {
            Text(
                text = "Send 6-Digit Recovery Code",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = onBackToLogin,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = NaturalMossPrimary
            )
        ) {
            Text("Cancel & Return to Sign In", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

// -------------------------------------------------------------
// 4. OTP Reset Password Content
// -------------------------------------------------------------

@Composable
private fun ResetPasswordVerifyContent(
    pendingEmail: String,
    generatedOtp: String,
    onCompleteReset: (String, String, String) -> Unit,
    onResendOtp: () -> Unit,
    onBackToLogin: () -> Unit
) {
    var otpInput by remember { mutableStateOf(generatedOtp) }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    Column {
        Text(
            text = "VERIFY IDENTITY & SET NEW PASSWORD",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.2.sp,
            color = NaturalOliveMuted
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Enter Verification Code",
            fontFamily = FontFamily.Serif,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = NaturalTextHeading
        )

        Spacer(modifier = Modifier.height(8.dp))

        // OTP Code Simulation notification
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .border(1.dp, NaturalSageBorder, RoundedCornerShape(14.dp)),
            color = NaturalSageContainer.copy(alpha = 0.5f)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "📲 SIMULATED SMS / EMAIL DISPATCH",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = NaturalMossDark,
                        letterSpacing = 0.8.sp
                    )
                    Text(
                        text = "Valid for 10m",
                        fontSize = 9.sp,
                        color = NaturalOliveMuted
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Code sent to $pendingEmail: OTP = $generatedOtp",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = NaturalTextHeading
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // OTP Input
        Text(text = "6-DIGIT OTP CODE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = NaturalOliveMuted)
        Spacer(modifier = Modifier.height(4.dp))
        AuthInputField(
            value = otpInput,
            onValueChange = { if (it.length <= 6) otpInput = it },
            placeholder = "Enter 6-digit code",
            keyboardType = KeyboardType.Number,
            testTag = "otp_code_input"
        )

        Spacer(modifier = Modifier.height(12.dp))

        // New Password
        Text(text = "NEW PASSWORD", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = NaturalOliveMuted)
        Spacer(modifier = Modifier.height(4.dp))
        AuthInputField(
            value = newPassword,
            onValueChange = { newPassword = it },
            placeholder = "Enter new password",
            leadingIcon = {
                Icon(Icons.Default.Lock, contentDescription = null, tint = NaturalOliveMuted, modifier = Modifier.size(18.dp))
            },
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }, modifier = Modifier.size(24.dp)) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = null,
                        tint = NaturalOliveMuted,
                        modifier = Modifier.size(18.dp)
                    )
                }
            },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardType = KeyboardType.Password,
            testTag = "reset_new_password_input"
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Confirm New Password
        Text(text = "CONFIRM NEW PASSWORD", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = NaturalOliveMuted)
        Spacer(modifier = Modifier.height(4.dp))
        AuthInputField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            placeholder = "Repeat new password",
            leadingIcon = {
                Icon(Icons.Default.Lock, contentDescription = null, tint = NaturalOliveMuted, modifier = Modifier.size(18.dp))
            },
            visualTransformation = PasswordVisualTransformation(),
            keyboardType = KeyboardType.Password,
            testTag = "reset_confirm_password_input"
        )

        Spacer(modifier = Modifier.height(18.dp))

        val canReset = otpInput.length == 6 && newPassword.isNotBlank() && newPassword == confirmPassword
        Button(
            onClick = { onCompleteReset(pendingEmail, otpInput, newPassword) },
            enabled = canReset,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("submit_password_reset_btn"),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = NaturalMossPrimary,
                contentColor = Color.White
            )
        ) {
            Text(
                text = "Update Password & Sign In",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedButton(
                onClick = onResendOtp,
                shape = RoundedCornerShape(10.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, NaturalSageBorder),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = NaturalMossPrimary),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                modifier = Modifier.height(34.dp)
            ) {
                Text(
                    text = "Resend Code",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            TextButton(
                onClick = onBackToLogin,
                colors = ButtonDefaults.textButtonColors(contentColor = NaturalOliveMuted),
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
                modifier = Modifier.height(34.dp)
            ) {
                Text(
                    text = "Back to Sign In",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

// -------------------------------------------------------------
// Common Input Field & Notification Cards
// -------------------------------------------------------------

@Composable
private fun AuthInputField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardType: KeyboardType = KeyboardType.Text,
    testTag: String = ""
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(NaturalBackground)
            .border(1.dp, NaturalCardBorder, RoundedCornerShape(12.dp))
            .padding(horizontal = 12.dp, vertical = 10.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            leadingIcon?.let {
                it()
                Spacer(modifier = Modifier.width(8.dp))
            }

            Box(modifier = Modifier.weight(1f)) {
                if (value.isEmpty()) {
                    Text(
                        text = placeholder,
                        fontSize = 12.sp,
                        color = NaturalOliveMuted.copy(alpha = 0.7f)
                    )
                }

                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    singleLine = true,
                    textStyle = TextStyle(
                        fontSize = 13.sp,
                        color = NaturalTextHeading,
                        fontWeight = FontWeight.Medium
                    ),
                    visualTransformation = visualTransformation,
                    keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag(testTag)
                )
            }

            trailingIcon?.let {
                Spacer(modifier = Modifier.width(6.dp))
                it()
            }
        }
    }
}

@Composable
private fun AuthNotificationCard(
    text: String,
    isError: Boolean,
    onDismiss: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .border(
                1.dp,
                if (isError) NaturalTerracotta.copy(alpha = 0.5f) else NaturalSageBorder,
                RoundedCornerShape(14.dp)
            ),
        color = if (isError) NaturalTerracotta.copy(alpha = 0.12f) else NaturalSageContainer
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = if (isError) "⚠️" else "✓",
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = text,
                    fontSize = 11.sp,
                    color = if (isError) NaturalTerracotta else NaturalMossDark,
                    fontWeight = FontWeight.SemiBold,
                    lineHeight = 15.sp
                )
            }

            Text(
                text = "✕",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = NaturalOliveMuted,
                modifier = Modifier
                    .clickable(onClick = onDismiss)
                    .padding(4.dp)
            )
        }
    }
}
