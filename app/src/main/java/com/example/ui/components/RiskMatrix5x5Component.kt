package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Represents the classified risk level for a 5x5 L-type matrix cell.
 */
enum class RiskLevel(
    val title: String,
    val titleTr: String,
    val scoreRange: String,
    val cellColor: Color,
    val textColor: Color,
    val icon: ImageVector,
    val actionText: String
) {
    LOW(
        title = "Low Risk (Acceptable)",
        titleTr = "Düşük Risk (Kabul Edilebilir)",
        scoreRange = "1 - 6",
        cellColor = Color(0xFF10B981), // Emerald Green
        textColor = Color(0xFFFFFFFF),
        icon = Icons.Default.CheckCircle,
        actionText = "Mevcut kontroller sürdürülür. Rutin izleme yeterlidir, acil aksiyon gerektirmez."
    ),
    MEDIUM(
        title = "Medium Risk (Attention Needed)",
        titleTr = "Orta Risk (Dikkate Değer)",
        scoreRange = "8 - 12",
        cellColor = Color(0xFFF59E0B), // Amber / Yellow
        textColor = Color(0xFFFFFFFF),
        icon = Icons.Default.WarningAmber,
        actionText = "Belirlenen önlemler vadeli plana alınmalıdır. Kontrol tedbirleri ivedilikle uygulanmalı, iyileştirme planı yapılmalıdır."
    ),
    HIGH(
        title = "High / Intolerable Risk",
        titleTr = "Yüksek / Katlanılamaz Risk",
        scoreRange = "15 - 25",
        cellColor = Color(0xFFEF4444), // Coral Red
        textColor = Color(0xFFFFFFFF),
        icon = Icons.Default.Warning,
        actionText = "İş derhal durdurulmalıdır! Risk kabul edilebilir düzeye indirilinceye kadar çalışmaya izin verilmez. Acil önlem şarttır."
    );

    companion object {
        fun fromScore(score: Int): RiskLevel {
            return when {
                score <= 6 -> LOW
                score in 8..12 -> MEDIUM
                else -> HIGH
            }
        }
    }
}

/**
 * Probability Scale Definition (Olasılık)
 */
val probabilityLabels = listOf(
    1 to "1 (Çok Küçük - Yılda bir)",
    2 to "2 (Küçük - Yılda birkaç kez)",
    3 to "3 (Orta - Ayda bir)",
    4 to "4 (Yüksek - Haftada bir)",
    5 to "5 (Çok Yüksek - Her gün)"
)

/**
 * Severity Scale Definition (Şiddet)
 */
val severityLabels = listOf(
    1 to "1 (Çok Hafif - İlkyardım)",
    2 to "2 (Hafif - Ayakta tedavi)",
    3 to "3 (Orta - Tedavi/Hastalık)",
    4 to "4 (Ciddi - Kalıcı maluliyet)",
    5 to "5 (Çok Ciddi - Ölüm)"
)

/**
 * Data class representing a cell in the 5x5 matrix
 */
data class MatrixCell(
    val probability: Int,
    val severity: Int,
    val score: Int = probability * severity,
    val level: RiskLevel = RiskLevel.fromScore(probability * severity)
)

/**
 * Interactive Jetpack Compose component visualizing the 5x5 Risk Assessment Matrix.
 * Allows users to tap any cell to view detailed definitions, calculation formulas, and recommended actions.
 */
@Composable
fun RiskMatrix5x5Component(
    modifier: Modifier = Modifier,
    initialProbability: Int = 3,
    initialSeverity: Int = 4,
    onCellSelected: ((MatrixCell) -> Unit)? = null
) {
    var selectedCell by remember {
        mutableStateOf(
            MatrixCell(
                probability = initialProbability,
                severity = initialSeverity
            )
        )
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("risk_matrix_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f)
        ),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "5x5 L-Tipi Risk Değerlendirme Matrisi",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Risk Skoru (R) = Olasılık (O) × Şiddet (Ş)",
                        style = MaterialTheme.typography.bodySmall,
                        fontFamily = FontFamily.Monospace,
                        color = Color(0xFF38BDF8)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = selectedCell.level.cellColor.copy(alpha = 0.2f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, selectedCell.level.cellColor)
                ) {
                    Text(
                        text = "Skor: ${selectedCell.score}",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 13.sp,
                        color = selectedCell.level.cellColor,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Axis Title: Olasılık (Y-Axis) & Matrix Container
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Vertical Y-Axis Label
                Text(
                    text = "OLASILIK (O) ↑",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF94A3B8),
                    modifier = Modifier.padding(end = 6.dp)
                )

                // Matrix Grid Table
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Render rows from Probability 5 (top) down to Probability 1 (bottom)
                    for (prob in 5 downTo 1) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Row Number Indicator (Probability)
                            Text(
                                text = "O$prob",
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF64748B),
                                modifier = Modifier.width(18.dp)
                            )

                            // 5 Columns: Severity 1 to 5
                            for (sev in 1..5) {
                                val score = prob * sev
                                val level = RiskLevel.fromScore(score)
                                val isSelected = selectedCell.probability == prob && selectedCell.severity == sev

                                val backgroundColor by animateColorAsState(
                                    targetValue = if (isSelected) level.cellColor else level.cellColor.copy(alpha = 0.85f),
                                    animationSpec = tween(durationMillis = 200),
                                    label = "cellBg"
                                )

                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .aspectRatio(1.15f)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(backgroundColor)
                                        .border(
                                            width = if (isSelected) 2.5.dp else 1.dp,
                                            color = if (isSelected) Color.White else Color.Black.copy(alpha = 0.25f),
                                            shape = RoundedCornerShape(6.dp)
                                        )
                                        .clickable {
                                            val cell = MatrixCell(prob, sev)
                                            selectedCell = cell
                                            onCellSelected?.invoke(cell)
                                        }
                                        .testTag("matrix_cell_${prob}_${sev}"),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Text(
                                            text = score.toString(),
                                            fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold,
                                            fontSize = if (isSelected) 14.sp else 12.sp,
                                            color = level.textColor
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // X-Axis Header (Severity 1 to 5)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Spacer(modifier = Modifier.width(18.dp))
                        for (sev in 1..5) {
                            Text(
                                text = "Ş$sev",
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF64748B),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    // Horizontal X-Axis Label
                    Text(
                        text = "→ ŞİDDET (Ş) 1'den 5'e",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF94A3B8),
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Details for Selected Cell
            AnimatedVisibility(visible = true) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("risk_detail_panel"),
                    shape = RoundedCornerShape(12.dp),
                    color = selectedCell.level.cellColor.copy(alpha = 0.12f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, selectedCell.level.cellColor.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = selectedCell.level.icon,
                                    contentDescription = null,
                                    tint = selectedCell.level.cellColor,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = selectedCell.level.titleTr,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = selectedCell.level.cellColor
                                )
                            }
                            Text(
                                text = "${selectedCell.probability} × ${selectedCell.severity} = ${selectedCell.score}",
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Parameter breakdown
                        val probText = probabilityLabels.firstOrNull { it.first == selectedCell.probability }?.second ?: ""
                        val sevText = severityLabels.firstOrNull { it.first == selectedCell.severity }?.second ?: ""

                        Row(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Olasılık Derecesi:",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = probText,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Şiddet Derecesi:",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = sevText,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Required Action / Sınav Notu
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = "Zorunlu Eylem / Aksiyon Prosedürü:",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = selectedCell.level.cellColor
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = selectedCell.level.actionText,
                                    fontSize = 12.sp,
                                    lineHeight = 16.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Legend indicators
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                RiskLevel.values().forEach { level ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(level.cellColor)
                        )
                        Text(
                            text = "${level.scoreRange}: ${level.title.substringBefore(" ")}",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}
