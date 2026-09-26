package com.example.ui.viewmodel

import android.app.Application
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.BuildConfig
import com.example.data.local.AppDatabase
import com.example.data.local.entity.ChatMessageEntity
import com.example.data.local.entity.DeploymentTask
import com.example.data.local.entity.ISGContent
import com.example.data.local.entity.PromptTemplate
import com.example.data.local.entity.QuizResult
import com.example.data.local.entity.ResourcePolicy
import com.example.data.local.entity.SavedSnippet
import com.example.data.local.entity.ShareInviteToken
import com.example.data.local.entity.TeamMember
import com.example.data.model.Project
import com.example.data.repository.DevStudioRepository
import com.example.data.repository.FirestoreProjectRepository
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class QuizQuestion(
    val id: Int,
    val question: String,
    val options: List<String>,
    val correctIndex: Int,
    val explanation: String
)

data class TestSuiteItem(
    val id: String,
    val name: String,
    val type: String, // "Unit", "Widget", "Integration", "E2E"
    var status: String = "IDLE", // "IDLE", "RUNNING", "PASSED", "FAILED"
    val durationMs: Long = 0,
    val error: String? = null
)

data class DevOsTerminalEntry(
    val id: String = java.util.UUID.randomUUID().toString(),
    val command: String,
    val output: String,
    val isSuccess: Boolean = true,
    val timestamp: String = java.text.SimpleDateFormat("HH:mm:ss", java.util.Locale.getDefault()).format(java.util.Date())
)

class DevStudioViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application, viewModelScope)
    private val repository = DevStudioRepository(database.devStudioDao())
    private val projectRepository = FirestoreProjectRepository(viewModelScope)

    // Cloud Firestore Projects & Dashboard State
    val projects: StateFlow<List<Project>> = projectRepository.projects
    val isFirestoreActive: StateFlow<Boolean> = projectRepository.isFirestoreActive
    val syncStatusMessage: StateFlow<String> = projectRepository.syncStatusMessage

    fun addProject(
        name: String,
        description: String,
        status: String = Project.STATUS_IN_PROGRESS,
        techStack: String = "Jetpack Compose"
    ) {
        projectRepository.addProject(name, description, status, techStack)
    }

    fun updateProjectStatus(projectId: String, newStatus: String) {
        projectRepository.updateProjectStatus(projectId, newStatus)
    }

    fun deleteProject(projectId: String) {
        projectRepository.deleteProject(projectId)
    }

    // Firebase Auth State
    private val firebaseAuth: FirebaseAuth? = try {
        FirebaseAuth.getInstance()
    } catch (e: Exception) {
        Log.w("DevStudioViewModel", "Firebase Auth init note: ${e.message}")
        null
    }

    private val _currentUser = MutableStateFlow<FirebaseUser?>(firebaseAuth?.currentUser)
    val currentUser: StateFlow<FirebaseUser?> = _currentUser.asStateFlow()

    private val _isGuestMode = MutableStateFlow(false)
    val isGuestMode: StateFlow<Boolean> = _isGuestMode.asStateFlow()

    private val _isAuthLoading = MutableStateFlow(false)
    val isAuthLoading: StateFlow<Boolean> = _isAuthLoading.asStateFlow()

    private val _authErrorMessage = MutableStateFlow<String?>(null)
    val authErrorMessage: StateFlow<String?> = _authErrorMessage.asStateFlow()

    private val _showLoginScreen = MutableStateFlow(false)
    val showLoginScreen: StateFlow<Boolean> = _showLoginScreen.asStateFlow()

    // App Navigation State
    val currentMainTab = MutableStateFlow(0) // 0: IDX Development, 1: AI Studio, 2: Saved Library
    val currentIdxSection = MutableStateFlow(0) // 0: Flutter, 1: Next.js, 2: Firebase, 3: Database, 4: Tests, 5: Deployment
    val currentAiStudioSection = MutableStateFlow(0) // 0: Models, 1: Prompt Studio, 2: Q&A Generator, 3: Chatbot Tutor, 4: Integration SDK

    // API Key State
    private val _customApiKey = MutableStateFlow("")
    val customApiKey: StateFlow<String> = _customApiKey.asStateFlow()

    // DevOS Workstation & Claude Code CLI State
    private val _terminalEntries = MutableStateFlow<List<DevOsTerminalEntry>>(
        listOf(
            DevOsTerminalEntry(
                command = "claude --version",
                output = "Claude Code CLI v1.0.4-dev (AI-native DevOS Workstation Assistant)\nDevOS Android Container | Kotlin 2.2 | AGP 9.1 | OpenJDK 17",
                isSuccess = true
            ),
            DevOsTerminalEntry(
                command = "claude status",
                output = "[OK] DevOS Workstation: ONLINE\n[OK] APK Çıktısı: Hazır (.build-outputs/app-debug.apk - 32.5 MB)\n[OK] Emülatör: Android 15 (API 36) Streaming Aktif\n[OK] Yerel Depolama: Room SQLite 2.7.0 Aktif",
                isSuccess = true
            )
        )
    )
    val terminalEntries: StateFlow<List<DevOsTerminalEntry>> = _terminalEntries.asStateFlow()

    private val _terminalInput = MutableStateFlow("")
    val terminalInput: StateFlow<String> = _terminalInput.asStateFlow()

    fun setTerminalInput(input: String) {
        _terminalInput.value = input
    }

    fun executeTerminalCommand(cmd: String) {
        val trimmed = cmd.trim()
        if (trimmed.isBlank()) return

        val normalized = trimmed.lowercase()
        val output: String
        val success: Boolean

        when {
            normalized == "clear" || normalized == "cls" -> {
                _terminalEntries.value = emptyList()
                _terminalInput.value = ""
                return
            }
            normalized.contains("apk") || normalized.contains("build") -> {
                output = """
                    [APK DERLEME VE ÇIKTI BİLGİLERİ]
                    • Durum: Başarıyla Derlendi (BUILD SUCCESSFUL)
                    • Konum: .build-outputs/app-debug.apk
                    • Boyut: 32.5 MB (Tüm Compose & Room bağımlılıkları dahil)
                    • Paket Adı: com.aistudio.devstudiohub.qwmvtp
                    • Min SDK: 24 | Hedef SDK: 36 (Android 15)
                    • Hızlı Kurulum: adb install -r .build-outputs/app-debug.apk
                    • Başlatma: adb shell am start -n com.aistudio.devstudiohub.qwmvtp/com.example.MainActivity
                """.trimIndent()
                success = true
            }
            normalized.contains("test") -> {
                output = """
                    [DEVOS TEST KOŞUCUSU - OTOMATİK RAPOR]
                    ✓ ExampleUnitTest.kt ................... BAŞARILI (14ms)
                    ✓ ExampleRobolectricTest.kt ............ BAŞARILI (198ms)
                    ✓ GreetingScreenshotTest.kt ............ BAŞARILI (88ms)
                    --------------------------------------------------
                    Özet: 3 test çalıştırıldı, 3 başarılı, 0 hata | Süre: 300ms
                """.trimIndent()
                success = true
            }
            normalized.contains("git") -> {
                output = """
                    On branch main
                    Your branch is up to date with 'origin/main'.
                    Changes committed:
                      - DevOS Workstation kişisel geliştirici ortamı eklendi
                      - APK emülatör dağıtım kılavuzu optimize edildi
                      - Claude Code CLI terminali entegrasyonu tamamlandı
                    working tree clean
                """.trimIndent()
                success = true
            }
            normalized.contains("env") || normalized.contains("secret") -> {
                output = """
                    [DEVOS ORTAM VE SIRLAR DENETİMİ]
                    • Yapılandırma Dosyası: .env & .env.example
                    • GEMINI_API_KEY: ${if (isGeminiConfigured.value) "[YAPILANDIRILMIŞ / AKTİF]" else "[AI Studio Secrets Tarafından Yönetiliyor]"}
                    • FIREBASE_APPCHECK: Etkin (Debug ReCaptcha)
                    • ANDROID_SDK_ROOT: /android-sdk (Hazır)
                    • JAVA_HOME: OpenJDK 17.0
                """.trimIndent()
                success = true
            }
            normalized.contains("sys") || normalized.contains("monitor") || normalized.contains("top") -> {
                val runtime = Runtime.getRuntime()
                val totalMemMb = runtime.totalMemory() / (1024 * 1024)
                val freeMemMb = runtime.freeMemory() / (1024 * 1024)
                val usedMemMb = totalMemMb - freeMemMb
                output = """
                    [DEVOS SİSTEM VE DONANIM METRİKLERİ]
                    • Cihaz / Host: devos-workstation-x86_64
                    • JVM Bellek: ${usedMemMb}MB / ${totalMemMb}MB (Heap)
                    • Sanallaştırılmış Çekirdek: ${runtime.availableProcessors()} Cores
                    • İşletim Sistemi: Android Linux Container (API 36)
                    • Ağ: 1000Mbps Virtual Loopback (Online, 14ms)
                """.trimIndent()
                success = true
            }
            normalized.contains("help") -> {
                output = """
                    Claude Code DevOS Terminal Komutları:
                      claude apk-info          - APK dosya konumu, boyutu ve kurulum komutu
                      claude test              - Robolectric & Unit testlerini koştur
                      claude sys-monitor       - CPU, RAM ve sistem metriklerini görüntüle
                      claude env               - Ortam değişkenleri ve API anahtarı durumu
                      claude git-status        - Git çalışma ağacı ve dal durumu
                      clear                    - Terminal ekranını temizle
                """.trimIndent()
                success = true
            }
            else -> {
                output = "claude-code: '$trimmed' komutu DevOS Workstation üzerinde başarıyla yürütüldü.\n[ÇIKIŞ KODU: 0 (BAŞARILI)]"
                success = true
            }
        }

        _terminalEntries.value = _terminalEntries.value + DevOsTerminalEntry(
            command = trimmed,
            output = output,
            isSuccess = success
        )
        _terminalInput.value = ""
    }

    fun clearTerminal() {
        _terminalEntries.value = emptyList()
    }

    val isGeminiConfigured: StateFlow<Boolean> = MutableStateFlow(
        try {
            val key = BuildConfig.GEMINI_API_KEY
            key.isNotBlank() && key != "MY_GEMINI_API_KEY"
        } catch (e: Exception) { false }
    )

    fun setCustomApiKey(key: String) {
        _customApiKey.value = key
    }

    fun setShowLoginScreen(show: Boolean) {
        _showLoginScreen.value = show
    }

    fun dismissAuthError() {
        _authErrorMessage.value = null
    }

    fun continueAsGuest() {
        _isGuestMode.value = true
        _showLoginScreen.value = false
        _authErrorMessage.value = null
    }

    fun signOut() {
        try {
            firebaseAuth?.signOut()
        } catch (e: Exception) {
            Log.e("DevStudioViewModel", "Sign out error", e)
        }
        _currentUser.value = null
        _isGuestMode.value = false
        _showLoginScreen.value = false
    }

    fun signInWithEmail(email: String, pass: String) {
        val auth = firebaseAuth
        if (auth == null) {
            _authErrorMessage.value = "Firebase Auth is unavailable. Check network or configuration."
            return
        }
        _isAuthLoading.value = true
        _authErrorMessage.value = null
        auth.signInWithEmailAndPassword(email, pass)
            .addOnCompleteListener { task ->
                _isAuthLoading.value = false
                if (task.isSuccessful) {
                    _currentUser.value = auth.currentUser
                    _isGuestMode.value = false
                    _showLoginScreen.value = false
                } else {
                    _authErrorMessage.value = task.exception?.localizedMessage ?: "Failed to sign in. Please verify credentials."
                }
            }
    }

    fun signUpWithEmail(email: String, pass: String) {
        val auth = firebaseAuth
        if (auth == null) {
            _authErrorMessage.value = "Firebase Auth is unavailable. Check network or configuration."
            return
        }
        _isAuthLoading.value = true
        _authErrorMessage.value = null
        auth.createUserWithEmailAndPassword(email, pass)
            .addOnCompleteListener { task ->
                _isAuthLoading.value = false
                if (task.isSuccessful) {
                    _currentUser.value = auth.currentUser
                    _isGuestMode.value = false
                    _showLoginScreen.value = false
                } else {
                    _authErrorMessage.value = task.exception?.localizedMessage ?: "Failed to create account."
                }
            }
    }

    fun signInWithGoogle(context: Context) {
        val auth = firebaseAuth
        if (auth == null) {
            _authErrorMessage.value = "Firebase Auth is not initialized."
            return
        }

        viewModelScope.launch {
            _isAuthLoading.value = true
            _authErrorMessage.value = null
            try {
                val credentialManager = CredentialManager.create(context)
                val googleIdOption = GetSignInWithGoogleOption.Builder("YOUR_WEB_CLIENT_ID")
                    .build()

                val request = GetCredentialRequest.Builder()
                    .addCredentialOption(googleIdOption)
                    .build()

                val result = credentialManager.getCredential(
                    request = request,
                    context = context
                )

                val credential = result.credential
                if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                    val idToken = googleIdTokenCredential.idToken
                    val authCredential = GoogleAuthProvider.getCredential(idToken, null)

                    auth.signInWithCredential(authCredential)
                        .addOnCompleteListener { task ->
                            _isAuthLoading.value = false
                            if (task.isSuccessful) {
                                _currentUser.value = auth.currentUser
                                _isGuestMode.value = false
                                _showLoginScreen.value = false
                            } else {
                                _authErrorMessage.value = task.exception?.localizedMessage ?: "Google Firebase Authentication failed."
                            }
                        }
                } else {
                    _isAuthLoading.value = false
                    _authErrorMessage.value = "Unrecognized credential returned from Google."
                }
            } catch (e: GetCredentialCancellationException) {
                _isAuthLoading.value = false
                // User cancelled flow
            } catch (e: GetCredentialException) {
                _isAuthLoading.value = false
                _authErrorMessage.value = "Google Sign-In: ${e.message ?: "Authentication cancelled or unavailable"}"
            } catch (e: Exception) {
                _isAuthLoading.value = false
                _authErrorMessage.value = "Sign-in error: ${e.localizedMessage ?: "Unknown error"}"
            }
        }
    }

    // Database Flows
    val savedSnippets: StateFlow<List<SavedSnippet>> = repository.allSnippets
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val promptTemplates: StateFlow<List<PromptTemplate>> = repository.allPrompts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val quizHistory: StateFlow<List<QuizResult>> = repository.allQuizResults
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val deploymentTasks: StateFlow<List<DeploymentTask>> = repository.deploymentTasks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Team & Access Control Flows
    val teamMembers: StateFlow<List<TeamMember>> = repository.allTeamMembers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val resourcePolicies: StateFlow<List<ResourcePolicy>> = repository.allResourcePolicies
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeInviteTokens: StateFlow<List<ShareInviteToken>> = repository.allActiveInviteTokens
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val isgLessons: StateFlow<List<ISGContent>> = repository.allISGContent
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Team UI Filters & State
    val teamSearchQuery = MutableStateFlow("")
    val selectedRoleFilter = MutableStateFlow("ALL") // "ALL", "OWNER", "ADMIN", "EDITOR", "VIEWER"
    val selectedTeamSubTab = MutableStateFlow(0) // 0: Team Members, 1: RBAC & Policies, 2: Invite Links

    fun inviteMember(name: String, email: String, role: String, department: String) {
        viewModelScope.launch {
            val colorHex = when (role) {
                "OWNER" -> "#F59E0B"
                "ADMIN" -> "#EC4899"
                "EDITOR" -> "#38BDF8"
                else -> "#34D399"
            }
            repository.addTeamMember(
                TeamMember(
                    name = name.trim(),
                    email = email.trim().lowercase(),
                    role = role,
                    department = department.trim(),
                    avatarColorHex = colorHex,
                    status = "ACTIVE"
                )
            )
        }
    }

    fun changeMemberRole(memberId: Long, newRole: String) {
        viewModelScope.launch {
            repository.updateMemberRole(memberId, newRole)
        }
    }

    fun removeMember(memberId: Long) {
        viewModelScope.launch {
            repository.removeTeamMember(memberId)
        }
    }

    fun createShareInvite(targetRole: String, validDays: Int, maxUses: Int): String {
        val randomChars = ('A'..'Z') + ('0'..'9')
        val suffix = (1..5).map { randomChars.random() }.joinToString("")
        val tokenCode = "STUDIO-${targetRole.take(3)}-$suffix"
        val expiresAt = System.currentTimeMillis() + (validDays.toLong() * 24 * 60 * 60 * 1000)
        val creatorEmail = _currentUser.value?.email ?: "developer@devstudio.io"

        viewModelScope.launch {
            repository.createInviteToken(
                ShareInviteToken(
                    tokenCode = tokenCode,
                    targetRole = targetRole,
                    creatorEmail = creatorEmail,
                    expiresAt = expiresAt,
                    maxUses = maxUses,
                    usedCount = 0,
                    isRevoked = false
                )
            )
        }
        return tokenCode
    }

    fun revokeInvite(tokenId: Long) {
        viewModelScope.launch {
            repository.revokeInviteToken(tokenId)
        }
    }

    fun updateResourcePolicy(resourceId: Long, resourceType: String, title: String, visibility: String, minRole: String) {
        viewModelScope.launch {
            val userEmail = _currentUser.value?.email ?: "admin@devstudio.io"
            repository.saveResourcePolicy(
                ResourcePolicy(
                    resourceType = resourceType,
                    resourceId = resourceId,
                    resourceTitle = title,
                    visibility = visibility,
                    minRequiredRole = minRole,
                    lastModifiedBy = userEmail,
                    updatedTimestamp = System.currentTimeMillis()
                )
            )
        }
    }

    fun copyToClipboard(context: Context, text: String, label: String = "Invite Code") {
        try {
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText(label, text)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(context, "$label copied to clipboard!", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Log.e("DevStudioViewModel", "Clipboard error", e)
        }
    }

    // Role Permissions Evaluator
    fun getCurrentUserRole(): String {
        val user = _currentUser.value
        if (user == null) {
            return if (_isGuestMode.value) "GUEST" else "UNAUTHENTICATED"
        }
        // Match user email with team members if available, or default to OWNER/ADMIN for primary user
        val member = teamMembers.value.find { it.email.equals(user.email, ignoreCase = true) }
        return member?.role ?: "OWNER"
    }

    fun canManageTeam(): Boolean {
        val role = getCurrentUserRole()
        return role == "OWNER" || role == "ADMIN"
    }

    fun canEditResources(): Boolean {
        val role = getCurrentUserRole()
        return role == "OWNER" || role == "ADMIN" || role == "EDITOR"
    }

    // Chatbot State
    val selectedPersona = MutableStateFlow("Full-Stack Architect")
    private val _chatMessages = MutableStateFlow<List<ChatMessageEntity>>(emptyList())
    val chatMessages: StateFlow<List<ChatMessageEntity>> = _chatMessages.asStateFlow()

    private val _isChatLoading = MutableStateFlow(false)
    val isChatLoading: StateFlow<Boolean> = _isChatLoading.asStateFlow()

    init {
        loadChatForPersona(selectedPersona.value)
    }

    fun selectPersona(persona: String) {
        selectedPersona.value = persona
        loadChatForPersona(persona)
    }

    private fun loadChatForPersona(persona: String) {
        viewModelScope.launch {
            repository.getChatMessages(persona).collect { messages ->
                _chatMessages.value = messages
            }
        }
    }

    fun sendChatMessage(text: String) {
        if (text.isBlank()) return
        viewModelScope.launch {
            _isChatLoading.value = true
            repository.sendChatMessage(
                userMsg = text,
                persona = selectedPersona.value,
                customApiKey = _customApiKey.value
            )
            _isChatLoading.value = false
        }
    }

    fun clearChatHistory() {
        viewModelScope.launch {
            repository.clearChat(selectedPersona.value)
        }
    }

    // Prompt Engineering Studio State
    val promptStudioSystem = MutableStateFlow("You are an expert system designer. Return concise, robust code patterns with clean documentation.")
    val promptStudioUser = MutableStateFlow("Create an end-to-end data pipeline connecting Flutter Web, Next.js API route, and Firebase Firestore.")
    val promptStudioModel = MutableStateFlow("gemini-3.5-flash")
    val promptStudioTemperature = MutableStateFlow(0.7f)

    private val _promptResult = MutableStateFlow<String?>(null)
    val promptResult: StateFlow<String?> = _promptResult.asStateFlow()

    private val _isPromptRunning = MutableStateFlow(false)
    val isPromptRunning: StateFlow<Boolean> = _isPromptRunning.asStateFlow()

    fun runPromptTest() {
        viewModelScope.launch {
            _isPromptRunning.value = true
            _promptResult.value = null
            val res = repository.askGemini(
                prompt = promptStudioUser.value,
                systemInstruction = promptStudioSystem.value,
                temperature = promptStudioTemperature.value,
                modelName = promptStudioModel.value,
                customApiKey = _customApiKey.value
            )
            _promptResult.value = res.getOrElse { "Error running prompt: ${it.message}" }
            _isPromptRunning.value = false
        }
    }

    // AI Q&A Generator State
    val selectedQuizTopic = MutableStateFlow("Full-Stack & Cloud")
    val selectedQuizDifficulty = MutableStateFlow("Mid-Level")
    private val _quizQuestions = MutableStateFlow<List<QuizQuestion>>(emptyList())
    val quizQuestions: StateFlow<List<QuizQuestion>> = _quizQuestions.asStateFlow()

    val currentQuestionIndex = MutableStateFlow(0)
    val userSelectedOption = MutableStateFlow<Int?>(null)
    val isAnswerSubmitted = MutableStateFlow(false)
    val scoreCount = MutableStateFlow(0)
    val isQuizFinished = MutableStateFlow(false)

    init {
        generateQuizQuestions(selectedQuizTopic.value, selectedQuizDifficulty.value)
    }

    fun generateQuizQuestions(topic: String, difficulty: String) {
        selectedQuizTopic.value = topic
        selectedQuizDifficulty.value = difficulty
        currentQuestionIndex.value = 0
        userSelectedOption.value = null
        isAnswerSubmitted.value = false
        scoreCount.value = 0
        isQuizFinished.value = false

        _quizQuestions.value = getPrebuiltQuizQuestions(topic, difficulty)
    }

    fun selectQuizOption(index: Int) {
        if (!isAnswerSubmitted.value) {
            userSelectedOption.value = index
        }
    }

    fun submitQuizAnswer() {
        if (userSelectedOption.value == null || isAnswerSubmitted.value) return
        isAnswerSubmitted.value = true
        val curQ = _quizQuestions.value.getOrNull(currentQuestionIndex.value)
        if (curQ != null && userSelectedOption.value == curQ.correctIndex) {
            scoreCount.value += 1
        }
    }

    fun nextQuizQuestion() {
        if (currentQuestionIndex.value + 1 < _quizQuestions.value.size) {
            currentQuestionIndex.value += 1
            userSelectedOption.value = null
            isAnswerSubmitted.value = false
        } else {
            isQuizFinished.value = true
            // Save result to Room
            viewModelScope.launch {
                repository.saveQuizResult(
                    QuizResult(
                        topic = selectedQuizTopic.value,
                        difficulty = selectedQuizDifficulty.value,
                        score = scoreCount.value,
                        totalQuestions = _quizQuestions.value.size,
                        feedback = "Completed ${scoreCount.value}/${_quizQuestions.value.size} questions correctly in ${selectedQuizTopic.value} (${selectedQuizDifficulty.value})."
                    )
                )
            }
        }
    }

    fun recordIsgQuizResult(score: Int, total: Int, topic: String = "İSG Mevzuat & Pratik Testi") {
        viewModelScope.launch {
            repository.saveQuizResult(
                QuizResult(
                    topic = topic,
                    difficulty = "İSG Sınav Modülü",
                    score = score,
                    totalQuestions = total,
                    feedback = "6331 İSG ve Room Veritabanı testinde $total sorudan $score doğru yapıldı (%${if (total > 0) (score * 100) / total else 0} başarı)."
                )
            )
        }
    }

    // Testing Suite Live Runner Simulator State
    private val _testSuiteList = MutableStateFlow(
        listOf(
            TestSuiteItem("t1", "Flutter Widget: Responsive Navbar Test", "Widget", "IDLE", 45),
            TestSuiteItem("t2", "Next.js RSC: Server Action Mutation Test", "Unit", "IDLE", 62),
            TestSuiteItem("t3", "Firebase Firestore: Security Rules Auth Check", "Integration", "IDLE", 120),
            TestSuiteItem("t4", "Database: SQLite / Room Schema Migration", "Unit", "IDLE", 38),
            TestSuiteItem("t5", "Gemini API: Tokenizer & Prompt Contract Test", "Integration", "IDLE", 185),
            TestSuiteItem("t6", "End-to-End: Cloud Run Auth & Deployment Smoke Test", "E2E", "IDLE", 310)
        )
    )
    val testSuiteList: StateFlow<List<TestSuiteItem>> = _testSuiteList.asStateFlow()

    private val _isTestsRunning = MutableStateFlow(false)
    val isTestsRunning: StateFlow<Boolean> = _isTestsRunning.asStateFlow()

    fun runAllTests() {
        if (_isTestsRunning.value) return
        viewModelScope.launch {
            _isTestsRunning.value = true
            val updated = _testSuiteList.value.map { it.copy(status = "RUNNING") }
            _testSuiteList.value = updated

            for (i in updated.indices) {
                kotlinx.coroutines.delay(400)
                _testSuiteList.value = _testSuiteList.value.mapIndexed { idx, item ->
                    if (idx == i) item.copy(status = "PASSED") else item
                }
            }
            _isTestsRunning.value = false
        }
    }

    // Deployment Checklist Toggle
    fun toggleDeploymentTask(task: DeploymentTask) {
        viewModelScope.launch {
            repository.updateDeploymentTask(task.copy(isCompleted = !task.isCompleted))
        }
    }

    // Room Snippet Actions
    fun saveSnippet(title: String, language: String, category: String, code: String, description: String) {
        viewModelScope.launch {
            repository.saveSnippet(
                SavedSnippet(
                    title = title,
                    language = language,
                    category = category,
                    code = code,
                    description = description
                )
            )
        }
    }

    fun deleteSnippet(id: Long) {
        viewModelScope.launch {
            repository.deleteSnippet(id)
        }
    }

    fun savePromptTemplate(title: String, system: String, user: String, tag: String) {
        viewModelScope.launch {
            repository.savePrompt(
                PromptTemplate(
                    title = title,
                    systemInstruction = system,
                    userPrompt = user,
                    tag = tag
                )
            )
        }
    }

    fun deletePromptTemplate(id: Long) {
        viewModelScope.launch {
            repository.deletePrompt(id)
        }
    }

    private fun getPrebuiltQuizQuestions(topic: String, difficulty: String): List<QuizQuestion> {
        return when {
            topic.contains("ISG", ignoreCase = true) || topic.contains("İSG", ignoreCase = true) -> {
                val currentLessons = isgLessons.value
                if (currentLessons.isNotEmpty()) {
                    currentLessons.mapIndexed { idx, item ->
                        val correctAnswer = item.content.split(".").firstOrNull()?.trim() ?: item.content
                        val shortAns = if (correctAnswer.length > 100) correctAnswer.take(97) + "..." else correctAnswer
                        val dist1 = "Yalnızca iş kazası oluştuktan sonra bildirim yapılır ve önleyici tedbir alınmaz."
                        val dist2 = "Tüm risk kontrol adımları atlanarak sadece KKD dağıtımı ile yetinilir."
                        val dist3 = "Risk skoru formülü: Risk = Hız x Ağırlık olarak kabul edilir."
                        QuizQuestion(
                            id = idx + 1,
                            question = "6331 sayılı İSG Kanunu ve mevzuatına göre '${item.title}' konusunda temel ilke nedir?",
                            options = listOf(shortAns, dist1, dist2, dist3),
                            correctIndex = 0,
                            explanation = "${item.category}: ${item.content}"
                        )
                    }
                } else {
                    listOf(
                        QuizQuestion(
                            1,
                            "6331 sayılı İSG Kanunu'na göre işverenin genel yükümlülüğü nedir?",
                            listOf("Risklerden kaçınmak ve riskleri kaynağında yok etmek", "Sadece kaza sonrası tutanak tutmak", "Yılda bir kez KKD dağıtmak", "İş güvenliği uzmanı görevlendirmemek"),
                            0,
                            "İşveren, çalışanların işle ilgili sağlık ve güvenliğini sağlamakla yükümlüdür ve öncelik riskleri kaynağında yok etmektir."
                        ),
                        QuizQuestion(
                            2,
                            "5x5 L-Tipi Risk Matrisinde Risk Skoru nasıl hesaplanır?",
                            listOf("Risk Skoru = Olasılık (1-5) x Şiddet (1-5)", "Risk Skoru = Maliyet / Süre", "Risk Skoru = Çalışan Sayısı x Vardiya", "Risk Skoru = Tehlike + Kaza Sayısı"),
                            0,
                            "Risk (R) = Olasılık (O) x Şiddet (Ş). 1-25 puan arası matris sınıflandırması yapılır."
                        ),
                        QuizQuestion(
                            3,
                            "Gürültü Yönetmeliğine göre En Düşük Maruziyet Eylem Değeri kaç dB(A)'dir?",
                            listOf("80 dB(A)", "85 dB(A)", "87 dB(A)", "90 dB(A)"),
                            0,
                            "En düşük maruziyet eylem değeri: 80 dB(A), En yüksek maruziyet eylem değeri: 85 dB(A), Sınır değer: 87 dB(A)."
                        )
                    )
                }
            }
            topic.contains("Flutter") -> listOf(
                QuizQuestion(
                    1,
                    "Which Flutter widget is best suited to dynamically adapt between a desktop drawer and a mobile bottom navigation bar?",
                    listOf("LayoutBuilder", "SingleChildScrollView", "Column", "Align"),
                    0,
                    "LayoutBuilder provides BoxConstraints, allowing conditional tree construction based on screen width."
                ),
                QuizQuestion(
                    2,
                    "What compiler target enables near-native 60fps execution for Flutter Web in modern browsers?",
                    listOf("WASM (WebAssembly) with CanvasKit", "Dart2JS HTML DOM", "V8 Engine JIT", "WebKit Polyfill"),
                    0,
                    "WASM with CanvasKit compiles Dart to WebAssembly for multi-threaded, GPU-accelerated rendering."
                ),
                QuizQuestion(
                    3,
                    "In Riverpod 2.x, which provider is recommended for managing asynchronous remote mutations?",
                    listOf("AsyncNotifierProvider", "StateProvider", "FutureProvider", "ChangeNotifierProvider"),
                    0,
                    "AsyncNotifierProvider cleanly manages loading, data, and error states with type safety."
                )
            )
            topic.contains("Next.js") -> listOf(
                QuizQuestion(
                    1,
                    "By default, what type of component is created in the Next.js App Router (/app directory)?",
                    listOf("React Server Component (RSC)", "Client Component", "Static HTML Hook", "Hydrated Fragment"),
                    0,
                    "All components in the Next.js App Router default to Server Components, sending 0kb JS to the browser."
                ),
                QuizQuestion(
                    2,
                    "Which directive turns an async function into a Next.js Server Action callable directly from forms?",
                    listOf("'use server'", "'use client'", "'use action'", "'export server'"),
                    0,
                    "'use server' defines a Server Action for direct, secure server-side execution without API route boilerplate."
                ),
                QuizQuestion(
                    3,
                    "How does Next.js incremental static regeneration (ISR) handle cached pages?",
                    listOf("Serves stale cache while revalidating in background", "Rebuilds the entire site synchronously", "Disables browser cache", "Requires cold server restarts"),
                    0,
                    "ISR serves stale cache instantly to users while asynchronously re-rendering and caching the new page."
                )
            )
            topic.contains("Firebase") || topic.contains("Database") -> listOf(
                QuizQuestion(
                    1,
                    "In Cloud Firestore Security Rules, how do you verify that the user is authenticated and modifying their own document?",
                    listOf("request.auth != null && request.auth.uid == userId", "auth.isLoggedIn == true", "session.user == 'admin'", "allow write: true"),
                    0,
                    "request.auth contains the authenticated token; matching request.auth.uid ensures user document privacy."
                ),
                QuizQuestion(
                    2,
                    "What index type is required in Firestore when filtering on one field and ordering by another?",
                    listOf("Composite Index", "Single-Field Index", "B-Tree Primary Key", "Full-Text Search Index"),
                    0,
                    "Queries with range filters or inequality filters combined with order-by require a Composite Index."
                ),
                QuizQuestion(
                    3,
                    "Which Firebase product allows serverless event-driven background processing for database triggers?",
                    listOf("Cloud Functions for Firebase", "Firebase Hosting", "Firebase Crashlytics", "Firebase Remote Config"),
                    0,
                    "Cloud Functions triggers execute code automatically in response to database changes, auth events, or HTTP requests."
                )
            )
            else -> listOf(
                QuizQuestion(
                    1,
                    "Which Gemini model should you use as default for low latency, high throughput code generation and analysis?",
                    listOf("gemini-3.5-flash", "gemini-1.5-flash", "gemini-pro", "gemini-2.0-flash-thinking"),
                    0,
                    "gemini-3.5-flash is the premier modern fast model for text, code reasoning, and multi-turn workflows."
                ),
                QuizQuestion(
                    2,
                    "What prompt engineering technique provides concrete input-output demonstrations to guide the model?",
                    listOf("Few-Shot Prompting", "Zero-Shot Prompting", "Temperature Scaling", "Beam Search"),
                    0,
                    "Few-Shot prompting provides high-quality examples to establish desired format and domain context."
                ),
                QuizQuestion(
                    3,
                    "How do you enforce deterministic structured JSON responses from Gemini via REST API?",
                    listOf("Define responseFormat with application/json and schema", "Ask nicely in text prompt only", "Set temperature to 2.0", "Use cURL POST headers only"),
                    0,
                    "Configuring responseFormat with mimeType 'application/json' and a schema ensures type-safe JSON output."
                )
            )
        }
    }
}
