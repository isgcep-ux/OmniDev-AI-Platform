package com.example.ui.screens.workstation

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DeveloperMode
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.GlassmorphicCard
import com.example.ui.components.StatusBadge
import com.example.ui.theme.DarkCodeBg
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.DevAmber
import com.example.ui.theme.DevBlueDark
import com.example.ui.theme.DevBlueLight
import com.example.ui.theme.DevEmeraldDark
import com.example.ui.theme.DevEmeraldLight
import com.example.ui.theme.DevIndigoLight
import com.example.ui.theme.DevRose
import com.example.ui.viewmodel.DevOsTerminalEntry

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DevOsWorkstationScreen(
    terminalEntries: List<DevOsTerminalEntry>,
    terminalInput: String,
    onTerminalInputChange: (String) -> Unit,
    onExecuteCommand: (String) -> Unit,
    onClearTerminal: () -> Unit,
    onNavigateTab: (Int) -> Unit,
    onSaveSnippet: (String, String, String, String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    // Interactive state for guides
    var expandedGuideIndex by remember { mutableStateOf<Int?>(0) }
    var scratchpadNote by remember { mutableStateOf("## DevOS Çalışma Notları\n- APK derlemesi tamamlandı (.build-outputs/app-debug.apk)\n- Emülatör API 36 üzerinde test edildi\n- Claude Code CLI terminali entegre edildi") }

    fun copyToClipboard(text: String, label: String = "DevOS") {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label, text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "$label panoya kopyalandı", Toast.LENGTH_SHORT).show()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        // ==========================================
        // 1. DEVOS WORKSTATION HEADER & STATUS
        // ==========================================
        GlassmorphicCard(
            cornerRadius = 20.dp,
            borderColor = DevBlueLight.copy(alpha = 0.35f),
            accentGradient = Brush.horizontalGradient(
                colors = listOf(
                    Color(0xFF0F172A),
                    Color(0xFF1E293B)
                )
            ),
            modifier = Modifier.testTag("devos_hero_card")
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(DevBlueLight.copy(alpha = 0.15f))
                                .border(1.dp, DevBlueLight.copy(alpha = 0.4f), RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Computer,
                                contentDescription = "DevOS Workstation",
                                tint = DevBlueLight,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "DevOS Workstation",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                StatusBadge(text = "v3.2 PRO", color = DevEmeraldLight)
                            }
                            Text(
                                text = "Kişisel Geliştirici Bilgisayarı • Cloud Emulator Edition",
                                fontSize = 11.sp,
                                color = Color(0xFF94A3B8)
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(DevEmeraldDark.copy(alpha = 0.3f))
                            .border(1.dp, DevEmeraldLight.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(DevEmeraldLight)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "ONLINE",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = DevEmeraldLight
                            )
                        }
                    }
                }

                HorizontalDivider(color = Color(0xFF334155))

                // Hardware & Emulation Metrics Grid
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    MetricMiniBox(
                        icon = Icons.Default.Speed,
                        label = "CPU Virtual",
                        value = "8 Cores (18%)",
                        tint = DevBlueLight
                    )
                    MetricMiniBox(
                        icon = Icons.Default.Memory,
                        label = "JVM Heap RAM",
                        value = "214 / 512 MB",
                        tint = DevIndigoLight
                    )
                    MetricMiniBox(
                        icon = Icons.Default.Storage,
                        label = "APK Boyutu",
                        value = "32.5 MB",
                        tint = DevEmeraldLight
                    )
                    MetricMiniBox(
                        icon = Icons.Default.Dns,
                        label = "Ağ Gecikmesi",
                        value = "14 ms",
                        tint = DevAmber
                    )
                }
            }
        }

        // ==========================================
        // 2. APK DAĞITIM VE EMÜLATÖR MERKEZİ (CLAUDE CODE ANLATIMI)
        // ==========================================
        ElevatedCard(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("devos_apk_hub_card"),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.elevatedCardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(DevEmeraldLight.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Android,
                                contentDescription = null,
                                tint = DevEmeraldLight,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "APK Dağıtım & Emülatör Merkezi",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Derlenmiş APK çıktısı ve sadeleştirilmiş kullanım rehberi",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    StatusBadge(text = "HAZIR ✓", color = DevEmeraldLight)
                }

                // APK Output Specs Box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "📁 Dosya Çıktısı:",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = ".build-outputs/app-debug.apk",
                                fontSize = 12.sp,
                                fontFamily = FontFamily.Monospace,
                                color = DevBlueLight
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "📦 Paket Kimliği:",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "com.aistudio.devstudiohub.qwmvtp",
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "⚡ Varyant / Sürüm:",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "Debug (v1.0) • Android 15 (API 36)",
                                fontSize = 11.sp,
                                color = DevEmeraldLight
                            )
                        }
                    }
                }

                Text(
                    text = "APK'YI ÇALIŞTIRMA VE KULLANMA REHBERİ (3 Kolay Yol)",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = DevIndigoLight,
                    modifier = Modifier.padding(top = 4.dp)
                )

                // Step 1: Tarayıcı İçi Web Emülatörü
                ApkGuideStepCard(
                    stepNumber = "1",
                    title = "Tarayıcı İçi Canlı Emülatör (Sıfır Kurulum)",
                    subtitle = "Doğrudan Google AI Studio ortamında anında test edin",
                    isExpanded = expandedGuideIndex == 0,
                    onToggle = { expandedGuideIndex = if (expandedGuideIndex == 0) null else 0 },
                    explanation = "AI Studio pencerenizin sağ tarafında çalışan Android Streaming Emülatörü, kod veya ayar değiştiğinde APK'yı otomatik olarak kurar ve başlatır. Bilgisayarınıza dosya indirmenize ya da harici emülatör açmanıza gerek yoktur.",
                    actionLabel = "Paket Adını Kopyala",
                    onAction = { copyToClipboard("com.aistudio.devstudiohub.qwmvtp", "Paket Adı") }
                )

                // Step 2: Android Studio & Harici Emülatör (Sürükle-Bırak)
                ApkGuideStepCard(
                    stepNumber = "2",
                    title = "Harici Emülatörde Sürükle & Bırak",
                    subtitle = "Android Studio emülatörü veya Nox/BlueStacks/Genymotion",
                    isExpanded = expandedGuideIndex == 1,
                    onToggle = { expandedGuideIndex = if (expandedGuideIndex == 1) null else 1 },
                    explanation = "1. Sol paneldeki dosya yöneticisinden '.build-outputs/app-debug.apk' dosyasını bilgisayarınıza indirin.\n2. Bilgisayarınızda açık olan Android emülatör penceresinin üzerine dosyayı sürükleyip bırakın.\n3. Emülatör 3 saniye içinde kurulumu tamamlar ve ana ekrana 'DevStudio Hub' simgesini ekler.",
                    actionLabel = "APK Yolunu Kopyala",
                    onAction = { copyToClipboard(".build-outputs/app-debug.apk", "APK Yolu") }
                )

                // Step 3: Gerçek Cihaz / ADB Komut Satırı
                ApkGuideStepCard(
                    stepNumber = "3",
                    title = "Terminal & ADB ile Hızlı Yükleme",
                    subtitle = "Geliştirici modu açık telefon veya yerel bilgisayar terminali",
                    isExpanded = expandedGuideIndex == 2,
                    onToggle = { expandedGuideIndex = if (expandedGuideIndex == 2) null else 2 },
                    explanation = "Cihazınız USB ile bağlıyken veya kablosuz hata ayıklama aktifken terminalde şu komutu çalıştırın:\n$ adb install -r .build-outputs/app-debug.apk\n\nUygulamayı doğrudan başlatmak için:\n$ adb shell am start -n com.aistudio.devstudiohub.qwmvtp/com.example.MainActivity",
                    actionLabel = "ADB Yükleme Komutunu Kopyala",
                    onAction = { copyToClipboard("adb install -r .build-outputs/app-debug.apk", "ADB Komutu") }
                )
            }
        }

        // ==========================================
        // 3. CLAUDE CODE ETKİLEŞİMLİ TERMİNAL (AI CLI)
        // ==========================================
        ElevatedCard(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("devos_claude_terminal_card"),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.elevatedCardColors(
                containerColor = DarkCodeBg
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Terminal Header Window Controls
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(Color(0xFFEF4444)))
                        Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(Color(0xFFF59E0B)))
                        Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(Color(0xFF10B981)))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "claude-code-cli @ devos: ~/workspace",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFCBD5E1)
                        )
                    }

                    IconButton(
                        onClick = onClearTerminal,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Terminali Temizle",
                            tint = Color(0xFF94A3B8),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                HorizontalDivider(color = Color(0xFF1E293B))

                // Quick Action Chips for Fast Commands
                Text(
                    text = "Hızlı Claude Code Komutları:",
                    fontSize = 11.sp,
                    color = Color(0xFF64748B)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TerminalQuickChip(text = "claude apk-info") { onExecuteCommand("claude apk-info") }
                    TerminalQuickChip(text = "claude sha256") { onExecuteCommand("claude sha256") }
                    TerminalQuickChip(text = "claude test") { onExecuteCommand("claude test") }
                    TerminalQuickChip(text = "claude sys-monitor") { onExecuteCommand("claude sys-monitor") }
                    TerminalQuickChip(text = "claude env") { onExecuteCommand("claude env") }
                    TerminalQuickChip(text = "claude git-status") { onExecuteCommand("claude git-status") }
                    TerminalQuickChip(text = "claude help") { onExecuteCommand("claude help") }
                }

                // Terminal Output Area
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 160.dp, max = 280.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF06090E))
                        .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(8.dp))
                        .padding(12.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        terminalEntries.forEach { entry ->
                            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "$ ",
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = DevEmeraldLight
                                    )
                                    Text(
                                        text = entry.command,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = DevBlueLight
                                    )
                                    Spacer(modifier = Modifier.weight(1f))
                                    Text(
                                        text = entry.timestamp,
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 10.sp,
                                        color = Color(0xFF475569)
                                    )
                                }
                                Text(
                                    text = entry.output,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 11.sp,
                                    color = if (entry.isSuccess) Color(0xFFE2E8F0) else DevRose,
                                    lineHeight = 16.sp
                                )
                            }
                            HorizontalDivider(color = Color(0xFF0F172A))
                        }
                    }
                }

                // Interactive Command Prompt Input
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = terminalInput,
                        onValueChange = onTerminalInputChange,
                        placeholder = {
                            Text(
                                "Komut yazın (örn: claude apk-info, test, help)...",
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                color = Color(0xFF475569)
                            )
                        },
                        singleLine = true,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("claude_terminal_input"),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color(0xFFE2E8F0),
                            focusedBorderColor = DevBlueLight,
                            unfocusedBorderColor = Color(0xFF334155),
                            focusedContainerColor = Color(0xFF0B0F17),
                            unfocusedContainerColor = Color(0xFF0B0F17)
                        ),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                        keyboardActions = KeyboardActions(onSend = { onExecuteCommand(terminalInput) })
                    )

                    Button(
                        onClick = { onExecuteCommand(terminalInput) },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = DevBlueDark),
                        modifier = Modifier.testTag("claude_terminal_run_btn")
                    ) {
                        Icon(imageVector = Icons.Default.PlayArrow, contentDescription = "Çalıştır")
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Çalıştır", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // ==========================================
        // 4. DEVOS WORKSPACES & MODULES
        // ==========================================
        Text(
            text = "GELİŞTİRİCİ MODÜLLERİ & ÇALIŞMA ALANLARI",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            DevOsModuleCard(
                title = "Primary Dev",
                subtitle = "IDX • Web & Cloud",
                icon = Icons.Default.Code,
                color = DevBlueLight,
                modifier = Modifier.weight(1f),
                onClick = { onNavigateTab(0) }
            )
            DevOsModuleCard(
                title = "AI Studio",
                subtitle = "Gemini 3.5 AI",
                icon = Icons.Default.AutoAwesome,
                color = DevIndigoLight,
                modifier = Modifier.weight(1f),
                onClick = { onNavigateTab(1) }
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            DevOsModuleCard(
                title = "Team RBAC",
                subtitle = "Yetkilendirme & Paylaşım",
                icon = Icons.Default.Security,
                color = DevRose,
                modifier = Modifier.weight(1f),
                onClick = { onNavigateTab(2) }
            )
            DevOsModuleCard(
                title = "Saved Library",
                subtitle = "Room SQLite Deposu",
                icon = Icons.Default.Bookmark,
                color = DevEmeraldLight,
                modifier = Modifier.weight(1f),
                onClick = { onNavigateTab(3) }
            )
        }

        // ==========================================
        // 5. WORKSTATION SCRATCHPAD (GELİŞTİRİCİ NOTLARI)
        // ==========================================
        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.elevatedCardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Save,
                            contentDescription = null,
                            tint = DevAmber,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "Workstation Hızlı Not Defteri",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    OutlinedButton(
                        onClick = {
                            onSaveSnippet(
                                "DevOS Workstation Notu",
                                "markdown",
                                "Workstation",
                                scratchpadNote,
                                "DevOS Kişisel Geliştirici Bilgisayarı Karalama Defteri"
                            )
                            Toast.makeText(context, "Not Room Kütüphanesine Kaydedildi", Toast.LENGTH_SHORT).show()
                        },
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Kütüphaneye Kaydet", fontSize = 11.sp)
                    }
                }

                OutlinedTextField(
                    value = scratchpadNote,
                    onValueChange = { scratchpadNote = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    shape = RoundedCornerShape(10.dp),
                    textStyle = androidx.compose.ui.text.TextStyle(
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

// ----------------------------------------------------
// HELPER COMPOSABLES
// ----------------------------------------------------

@Composable
private fun MetricMiniBox(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    tint: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(18.dp)
        )
        Text(text = label, fontSize = 10.sp, color = Color(0xFF94A3B8))
        Text(
            text = value,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}

@Composable
private fun TerminalQuickChip(
    text: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(Color(0xFF1E293B))
            .border(1.dp, Color(0xFF334155), RoundedCornerShape(6.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = "$ $text",
            fontFamily = FontFamily.Monospace,
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium,
            color = DevBlueLight
        )
    }
}

@Composable
private fun ApkGuideStepCard(
    stepNumber: String,
    title: String,
    subtitle: String,
    isExpanded: Boolean,
    onToggle: () -> Unit,
    explanation: String,
    actionLabel: String,
    onAction: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f))
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
            .clickable(onClick = onToggle)
            .padding(12.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(DevBlueLight),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stepNumber,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A)
                        )
                    }
                    Column {
                        Text(
                            text = title,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = subtitle,
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Icon(
                    imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            AnimatedVisibility(visible = isExpanded) {
                Column(
                    modifier = Modifier.padding(top = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = explanation,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 18.sp
                    )

                    Button(
                        onClick = onAction,
                        colors = ButtonDefaults.buttonColors(containerColor = DevIndigoLight),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(actionLabel, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}

@Composable
private fun DevOsModuleCard(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    ElevatedCard(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(18.dp)
                )
            }
            Column {
                Text(
                    text = title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
