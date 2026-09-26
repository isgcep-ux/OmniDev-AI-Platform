package com.example

import android.app.Application
import android.util.Log
import com.example.security.AppCheckSecurityManager

/**
 * OmniDevApplication is the primary application module entry point.
 * Initializes Firebase App Check with the Google Play Integrity provider
 * to protect Firestore, Cloud Functions, and API backends against abuse.
 */
class OmniDevApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Log.i(TAG, "Initializing OmniDevApplication...")

        // Implement Firebase App Check with Google Play Integrity provider
        AppCheckSecurityManager.initialize(this)
    }

    companion object {
        private const val TAG = "OmniDevApplication"
    }
}
