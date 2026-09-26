package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Project
import com.example.ui.theme.DevAmber
import com.example.ui.theme.DevAmberLight
import com.example.ui.theme.DevBlueDark
import com.example.ui.theme.DevBlueLight
import com.example.ui.theme.DevEmeraldDark
import com.example.ui.theme.DevEmeraldLight
import kotlin.math.max

data class StatusDistributionItem(
    val status: String,
    val count: Int,
    val percentage: Float,
    val primaryColor: Color,
    val secondaryColor: Color
)

@Composable
fun RechartsBarChart(
    projects: List<Project>,
    selectedStatusFilter: String?,
    onSelectStatusFilter: (String?) -> Unit,
    modifier: Modifier = Modifier
) {
    val totalCount = projects.size
    val inProgressCount = projects.count { it.status == Project.STATUS_IN_PROGRESS }
    val completedCount = projects.count { it.status == Project.STATUS_COMPLETED }
    val onHoldCount = projects.count { it.status == Project.STATUS_ON_HOLD }

    val inProgressPct = if (totalCount > 0) (inProgressCount * 100f / totalCount) else 0f
    val completedPct = if (totalCount > 0) (completedCount * 100f / totalCount) else 0f
    val onHoldPct = if (totalCount > 0) (onHoldCount * 100f / totalCount) else 0f

    val distribution = remember(projects) {
        listOf(
            StatusDistributionItem(
                status = Project.STATUS_IN_PROGRESS,
                count = inProgressCount,
                percentage = inProgressPct,
                primaryColor = DevBlueLight,
                secondaryColor = DevBlueDark
            ),
            StatusDistributionItem(
                status = Project.STATUS_COMPLETED,
                count = completedCount,
                percentage = completedPct,
                primaryColor = DevEmeraldLight,
                secondaryColor = DevEmeraldDark
            ),
            StatusDistributionItem(
                status = Project.STATUS_ON_HOLD,
                count = onHoldCount,
                percentage = onHoldPct,
                primaryColor = DevAmberLight,
                secondaryColor = DevAmber
            )
        )
    }

    var activeHoveredStatus by remember { mutableStateOf<StatusDistributionItem?>(null) }
    var animationTrigger by remember { mutableStateOf(false) }

    LaunchedEffect(projects) {
        animationTrigger = true
    }

    GlassmorphicCard(
        modifier = modifier
            .fillMaxWidth()
            .testTag("recharts_status_distribution_card"),
        borderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.25f),
        backgroundColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header with Recharts badge and summary
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                Brush.linearGradient(
                                    listOf(DevBlueLight.copy(alpha = 0.25f), DevEmeraldLight.copy(alpha = 0.15f))
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.BarChart,
                            contentDescription = null,
                            tint = DevBlueLight,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Project Status Distribution",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Text(
                            text = "Recharts Visualization • Interactive Bar Metrics",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                            fontSize = 11.sp
                        )
                    }
                }

                // Recharts Tag Pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(DevBlueLight.copy(alpha = 0.15f))
                        .border(1.dp, DevBlueLight.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "Total: $totalCount",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = DevBlueLight
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Active Tooltip / Inspect Panel (Recharts Tooltip style)
            val displayItem = activeHoveredStatus ?: distribution.firstOrNull { it.status == selectedStatusFilter }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.7f))
                    .border(
                        1.dp,
                        (displayItem?.primaryColor ?: MaterialTheme.colorScheme.outline).copy(alpha = 0.35f),
                        RoundedCornerShape(10.dp)
                    )
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                if (displayItem != null) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(displayItem.primaryColor)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = displayItem.status,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "•  ${displayItem.count} ${if (displayItem.count == 1) "project" else "projects"}",
                                fontSize = 12.sp,
                                color = displayItem.primaryColor,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Share: ${"%.1f".format(displayItem.percentage)}%",
                                fontSize = 12.sp,
                                fontFamily = FontFamily.Monospace,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            if (selectedStatusFilter == displayItem.status) {
                                Spacer(modifier = Modifier.width(6.dp))
                                StatusBadge(text = "FILTER ACTIVE", color = displayItem.primaryColor)
                            }
                        }
                    }
                } else {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Tap any bar to inspect metrics or filter the project list",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Recharts Chart Body with Cartesian Grid and Animated Bars
            val maxCount = max(1, distribution.maxOfOrNull { it.count } ?: 1)
            // Ceiling for grid lines
            val yAxisMax = ((maxCount + 1) / 2) * 2 + 1

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(170.dp)
                    .padding(vertical = 4.dp)
            ) {
                // Background Cartesian Grid Lines (Recharts CartesianGrid style)
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val width = size.width
                    val height = size.height - 24.dp.toPx()
                    val gridSteps = 4
                    val pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f), 0f)

                    for (i in 0..gridSteps) {
                        val y = height * (i.toFloat() / gridSteps)
                        drawLine(
                            color = Color(0xFF64748B).copy(alpha = 0.2f),
                            start = Offset(32.dp.toPx(), y),
                            end = Offset(width, y),
                            strokeWidth = 1.dp.toPx(),
                            pathEffect = pathEffect
                        )
                    }
                }

                // Y-Axis Labels
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(bottom = 26.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "$yAxisMax",
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        modifier = Modifier.width(26.dp)
                    )
                    Text(
                        text = "${yAxisMax / 2}",
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        modifier = Modifier.width(26.dp)
                    )
                    Text(
                        text = "0",
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        modifier = Modifier.width(26.dp)
                    )
                }

                // Bars Container
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(start = 32.dp, bottom = 4.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.Bottom
                ) {
                    distribution.forEach { item ->
                        val targetHeightRatio = (item.count.toFloat() / yAxisMax).coerceIn(0.04f, 1f)
                        val animatedHeightRatio by animateFloatAsState(
                            targetValue = if (animationTrigger) targetHeightRatio else 0f,
                            animationSpec = tween(durationMillis = 650, easing = FastOutSlowInEasing),
                            label = "bar_height_${item.status}"
                        )

                        val isSelected = selectedStatusFilter == item.status
                        val isHovered = activeHoveredStatus?.status == item.status

                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) {
                                    activeHoveredStatus = item
                                    if (selectedStatusFilter == item.status) {
                                        onSelectStatusFilter(null) // unfilter
                                    } else {
                                        onSelectStatusFilter(item.status)
                                    }
                                }
                                .padding(horizontal = 10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Bottom
                        ) {
                            // Value bubble on top of bar
                            Box(
                                modifier = Modifier
                                    .padding(bottom = 4.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(
                                        if (isSelected || isHovered) item.primaryColor.copy(alpha = 0.25f)
                                        else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
                                    )
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "${item.count}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected || isHovered) item.primaryColor else MaterialTheme.colorScheme.onSurface
                                )
                            }

                            // The Bar
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .fillMaxHeight(0.68f * animatedHeightRatio)
                                    .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp, bottomStart = 2.dp, bottomEnd = 2.dp))
                                    .background(
                                        Brush.verticalGradient(
                                            colors = listOf(
                                                item.primaryColor,
                                                item.secondaryColor.copy(alpha = if (isSelected || isHovered) 0.9f else 0.65f)
                                            )
                                        )
                                    )
                                    .border(
                                        width = if (isSelected) 2.dp else if (isHovered) 1.5.dp else 0.dp,
                                        color = if (isSelected) Color.White else item.primaryColor,
                                        shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp, bottomStart = 2.dp, bottomEnd = 2.dp)
                                    )
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            // X-Axis Category Label
                            Text(
                                text = when (item.status) {
                                    Project.STATUS_IN_PROGRESS -> "In Progress"
                                    Project.STATUS_COMPLETED -> "Completed"
                                    Project.STATUS_ON_HOLD -> "On Hold"
                                    else -> item.status
                                },
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) item.primaryColor else MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center,
                                maxLines = 1
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Recharts Legend Row (Interactive)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                distribution.forEach { item ->
                    val isSelected = selectedStatusFilter == item.status
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable {
                                if (isSelected) onSelectStatusFilter(null)
                                else onSelectStatusFilter(item.status)
                            }
                            .background(
                                if (isSelected) item.primaryColor.copy(alpha = 0.15f)
                                else Color.Transparent
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(item.primaryColor)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "${item.status} (${item.count})",
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) item.primaryColor else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}
