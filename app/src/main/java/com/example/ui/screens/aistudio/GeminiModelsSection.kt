package com.example.ui.screens.aistudio

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.GlassmorphicCard
import com.example.ui.components.StatusBadge
import com.example.ui.theme.DevAmber
import com.example.ui.theme.DevBlueLight
import com.example.ui.theme.DevEmeraldLight
import com.example.ui.theme.DevIndigoLight
import com.example.ui.theme.DevPurple

data class ModelSpec(
    val name: String,
    val alias: String,
    val taskType: String,
    val tokenLimit: String,
    val speedLatency: String,
    val icon: ImageVector,
    val color: Color
)

@Composable
fun GeminiModelsSection(
    modifier: Modifier = Modifier
) {
    val models = listOf(
        ModelSpec(
            name = "gemini-3.5-flash",
            alias = "Flash 3.5",
            taskType = "High-speed text, code generation, summarization & chat",
            tokenLimit = "1,048,576 tokens",
            speedLatency = "Ultra Fast (< 400ms TTFT)",
            icon = Icons.Default.Bolt,
            color = DevBlueLight
        ),
        ModelSpec(
            name = "gemini-3.1-pro-preview",
            alias = "Pro 3.1",
            taskType = "Complex STEM reasoning, deep architectural analysis",
            tokenLimit = "2,097,152 tokens",
            speedLatency = "Deep Compute (~1.2s)",
            icon = Icons.Default.Psychology,
            color = DevPurple
        ),
        ModelSpec(
            name = "gemini-2.5-flash-image",
            alias = "Flash Image",
            taskType = "Image generation, visual asset creation & editing",
            tokenLimit = "Multimodal In/Out",
            speedLatency = "Fast Synthesis (~2.0s)",
            icon = Icons.Default.Image,
            color = DevEmeraldLight
        ),
        ModelSpec(
            name = "gemini-2.5-flash-preview-tts",
            alias = "Flash TTS",
            taskType = "High-fidelity Text-To-Speech audio synthesis",
            tokenLimit = "Native Audio Out",
            speedLatency = "Real-time Stream",
            icon = Icons.Default.Mic,
            color = DevAmber
        ),
        ModelSpec(
            name = "veo-3.1-generate-preview",
            alias = "Veo 3.1",
            taskType = "High-definition video generation & cinematic clips",
            tokenLimit = "1080p Video Out",
            speedLatency = "Heavy Batch",
            icon = Icons.Default.Videocam,
            color = Color(0xFFF43F5E)
        )
    )

    var selectedModel by remember { mutableStateOf(models[0]) }
    var temperature by remember { mutableFloatStateOf(0.7f) }
    var topP by remember { mutableFloatStateOf(0.95f) }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Section Hero
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
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = DevBlueLight,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Gemini Models Explorer",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    StatusBadge(text = "Google GenAI", color = DevBlueLight)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Explore current generation Gemini model specifications, latency benchmarks, context windows, and tune runtime hyperparameters.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Model List Cards
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            models.forEach { model ->
                val isSelected = selectedModel.name == model.name
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSelected) model.color.copy(alpha = 0.15f) else Color(0xFF0F172A))
                        .border(
                            1.dp,
                            if (isSelected) model.color else Color(0xFF334155),
                            RoundedCornerShape(12.dp)
                        )
                        .padding(12.dp)
                        .testTag("model_card_${model.name}")
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(model.color.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(model.icon, contentDescription = null, tint = model.color, modifier = Modifier.size(20.dp))
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = model.name,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace,
                                        color = if (isSelected) model.color else Color(0xFFF8FAFC)
                                    )
                                }
                                Text(
                                    text = model.taskType,
                                    fontSize = 11.sp,
                                    color = Color(0xFF94A3B8)
                                )
                            }
                        }

                        Button(
                            onClick = { selectedModel = model },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isSelected) model.color else Color(0xFF1E293B),
                                contentColor = if (isSelected) Color(0xFF0F172A) else Color(0xFFCBD5E1)
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.height(34.dp)
                        ) {
                            Text(if (isSelected) "Active" else "Select", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Live Parameter Tuning Card
        GlassmorphicCard(
            borderColor = Color(0xFF475569),
            backgroundColor = MaterialTheme.colorScheme.surface
        ) {
            Column {
                Text(
                    text = "Hyperparameter Playground (${selectedModel.name})",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(12.dp))

                // Temperature Slider
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Temperature (Creativity):", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
                    Text(String.format("%.2f", temperature), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DevBlueLight)
                }
                Slider(
                    value = temperature,
                    onValueChange = { temperature = it },
                    valueRange = 0.0f..2.0f,
                    colors = SliderDefaults.colors(
                        thumbColor = DevBlueLight,
                        activeTrackColor = DevBlueLight,
                        inactiveTrackColor = Color(0xFF334155)
                    ),
                    modifier = Modifier.testTag("temperature_slider")
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Top-P Slider
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Top-P (Nucleus Sampling):", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
                    Text(String.format("%.2f", topP), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DevEmeraldLight)
                }
                Slider(
                    value = topP,
                    onValueChange = { topP = it },
                    valueRange = 0.0f..1.0f,
                    colors = SliderDefaults.colors(
                        thumbColor = DevEmeraldLight,
                        activeTrackColor = DevEmeraldLight,
                        inactiveTrackColor = Color(0xFF334155)
                    ),
                    modifier = Modifier.testTag("topp_slider")
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Benchmark Specs
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF0F172A))
                            .border(1.dp, Color(0xFF334155), RoundedCornerShape(8.dp))
                            .padding(8.dp)
                    ) {
                        Column {
                            Text("Context Window", fontSize = 10.sp, color = Color(0xFF94A3B8))
                            Text(selectedModel.tokenLimit, fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF0F172A))
                            .border(1.dp, Color(0xFF334155), RoundedCornerShape(8.dp))
                            .padding(8.dp)
                    ) {
                        Column {
                            Text("Latency SLA", fontSize = 10.sp, color = Color(0xFF94A3B8))
                            Text(selectedModel.speedLatency, fontSize = 11.sp, color = DevEmeraldLight, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
