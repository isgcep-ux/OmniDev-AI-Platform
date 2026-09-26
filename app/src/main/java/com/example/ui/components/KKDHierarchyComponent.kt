package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Engineering
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.ManageAccounts
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Data model representing each tier in the Hierarchy of Risk Controls / PPE (KKD).
 */
data class HierarchyLevel(
    val rank: Int,
    val titleTr: String,
    val titleEn: String,
    val effectiveness: String,
    val accentColor: Color,
    val icon: ImageVector,
    val summary: String,
    val detailedPrinciple: String,
    val examNotes: String,
    val subItems: List<HierarchySubItem>
)

data class HierarchySubItem(
    val name: String,
    val practicalExample: String,
    val isPrimaryOption: Boolean = false
)

/**
 * Predefined 5-stage Risk Control & PPE Hierarchy data according to Turkish 6331 ISG
 * legislation and international ILO/OSHA standards.
 */
val kkdHierarchyList = listOf(
    HierarchyLevel(
        rank = 1,
        titleTr = "1. ELİMİNASYON (Yok Etme)",
        titleEn = "Elimination",
        effectiveness = "En Etkili (%100 Koruma)",
        accentColor = Color(0xFF10B981), // Emerald Green
        icon = Icons.Default.DeleteSweep,
        summary = "Tehlikenin veya tehlike kaynağının işyerinden tamamen kaldırılması.",
        detailedPrinciple = "Risk kontrol hiyerarşisinin en tepesidir. Tehlikeli kimyasal, ekipman veya fiziksel işlem kaynağında yok edilir. Çalışanın tehlikeyle temas ihtimali sıfırlanır.",
        examNotes = "Sınav Notu: Tehlikeyi önlemede en ilk ve en öncelikli tercihtir. Eğer tehlike yok edilebiliyorsa sonraki basamaklara geçilmez.",
        subItems = listOf(
            HierarchySubItem("Yüksekte Montajın Kaldırılması", "Tavan veya cephe elemanlarının yerde monte edilip vinçle yekpare kaldırılması."),
            HierarchySubItem("Tehlikeli Adımın İptali", "Zehirli kimyasal kullanılan temizleme prosesinin mekanik fırçalamayla değiştirilmesi."),
            HierarchySubItem("Manuel Taşımanın Kaldırılması", "Ağır yük kaldırma ihtiyacını sıfırlayan konveyör veya otomatik robotik hat kurulması.")
        )
    ),
    HierarchyLevel(
        rank = 2,
        titleTr = "2. İKAME (Substitusyon)",
        titleEn = "Substitution",
        effectiveness = "Çok Yüksek Etkinlik",
        accentColor = Color(0xFF06B6D4), // Cyan
        icon = Icons.Default.SwapHoriz,
        summary = "Tehlikeli olanın, tehlikesiz veya daha az tehlikeli olanla değiştirilmesi.",
        detailedPrinciple = "Tehlike tamamen yok edilemiyorsa, riski kat kat daha az olan alternatif malzeme, teknoloji veya enerji kaynağı kullanılır.",
        examNotes = "Sınav Notu: Solvent bazlı yanıcı/toksik boyaların su bazlı boyalarla değiştirilmesi tipik ikame örneğidir.",
        subItems = listOf(
            HierarchySubItem("Kimyasal Değişimi", "Solvent bazlı yapıştırıcı yerine su bazlı akrilik yapıştırıcı kullanılması."),
            HierarchySubItem("Ekipman Değişimi", "Yüksek gürültülü pnömatik sistem yerine sessiz çalışan elektrikli motor kullanımı."),
            HierarchySubItem("Form Değişimi", "Tozuma yapan kuru kimyasal yerine granül veya pelet formundaki kimyasal kullanımı.")
        )
    ),
    HierarchyLevel(
        rank = 3,
        titleTr = "3. MÜHENDİSLİK KONTROLLERİ",
        titleEn = "Engineering Controls",
        effectiveness = "Yüksek (Toplu Koruma)",
        accentColor = Color(0xFF3B82F6), // Blue
        icon = Icons.Default.Engineering,
        summary = "İnsanları tehlikeden fiziksel olarak tecrit etme / toplu koruma tedbirleri.",
        detailedPrinciple = "Tehlikeli bölgeye erişimi engelleyen fiziksel bariyerler, emiş sistemleri, ses kabinleri ve kilit mekanizmaları kurulmasıdır.",
        examNotes = "Sınav Notu: Toplu koruma önlemleri, kişisel koruma önlemlerine her zaman üstündür ve önceliklidir!",
        subItems = listOf(
            HierarchySubItem("Yerel Cebri Havalandırma (LEV)", "Kaynak dumanı veya kimyasal buharını kaynağından emen akrobat kollar."),
            HierarchySubItem("Makine Koruyucuları & Işık Perdeleri", "Preslerde operatör elini koruyan optik emniyet bariyerleri."),
            HierarchySubItem("Akustik İzolasyon & Kabin", "Gürültü kaynağı kompresör veya jeneratörün ses geçirmez odaya hapsedilmesi."),
            HierarchySubItem("Korkuluk & Güvenlik Ağları", "İnşaatlarda boşluk kenarlarına çift sıra sağlam korkuluk yapılması.")
        )
    ),
    HierarchyLevel(
        rank = 4,
        titleTr = "4. İDARİ KONTROLLER & EĞİTİM",
        titleEn = "Administrative Controls",
        effectiveness = "Orta (Davranışa Bağlı)",
        accentColor = Color(0xFFF59E0B), // Amber
        icon = Icons.Default.ManageAccounts,
        summary = "Çalışma sisteminin düzenlenmesi, rotasyon, prosedürler ve İSG eğitimleri.",
        detailedPrinciple = "Çalışanın tehlikeye maruziyet süresini ve sıklığını azaltan organizasyonel düzenlemelerdir. İnsan davranışına bağlı olduğu için mühendislik önlemlerine göre daha kırılgandır.",
        examNotes = "Sınav Notu: İş izni (PTW - Permit to Work), vardiya rotasyonu ve güvenlik işaretleri bu basamaktadır.",
        subItems = listOf(
            HierarchySubItem("Vardiya Rotasyonu", "Gürültülü veya titreşimli işte çalışanların gün içinde yer değiştirerek maruziyet sürelerinin düşürülmesi."),
            HierarchySubItem("Çalışma İzni Sistemi (PTW)", "Kapalı alan, sıcak çalışma veya yüksekte çalışma öncesi yazılı izin onay mekanizması."),
            HierarchySubItem("Güvenlik ve Sağlık İşaretleri", "Sarı-Siyah tehlike şeritleri, ikaz ve uyarı levhalarının sahaya yerleştirilmesi."),
            HierarchySubItem("Periyodik İSG Eğitimleri", "Tehlikeler ve acil durum eylem planı hakkında çalışanların eğitilmesi.")
        )
    ),
    HierarchyLevel(
        rank = 5,
        titleTr = "5. KİŞİSEL KORUYUCU DONANIM (KKD)",
        titleEn = "Personal Protective Equipment",
        effectiveness = "Son Çare / En Düşük (Kişisel Koruma)",
        accentColor = Color(0xFFEF4444), // Red
        icon = Icons.Default.HealthAndSafety,
        summary = "Çalışanın ekipmanlarla donatılarak tehlikeden bireysel olarak korunması.",
        detailedPrinciple = "Yukarıdaki 4 basamak uygulandıktan sonra ARTA KALAN (bakiye) riskler için çalışanlara uygun KKD (baret, gözlük, kulaklık, emniyet kemeri vb.) zimmetlenir.",
        examNotes = "Sınav Notu: KKD daima EN SON ÇAREDİR! Toplu koruma önlemleri yerine asla tek başına ikame edilemez.",
        subItems = listOf(
            HierarchySubItem("Baş & Yüz Koruması", "EN 397 Endüstriyel Baret, EN 166 Koruyucu Vizör/Gözlük."),
            HierarchySubItem("İşitme Koruması", "EN 352-1 Kulaklık ve EN 352-2 Kulak Tıkaçları (85 dB ve üzeri zorunlu)."),
            HierarchySubItem("Solunum Koruması", "EN 149 FFP2/FFP3 Toz Maskeleri, Gaz filtreli yarım/tam yüz maskeler."),
            HierarchySubItem("Düşüş Durdurucu Sistem", "EN 361 Paraşüt Tipi Emniyet Kemeri, lanyard ve şok emiciler.")
        )
    )
)

/**
 * Interactive Jetpack Compose component visualizing the KKD / Risk Control Hierarchy
 * with inverted pyramid visual flow, expandable tree nodes, practical examples, and exam hints.
 */
@Composable
fun KKDHierarchyComponent(
    modifier: Modifier = Modifier,
    initialExpandedIndex: Int = 0
) {
    var expandedIndex by remember { mutableStateOf<Int?>(initialExpandedIndex) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("kkd_hierarchy_card"),
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
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Risk Kontrol Önlemleri & KKD Hiyerarşisi",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Mevzuat (6331 İSGK) uyarınca en etkiliden son çareye doğru sıralama",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF38BDF8)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFF10B981).copy(alpha = 0.15f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF10B981))
                ) {
                    Text(
                        text = "5 Basamak",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF10B981),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Inverted Pyramid Flow Indicator
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.65f),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E293B)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "▲ EN ETKİLİ (Toplu)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF10B981)
                        )
                    }
                    Text(
                        text = "→ → Hiyerarşik Öncelik Sırası → →",
                        fontSize = 10.sp,
                        color = Color(0xFF64748B),
                        fontFamily = FontFamily.Monospace
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "▼ SON ÇARE (Bireysel)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFFEF4444)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Hierarchical Tree List
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                kkdHierarchyList.forEachIndexed { index, level ->
                    val isExpanded = expandedIndex == index
                    val rotationState by animateFloatAsState(
                        targetValue = if (isExpanded) 180f else 0f,
                        animationSpec = tween(250),
                        label = "arrowRotation"
                    )

                    // Card for this hierarchy tier
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .border(
                                width = if (isExpanded) 1.5.dp else 1.dp,
                                color = if (isExpanded) level.accentColor else Color(0xFF334155),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable {
                                expandedIndex = if (isExpanded) null else index
                            }
                            .testTag("hierarchy_level_${level.rank}"),
                        color = if (isExpanded) level.accentColor.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surface.copy(alpha = 0.45f)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp)
                                .animateContentSize()
                        ) {
                            // Row Header
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    // Rank circle icon
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .background(level.accentColor.copy(alpha = 0.2f))
                                            .border(1.dp, level.accentColor, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = level.icon,
                                            contentDescription = null,
                                            tint = level.accentColor,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(10.dp))

                                    Column {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = level.titleTr,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp,
                                                color = if (isExpanded) level.accentColor else MaterialTheme.colorScheme.onSurface
                                            )
                                        }
                                        Text(
                                            text = "${level.titleEn} • ${level.effectiveness}",
                                            fontSize = 11.sp,
                                            color = level.accentColor,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }

                                IconButton(
                                    onClick = { expandedIndex = if (isExpanded) null else index },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ExpandMore,
                                        contentDescription = "Detayları Aç",
                                        tint = if (isExpanded) level.accentColor else Color(0xFF94A3B8),
                                        modifier = Modifier.rotate(rotationState)
                                    )
                                }
                            }

                            // Summary (Always visible briefly)
                            Text(
                                text = level.summary,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(top = 6.dp, start = 46.dp)
                            )

                            // Expandable Details & Tree Branch Children
                            AnimatedVisibility(
                                visible = isExpanded,
                                enter = expandVertically() + fadeIn(),
                                exit = shrinkVertically() + fadeOut()
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 12.dp, start = 12.dp, end = 4.dp)
                                ) {
                                    // Principle note
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = level.accentColor.copy(alpha = 0.12f),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Column(modifier = Modifier.padding(10.dp)) {
                                            Text(
                                                text = "Temel Çalışma Mantığı:",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = level.accentColor
                                            )
                                            Spacer(modifier = Modifier.height(3.dp))
                                            Text(
                                                text = level.detailedPrinciple,
                                                fontSize = 12.sp,
                                                lineHeight = 16.sp,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))

                                    // Sub-items Tree Structure
                                    Text(
                                        text = "Uygulama Örnekleri & Ağaç Dalları:",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))

                                    level.subItems.forEachIndexed { subIndex, subItem ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 4.dp),
                                            verticalAlignment = Alignment.Top
                                        ) {
                                            // Tree branch guide symbol
                                            Text(
                                                text = if (subIndex == level.subItems.lastIndex) " └─ " else " ├─ ",
                                                fontFamily = FontFamily.Monospace,
                                                fontSize = 12.sp,
                                                color = level.accentColor,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    text = subItem.name,
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = MaterialTheme.colorScheme.onSurface
                                                )
                                                Text(
                                                    text = subItem.practicalExample,
                                                    fontSize = 11.sp,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))

                                    // Exam note banner
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = Color(0xFF0F172A),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155)),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(10.dp),
                                            verticalAlignment = Alignment.Top
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Info,
                                                contentDescription = null,
                                                tint = Color(0xFF38BDF8),
                                                modifier = Modifier.size(16.dp).padding(top = 1.dp)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = level.examNotes,
                                                fontSize = 11.sp,
                                                lineHeight = 15.sp,
                                                color = Color(0xFFE2E8F0)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Bottom Rule Callout
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = Color(0xFFEF4444).copy(alpha = 0.12f),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEF4444).copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        tint = Color(0xFFEF4444),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Mevzuat Prensibi: İşverenin çalışanlara KKD temin etmesi, tehlikeyi kaynağında yok etme veya toplu koruma tedbirlerini alma yükümlülüğünü asla ortadan kaldırmaz.",
                        fontSize = 11.sp,
                        lineHeight = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}
