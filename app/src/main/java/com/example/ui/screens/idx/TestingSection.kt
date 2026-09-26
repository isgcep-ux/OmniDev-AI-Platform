package com.example.ui.screens.idx

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Science
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.ui.viewmodel.TestSuiteItem

@Composable
fun TestingSection(
    testSuite: List<TestSuiteItem>,
    isRunning: Boolean,
    onRunAllTests: () -> Unit,
    onSaveSnippet: (String, String, String, String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    val passedCount = testSuite.count { it.status == "PASSED" }
    val progress = if (testSuite.isNotEmpty()) passedCount.toFloat() / testSuite.size.toFloat() else 0f

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
                            imageVector = Icons.Default.Science,
                            contentDescription = null,
                            tint = DevIndigoLight,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Automated Test Suite",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    StatusBadge(text = "$passedCount/${testSuite.size} Passed", color = if (passedCount == testSuite.size) DevEmeraldLight else DevBlueLight)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Multi-tier testing for Full-Stack applications: Fast unit tests, Robolectric Android JVM tests, Flutter widget tests, Next.js Jest assertions, and End-to-End browser tests.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Test Runner Control Panel
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
                    Column {
                        Text(
                            text = "Continuous Integration Test Runner",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (isRunning) "Executing test suites..." else "Ready to verify build integrity",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Button(
                        onClick = onRunAllTests,
                        enabled = !isRunning,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = DevIndigoLight,
                            disabledContainerColor = Color(0xFF334155)
                        ),
                        modifier = Modifier.height(42.dp).testTag("run_all_tests_btn")
                    ) {
                        if (isRunning) {
                            CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White, strokeWidth = 2.dp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Testing...", fontSize = 12.sp, color = Color.White)
                        } else {
                            Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color(0xFF1E1B4B), modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Run All Tests", fontSize = 12.sp, color = Color(0xFF1E1B4B), fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                    color = DevEmeraldLight,
                    trackColor = Color(0xFF1E293B),
                )
            }
        }

        // Live Test Suite List
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            testSuite.forEach { item ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF0F172A))
                        .border(1.dp, Color(0xFF334155), RoundedCornerShape(10.dp))
                        .padding(12.dp)
                        .testTag("test_item_${item.id}")
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            when (item.status) {
                                "PASSED" -> Icon(Icons.Default.CheckCircle, contentDescription = null, tint = DevEmeraldLight, modifier = Modifier.size(18.dp))
                                "RUNNING" -> CircularProgressIndicator(modifier = Modifier.size(16.dp), color = DevIndigoLight, strokeWidth = 2.dp)
                                else -> Icon(Icons.Default.HourglassTop, contentDescription = null, tint = Color(0xFF64748B), modifier = Modifier.size(18.dp))
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = item.name,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFFF8FAFC)
                                )
                                Text(
                                    text = "${item.type} Test • ${item.durationMs}ms",
                                    fontSize = 11.sp,
                                    color = Color(0xFF94A3B8),
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }

                        StatusBadge(
                            text = item.status,
                            color = when (item.status) {
                                "PASSED" -> DevEmeraldLight
                                "RUNNING" -> DevIndigoLight
                                else -> Color(0xFF64748B)
                            }
                        )
                    }
                }
            }
        }

        val testExample = """
// Robolectric & Jetpack Compose Critical User Journey Test
@RunWith(RobolectricTestRunner::class)
class DevStudioCUJTest {
  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun testCounterStateIncrement() {
    composeTestRule.setContent {
      MyApplicationTheme {
        FlutterSection(onSaveSnippet = { _, _, _, _, _ -> })
      }
    }

    // Verify initial state and interaction
    composeTestRule.onNodeWithTag("flutter_counter_inc_btn").performClick()
    composeTestRule.onNodeWithText("1").assertIsDisplayed()
  }
}
""".trimIndent()

        CodeBlockView(
            code = testExample,
            language = "kotlin",
            title = "Robolectric & Compose CUJ Test",
            onSaveSnippet = {
                onSaveSnippet(
                    "Robolectric CUJ Test",
                    "kotlin",
                    "Testing",
                    testExample,
                    "Automated Android unit and integration test template"
                )
            }
        )
    }
}
