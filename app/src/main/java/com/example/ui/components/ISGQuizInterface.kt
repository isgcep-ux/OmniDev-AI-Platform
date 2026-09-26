package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.ISGContent
import com.example.ui.theme.DevAmber
import com.example.ui.theme.DevBlueLight
import com.example.ui.theme.DevEmeraldLight
import com.example.ui.theme.DevRose

/**
 * Question model dynamically synthesized from an [ISGContent] Room database entity.
 */
data class DynamicISGQuestion(
    val id: Long,
    val isgSource: ISGContent,
    val category: String,
    val questionText: String,
    val options: List<String>,
    val correctIndex: Int,
    val explanation: String
)

/**
 * Generator helper that turns a list of [ISGContent] Room database entries into
 * multi-choice practice questions with plausible distractors from other entries.
 */
object ISGQuestionGenerator {
    fun generateQuestions(lessons: List<ISGContent>): List<DynamicISGQuestion> {
        if (lessons.isEmpty()) return emptyList()

        return lessons.mapIndexed { index, item ->
            // Distractors chosen from other lessons or authentic OHS distractors
            val distractors = generateDistractorsFor(item, lessons.filter { it.id != item.id })

            // The correct answer formulation based on content summary
            val correctAnswer = formatCorrectAnswer(item)

            // Combine and shuffle options
            val allOptions = (listOf(correctAnswer) + distractors).take(4)
            // We can place the correct answer deterministically or based on index to keep tests consistent
            val targetIndex = (index) % allOptions.size
            val shiftedOptions = allOptions.toMutableList()
            if (targetIndex != 0) {
                val temp = shiftedOptions[0]
                shiftedOptions[0] = shiftedOptions[targetIndex]
                shiftedOptions[targetIndex] = temp
            }

            val questionPrompt = when {
                item.title.contains("Kanun", ignoreCase = true) || item.category.contains("Mevzuat", ignoreCase = true) ->
                    "6331 sayılı İSG Kanunu ve mevzuatına göre '${item.title}' ile ilgili temel esas aşağıdakilerden hangisidir?"
                item.title.contains("Matris", ignoreCase = true) || item.category.contains("Risk", ignoreCase = true) ->
                    "İş sağlığı ve güvenliği risk değerlendirmesinde '${item.title}' için uygulanan temel kural veya formül nedir?"
                item.title.contains("Gürültü", ignoreCase = true) || item.category.contains("Fiziksel", ignoreCase = true) ->
                    "İşyerlerinde gürültü ve fiziksel risk etmenleri yönetmeliği kapsamında '${item.title}' standardı nedir?"
                else ->
                    "İSG sınav standartları gereğince '${item.title}' konusunda geçerli tanım ve ilke aşağıdakilerden hangisidir?"
            }

            DynamicISGQuestion(
                id = item.id,
                isgSource = item,
                category = item.category,
                questionText = questionPrompt,
                options = shiftedOptions,
                correctIndex = targetIndex,
                explanation = item.content
            )
        }
    }

    private fun formatCorrectAnswer(item: ISGContent): String {
        // First sentence or first 120 chars as the definitive correct answer statement
        val firstSentence = item.content.split(".").firstOrNull()?.trim() ?: item.content
        return if (firstSentence.length > 110) {
            firstSentence.take(107) + "..."
        } else {
            firstSentence
        }
    }

    private fun generateDistractorsFor(target: ISGContent, others: List<ISGContent>): List<String> {
        val pool = mutableListOf<String>()

        // Try to draw from other lessons in Room DB first
        for (other in others) {
            val dist = formatCorrectAnswer(other)
            if (dist != formatCorrectAnswer(target) && !pool.contains(dist)) {
                pool.add(dist)
            }
            if (pool.size >= 3) break
        }

        // Fallback realistic OHS distractor options if Room has fewer than 4 items
        val fallbackPool = listOf(
            "Yalnızca iş kazası oluştuktan sonra geriye dönük idari bildirimde bulunulur.",
            "Tüm risk kontrol adımları atlanarak sadece kişisel koruyucu donanım (KKD) dağıtımıyla yetinilir.",
            "Risk skoru formülü: Risk = Hız x Ağırlık olarak hesaplanır ve yılda bir kez gözden geçirilir.",
            "İşveren çalışan temsilcisinin görüşünü almadan tek taraflı karar verebilir.",
            "En yüksek maruziyet eylem değeri 65 dB(A) olarak kabul edilir ve tedbir alınmaz."
        )

        for (fallback in fallbackPool) {
            if (pool.size >= 3) break
            if (!pool.contains(fallback)) {
                pool.add(fallback)
            }
        }

        return pool.take(3)
    }
}

/**
 * Standalone, interactive Jetpack Compose Quiz component built upon the [ISGContent] Room database.
 *
 * Includes:
 * - Dynamic question synthesis from Room lessons
 * - Category filter chip row (Mevzuat, Risk, Fiziksel, vb.)
 * - Animated progress tracker & counter
 * - Immediate answer validation with color-coded feedback (Emerald/Rose)
 * - Educational explanation box linking back to legislation
 * - Final score summary with percentage mastery badge and restart option
 */
@Composable
fun ISGQuizInterface(
    isgLessons: List<ISGContent>,
    modifier: Modifier = Modifier,
    onSaveQuizScore: ((score: Int, total: Int, topic: String) -> Unit)? = null
) {
    // Generate questions dynamically whenever isgLessons changes
    val allQuestions = remember(isgLessons) {
        ISGQuestionGenerator.generateQuestions(isgLessons)
    }

    var selectedCategoryFilter by remember { mutableStateOf("Tümü") }
    var currentIndex by remember { mutableIntStateOf(0) }
    var selectedOption by remember { mutableStateOf<Int?>(null) }
    var isSubmitted by remember { mutableStateOf(false) }
    var correctScore by remember { mutableIntStateOf(0) }
    var isQuizFinished by remember { mutableStateOf(false) }

    // Filter questions if user selects a specific category
    val filteredQuestions = remember(allQuestions, selectedCategoryFilter) {
        if (selectedCategoryFilter == "Tümü") {
            allQuestions
        } else {
            allQuestions.filter { it.category.contains(selectedCategoryFilter, ignoreCase = true) }
        }
    }

    val currentQuestion = filteredQuestions.getOrNull(currentIndex.coerceIn(0, (filteredQuestions.size - 1).coerceAtLeast(0)))

    // Reset state on category change
    fun restartQuiz(category: String = selectedCategoryFilter) {
        selectedCategoryFilter = category
        currentIndex = 0
        selectedOption = null
        isSubmitted = false
        correctScore = 0
        isQuizFinished = false
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("isg_quiz_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header: Title & Room DB Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(DevAmber.copy(alpha = 0.2f))
                            .border(1.dp, DevAmber, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Quiz,
                            contentDescription = null,
                            tint = DevAmber,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "İSG Sınav & Pratik Testi",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Room 'ISGContent' veritabanından dinamik üretilen sorular",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF38BDF8)
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = DevAmber.copy(alpha = 0.15f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DevAmber)
                ) {
                    Text(
                        text = "Skor: $correctScore / ${filteredQuestions.size}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = DevAmber,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Category Selector Chips
            val categories = remember(allQuestions) {
                listOf("Tümü") + allQuestions.map { it.category }.distinct()
            }
            if (categories.size > 2) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    categories.take(4).forEach { cat ->
                        val isSelected = selectedCategoryFilter == cat
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) DevAmber else Color(0xFF0F172A),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSelected) DevAmber else Color(0xFF334155)
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .height(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .clickable {
                                    if (selectedCategoryFilter != cat) {
                                        restartQuiz(cat)
                                    }
                                }
                                .testTag("quiz_cat_${cat.replace(" ", "_")}")
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = cat,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color(0xFF451A03) else Color(0xFFCBD5E1),
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))
            }

            // If empty
            if (filteredQuestions.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(Icons.Default.School, contentDescription = null, tint = DevAmber, modifier = Modifier.size(40.dp))
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Bu kategoride soru oluşturulacak Room DB kaydı bulunamadı.",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
                return@Column
            }

            // Quiz Body
            if (isQuizFinished) {
                // Final Score Summary View
                val totalQ = filteredQuestions.size
                val percentage = if (totalQ > 0) (correctScore * 100) / totalQ else 0
                val isSuccess = percentage >= 60

                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = if (isSuccess) Color(0xFF064E3B).copy(alpha = 0.35f) else Color(0xFF451A03).copy(alpha = 0.35f),
                    border = androidx.compose.foundation.BorderStroke(
                        1.5.dp,
                        if (isSuccess) DevEmeraldLight else DevAmber
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("quiz_summary_card")
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(if (isSuccess) DevEmeraldLight.copy(alpha = 0.2f) else DevAmber.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isSuccess) Icons.Default.EmojiEvents else Icons.Default.HelpOutline,
                                contentDescription = null,
                                tint = if (isSuccess) DevEmeraldLight else DevAmber,
                                modifier = Modifier.size(32.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = if (isSuccess) "Tebrikler! Test Başarıyla Tamamlandı" else "Test Tamamlandı! Biraz Daha Tekrar Gerekli",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (isSuccess) DevEmeraldLight else DevAmber,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Toplam $totalQ soruda $correctScore doğru cevap verdiniz (%$percentage Başarı Oranı).",
                            fontSize = 13.sp,
                            color = Color(0xFFF1F5F9),
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Score Breakdown Pill Row
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = DevEmeraldLight.copy(alpha = 0.15f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, DevEmeraldLight)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = DevEmeraldLight, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("$correctScore Doğru", fontSize = 12.sp, color = DevEmeraldLight, fontWeight = FontWeight.Bold)
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = DevRose.copy(alpha = 0.15f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, DevRose)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Cancel, contentDescription = null, tint = DevRose, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("${totalQ - correctScore} Yanlış", fontSize = 12.sp, color = DevRose, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        Button(
                            onClick = { restartQuiz() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isSuccess) DevEmeraldLight else DevAmber,
                                contentColor = Color(0xFF0F172A)
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth().height(44.dp).testTag("quiz_restart_button")
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Testi Yeniden Başlat", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }
            } else if (currentQuestion != null) {
                // Progress Bar
                val progressFraction = (currentIndex + 1).toFloat() / filteredQuestions.size.toFloat()
                val animatedProgress by animateFloatAsState(
                    targetValue = progressFraction,
                    animationSpec = tween(300, easing = FastOutSlowInEasing),
                    label = "quiz_progress"
                )

                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Soru ${currentIndex + 1} / ${filteredQuestions.size}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = DevAmber
                        )
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFF0284C7).copy(alpha = 0.2f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF38BDF8))
                        ) {
                            Text(
                                text = currentQuestion.category,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF38BDF8),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(Color(0xFF1E293B))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(animatedProgress)
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(Color(0xFFF59E0B), Color(0xFFFBBF24))
                                    )
                                )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Question Card
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = currentQuestion.questionText,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            lineHeight = 22.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Options List (A, B, C, D)
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    currentQuestion.options.forEachIndexed { optIdx, optionText ->
                        val isSelected = selectedOption == optIdx
                        val isCorrect = optIdx == currentQuestion.correctIndex

                        val borderCol = when {
                            isSubmitted && isCorrect -> DevEmeraldLight
                            isSubmitted && isSelected && !isCorrect -> DevRose
                            isSelected -> DevAmber
                            else -> Color(0xFF334155)
                        }

                        val bgCol = when {
                            isSubmitted && isCorrect -> Color(0xFF064E3B).copy(alpha = 0.35f)
                            isSubmitted && isSelected && !isCorrect -> Color(0xFF881337).copy(alpha = 0.35f)
                            isSelected -> DevAmber.copy(alpha = 0.15f)
                            else -> Color(0xFF0F172A)
                        }

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = bgCol,
                            border = androidx.compose.foundation.BorderStroke(1.dp, borderCol),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .clickable(enabled = !isSubmitted) {
                                    selectedOption = optIdx
                                }
                                .testTag("quiz_option_$optIdx")
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Option Letter Badge
                                Box(
                                    modifier = Modifier
                                        .size(26.dp)
                                        .clip(CircleShape)
                                        .background(
                                            when {
                                                isSubmitted && isCorrect -> DevEmeraldLight
                                                isSubmitted && isSelected && !isCorrect -> DevRose
                                                isSelected -> DevAmber
                                                else -> Color(0xFF1E293B)
                                            }
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = ('A' + optIdx).toString(),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected || (isSubmitted && isCorrect)) Color(0xFF0F172A) else Color(0xFFCBD5E1)
                                    )
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Text(
                                    text = optionText,
                                    fontSize = 13.sp,
                                    color = Color(0xFFF8FAFC),
                                    modifier = Modifier.weight(1f),
                                    lineHeight = 18.sp
                                )

                                if (isSubmitted) {
                                    if (isCorrect) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = "Doğru Cevap",
                                            tint = DevEmeraldLight,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    } else if (isSelected) {
                                        Icon(
                                            imageVector = Icons.Default.Cancel,
                                            contentDescription = "Yanlış Seçim",
                                            tint = DevRose,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Educational Explanation Box (Shows upon submission)
                AnimatedVisibility(
                    visible = isSubmitted,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFF0F172A),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Lightbulb,
                                    contentDescription = null,
                                    tint = DevEmeraldLight,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "İSG Mevzuat & Mantık Açıklaması",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = DevEmeraldLight
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = currentQuestion.explanation,
                                fontSize = 12.sp,
                                lineHeight = 17.sp,
                                color = Color(0xFFE2E8F0)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Bottom Action Buttons (Cevapla / Sonraki)
                if (!isSubmitted) {
                    Button(
                        onClick = {
                            if (selectedOption != null) {
                                isSubmitted = true
                                if (selectedOption == currentQuestion.correctIndex) {
                                    correctScore++
                                }
                            }
                        },
                        enabled = selectedOption != null,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = DevAmber,
                            disabledContainerColor = Color(0xFF334155),
                            contentColor = Color(0xFF451A03),
                            disabledContentColor = Color(0xFF64748B)
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("quiz_submit_btn")
                    ) {
                        Text("Cevabı Onayla", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                } else {
                    Button(
                        onClick = {
                            if (currentIndex + 1 < filteredQuestions.size) {
                                currentIndex++
                                selectedOption = null
                                isSubmitted = false
                            } else {
                                isQuizFinished = true
                                onSaveQuizScore?.invoke(correctScore, filteredQuestions.size, "İSG Room Pratik Testi")
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = DevBlueLight,
                            contentColor = Color(0xFF082F49)
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("quiz_next_btn")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = if (currentIndex + 1 < filteredQuestions.size) "Sonraki Soru" else "Sonuçları Gör",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(Icons.AutoMirrored.Filled.NavigateNext, contentDescription = null)
                        }
                    }
                }
            }
        }
    }
}
