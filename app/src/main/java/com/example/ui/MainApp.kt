package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.DeveloperMode
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.StatusBadge
import com.example.ui.screens.aistudio.AiStudioScreen
import com.example.ui.screens.auth.LoginScreen
import com.example.ui.screens.idx.IdxDevelopmentScreen
import com.example.ui.screens.projects.ProjectDashboard
import com.example.ui.screens.saved.SavedWorkScreen
import com.example.ui.screens.team.TeamAccessControlScreen
import com.example.ui.screens.workstation.DevOsWorkstationScreen
import com.example.ui.theme.DevAmber
import com.example.ui.theme.DevBlueLight
import com.example.ui.theme.DevEmeraldLight
import com.example.ui.theme.DevIndigoLight
import com.example.ui.theme.DevRose
import com.example.ui.viewmodel.DevStudioViewModel

data class NavItem(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val tag: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainApp(
    viewModel: DevStudioViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val currentMainTab by viewModel.currentMainTab.collectAsStateWithLifecycle()
    val currentIdxSection by viewModel.currentIdxSection.collectAsStateWithLifecycle()
    val currentAiStudioSection by viewModel.currentAiStudioSection.collectAsStateWithLifecycle()

    // Auth state
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val isGuestMode by viewModel.isGuestMode.collectAsStateWithLifecycle()
    val isAuthLoading by viewModel.isAuthLoading.collectAsStateWithLifecycle()
    val authErrorMessage by viewModel.authErrorMessage.collectAsStateWithLifecycle()
    val showLoginScreen by viewModel.showLoginScreen.collectAsStateWithLifecycle()

    val savedSnippets by viewModel.savedSnippets.collectAsStateWithLifecycle()
    val promptTemplates by viewModel.promptTemplates.collectAsStateWithLifecycle()
    val quizHistory by viewModel.quizHistory.collectAsStateWithLifecycle()
    val isgLessons by viewModel.isgLessons.collectAsStateWithLifecycle()
    val deploymentTasks by viewModel.deploymentTasks.collectAsStateWithLifecycle()

    // Team and RBAC state
    val teamMembers by viewModel.teamMembers.collectAsStateWithLifecycle()
    val resourcePolicies by viewModel.resourcePolicies.collectAsStateWithLifecycle()
    val activeInviteTokens by viewModel.activeInviteTokens.collectAsStateWithLifecycle()

    val customApiKey by viewModel.customApiKey.collectAsStateWithLifecycle()
    val isGeminiConfigured by viewModel.isGeminiConfigured.collectAsStateWithLifecycle()

    // Prompt Studio state
    val promptStudioSystem by viewModel.promptStudioSystem.collectAsStateWithLifecycle()
    val promptStudioUser by viewModel.promptStudioUser.collectAsStateWithLifecycle()
    val promptStudioModel by viewModel.promptStudioModel.collectAsStateWithLifecycle()
    val promptStudioTemperature by viewModel.promptStudioTemperature.collectAsStateWithLifecycle()
    val promptResult by viewModel.promptResult.collectAsStateWithLifecycle()
    val isPromptRunning by viewModel.isPromptRunning.collectAsStateWithLifecycle()

    // Quiz state
    val selectedQuizTopic by viewModel.selectedQuizTopic.collectAsStateWithLifecycle()
    val selectedQuizDifficulty by viewModel.selectedQuizDifficulty.collectAsStateWithLifecycle()
    val quizQuestions by viewModel.quizQuestions.collectAsStateWithLifecycle()
    val currentQuizIndex by viewModel.currentQuestionIndex.collectAsStateWithLifecycle()
    val userSelectedOption by viewModel.userSelectedOption.collectAsStateWithLifecycle()
    val isAnswerSubmitted by viewModel.isAnswerSubmitted.collectAsStateWithLifecycle()
    val scoreCount by viewModel.scoreCount.collectAsStateWithLifecycle()
    val isQuizFinished by viewModel.isQuizFinished.collectAsStateWithLifecycle()

    // Chatbot state
    val selectedPersona by viewModel.selectedPersona.collectAsStateWithLifecycle()
    val chatMessages by viewModel.chatMessages.collectAsStateWithLifecycle()
    val isChatLoading by viewModel.isChatLoading.collectAsStateWithLifecycle()

    // Tests state
    val testSuiteList by viewModel.testSuiteList.collectAsStateWithLifecycle()
    val isTestsRunning by viewModel.isTestsRunning.collectAsStateWithLifecycle()

    // DevOS Workstation state
    val terminalEntries by viewModel.terminalEntries.collectAsStateWithLifecycle()
    val terminalInput by viewModel.terminalInput.collectAsStateWithLifecycle()

    // Cloud Firestore Projects state
    val projects by viewModel.projects.collectAsStateWithLifecycle()
    val isFirestoreActive by viewModel.isFirestoreActive.collectAsStateWithLifecycle()
    val syncStatusMessage by viewModel.syncStatusMessage.collectAsStateWithLifecycle()

    val navItems = listOf(
        NavItem("Dashboard", "Projeler & Recharts", Icons.Default.Dashboard, "nav_projects"),
        NavItem("DevOS", "Workstation & CLI", Icons.Default.Computer, "nav_devos"),
        NavItem("Primary Dev", "idx.google.com", Icons.Default.Terminal, "nav_idx"),
        NavItem("AI Studio", "aistudio.google.com", Icons.Default.AutoAwesome, "nav_aistudio"),
        NavItem("Ekip & RBAC", "RBAC Sharing", Icons.Default.Group, "nav_team"),
        NavItem("Kütüphane", "Room Local DB", Icons.Default.Bookmark, "nav_saved")
    )

    Scaffold(
        modifier = modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(androidx.compose.foundation.shape.RoundedCornerShape(8.dp))
                                .background(
                                    Brush.linearGradient(
                                        listOf(DevBlueLight, Color(0xFF7B61FF))
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "O",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 16.sp,
                                color = Color.White
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "OmniDev ",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 17.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "AI",
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 17.sp,
                                        color = DevBlueLight
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                StatusBadge(
                                    text = when (currentMainTab) {
                                        0 -> "Dashboard"
                                        1 -> "DevOS CLI"
                                        2 -> "IDX"
                                        3 -> "Gemini 3.5"
                                        4 -> "Team RBAC"
                                        5 -> "Room DB"
                                        else -> if (currentUser != null) "Auth" else "Guest"
                                    },
                                    color = when (currentMainTab) {
                                        0 -> DevBlueLight
                                        1 -> DevEmeraldLight
                                        2 -> DevBlueLight
                                        3 -> DevIndigoLight
                                        4 -> DevRose
                                        5 -> DevEmeraldLight
                                        else -> if (currentUser != null) DevEmeraldLight else DevAmber
                                    }
                                )
                            }
                            Text(
                                text = if (currentUser != null) {
                                    "${currentUser?.email ?: "Signed in"} (${viewModel.getCurrentUserRole()})"
                                } else {
                                    "Yeni Nesil Yapay Zeka & Bulut Geliştirici Mimarisi"
                                },
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        IconButton(
                            onClick = { viewModel.currentMainTab.value = 6 },
                            modifier = Modifier.testTag("topbar_auth_btn")
                        ) {
                            Icon(
                                imageVector = if (currentUser != null) Icons.Default.AccountCircle else Icons.Default.Lock,
                                contentDescription = "Authentication Profile",
                                tint = if (currentUser != null) DevEmeraldLight else DevBlueLight
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                ),
                modifier = Modifier.windowInsetsPadding(WindowInsets.statusBars)
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .testTag("main_navigation_bar")
            ) {
                navItems.forEachIndexed { index, item ->
                    val isSelected = currentMainTab == index
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { viewModel.currentMainTab.value = index },
                        icon = {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.title,
                                tint = if (isSelected) DevBlueLight else Color(0xFF94A3B8)
                            )
                        },
                        label = {
                            Text(
                                text = item.title,
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) DevBlueLight else Color(0xFF94A3B8),
                                maxLines = 1
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = DevBlueLight,
                            indicatorColor = DevBlueLight.copy(alpha = 0.2f)
                        ),
                        modifier = Modifier.testTag(item.tag)
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            when (currentMainTab) {
                0 -> ProjectDashboard(
                    projects = projects,
                    isFirestoreActive = isFirestoreActive,
                    syncStatusMessage = syncStatusMessage,
                    onAddProject = { name, desc, status, tech ->
                        viewModel.addProject(name, desc, status, tech)
                    },
                    onUpdateProjectStatus = { projId, status ->
                        viewModel.updateProjectStatus(projId, status)
                    },
                    onDeleteProject = { projId ->
                        viewModel.deleteProject(projId)
                    },
                    onNavigateToWorkstation = { viewModel.currentMainTab.value = 1 },
                    onNavigateToAiStudio = { viewModel.currentMainTab.value = 3 }
                )
                1 -> DevOsWorkstationScreen(
                    terminalEntries = terminalEntries,
                    terminalInput = terminalInput,
                    onTerminalInputChange = { viewModel.setTerminalInput(it) },
                    onExecuteCommand = { viewModel.executeTerminalCommand(it) },
                    onClearTerminal = { viewModel.clearTerminal() },
                    onNavigateTab = { targetTab -> viewModel.currentMainTab.value = targetTab },
                    onSaveSnippet = { title, lang, cat, code, desc ->
                        viewModel.saveSnippet(title, lang, cat, code, desc)
                    }
                )
                2 -> IdxDevelopmentScreen(
                    currentSection = currentIdxSection,
                    onSelectSection = { viewModel.currentIdxSection.value = it },
                    testSuite = testSuiteList,
                    isTestsRunning = isTestsRunning,
                    onRunAllTests = { viewModel.runAllTests() },
                    deploymentTasks = deploymentTasks,
                    onToggleDeploymentTask = { viewModel.toggleDeploymentTask(it) },
                    onSaveSnippet = { title, lang, cat, code, desc ->
                        viewModel.saveSnippet(title, lang, cat, code, desc)
                    }
                )
                3 -> AiStudioScreen(
                    currentSection = currentAiStudioSection,
                    onSelectSection = { viewModel.currentAiStudioSection.value = it },
                    isGeminiConfigured = isGeminiConfigured,
                    customApiKey = customApiKey,
                    onSaveApiKey = { viewModel.setCustomApiKey(it) },
                    systemInstruction = promptStudioSystem,
                    onSystemInstructionChange = { viewModel.promptStudioSystem.value = it },
                    userPrompt = promptStudioUser,
                    onUserPromptChange = { viewModel.promptStudioUser.value = it },
                    targetModel = promptStudioModel,
                    temperature = promptStudioTemperature,
                    isPromptRunning = isPromptRunning,
                    promptResult = promptResult,
                    onRunPrompt = { viewModel.runPromptTest() },
                    onSavePromptTemplate = { title, system, user, tag ->
                        viewModel.savePromptTemplate(title, system, user, tag)
                    },
                    selectedQuizTopic = selectedQuizTopic,
                    selectedQuizDifficulty = selectedQuizDifficulty,
                    quizQuestions = quizQuestions,
                    currentQuizIndex = currentQuizIndex,
                    selectedOption = userSelectedOption,
                    isQuizSubmitted = isAnswerSubmitted,
                    quizScore = scoreCount,
                    isQuizFinished = isQuizFinished,
                    onTopicChange = { viewModel.generateQuizQuestions(it, selectedQuizDifficulty) },
                    onDifficultyChange = { viewModel.generateQuizQuestions(selectedQuizTopic, it) },
                    onSelectOption = { viewModel.selectQuizOption(it) },
                    onSubmitQuizAnswer = { viewModel.submitQuizAnswer() },
                    onNextQuizQuestion = { viewModel.nextQuizQuestion() },
                    onRestartQuiz = { viewModel.generateQuizQuestions(selectedQuizTopic, selectedQuizDifficulty) },
                    selectedPersona = selectedPersona,
                    chatMessages = chatMessages,
                    isChatLoading = isChatLoading,
                    onSelectPersona = { viewModel.selectPersona(it) },
                    onSendChatMessage = { viewModel.sendChatMessage(it) },
                    onClearChat = { viewModel.clearChatHistory() },
                    onSaveSnippet = { title, lang, cat, code, desc ->
                        viewModel.saveSnippet(title, lang, cat, code, desc)
                    }
                )
                4 -> TeamAccessControlScreen(
                    teamMembers = teamMembers,
                    resourcePolicies = resourcePolicies,
                    activeInviteTokens = activeInviteTokens,
                    savedSnippets = savedSnippets,
                    promptTemplates = promptTemplates,
                    currentUserRole = viewModel.getCurrentUserRole(),
                    canManageTeam = viewModel.canManageTeam(),
                    onInviteMember = { name, email, role, dept ->
                        viewModel.inviteMember(name, email, role, dept)
                    },
                    onChangeMemberRole = { memberId, newRole ->
                        viewModel.changeMemberRole(memberId, newRole)
                    },
                    onRemoveMember = { memberId ->
                        viewModel.removeMember(memberId)
                    },
                    onCreateInviteLink = { role, days, maxUses ->
                        viewModel.createShareInvite(role, days, maxUses)
                    },
                    onRevokeInvite = { tokenId ->
                        viewModel.revokeInvite(tokenId)
                    },
                    onUpdateResourcePolicy = { resId, resType, title, vis, minRole ->
                        viewModel.updateResourcePolicy(resId, resType, title, vis, minRole)
                    },
                    onCopyText = { text, label ->
                        viewModel.copyToClipboard(context, text, label)
                    }
                )
                5 -> SavedWorkScreen(
                    snippets = savedSnippets,
                    prompts = promptTemplates,
                    quizHistory = quizHistory,
                    isgLessons = isgLessons,
                    onDeleteSnippet = { viewModel.deleteSnippet(it) },
                    onDeletePrompt = { viewModel.deletePromptTemplate(it) },
                    onLoadPrompt = { tmpl ->
                        viewModel.promptStudioSystem.value = tmpl.systemInstruction
                        viewModel.promptStudioUser.value = tmpl.userPrompt
                        viewModel.currentMainTab.value = 3
                        viewModel.currentAiStudioSection.value = 1
                    },
                    onSaveQuizResult = { score, total, topic ->
                        viewModel.recordIsgQuizResult(score, total, topic)
                    }
                )
                else -> LoginScreen(
                    isAuthenticated = currentUser != null,
                    currentUserEmail = currentUser?.email,
                    currentDisplayName = currentUser?.displayName,
                    isGuest = isGuestMode,
                    isLoading = isAuthLoading,
                    errorMessage = authErrorMessage,
                    onSignInEmail = { email, pass -> viewModel.signInWithEmail(email, pass) },
                    onSignUpEmail = { email, pass -> viewModel.signUpWithEmail(email, pass) },
                    onSignInGoogle = { ctx -> viewModel.signInWithGoogle(ctx) },
                    onContinueGuest = {
                        viewModel.continueAsGuest()
                        viewModel.currentMainTab.value = 0
                    },
                    onSignOut = { viewModel.signOut() },
                    onDismissError = { viewModel.dismissAuthError() },
                    onEnterWorkspace = { viewModel.currentMainTab.value = 0 }
                )
            }
        }
    }
}

