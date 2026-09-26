package com.example.ui.screens.aistudio

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.DataObject
import androidx.compose.material.icons.filled.IntegrationInstructions
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.ChatMessageEntity
import com.example.ui.components.ApiKeyBanner
import com.example.ui.theme.DevAmber
import com.example.ui.theme.DevBlueLight
import com.example.ui.theme.DevEmeraldLight
import com.example.ui.theme.DevIndigoLight
import com.example.ui.theme.DevPurple
import com.example.ui.viewmodel.QuizQuestion

data class AiStudioTabItem(
    val title: String,
    val icon: ImageVector,
    val color: Color
)

@Composable
fun AiStudioScreen(
    currentSection: Int,
    onSelectSection: (Int) -> Unit,
    isGeminiConfigured: Boolean,
    customApiKey: String,
    onSaveApiKey: (String) -> Unit,
    // Prompt Studio
    systemInstruction: String,
    onSystemInstructionChange: (String) -> Unit,
    userPrompt: String,
    onUserPromptChange: (String) -> Unit,
    targetModel: String,
    temperature: Float,
    isPromptRunning: Boolean,
    promptResult: String?,
    onRunPrompt: () -> Unit,
    onSavePromptTemplate: (String, String, String, String) -> Unit,
    // Quiz Generator
    selectedQuizTopic: String,
    selectedQuizDifficulty: String,
    quizQuestions: List<QuizQuestion>,
    currentQuizIndex: Int,
    selectedOption: Int?,
    isQuizSubmitted: Boolean,
    quizScore: Int,
    isQuizFinished: Boolean,
    onTopicChange: (String) -> Unit,
    onDifficultyChange: (String) -> Unit,
    onSelectOption: (Int) -> Unit,
    onSubmitQuizAnswer: () -> Unit,
    onNextQuizQuestion: () -> Unit,
    onRestartQuiz: () -> Unit,
    // Chatbot Tutor
    selectedPersona: String,
    chatMessages: List<ChatMessageEntity>,
    isChatLoading: Boolean,
    onSelectPersona: (String) -> Unit,
    onSendChatMessage: (String) -> Unit,
    onClearChat: () -> Unit,
    // Snippet Saver
    onSaveSnippet: (String, String, String, String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    val tabs = listOf(
        AiStudioTabItem("Models", Icons.Default.AutoAwesome, DevBlueLight),
        AiStudioTabItem("Prompt Studio", Icons.Default.Tune, DevIndigoLight),
        AiStudioTabItem("AI Q&A Gen", Icons.Default.Quiz, DevAmber),
        AiStudioTabItem("Chatbot Tutor", Icons.Default.ChatBubble, DevPurple),
        AiStudioTabItem("Integration SDK", Icons.Default.IntegrationInstructions, DevEmeraldLight)
    )

    val verticalScrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(verticalScrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Status & Key Banner
        ApiKeyBanner(
            isConfigured = isGeminiConfigured || customApiKey.isNotBlank(),
            customApiKey = customApiKey,
            onSaveKey = onSaveApiKey
        )

        // Horizontal Scrollable Tabs
        ScrollableTabRow(
            selectedTabIndex = currentSection,
            edgePadding = 0.dp,
            containerColor = Color.Transparent,
            contentColor = DevBlueLight,
            indicator = { tabPositions ->
                if (currentSection < tabPositions.size) {
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[currentSection]),
                        color = tabs[currentSection].color,
                        height = 3.dp
                    )
                }
            },
            divider = {}
        ) {
            tabs.forEachIndexed { index, tabItem ->
                val isSelected = currentSection == index
                Tab(
                    selected = isSelected,
                    onClick = { onSelectSection(index) },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = tabItem.icon,
                                contentDescription = null,
                                tint = if (isSelected) tabItem.color else Color(0xFF64748B),
                                modifier = Modifier.padding(end = 6.dp)
                            )
                            Text(
                                text = tabItem.title,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 13.sp,
                                color = if (isSelected) MaterialTheme.colorScheme.onSurface else Color(0xFF94A3B8)
                            )
                        }
                    },
                    modifier = Modifier.testTag("aistudio_tab_$index")
                )
            }
        }

        // Section Content
        when (currentSection) {
            0 -> GeminiModelsSection()
            1 -> PromptStudioSection(
                systemInstruction = systemInstruction,
                onSystemInstructionChange = onSystemInstructionChange,
                userPrompt = userPrompt,
                onUserPromptChange = onUserPromptChange,
                targetModel = targetModel,
                temperature = temperature,
                isPromptRunning = isPromptRunning,
                promptResult = promptResult,
                onRunPrompt = onRunPrompt,
                onSavePromptTemplate = onSavePromptTemplate
            )
            2 -> AiQuizGeneratorSection(
                selectedTopic = selectedQuizTopic,
                selectedDifficulty = selectedQuizDifficulty,
                questions = quizQuestions,
                currentIndex = currentQuizIndex,
                selectedOption = selectedOption,
                isSubmitted = isQuizSubmitted,
                score = quizScore,
                isFinished = isQuizFinished,
                onTopicChange = onTopicChange,
                onDifficultyChange = onDifficultyChange,
                onSelectOption = onSelectOption,
                onSubmitAnswer = onSubmitQuizAnswer,
                onNextQuestion = onNextQuizQuestion,
                onRestartQuiz = onRestartQuiz
            )
            3 -> AiTutorChatSection(
                selectedPersona = selectedPersona,
                messages = chatMessages,
                isLoading = isChatLoading,
                onSelectPersona = onSelectPersona,
                onSendMessage = onSendChatMessage,
                onClearChat = onClearChat
            )
            4 -> IntegrationSdkSection(onSaveSnippet = onSaveSnippet)
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}
