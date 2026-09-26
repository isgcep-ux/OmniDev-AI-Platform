package com.example.ui.screens.idx

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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Speed
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
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.ui.theme.DevBlueDark
import com.example.ui.theme.DevBlueLight
import com.example.ui.theme.DevEmeraldLight
import com.example.ui.theme.DevIndigoLight

@Composable
fun FlutterSection(
    onSaveSnippet: (String, String, String, String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    var counterValue by remember { mutableIntStateOf(0) }
    var selectedStateOption by remember { mutableStateOf("Riverpod") }

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
                            imageVector = Icons.Default.Devices,
                            contentDescription = null,
                            tint = DevBlueLight,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Flutter Web on Project IDX",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    StatusBadge(text = "WASM Ready", color = DevEmeraldLight)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Build multiplatform web and mobile applications with 60fps WebAssembly compilation, hot reload in Cloud Workspaces, and unified Dart codebase.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Interactive Live Widget Simulator
        GlassmorphicCard(
            borderColor = DevIndigoLight.copy(alpha = 0.3f),
            backgroundColor = MaterialTheme.colorScheme.surface
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Interactive Flutter Widget Simulator",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    StatusBadge(text = "Hot Reload", color = DevBlueLight, icon = Icons.Default.ElectricBolt)
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Simulated Phone / Canvas Preview
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF0F172A))
                        .border(1.dp, Color(0xFF334155), RoundedCornerShape(12.dp))
                        .padding(16.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Flutter Counter Widget State",
                            color = Color(0xFF94A3B8),
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "$counterValue",
                            color = DevBlueLight,
                            fontSize = 36.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = { if (counterValue > 0) counterValue-- },
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFF1E293B))
                                    .size(48.dp)
                                    .testTag("flutter_counter_dec_btn")
                            ) {
                                Icon(Icons.Default.Remove, contentDescription = "Decrement", tint = Color.White)
                            }

                            Button(
                                onClick = { counterValue = 0 },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF334155)),
                                modifier = Modifier.height(48.dp).testTag("flutter_counter_reset_btn")
                            ) {
                                Text("Reset State", fontSize = 12.sp, color = Color.White)
                            }

                            IconButton(
                                onClick = { counterValue++ },
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(DevBlueDark)
                                    .size(48.dp)
                                    .testTag("flutter_counter_inc_btn")
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "Increment", tint = Color.White)
                            }
                        }
                    }
                }
            }
        }

        // State Management Matrix Selector
        Column {
            Text(
                text = "State Management Architecture in Dart",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("Riverpod", "Bloc / Cubit", "ValueNotifier").forEach { option ->
                    val isSelected = selectedStateOption == option
                    Button(
                        onClick = { selectedStateOption = option },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isSelected) DevBlueLight else MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = if (isSelected) Color(0xFF082F49) else MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f).height(40.dp).testTag("state_tab_$option")
                    ) {
                        Text(option, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Code Snippet based on selected state management
        val sampleCode = when (selectedStateOption) {
            "Riverpod" -> """
import 'package:flutter_riverpod/flutter_riverpod.dart';

// 1. Define Asynchronous Notifier
@riverpod
class CounterNotifier extends _${'$'}CounterNotifier {
  @override
  FutureOr<int> build() async => 0;

  Future<void> increment() async {
    state = AsyncValue.data((state.value ?? 0) + 1);
  }
}

// 2. Consume in Responsive Web Widget
class CounterWidget extends ConsumerWidget {
  const CounterWidget({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final count = ref.watch(counterNotifierProvider);
    return count.when(
      data: (val) => Text('Count: ${'$'}val', style: TextStyle(fontSize: 24)),
      loading: () => const CircularProgressIndicator(),
      error: (err, _) => Text('Error: ${'$'}err'),
    );
  }
}
""".trimIndent()
            "Bloc / Cubit" -> """
import 'package:flutter_bloc/flutter_bloc.dart';

class CounterCubit extends Cubit<int> {
  CounterCubit() : super(0);
  void increment() => emit(state + 1);
  void decrement() => emit(state > 0 ? state - 1 : 0);
}

class BlocCounterView extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return BlocBuilder<CounterCubit, int>(
      builder: (context, state) => Text('State: ${'$'}state'),
    );
  }
}
""".trimIndent()
            else -> """
import 'package:flutter/material.dart';

class AdaptiveNotifierView extends StatelessWidget {
  final ValueNotifier<int> counter = ValueNotifier<int>(0);

  @override
  Widget build(BuildContext context) {
    return ValueListenableBuilder<int>(
      valueListenable: counter,
      builder: (context, value, child) {
        return Text('ValueNotifier Count: ${'$'}value');
      },
    );
  }
}
""".trimIndent()
        }

        CodeBlockView(
            code = sampleCode,
            language = "dart",
            title = "Flutter $selectedStateOption Pattern",
            onSaveSnippet = {
                onSaveSnippet(
                    "Flutter $selectedStateOption Pattern",
                    "dart",
                    "Flutter",
                    sampleCode,
                    "Production pattern for $selectedStateOption in Flutter Web"
                )
            }
        )
    }
}
