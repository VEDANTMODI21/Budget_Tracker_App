package com.example.data

import android.content.Context
import android.content.SharedPreferences

object AuthManager {
    private const val PREF_NAME = "pocketflow_security_prefs"
    private const val KEY_IS_REGISTERED = "is_registered"
    private const val KEY_USER_NAME = "user_name"
    private const val KEY_USER_EMAIL = "user_email"
    private const val KEY_USER_PASSWORD = "user_password"
    private const val KEY_USER_PIN = "user_pin"
    
    // In-memory session state to protect the app upon start/reset
    private var isSessionActive = false

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun isRegistered(context: Context): Boolean {
        return getPrefs(context).getBoolean(KEY_IS_REGISTERED, false)
    }

    fun registerUser(context: Context, name: String, email: String, password: String, pin: String): Boolean {
        if (name.isBlank() || email.isBlank() || password.isBlank() || pin.length != 4) {
            return false
        }
        getPrefs(context).edit().apply {
            putBoolean(KEY_IS_REGISTERED, true)
            putString(KEY_USER_NAME, name.trim())
            putString(KEY_USER_EMAIL, email.trim().lowercase())
            putString(KEY_USER_PASSWORD, password) // Simple storage for offline demonstration
            putString(KEY_USER_PIN, pin)
            apply()
        }
        isSessionActive = true
        return true
    }

    fun loginWithPassword(context: Context, email: String, password: String): Boolean {
        val prefs = getPrefs(context)
        val savedEmail = prefs.getString(KEY_USER_EMAIL, "") ?: ""
        val savedPassword = prefs.getString(KEY_USER_PASSWORD, "") ?: ""
        
        val success = email.trim().lowercase() == savedEmail && password == savedPassword
        if (success) {
            isSessionActive = true
        }
        return success
    }

    fun loginWithPin(context: Context, pin: String): Boolean {
        val prefs = getPrefs(context)
        val savedPin = prefs.getString(KEY_USER_PIN, "") ?: ""
        
        val success = pin == savedPin
        if (success) {
            isSessionActive = true
        }
        return success
    }

    fun getUserName(context: Context): String {
        return getPrefs(context).getString(KEY_USER_NAME, "Vedant") ?: "Vedant"
    }

    fun getUserEmail(context: Context): String {
        return getPrefs(context).getString(KEY_USER_EMAIL, "vedantmodi1221@gmail.com") ?: "vedantmodi1221@gmail.com"
    }

    fun isSessionActive(): Boolean {
        return isSessionActive
    }

    fun setSessionActive(active: Boolean) {
        isSessionActive = active
    }

    fun logout() {
        isSessionActive = false
    }

    // Utility to reset the account if needed (for security testing)
    fun clearCredentials(context: Context) {
        getPrefs(context).edit().clear().apply()
        isSessionActive = false
    }
}
