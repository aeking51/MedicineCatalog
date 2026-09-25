package com.example.ui.components

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import com.example.data.local.ThemePreferences
import com.example.ui.theme.AyurTheme
import com.example.ui.theme.NaturalCardBorder
import com.example.ui.theme.NaturalCardSurface
import com.example.ui.theme.NaturalMossDark
import com.example.ui.theme.NaturalMossPrimary
import com.example.ui.theme.NaturalOliveMuted
import com.example.ui.theme.NaturalSageContainer
import com.example.ui.theme.NaturalTextHeading
import com.example.ui.theme.NaturalTextPrimary

/**
 * Handles requesting Location and Notification permissions upon initial app installation/launch.
 *
 * Automatically triggers the Android system runtime permission prompts on first launch,
 * and provides an educational rationale dialog compliant with Google Play Policies.
 */
@Composable
fun InitialPermissionsRequester(
    onPermissionsResult: (allGranted: Boolean) -> Unit = {}
) {
    val context = LocalContext.current
    var showRationaleDialog by remember { mutableStateOf(false) }

    val permissionsToRequest = remember {
        val list = mutableListOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            list.add(Manifest.permission.POST_NOTIFICATIONS)
        }
        list.toTypedArray()
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { results ->
        ThemePreferences.setHasRequestedInitialPermissions(true)
        showRationaleDialog = false
        val allGranted = results.values.all { it }
        onPermissionsResult(allGranted)
    }

    LaunchedEffect(Unit) {
        val hasRequestedBefore = ThemePreferences.hasRequestedInitialPermissions()
        val allAlreadyGranted = permissionsToRequest.all { perm ->
            ContextCompat.checkSelfPermission(context, perm) == PackageManager.PERMISSION_GRANTED
        }

        if (!hasRequestedBefore && !allAlreadyGranted) {
            // First time installing / launching: directly trigger the system permission request
            permissionLauncher.launch(permissionsToRequest)
        }
    }

    if (showRationaleDialog) {
        PermissionsRationaleDialog(
            onGrantClick = {
                permissionLauncher.launch(permissionsToRequest)
            },
            onDismiss = {
                ThemePreferences.setHasRequestedInitialPermissions(true)
                showRationaleDialog = false
            }
        )
    }
}

@Composable
fun PermissionsRationaleDialog(
    onGrantClick: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .border(1.dp, NaturalCardBorder, RoundedCornerShape(24.dp))
                .testTag("initial_permissions_dialog"),
            color = NaturalCardSurface,
            shadowElevation = 8.dp
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(NaturalSageContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = null,
                        tint = NaturalMossPrimary,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Welcome to Sitaram Ayurveda",
                    fontFamily = FontFamily.Serif,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = NaturalTextHeading
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Enable Location and Notification access to unlock the full clinical experience.",
                    fontSize = 12.sp,
                    color = NaturalOliveMuted,
                    lineHeight = 17.sp,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )

                Spacer(modifier = Modifier.height(18.dp))

                PermissionRationaleItem(
                    icon = Icons.Default.LocationOn,
                    title = "Location Access",
                    description = "Discover nearby authentic Ayurvedic dispensaries, Vaidya clinics, and seasonal climate-specific herb advice."
                )

                Spacer(modifier = Modifier.height(12.dp))

                PermissionRationaleItem(
                    icon = Icons.Default.Notifications,
                    title = "Notification Alerts",
                    description = "Receive daily formulation spotlights, vitality regimen reminders, and pharmacopeia handbook updates."
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .testTag("permissions_skip_btn")
                    ) {
                        Text(
                            text = "Skip for Now",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = NaturalOliveMuted
                        )
                    }

                    Button(
                        onClick = onGrantClick,
                        colors = ButtonDefaults.buttonColors(containerColor = NaturalMossPrimary),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1.3f)
                            .height(44.dp)
                            .testTag("permissions_grant_btn")
                    ) {
                        Text(
                            text = "Allow Access",
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PermissionRationaleItem(
    icon: ImageVector,
    title: String,
    description: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(NaturalSageContainer.copy(alpha = 0.5f))
            .padding(12.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(NaturalMossPrimary.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = NaturalMossPrimary,
                modifier = Modifier.size(18.dp)
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = NaturalTextHeading
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = description,
                fontSize = 11.sp,
                color = NaturalTextPrimary.copy(alpha = 0.85f),
                lineHeight = 15.sp
            )
        }
    }
}
