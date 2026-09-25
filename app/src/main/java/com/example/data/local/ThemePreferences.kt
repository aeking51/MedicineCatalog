package com.example.data.local

import android.content.Context
import android.content.SharedPreferences
import com.example.ui.theme.AppThemeMode
import com.example.ui.theme.GlassMotionProfile

/**
 * Manages persistent storage of the user's selected visual theme mode
 * (Classic Light, Glass Theme, or Dark Theme) and motion dynamics profile.
 *
 * Ensures that the selected theme and motion physics apply permanently across the entire
 * application and survives app restarts, process recreation, and user sessions.
 */
object ThemePreferences {
    private const val PREFS_NAME = "ayurveda_theme_preferences"
    private const val KEY_THEME_MODE = "saved_app_theme_mode"
    private const val KEY_MOTION_PROFILE = "saved_glass_motion_profile"
    private const val KEY_INITIAL_PERMISSIONS_REQUESTED = "saved_initial_permissions_requested"

    private var sharedPreferences: SharedPreferences? = null

    /**
     * Initializes the SharedPreferences instance using the application context.
     * Safe to call multiple times; idempotently retains the application context.
     */
    fun init(context: Context) {
        if (sharedPreferences == null) {
            sharedPreferences = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        }
    }

    /**
     * Reads the permanently stored visual theme. Defaults to [AppThemeMode.GLASS] (Liquid Glass Theme)
     * if no preference has been saved yet or if an invalid value is encountered.
     */
    fun getThemeMode(): AppThemeMode {
        val savedName = sharedPreferences?.getString(KEY_THEME_MODE, AppThemeMode.GLASS.name)
            ?: AppThemeMode.GLASS.name
        return try {
            AppThemeMode.valueOf(savedName)
        } catch (e: Exception) {
            AppThemeMode.GLASS
        }
    }

    /**
     * Permanently commits the user's chosen theme mode to disk.
     */
    fun setThemeMode(mode: AppThemeMode) {
        sharedPreferences?.edit()?.putString(KEY_THEME_MODE, mode.name)?.apply()
    }

    /**
     * Reads the permanently stored Liquid Glass motion profile. Defaults to [GlassMotionProfile.DEFAULT].
     */
    fun getMotionProfile(): GlassMotionProfile {
        val savedName = sharedPreferences?.getString(KEY_MOTION_PROFILE, GlassMotionProfile.DEFAULT.name)
            ?: GlassMotionProfile.DEFAULT.name
        return try {
            GlassMotionProfile.valueOf(savedName)
        } catch (e: Exception) {
            GlassMotionProfile.DEFAULT
        }
    }

    /**
     * Permanently commits the user's chosen Liquid Glass motion profile to disk.
     */
    fun setMotionProfile(profile: GlassMotionProfile) {
        sharedPreferences?.edit()?.putString(KEY_MOTION_PROFILE, profile.name)?.apply()
    }

    /**
     * Checks if the app has already asked for initial runtime permissions (Location & Notifications)
     * upon app installation / first launch.
     */
    fun hasRequestedInitialPermissions(): Boolean {
        return sharedPreferences?.getBoolean(KEY_INITIAL_PERMISSIONS_REQUESTED, false) ?: false
    }

    /**
     * Records that initial runtime permissions have been requested or handled.
     */
    fun setHasRequestedInitialPermissions(requested: Boolean = true) {
        sharedPreferences?.edit()?.putBoolean(KEY_INITIAL_PERMISSIONS_REQUESTED, requested)?.apply()
    }
}
