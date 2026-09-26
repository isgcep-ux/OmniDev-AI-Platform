package com.example.ui.screens.aistudio

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.filled.BookmarkAdd
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import com.example.ui.components.CodeBlockView
import com.example.ui.components.GlassmorphicCard
import com.example.ui.components.StatusBadge
import com.example.ui.theme.DevBlueLight
import com.example.ui.theme.DevEmeraldLight
import com.example.ui.theme.DevIndigoLight

@Composable
fun PromptStudioSection(
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
    modifier: Modifier = Modifier
) {
    var templateTitle by remember { mutableStateOf("") }
    var showSaveInput by remember { mutableStateOf(false) }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Section Hero
        GlassmorphicCard(
            borderColor = DevIndigoLight.copy(alpha = 0.4f),
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
                            imageVector = Icons.Default.Tune,
                            contentDescription = null,
                            tint = DevIndigoLight,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Prompt Engineering Studio",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    StatusBadge(text = "System Prompts", color = DevIndigoLight)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Craft high-performance prompts with system instructions, few-shot examples, and chain-of-thought instructions tested live against Gemini 3.5 Flash.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Quick Preset Chips
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val presets = listOf(
                "Code Reviewer" to "You are a Principal Software Architect. Review given code for performance, security, and clean architecture.",
                "API Schema Gen" to "You are a backend schema designer. Return structured TypeScript types and SQL DDL tables.",
                "Bug Diagnoser" to "You are an expert debugger. Explain the root cause of stack traces and provide copy-paste solutions."
            )

            presets.forEach { (name, sysPrompt) ->
                Button(
                    onClick = { onSystemInstructionChange(sysPrompt) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = MaterialTheme.colorScheme.onSurface
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f).height(38.dp)
                ) {
                    Text(name, fontSize = 10.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                }
            }
        }

        // System Instruction Editor
        Column {
            Text(
                text = "System Instruction (Context & Persona)",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = systemInstruction,
                onValueChange = onSystemInstructionChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .testTag("prompt_system_input"),
                placeholder = { Text("e.g., You are a Senior Flutter and Next.js architect...") },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = DevIndigoLight,
                    unfocusedBorderColor = Color(0xFF334155),
                    focusedContainerColor = Color(0xFF0F172A),
                    unfocusedContainerColor = Color(0xFF0F172A),
                    focusedTextColor = Color(0xFFF8FAFC),
                    unfocusedTextColor = Color(0xFFF8FAFC)
                )
            )
        }

        // User Prompt Editor
        Column {
            Text(
                text = "User Prompt",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = userPrompt,
                onValueChange = onUserPromptChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
                    .testTag("prompt_user_input"),
                placeholder = { Text("Enter task or code to analyze...") },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = DevBlueLight,
                    unfocusedBorderColor = Color(0xFF334155),
                    focusedContainerColor = Color(0xFF0F172A),
                    unfocusedContainerColor = Color(0xFF0F172A),
                    focusedTextColor = Color(0xFFF8FAFC),
                    unfocusedTextColor = Color(0xFFF8FAFC)
                )
            )
        }

        // Action Controls
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = onRunPrompt,
                enabled = !isPromptRunning && userPrompt.isNotBlank(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = DevBlueLight,
                    disabledContainerColor = Color(0xFF334155)
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.weight(1f).height(46.dp).testTag("run_prompt_btn")
            ) {
                if (isPromptRunning) {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color(0xFF082F49), strokeWidth = 2.dp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Executing Gemini Query...", color = Color(0xFF082F49), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                } else {
                    Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color(0xFF082F49), modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Execute Prompt", color = Color(0xFF082F49), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }

            Button(
                onClick = { showSaveInput = !showSaveInput },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.height(46.dp).testTag("toggle_save_prompt_btn")
            ) {
                Icon(Icons.Default.BookmarkAdd, contentDescription = null, tint = DevEmeraldLight)
            }
        }

        AnimatedVisibility(visible = showSaveInput) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = templateTitle,
                    onValueChange = { templateTitle = it },
                    placeholder = { Text("Prompt Template Name") },
                    singleLine = true,
                    modifier = Modifier.weight(1f).testTag("prompt_title_input")
                )
                Button(
                    onClick = {
                        if (templateTitle.isNotBlank()) {
                            onSavePromptTemplate(templateTitle, systemInstruction, userPrompt, "Engineering")
                            templateTitle = ""
                            showSaveInput = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DevEmeraldLight)
                ) {
                    Text("Save", color = Color(0xFF064E3B), fontWeight = FontWeight.Bold)
                }
            }
        }

        // Prompt Execution Output
        if (promptResult != null) {
            Column {
                Text(
                    text = "Gemini 3.5 Flash Response",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = DevEmeraldLight
                )
                Spacer(modifier = Modifier.height(8.dp))
                CodeBlockView(
                    code = promptResult,
                    language = "markdown",
                    title = "Model Output (gemini-3.5-flash)"
                )
            }
        }
    }
}
