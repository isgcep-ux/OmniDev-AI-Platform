package com.example.ui.screens.aistudio

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.DataObject
import androidx.compose.material.icons.filled.IntegrationInstructions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
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
fun IntegrationSdkSection(
    onSaveSnippet: (String, String, String, String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedLanguage by remember { mutableStateOf("Kotlin") }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Section Hero
        GlassmorphicCard(
            borderColor = DevEmeraldLight.copy(alpha = 0.4f),
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
                            imageVector = Icons.Default.IntegrationInstructions,
                            contentDescription = null,
                            tint = DevEmeraldLight,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Gemini Integration SDKs",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    StatusBadge(text = "REST & SDK", color = DevEmeraldLight)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Production-ready SDK integration templates in Kotlin, Dart/Flutter, TypeScript/Next.js, Python, and cURL for invoking Gemini 3.5 Flash.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Language Switcher Tabs
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            listOf("Kotlin", "Dart / Flutter", "TypeScript", "Python", "cURL").forEach { lang ->
                val isSelected = selectedLanguage == lang
                Button(
                    onClick = { selectedLanguage = lang },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isSelected) DevEmeraldLight else MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = if (isSelected) Color(0xFF064E3B) else MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f).height(38.dp).testTag("sdk_lang_$lang")
                ) {
                    Text(lang, fontSize = 10.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                }
            }
        }

        val codeSnippet = when (selectedLanguage) {
            "Kotlin" -> """
// Kotlin / Android Gemini 3.5 REST Invocation
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody

suspend fun askGemini(prompt: String, apiKey: String): String {
    val client = OkHttpClient()
    val jsonPayload = ""${'"'}
    {
      "contents": [{ "role": "user", "parts": [{ "text": "${'$'}prompt" }] }],
      "generationConfig": { "temperature": 0.7 }
    }
    ""${'"'}.trimIndent()

    val request = Request.Builder()
        .url("https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=${'$'}apiKey")
        .post(jsonPayload.toRequestBody("application/json".toMediaType()))
        .build()

    val response = client.newCall(request).execute()
    return response.body?.string() ?: "No response"
}
""".trimIndent()

            "Dart / Flutter" -> """
// Dart / Flutter Google GenAI Integration
import 'package:google_generative_ai/google_generative_ai.dart';

Future<String?> generateAiCode(String userPrompt, String apiKey) async {
  final model = GenerativeModel(
    model: 'gemini-3.5-flash',
    apiKey: apiKey,
    generationConfig: GenerationConfig(temperature: 0.7),
  );

  final content = [Content.text(userPrompt)];
  final response = await model.generateContent(content);
  return response.text;
}
""".trimIndent()

            "TypeScript" -> """
// TypeScript / Next.js 15 Server Action with @google/genai
import { GoogleGenAI } from '@google/genai';

const ai = new GoogleGenAI({ apiKey: process.env.GEMINI_API_KEY });

export async function askGeminiAssistant(prompt: string) {
  'use server';
  
  const response = await ai.models.generateContent({
    model: 'gemini-3.5-flash',
    contents: prompt,
    config: {
      temperature: 0.7,
    },
  });

  return response.text;
}
""".trimIndent()

            "Python" -> """
# Python Google GenAI SDK (gemini-3.5-flash)
from google import genai

client = genai.Client()

response = client.models.generate_content(
    model='gemini-3.5-flash',
    contents='Design an optimal Flutter and Next.js full-stack database schema.'
)

print(response.text)
""".trimIndent()

            else -> """
# cURL Direct REST API Request to Gemini 3.5 Flash
curl "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=${'$'}GEMINI_API_KEY" \
  -H 'Content-Type: application/json' \
  -X POST \
  -d '{
    "contents": [
      {
        "role": "user",
        "parts": [
          { "text": "Provide Flutter Web best practices on Project IDX." }
        ]
      }
    ],
    "generationConfig": {
      "temperature": 0.7
    }
  }'
""".trimIndent()
        }

        CodeBlockView(
            code = codeSnippet,
            language = when (selectedLanguage) {
                "Kotlin" -> "kotlin"
                "Dart / Flutter" -> "dart"
                "TypeScript" -> "typescript"
                "Python" -> "python"
                else -> "bash"
            },
            title = "Gemini 3.5 Flash ($selectedLanguage)",
            onSaveSnippet = {
                onSaveSnippet(
                    "Gemini 3.5 ($selectedLanguage)",
                    when (selectedLanguage) {
                        "Kotlin" -> "kotlin"
                        "Dart / Flutter" -> "dart"
                        "TypeScript" -> "typescript"
                        "Python" -> "python"
                        else -> "bash"
                    },
                    "AI Studio",
                    codeSnippet,
                    "SDK integration code for $selectedLanguage"
                )
            }
        )
    }
}
