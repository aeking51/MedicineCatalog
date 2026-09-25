package com.example.data.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

object FirebaseAuthRepository {

    private const val TAG = "FirebaseAuthRepo"

    val auth: FirebaseAuth? by lazy {
        try {
            FirebaseAuth.getInstance()
        } catch (t: Throwable) {
            Log.w(TAG, "FirebaseAuth initialization unavailable: ${t.message}")
            null
        }
    }

    val isAuthAvailable: Boolean
        get() = auth != null

    val currentFirebaseUser: FirebaseUser?
        get() = auth?.currentUser

    val isUserLoggedIn: Boolean
        get() = currentFirebaseUser != null

    fun signUpWithEmail(
        email: String,
        pass: String,
        onSuccess: (FirebaseUser) -> Unit,
        onError: (String) -> Unit
    ) {
        val firebaseAuth = auth
        if (firebaseAuth == null) {
            onError("Firebase Authentication is not configured or unavailable.")
            return
        }

        firebaseAuth.createUserWithEmailAndPassword(email.trim(), pass.trim())
            .addOnSuccessListener { result ->
                val user = result.user
                if (user != null) {
                    Log.d(TAG, "Successfully created user: ${user.uid}")
                    onSuccess(user)
                } else {
                    onError("User registration succeeded but user record is null.")
                }
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Sign up failed: ${exception.message}", exception)
                onError(exception.localizedMessage ?: "Failed to sign up with email and password.")
            }
    }

    fun signInWithEmail(
        email: String,
        pass: String,
        onSuccess: (FirebaseUser) -> Unit,
        onError: (String) -> Unit
    ) {
        val firebaseAuth = auth
        if (firebaseAuth == null) {
            onError("Firebase Authentication is not configured or unavailable.")
            return
        }

        firebaseAuth.signInWithEmailAndPassword(email.trim(), pass.trim())
            .addOnSuccessListener { result ->
                val user = result.user
                if (user != null) {
                    Log.d(TAG, "Successfully authenticated user: ${user.uid}")
                    onSuccess(user)
                } else {
                    onError("Authentication succeeded but user record is null.")
                }
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Sign in failed: ${exception.message}", exception)
                onError(exception.localizedMessage ?: "Failed to sign in. Check email and password.")
            }
    }

    fun sendPasswordResetEmail(
        email: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val firebaseAuth = auth
        if (firebaseAuth == null) {
            onError("Firebase Authentication is not configured or unavailable.")
            return
        }

        firebaseAuth.sendPasswordResetEmail(email.trim())
            .addOnSuccessListener {
                Log.d(TAG, "Password reset email dispatched to $email")
                onSuccess()
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Password reset dispatch failed: ${exception.message}")
                onError(exception.localizedMessage ?: "Could not dispatch password reset email.")
            }
    }

    fun updateUserPassword(
        newPass: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val user = currentFirebaseUser
        if (user != null) {
            user.updatePassword(newPass.trim())
                .addOnSuccessListener {
                    Log.d(TAG, "Successfully updated Firebase user password")
                    onSuccess()
                }
                .addOnFailureListener { exception ->
                    Log.e(TAG, "Failed to update Firebase user password: ${exception.message}")
                    onError(exception.localizedMessage ?: "Could not update password.")
                }
        } else {
            onSuccess()
        }
    }

    fun signOut() {
        try {
            auth?.signOut()
            Log.d(TAG, "User signed out from Firebase Auth")
        } catch (t: Throwable) {
            Log.e(TAG, "Sign out error: ${t.message}")
        }
    }
}
