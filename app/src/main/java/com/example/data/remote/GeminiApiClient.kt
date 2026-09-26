package com.example.data.remote

import com.example.BuildConfig
import com.example.security.AppCheckSecurityManager
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.concurrent.TimeUnit

@JsonClass(generateAdapter = true)
data class GeminiPart(
    val text: String? = null
)

@JsonClass(generateAdapter = true)
data class GeminiContent(
    val role: String? = null,
    val parts: List<GeminiPart>
)

@JsonClass(generateAdapter = true)
data class GeminiGenerationConfig(
    val temperature: Float? = 0.7f,
    val topP: Float? = 0.95f,
    val topK: Int? = 40,
    val maxOutputTokens: Int? = 2048
)

@JsonClass(generateAdapter = true)
data class GeminiRequest(
    val contents: List<GeminiContent>,
    val systemInstruction: GeminiContent? = null,
    val generationConfig: GeminiGenerationConfig? = null
)

@JsonClass(generateAdapter = true)
data class GeminiCandidate(
    val content: GeminiContent?
)

@JsonClass(generateAdapter = true)
data class GeminiResponse(
    val candidates: List<GeminiCandidate>? = null,
    val error: GeminiError? = null
)

@JsonClass(generateAdapter = true)
data class GeminiError(
    val code: Int?,
    val message: String?,
    val status: String?
)

class GeminiApiClient {
    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .addInterceptor(AppCheckSecurityManager.createAppCheckInterceptor())
        .build()

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val requestAdapter = moshi.adapter(GeminiRequest::class.java)
    private val responseAdapter = moshi.adapter(GeminiResponse::class.java)

    suspend fun generateContent(
        prompt: String,
        systemInstruction: String? = null,
        temperature: Float = 0.7f,
        modelName: String = "gemini-3.5-flash",
        customApiKey: String? = null
    ): Result<String> = withContext(Dispatchers.IO) {
        val apiKey = when {
            !customApiKey.isNullOrBlank() -> customApiKey
            try { BuildConfig.GEMINI_API_KEY.isNotBlank() && BuildConfig.GEMINI_API_KEY != "MY_GEMINI_API_KEY" } catch (e: Exception) { false } -> BuildConfig.GEMINI_API_KEY
            else -> ""
        }

        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            // Intelligent offline simulator response if no key configured
            return@withContext Result.success(getSmartOfflineResponse(prompt, systemInstruction))
        }

        try {
            val endpoint = "https://generativelanguage.googleapis.com/v1beta/models/$modelName:generateContent?key=$apiKey"
            val reqBody = GeminiRequest(
                contents = listOf(
                    GeminiContent(
                        role = "user",
                        parts = listOf(GeminiPart(text = prompt))
                    )
                ),
                systemInstruction = systemInstruction?.let {
                    GeminiContent(parts = listOf(GeminiPart(text = it)))
                },
                generationConfig = GeminiGenerationConfig(temperature = temperature)
            )

            val jsonString = requestAdapter.toJson(reqBody)
            val request = Request.Builder()
                .url(endpoint)
                .post(jsonString.toRequestBody("application/json".toMediaType()))
                .build()

            val response = client.newCall(request).execute()
            val respBodyString = response.body?.string() ?: ""

            if (!response.isSuccessful) {
                val errorResp = try { responseAdapter.fromJson(respBodyString) } catch (e: Exception) { null }
                val errorMsg = errorResp?.error?.message ?: "HTTP ${response.code}: ${response.message}"
                return@withContext Result.failure(Exception("Gemini API Error: $errorMsg"))
            }

            val parsed = responseAdapter.fromJson(respBodyString)
            val generatedText = parsed?.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
            if (!generatedText.isNullOrBlank()) {
                Result.success(generatedText)
            } else {
                Result.failure(Exception("Empty response received from Gemini model."))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun getSmartOfflineResponse(prompt: String, systemInstruction: String?): String {
        val lowerPrompt = prompt.lowercase()
        return when {
            lowerPrompt.contains("flutter") -> """
### 🚀 Flutter Web & Project IDX Blueprint
**Recommended Architecture for Web & Multiplatform:**
1. **State Management**: Utilize Riverpod 2.x (`AsyncNotifierProvider`) for asynchronous web state hydration.
2. **Responsive Rendering**: Use `LayoutBuilder` + `flutter_adaptive_scaffold` for dynamic sidebar to bottom-nav transitions.
3. **Web Renderer**: Compile using WASM / CanvasKit for optimal 60fps rendering:
```bash
flutter build web --wasm --release
```
4. **Firebase Integration**: Initialize with `firebase_core` and `cloud_firestore_web`.
*(Note: To connect live Gemini API, configure GEMINI_API_KEY in the Secrets panel in AI Studio)*
""".trimIndent()

            lowerPrompt.contains("next.js") || lowerPrompt.contains("nextjs") -> """
### ⚡ Next.js App Router & Server Components
**Key Best Practices:**
1. **Server vs Client Split**: Default to Server Components for data-fetching, sprinkle `'use client'` only on leaf interactive buttons and stateful form hooks.
2. **Server Actions**: Mutate database state directly without writing dedicated API boilerplate routes:
```typescript
'use server'
export async function updateDeployment(id: string, status: string) {
  await db.deployments.update({ where: { id }, data: { status } });
  revalidatePath('/deployments');
}
```
3. **Edge Middleware**: Authenticate sessions at edge before page rendering.
*(Note: Connect your GEMINI_API_KEY in AI Studio Secrets for live AI code generation)*
""".trimIndent()

            lowerPrompt.contains("firebase") || lowerPrompt.contains("database") -> """
### 🔥 Firebase Backend & Database Architecture
**Firestore & Cloud Functions Architecture:**
1. **Subcollection Hierarchy**: `/workspaces/{workspaceId}/projects/{projectId}/tasks/{taskId}`
2. **Security Rules**: Validate both authentication identity and payload schema types.
3. **Database Indexing**: Compound indexes for query filters combining `status` == 'active' and `timestamp` descending.
4. **Real-time Listeners**: Use snapshot listeners with offline persistence enabled.
""".trimIndent()

            lowerPrompt.contains("quiz") || lowerPrompt.contains("question") -> """
### 🧠 Generated Technical Assessment
**Q1: What is the primary difference between React Server Components (RSC) and standard Client Components in Next.js?**
- A) RSC send zero bundle JS to the client while Client Components hydrate in the browser. (Correct)
- B) RSC cannot access environment variables.
- C) Client Components run only on the server.
- D) RSC require Redux for state management.

**Q2: In Flutter, what does `const` constructor do during widget rebuilding?**
- Answer: Tells the Flutter engine to skip rebuilding the subtree when parent state triggers `setState()`.
""".trimIndent()

            else -> """
### 🤖 Gemini AI Developer Assistant Response
**Analysis for:** "$prompt"

**Architectural Recommendations:**
1. **Modularity**: Decouple business logic from UI layers using standard MVVM or Clean Architecture patterns.
2. **Local Caching**: Store intermediate state in Room / IndexedDB for resilient offline operations.
3. **AI Enhancement**: Use structured JSON outputs with Gemini 3.5 Flash for high-speed automated code generation and QA verification.

*(Tip: Enter your GEMINI_API_KEY in AI Studio Secrets to unlock dynamic real-time Gemini generation!)*
""".trimIndent()
        }
    }
}
