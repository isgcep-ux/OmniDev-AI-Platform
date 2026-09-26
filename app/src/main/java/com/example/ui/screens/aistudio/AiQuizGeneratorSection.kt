package com.example.ui.screens.aistudio

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.NavigateNext
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.GlassmorphicCard
import com.example.ui.components.StatusBadge
import com.example.ui.theme.DevAmber
import com.example.ui.theme.DevBlueLight
import com.example.ui.theme.DevEmeraldLight
import com.example.ui.theme.DevRose
import com.example.ui.viewmodel.QuizQuestion

@Composable
fun AiQuizGeneratorSection(
    selectedTopic: String,
    selectedDifficulty: String,
    questions: List<QuizQuestion>,
    currentIndex: Int,
    selectedOption: Int?,
    isSubmitted: Boolean,
    score: Int,
    isFinished: Boolean,
    onTopicChange: (String) -> Unit,
    onDifficultyChange: (String) -> Unit,
    onSelectOption: (Int) -> Unit,
    onSubmitAnswer: () -> Unit,
    onNextQuestion: () -> Unit,
    onRestartQuiz: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentQuestion = questions.getOrNull(currentIndex)

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Section Hero
        GlassmorphicCard(
            borderColor = DevAmber.copy(alpha = 0.4f),
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
                            imageVector = Icons.Default.Quiz,
                            contentDescription = null,
                            tint = DevAmber,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "AI Technical Q&A Generator",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    StatusBadge(text = "Score: $score/${questions.size}", color = DevAmber)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Generate technical interview challenges, architectural assessments, and full-stack quizzes across Project IDX & AI Studio frameworks.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Topic Selector Tabs
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            listOf("Flutter", "Next.js", "Firebase", "Gemini AI", "İSG Sınav").forEach { topic ->
                val isSelected = selectedTopic == topic
                Button(
                    onClick = { onTopicChange(topic) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isSelected) DevAmber else MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = if (isSelected) Color(0xFF451A03) else MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f).height(38.dp).testTag("quiz_topic_${topic.replace(" ", "_")}")
                ) {
                    Text(topic, fontSize = 10.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                }
            }
        }

        // Quiz Container
        if (isFinished) {
            GlassmorphicCard(
                borderColor = DevEmeraldLight.copy(alpha = 0.5f),
                backgroundColor = Color(0xFF064E3B).copy(alpha = 0.2f)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = DevEmeraldLight, modifier = Modifier.size(48.dp))
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Assessment Completed!",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = DevEmeraldLight
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "You scored $score out of ${questions.size} in $selectedTopic ($selectedDifficulty)",
                        fontSize = 14.sp,
                        color = Color(0xFFE2E8F0)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = onRestartQuiz,
                        colors = ButtonDefaults.buttonColors(containerColor = DevEmeraldLight),
                        modifier = Modifier.testTag("restart_quiz_btn")
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null, tint = Color(0xFF064E3B))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Retake Assessment", color = Color(0xFF064E3B), fontWeight = FontWeight.Bold)
                    }
                }
            }
        } else if (currentQuestion != null) {
            GlassmorphicCard(
                borderColor = Color(0xFF475569),
                backgroundColor = MaterialTheme.colorScheme.surface
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Question ${currentIndex + 1} of ${questions.size}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = DevAmber
                        )
                        StatusBadge(text = selectedDifficulty, color = DevBlueLight)
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = currentQuestion.question,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 22.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Options List
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        currentQuestion.options.forEachIndexed { optIndex, optionText ->
                            val isChosen = selectedOption == optIndex
                            val isCorrect = optIndex == currentQuestion.correctIndex
                            val borderCol = when {
                                isSubmitted && isCorrect -> DevEmeraldLight
                                isSubmitted && isChosen && !isCorrect -> DevRose
                                isChosen -> DevBlueLight
                                else -> Color(0xFF334155)
                            }
                            val bgCol = when {
                                isSubmitted && isCorrect -> Color(0xFF064E3B).copy(alpha = 0.3f)
                                isSubmitted && isChosen && !isCorrect -> Color(0xFF881337).copy(alpha = 0.3f)
                                isChosen -> DevBlueLight.copy(alpha = 0.15f)
                                else -> Color(0xFF0F172A)
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(bgCol)
                                    .border(1.dp, borderCol, RoundedCornerShape(10.dp))
                                    .clickable(enabled = !isSubmitted) { onSelectOption(optIndex) }
                                    .padding(12.dp)
                                    .testTag("quiz_option_$optIndex"),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(CircleShape)
                                        .background(if (isChosen) DevBlueLight else Color(0xFF1E293B)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = ('A' + optIndex).toString(),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isChosen) Color(0xFF0F172A) else Color(0xFFCBD5E1)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = optionText,
                                    fontSize = 13.sp,
                                    color = Color(0xFFF8FAFC),
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }

                    // Explanation Box
                    AnimatedVisibility(visible = isSubmitted) {
                        Column(modifier = Modifier.padding(top = 12.dp)) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFF0F172A))
                                    .border(1.dp, Color(0xFF334155), RoundedCornerShape(8.dp))
                                    .padding(10.dp)
                            ) {
                                Column {
                                    Text(
                                        text = "💡 Architectural Explanation",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = DevEmeraldLight
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = currentQuestion.explanation,
                                        fontSize = 12.sp,
                                        color = Color(0xFFCBD5E1)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Bottom Action Button
                    if (!isSubmitted) {
                        Button(
                            onClick = onSubmitAnswer,
                            enabled = selectedOption != null,
                            colors = ButtonDefaults.buttonColors(containerColor = DevAmber),
                            modifier = Modifier.fillMaxWidth().height(44.dp).testTag("submit_answer_btn")
                        ) {
                            Text("Submit Answer", color = Color(0xFF451A03), fontWeight = FontWeight.Bold)
                        }
                    } else {
                        Button(
                            onClick = onNextQuestion,
                            colors = ButtonDefaults.buttonColors(containerColor = DevBlueLight),
                            modifier = Modifier.fillMaxWidth().height(44.dp).testTag("next_question_btn")
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(if (currentIndex + 1 < questions.size) "Next Question" else "View Results", color = Color(0xFF082F49), fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(Icons.AutoMirrored.Filled.NavigateNext, contentDescription = null, tint = Color(0xFF082F49))
                            }
                        }
                    }
                }
            }
        }
    }
}
