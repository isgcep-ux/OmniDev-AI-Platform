package com.example.ui.screens.aistudio

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.example.data.local.entity.ChatMessageEntity
import com.example.ui.components.CodeBlockView
import com.example.ui.components.GlassmorphicCard
import com.example.ui.components.StatusBadge
import com.example.ui.theme.DevAmber
import com.example.ui.theme.DevBlueLight
import com.example.ui.theme.DevEmeraldLight
import com.example.ui.theme.DevIndigoLight

@Composable
fun AiTutorChatSection(
    selectedPersona: String,
    messages: List<ChatMessageEntity>,
    isLoading: Boolean,
    onSelectPersona: (String) -> Unit,
    onSendMessage: (String) -> Unit,
    onClearChat: () -> Unit,
    modifier: Modifier = Modifier
) {
    var inputText by remember { mutableStateOf("") }
    val personas = listOf(
        "Full-Stack Architect",
        "Flutter Master",
        "Next.js Pro",
        "Firebase Specialist",
        "AI & Prompt Engineer"
    )

    val quickQuestions = when (selectedPersona) {
        "Flutter Master" -> listOf(
            "How do I optimize Flutter Web for high-performance WASM rendering?",
            "What is the best Riverpod pattern for asynchronous caching?",
            "Show adaptive layout builder code with drawer navigation."
        )
        "Next.js Pro" -> listOf(
            "Explain React Server Components vs Client Components in App Router.",
            "How do Server Actions eliminate traditional API routes?",
            "Write Next.js middleware for JWT route authentication."
        )
        "Firebase Specialist" -> listOf(
            "Write secure Firestore Security Rules for workspace tenants.",
            "How do I structure compound indexes for high volume queries?",
            "Create a Cloud Functions v2 trigger for project updates."
        )
        "AI & Prompt Engineer" -> listOf(
            "How to structure Few-Shot examples with Gemini 3.5 Flash?",
            "Show JSON schema response configuration for Gemini REST API.",
            "How do I balance temperature and top-p for code generation?"
        )
        else -> listOf(
            "Explain the architecture linking Flutter Web, Next.js, and Firebase.",
            "What is the best database strategy: Room local vs Firestore cloud?",
            "How should CI/CD test automation be designed for full-stack apps?"
        )
    }

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
                            imageVector = Icons.Default.SupportAgent,
                            contentDescription = null,
                            tint = DevBlueLight,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "AI Chatbot Tutor",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    IconButton(
                        onClick = onClearChat,
                        modifier = Modifier.size(32.dp).testTag("clear_chat_btn")
                    ) {
                        Icon(Icons.Default.DeleteSweep, contentDescription = "Clear Chat", tint = Color(0xFF94A3B8))
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Specialized AI Engineering Tutor powered by Gemini 3.5 Flash with persistent room conversation logs.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Persona Selection Chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            personas.forEach { persona ->
                val isSelected = selectedPersona == persona
                Button(
                    onClick = { onSelectPersona(persona) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isSelected) DevBlueLight else MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = if (isSelected) Color(0xFF082F49) else MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.height(36.dp).testTag("persona_$persona")
                ) {
                    Text(persona, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Quick Prompt Suggestion Chips
        Column {
            Text(
                text = "Suggested Questions ($selectedPersona):",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                quickQuestions.forEach { question ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF0F172A))
                            .border(1.dp, Color(0xFF334155), RoundedCornerShape(12.dp))
                            .clickable { onSendMessage(question) }
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = question,
                            fontSize = 11.sp,
                            color = Color(0xFF93C5FD),
                            maxLines = 1
                        )
                    }
                }
            }
        }

        // Conversation Messages Flow
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            if (messages.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF0F172A))
                        .border(1.dp, Color(0xFF334155), RoundedCornerShape(12.dp))
                        .padding(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.SmartToy, contentDescription = null, tint = DevBlueLight, modifier = Modifier.size(32.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Ask $selectedPersona anything about architecture, code idioms, or debugging!",
                            fontSize = 13.sp,
                            color = Color(0xFF94A3B8),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            } else {
                messages.forEach { msg ->
                    val isUser = msg.role == "user"
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.92f)
                                .clip(RoundedCornerShape(14.dp))
                                .background(if (isUser) Color(0xFF0284C7).copy(alpha = 0.25f) else Color(0xFF1E293B))
                                .border(1.dp, if (isUser) DevBlueLight.copy(alpha = 0.4f) else Color(0xFF334155), RoundedCornerShape(14.dp))
                                .padding(12.dp)
                        ) {
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = if (isUser) Icons.Default.Person else Icons.Default.SmartToy,
                                            contentDescription = null,
                                            tint = if (isUser) DevBlueLight else DevEmeraldLight,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = if (isUser) "You" else selectedPersona,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isUser) DevBlueLight else DevEmeraldLight
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = msg.content,
                                    fontSize = 13.sp,
                                    color = Color(0xFFF8FAFC),
                                    lineHeight = 20.sp
                                )
                            }
                        }
                    }
                }
            }

            if (isLoading) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF1E293B))
                            .border(1.dp, Color(0xFF334155), RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            CircularProgressIndicator(modifier = Modifier.size(16.dp), color = DevEmeraldLight, strokeWidth = 2.dp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("$selectedPersona is thinking...", fontSize = 12.sp, color = Color(0xFFCBD5E1))
                        }
                    }
                }
            }
        }

        // Input Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = inputText,
                onValueChange = { inputText = it },
                placeholder = { Text("Ask technical question...") },
                modifier = Modifier.weight(1f).testTag("chat_input_field"),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = DevBlueLight,
                    unfocusedBorderColor = Color(0xFF334155),
                    focusedContainerColor = Color(0xFF0F172A),
                    unfocusedContainerColor = Color(0xFF0F172A),
                    focusedTextColor = Color(0xFFF8FAFC),
                    unfocusedTextColor = Color(0xFFF8FAFC)
                )
            )

            Button(
                onClick = {
                    if (inputText.isNotBlank()) {
                        val text = inputText
                        inputText = ""
                        onSendMessage(text)
                    }
                },
                enabled = inputText.isNotBlank() && !isLoading,
                colors = ButtonDefaults.buttonColors(containerColor = DevBlueLight),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.height(52.dp).testTag("send_chat_btn")
            ) {
                Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send", tint = Color(0xFF082F49))
            }
        }
    }
}
