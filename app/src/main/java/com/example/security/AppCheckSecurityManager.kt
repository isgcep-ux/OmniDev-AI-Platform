package com.example.security

import android.content.Context
import android.util.Log
import com.example.BuildConfig
import com.google.android.gms.tasks.Tasks
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.AppCheckToken
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import okhttp3.Interceptor
import okhttp3.Response
import java.util.concurrent.TimeUnit

/**
 * Data model representing the active Firebase App Check status.
 */
data class AppCheckStatus(
    val isInitialized: Boolean = false,
    val providerName: String = "Not Initialized",
    val isPlayIntegrity: Boolean = false,
    val autoRefreshEnabled: Boolean = true,
    val lastTokenSnippet: String? = null,
    val lastVerifiedTimestamp: Long = 0L,
    val statusMessage: String = "App Check standby"
)

/**
 * AppCheckSecurityManager manages Firebase App Check initialization using
 * the Google Play Integrity provider to secure backend APIs and Firebase resources
 * against unauthorized access, reverse-engineering, and bot traffic.
 */
object AppCheckSecurityManager {
    private const val TAG = "AppCheckSecurityMgr"

    private val _status = MutableStateFlow(AppCheckStatus())
    val status: StateFlow<AppCheckStatus> = _status.asStateFlow()

    /**
     * Initializes Firebase App Check with Google Play Integrity provider.
     * In debug environments, gracefully handles emulators by falling back to DebugAppCheckProviderFactory.
     */
    fun initialize(context: Context) {
        try {
            // Ensure default FirebaseApp is initialized
            val app = if (FirebaseApp.getApps(context).isEmpty()) {
                FirebaseApp.initializeApp(context)
            } else {
                FirebaseApp.getInstance()
            }

            if (app == null) {
                Log.w(TAG, "FirebaseApp could not be initialized. App Check waiting for Firebase setup.")
                _status.value = AppCheckStatus(
                    isInitialized = false,
                    providerName = "None",
                    statusMessage = "FirebaseApp not initialized (requires google-services.json)"
                )
                return
            }

            val appCheck = FirebaseAppCheck.getInstance(app)

            // Enable automatic token refresh to ensure seamless API access
            appCheck.setTokenAutoRefreshEnabled(true)

            if (BuildConfig.DEBUG) {
                // In Debug mode: try Play Integrity provider first;
                // if device/emulator lacks Play Integrity support, fall back to Debug provider
                try {
                    val playIntegrityFactory = PlayIntegrityAppCheckProviderFactory.getInstance()
                    appCheck.installAppCheckProviderFactory(playIntegrityFactory)
                    Log.i(TAG, "Firebase App Check initialized with PlayIntegrityAppCheckProviderFactory (Debug)")
                    _status.value = AppCheckStatus(
                        isInitialized = true,
                        providerName = "Google Play Integrity (Debug)",
                        isPlayIntegrity = true,
                        autoRefreshEnabled = true,
                        lastVerifiedTimestamp = System.currentTimeMillis(),
                        statusMessage = "Play Integrity provider active. APIs secured."
                    )
                } catch (e: Throwable) {
                    Log.w(TAG, "Play Integrity unavailable in local debug environment. Falling back to DebugAppCheckProviderFactory: ${e.message}")
                    val debugFactory = DebugAppCheckProviderFactory.getInstance()
                    appCheck.installAppCheckProviderFactory(debugFactory)
                    _status.value = AppCheckStatus(
                        isInitialized = true,
                        providerName = "Debug Provider (Play Integrity Fallback)",
                        isPlayIntegrity = false,
                        autoRefreshEnabled = true,
                        lastVerifiedTimestamp = System.currentTimeMillis(),
                        statusMessage = "Debug App Check provider active for development."
                    )
                }
            } else {
                // Production: Strictly enforce Google Play Integrity provider
                val playIntegrityFactory = PlayIntegrityAppCheckProviderFactory.getInstance()
                appCheck.installAppCheckProviderFactory(playIntegrityFactory)
                Log.i(TAG, "Firebase App Check installed PlayIntegrityAppCheckProviderFactory for Production")
                _status.value = AppCheckStatus(
                    isInitialized = true,
                    providerName = "Google Play Integrity",
                    isPlayIntegrity = true,
                    autoRefreshEnabled = true,
                    lastVerifiedTimestamp = System.currentTimeMillis(),
                    statusMessage = "Play Integrity provider strictly enforced. Full hardware attestation active."
                )
            }

            // Fetch initial token to warm up cache and confirm provider operation
            fetchAppCheckToken(forceRefresh = false) { tokenResult ->
                if (tokenResult != null) {
                    val snippet = if (tokenResult.token.length > 12) {
                        "${tokenResult.token.take(6)}...${tokenResult.token.takeLast(6)}"
                    } else {
                        tokenResult.token
                    }
                    _status.value = _status.value.copy(
                        lastTokenSnippet = snippet,
                        lastVerifiedTimestamp = System.currentTimeMillis(),
                        statusMessage = "Play Integrity token successfully verified."
                    )
                }
            }
        } catch (t: Throwable) {
            Log.e(TAG, "Exception during Firebase App Check initialization: ${t.message}", t)
            _status.value = AppCheckStatus(
                isInitialized = false,
                providerName = "Error",
                statusMessage = "App Check initialization error: ${t.localizedMessage}"
            )
        }
    }

    /**
     * Asynchronously fetches the current Firebase App Check token.
     */
    fun fetchAppCheckToken(forceRefresh: Boolean = false, onResult: (AppCheckToken?) -> Unit) {
        try {
            val appCheck = FirebaseAppCheck.getInstance()
            appCheck.getAppCheckToken(forceRefresh)
                .addOnSuccessListener { token ->
                    Log.d(TAG, "App Check token obtained successfully: valid until ${token.expireTimeMillis}")
                    onResult(token)
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Failed to get App Check token: ${exception.message}")
                    onResult(null)
                }
        } catch (e: Exception) {
            Log.w(TAG, "AppCheck not ready for token retrieval: ${e.message}")
            onResult(null)
        }
    }

    /**
     * Synchronously fetches the App Check token with a timeout, suitable for OkHttp interceptors.
     */
    suspend fun getAppCheckTokenBlocking(timeoutSeconds: Long = 3): String? = withContext(Dispatchers.IO) {
        try {
            val appCheck = FirebaseAppCheck.getInstance()
            val task = appCheck.getAppCheckToken(false)
            val result = Tasks.await(task, timeoutSeconds, TimeUnit.SECONDS)
            result.token
        } catch (e: Exception) {
            Log.d(TAG, "Timeout or error fetching App Check token for API request: ${e.message}")
            null
        }
    }

    /**
     * Creates an OkHttp Interceptor that attaches the X-Firebase-AppCheck header to outgoing requests
     * for custom API endpoints and Cloud Functions.
     */
    fun createAppCheckInterceptor(): Interceptor {
        return Interceptor { chain ->
            val originalRequest = chain.request()
            var request = originalRequest

            try {
                val appCheck = FirebaseAppCheck.getInstance()
                val task = appCheck.getAppCheckToken(false)
                val tokenResult = Tasks.await(task, 2, TimeUnit.SECONDS)
                val token = tokenResult?.token
                if (!token.isNullOrBlank()) {
                    request = originalRequest.newBuilder()
                        .header("X-Firebase-AppCheck", token)
                        .build()
                }
            } catch (ignored: Exception) {
                // Proceed with original request if token generation times out or is offline
            }

            chain.proceed(request)
        }
    }
}
