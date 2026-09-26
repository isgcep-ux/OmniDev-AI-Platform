package com.example.ui.screens.saved

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.LibraryBooks
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.HistoryEdu
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.ISGContent
import com.example.data.local.entity.PromptTemplate
import com.example.data.local.entity.QuizResult
import com.example.data.local.entity.SavedSnippet
import com.example.ui.components.CodeBlockView
import com.example.ui.components.GlassmorphicCard
import com.example.ui.components.ISGContentExplorerSection
import com.example.ui.components.ISGFlashcardDeck
import com.example.ui.components.ISGQuizInterface
import com.example.ui.components.KKDHierarchyComponent
import com.example.ui.components.RiskMatrix5x5Component
import com.example.ui.components.StatusBadge
import com.example.ui.theme.DevAmber
import com.example.ui.theme.DevBlueLight
import com.example.ui.theme.DevEmeraldLight
import com.example.ui.theme.DevIndigoLight
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun SavedWorkScreen(
    snippets: List<SavedSnippet>,
    prompts: List<PromptTemplate>,
    quizHistory: List<QuizResult>,
    isgLessons: List<ISGContent> = emptyList(),
    onDeleteSnippet: (Long) -> Unit,
    onDeletePrompt: (Long) -> Unit,
    onLoadPrompt: (PromptTemplate) -> Unit,
    onSaveQuizResult: ((score: Int, total: Int, topic: String) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var selectedCategory by remember { mutableStateOf("Snippets") }
    val dateFormat = remember { SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault()) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Card
        GlassmorphicCard(
            borderColor = DevBlueLight.copy(alpha = 0.4f),
            backgroundColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.LibraryBooks,
                            contentDescription = null,
                            tint = DevBlueLight,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Room Persistence & İSG Library",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    StatusBadge(text = "${snippets.size} Code / ${isgLessons.size} İSG", color = DevEmeraldLight)
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Yerel Room veritabanında saklanan kod blueprints, 5x5 L-tipi risk analiz matrisi, KKD hiyerarşisi ve İSG ders içerikleri.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Category Switcher
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            listOf("Snippets", "Prompts", "5x5 Matrix", "KKD Tree", "İSG Arama", "Flashcards", "İSG Quiz", "Quiz History").forEach { cat ->
                val isSelected = selectedCategory == cat
                Button(
                    onClick = { selectedCategory = cat },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isSelected) DevBlueLight else MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = if (isSelected) Color(0xFF082F49) else MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.height(38.dp).testTag("saved_cat_${cat.replace(" ", "_")}")
                ) {
                    Text(cat, fontSize = 11.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                }
            }
        }

        when (selectedCategory) {
            "Snippets" -> {
                if (snippets.isEmpty()) {
                    EmptyStateCard(message = "No saved snippets yet. Bookmark any code snippet from Flutter, Next.js, or Firebase!")
                } else {
                    snippets.forEach { snippet ->
                        Column(modifier = Modifier.fillMaxWidth().testTag("saved_snippet_${snippet.id}")) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = snippet.title,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "${snippet.category} • ${dateFormat.format(Date(snippet.timestamp))}",
                                        fontSize = 11.sp,
                                        color = Color(0xFF94A3B8)
                                    )
                                }
                                IconButton(
                                    onClick = { onDeleteSnippet(snippet.id) },
                                    modifier = Modifier.size(32.dp).testTag("delete_snippet_${snippet.id}")
                                ) {
                                    Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = Color(0xFFF43F5E))
                                }
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            CodeBlockView(
                                code = snippet.code,
                                language = snippet.language,
                                isSaved = true
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }
                }
            }
            "Prompts" -> {
                if (prompts.isEmpty()) {
                    EmptyStateCard(message = "No prompt templates saved yet. Create and save prompt templates in Prompt Studio!")
                } else {
                    prompts.forEach { prompt ->
                        GlassmorphicCard(
                            borderColor = Color(0xFF475569),
                            backgroundColor = MaterialTheme.colorScheme.surface,
                            modifier = Modifier.testTag("saved_prompt_${prompt.id}")
                        ) {
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = prompt.title,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = DevIndigoLight
                                    )
                                    IconButton(
                                        onClick = { onDeletePrompt(prompt.id) },
                                        modifier = Modifier.size(32.dp).testTag("delete_prompt_${prompt.id}")
                                    ) {
                                        Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = Color(0xFFF43F5E))
                                    }
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "System: ${prompt.systemInstruction}",
                                    fontSize = 12.sp,
                                    color = Color(0xFFE2E8F0),
                                    lineHeight = 18.sp
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "User: ${prompt.userPrompt}",
                                    fontSize = 12.sp,
                                    color = Color(0xFF94A3B8),
                                    fontFamily = FontFamily.Monospace
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Button(
                                    onClick = { onLoadPrompt(prompt) },
                                    colors = ButtonDefaults.buttonColors(containerColor = DevIndigoLight),
                                    shape = RoundedCornerShape(6.dp),
                                    modifier = Modifier.height(34.dp).testTag("load_prompt_btn_${prompt.id}")
                                ) {
                                    Text("Load into Studio", fontSize = 11.sp, color = Color(0xFF1E1B4B), fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
            "5x5 Matrix" -> {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    RiskMatrix5x5Component()

                    // Additional ISG Lessons from Room database
                    if (isgLessons.isNotEmpty()) {
                        Text(
                            text = "İş Sağlığı ve Güvenliği Dersleri (Room DB)",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(top = 8.dp)
                        )

                        isgLessons.forEach { lesson ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("isg_lesson_${lesson.id}"),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                                ),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155))
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = lesson.title,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            modifier = Modifier.weight(1f)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        StatusBadge(
                                            text = lesson.category,
                                            color = DevBlueLight
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = lesson.content,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        lineHeight = 18.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
            "KKD Tree" -> {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    KKDHierarchyComponent()
                }
            }
            "İSG Arama" -> {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    ISGContentExplorerSection(
                        lessons = isgLessons,
                        onTopicSelected = {
                            // Can route or switch tab
                        }
                    )
                }
            }
            "Flashcards" -> {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    ISGFlashcardDeck(
                        cards = isgLessons
                    )
                }
            }
            "İSG Quiz" -> {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    ISGQuizInterface(
                        isgLessons = isgLessons,
                        onSaveQuizScore = onSaveQuizResult
                    )
                }
            }
            else -> {
                if (quizHistory.isEmpty()) {
                    EmptyStateCard(message = "No assessments taken yet. Complete quizzes in AI Q&A Gen to track your mastery!")
                } else {
                    quizHistory.forEach { result ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF0F172A))
                                .border(1.dp, Color(0xFF334155), RoundedCornerShape(12.dp))
                                .padding(14.dp)
                                .testTag("quiz_history_${result.id}")
                        ) {
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Quiz, contentDescription = null, tint = DevAmber, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "${result.topic} (${result.difficulty})",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = Color(0xFFF8FAFC)
                                        )
                                    }
                                    StatusBadge(
                                        text = "${result.score}/${result.totalQuestions} Correct",
                                        color = if (result.score == result.totalQuestions) DevEmeraldLight else DevAmber
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = result.feedback,
                                    fontSize = 12.sp,
                                    color = Color(0xFFCBD5E1)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = dateFormat.format(Date(result.timestamp)),
                                    fontSize = 10.sp,
                                    color = Color(0xFF64748B),
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun EmptyStateCard(message: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF0F172A))
            .border(1.dp, Color(0xFF334155), RoundedCornerShape(12.dp))
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = message,
            fontSize = 13.sp,
            color = Color(0xFF94A3B8),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}
