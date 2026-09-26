package com.example.ui.screens.projects

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeveloperMode
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PauseCircle
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Project
import com.example.ui.components.GlassmorphicCard
import com.example.ui.components.RechartsBarChart
import com.example.ui.components.StatusBadge
import com.example.ui.theme.DevAmber
import com.example.ui.theme.DevAmberLight
import com.example.ui.theme.DevBlueDark
import com.example.ui.theme.DevBlueLight
import com.example.ui.theme.DevEmeraldDark
import com.example.ui.theme.DevEmeraldLight
import com.example.ui.theme.DevRose
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ProjectDashboard(
    projects: List<Project>,
    isFirestoreActive: Boolean,
    syncStatusMessage: String,
    onAddProject: (name: String, description: String, status: String, techStack: String) -> Unit,
    onUpdateProjectStatus: (projectId: String, newStatus: String) -> Unit,
    onDeleteProject: (projectId: String) -> Unit,
    onNavigateToWorkstation: () -> Unit,
    onNavigateToAiStudio: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isAddDialogOpen by remember { mutableStateOf(false) }
    var selectedStatusFilter by remember { mutableStateOf<String?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var projectToDelete by remember { mutableStateOf<Project?>(null) }

    // Filter projects based on status and search query
    val filteredProjects = remember(projects, selectedStatusFilter, searchQuery) {
        projects.filter { project ->
            val matchesStatus = selectedStatusFilter == null || project.status.equals(selectedStatusFilter, ignoreCase = true)
            val matchesSearch = searchQuery.isBlank() ||
                    project.name.contains(searchQuery, ignoreCase = true) ||
                    project.description.contains(searchQuery, ignoreCase = true) ||
                    project.techStack.contains(searchQuery, ignoreCase = true) ||
                    project.lead.contains(searchQuery, ignoreCase = true)
            matchesStatus && matchesSearch
        }
    }

    val totalCount = projects.size
    val inProgressCount = projects.count { it.status == Project.STATUS_IN_PROGRESS }
    val completedCount = projects.count { it.status == Project.STATUS_COMPLETED }
    val onHoldCount = projects.count { it.status == Project.STATUS_ON_HOLD }

    Box(
        modifier = modifier
            .fillMaxSize()
            .testTag("project_dashboard_screen")
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Hero / Landing Card
            item {
                GlassmorphicCard(
                    modifier = Modifier.fillMaxWidth(),
                    borderColor = DevBlueLight.copy(alpha = 0.35f),
                    backgroundColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(
                                            Brush.linearGradient(
                                                listOf(DevBlueLight, Color(0xFF7B61FF))
                                            )
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "O",
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 18.sp,
                                        color = Color.White
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = "OmniDev ",
                                            style = MaterialTheme.typography.titleLarge,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = "AI Platform",
                                            style = MaterialTheme.typography.titleLarge,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = DevBlueLight
                                        )
                                    }
                                    Text(
                                        text = "⚡ Yeni Nesil Yapay Zeka Mimarisi & Firestore Hub",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            // Firestore status badge
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(
                                        if (isFirestoreActive) DevEmeraldLight.copy(alpha = 0.15f)
                                        else DevAmber.copy(alpha = 0.15f)
                                    )
                                    .border(
                                        1.dp,
                                        if (isFirestoreActive) DevEmeraldLight.copy(alpha = 0.4f)
                                        else DevAmber.copy(alpha = 0.4f),
                                        RoundedCornerShape(20.dp)
                                    )
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Icon(
                                    imageVector = if (isFirestoreActive) Icons.Default.CloudDone else Icons.Default.CloudSync,
                                    contentDescription = null,
                                    tint = if (isFirestoreActive) DevEmeraldLight else DevAmber,
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(5.dp))
                                Text(
                                    text = if (isFirestoreActive) "Firestore Aktif" else "Yerel / Sync",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isFirestoreActive) DevEmeraldLight else DevAmber
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Quick Stat Cards
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            StatBox(
                                label = "Toplam",
                                value = "$totalCount",
                                color = DevBlueLight,
                                modifier = Modifier.weight(1f),
                                isSelected = selectedStatusFilter == null,
                                onClick = { selectedStatusFilter = null }
                            )
                            StatBox(
                                label = "In Progress",
                                value = "$inProgressCount",
                                color = DevBlueLight,
                                modifier = Modifier.weight(1f),
                                isSelected = selectedStatusFilter == Project.STATUS_IN_PROGRESS,
                                onClick = {
                                    selectedStatusFilter = if (selectedStatusFilter == Project.STATUS_IN_PROGRESS) null else Project.STATUS_IN_PROGRESS
                                }
                            )
                            StatBox(
                                label = "Completed",
                                value = "$completedCount",
                                color = DevEmeraldLight,
                                modifier = Modifier.weight(1f),
                                isSelected = selectedStatusFilter == Project.STATUS_COMPLETED,
                                onClick = {
                                    selectedStatusFilter = if (selectedStatusFilter == Project.STATUS_COMPLETED) null else Project.STATUS_COMPLETED
                                }
                            )
                            StatBox(
                                label = "On Hold",
                                value = "$onHoldCount",
                                color = DevAmber,
                                modifier = Modifier.weight(1f),
                                isSelected = selectedStatusFilter == Project.STATUS_ON_HOLD,
                                onClick = {
                                    selectedStatusFilter = if (selectedStatusFilter == Project.STATUS_ON_HOLD) null else Project.STATUS_ON_HOLD
                                }
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Quick Navigation Shortcuts
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = onNavigateToWorkstation,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                    contentColor = MaterialTheme.colorScheme.onSurface
                                ),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.Terminal, contentDescription = null, modifier = Modifier.size(16.dp), tint = DevBlueLight)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("DevOS CLI", fontSize = 12.sp)
                            }
                            Button(
                                onClick = onNavigateToAiStudio,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                    contentColor = MaterialTheme.colorScheme.onSurface
                                ),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp), tint = DevEmeraldLight)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Gemini AI", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }

            // RECHARTS STATUS DISTRIBUTION BAR CHART
            item {
                RechartsBarChart(
                    projects = projects,
                    selectedStatusFilter = selectedStatusFilter,
                    onSelectStatusFilter = { selectedStatusFilter = it }
                )
            }

            // Search Bar & Filter Chips Header
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    // Search Field
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Proje adı, teknoloji veya açıklama ara...", fontSize = 13.sp) },
                        leadingIcon = {
                            Icon(Icons.Default.Search, contentDescription = null, tint = DevBlueLight, modifier = Modifier.size(18.dp))
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Temizle", modifier = Modifier.size(18.dp))
                                }
                            }
                        },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = DevBlueLight,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("project_search_input")
                    )

                    // Status Filter Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.FilterList,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "Filtrele:",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            FilterChipItem(
                                label = "Tümü (${projects.size})",
                                isSelected = selectedStatusFilter == null,
                                activeColor = DevBlueLight,
                                onClick = { selectedStatusFilter = null }
                            )

                            FilterChipItem(
                                label = "In Progress",
                                isSelected = selectedStatusFilter == Project.STATUS_IN_PROGRESS,
                                activeColor = DevBlueLight,
                                onClick = {
                                    selectedStatusFilter = if (selectedStatusFilter == Project.STATUS_IN_PROGRESS) null else Project.STATUS_IN_PROGRESS
                                }
                            )

                            FilterChipItem(
                                label = "Completed",
                                isSelected = selectedStatusFilter == Project.STATUS_COMPLETED,
                                activeColor = DevEmeraldLight,
                                onClick = {
                                    selectedStatusFilter = if (selectedStatusFilter == Project.STATUS_COMPLETED) null else Project.STATUS_COMPLETED
                                }
                            )

                            FilterChipItem(
                                label = "On Hold",
                                isSelected = selectedStatusFilter == Project.STATUS_ON_HOLD,
                                activeColor = DevAmber,
                                onClick = {
                                    selectedStatusFilter = if (selectedStatusFilter == Project.STATUS_ON_HOLD) null else Project.STATUS_ON_HOLD
                                }
                            )
                        }
                    }
                }
            }

            // Project List Section Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (selectedStatusFilter != null) "$selectedStatusFilter Projeleri (${filteredProjects.size})" else "Tüm Projeler (${filteredProjects.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Button(
                        onClick = { isAddDialogOpen = true },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = DevBlueLight.copy(alpha = 0.2f),
                            contentColor = DevBlueLight
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("add_project_header_button")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Yeni Proje", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Project Items
            if (filteredProjects.isEmpty()) {
                item {
                    GlassmorphicCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 20.dp),
                        borderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.FolderOpen,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = if (searchQuery.isNotEmpty()) "Arama kriterine uygun proje bulunamadı" else "Bu filtrede aktif proje yok",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Yeni bir proje ekleyin veya arama filtresini temizleyin.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = {
                                    if (selectedStatusFilter != null || searchQuery.isNotEmpty()) {
                                        selectedStatusFilter = null
                                        searchQuery = ""
                                    } else {
                                        isAddDialogOpen = true
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = DevBlueLight,
                                    contentColor = Color.Black
                                )
                            ) {
                                Text(
                                    if (selectedStatusFilter != null || searchQuery.isNotEmpty()) "Filtreleri Temizle" else "İlk Projeyi Ekle",
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            } else {
                items(filteredProjects, key = { it.id }) { project ->
                    ProjectCardItem(
                        project = project,
                        onUpdateStatus = { newStatus -> onUpdateProjectStatus(project.id, newStatus) },
                        onDeleteClick = { projectToDelete = project }
                    )
                }
            }
        }

        // Floating Action Button to Add Project
        FloatingActionButton(
            onClick = { isAddDialogOpen = true },
            containerColor = DevBlueLight,
            contentColor = Color.Black,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
                .testTag("add_project_fab")
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Add, contentDescription = "Yeni Proje Ekle")
                Spacer(modifier = Modifier.width(6.dp))
                Text("Proje Ekle", fontWeight = FontWeight.Bold)
            }
        }

        // Add Project Dialog
        if (isAddDialogOpen) {
            AddProjectDialog(
                onDismiss = { isAddDialogOpen = false },
                onSaveProject = { name, desc, status, tech ->
                    onAddProject(name, desc, status, tech)
                }
            )
        }

        // Delete Confirmation Dialog
        projectToDelete?.let { proj ->
            AlertDialog(
                onDismissRequest = { projectToDelete = null },
                title = { Text("Projeyi Sil") },
                text = { Text("\"${proj.name}\" projesi Firestore ve yerel önbellekten silinecektir. Devam etmek istiyor musunuz?") },
                confirmButton = {
                    Button(
                        onClick = {
                            onDeleteProject(proj.id)
                            projectToDelete = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = DevRose)
                    ) {
                        Text("Sil", color = Color.White)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { projectToDelete = null }) {
                        Text("İptal")
                    }
                }
            )
        }
    }
}

@Composable
private fun StatBox(
    label: String,
    value: String,
    color: Color,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(
                if (isSelected) color.copy(alpha = 0.2f)
                else MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
            )
            .border(
                1.dp,
                if (isSelected) color else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                RoundedCornerShape(10.dp)
            )
            .clickable { onClick() }
            .padding(vertical = 8.dp, horizontal = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = value,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) color else MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = label,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun FilterChipItem(
    label: String,
    isSelected: Boolean,
    activeColor: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(
                if (isSelected) activeColor.copy(alpha = 0.2f)
                else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
            )
            .border(
                1.dp,
                if (isSelected) activeColor else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                RoundedCornerShape(16.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 10.dp, vertical = 5.dp)
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) activeColor else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun ProjectCardItem(
    project: Project,
    onUpdateStatus: (String) -> Unit,
    onDeleteClick: () -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }

    val statusColor = when (project.status) {
        Project.STATUS_IN_PROGRESS -> DevBlueLight
        Project.STATUS_COMPLETED -> DevEmeraldLight
        else -> DevAmber
    }

    val formattedDate = remember(project.createdAt) {
        val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        sdf.format(Date(project.createdAt))
    }

    GlassmorphicCard(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("project_item_${project.id}"),
        borderColor = statusColor.copy(alpha = 0.25f),
        backgroundColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row: Title, Status Badge, Menu
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = project.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        StatusBadge(
                            text = project.status,
                            color = statusColor
                        )

                        Text(
                            text = "•  $formattedDate",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Action Menu
                Box {
                    IconButton(
                        onClick = { menuExpanded = true },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Seçenekler",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false }
                    ) {
                        Text(
                            text = "Durumu Değiştir",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                        Project.ALL_STATUSES.forEach { statusOption ->
                            DropdownMenuItem(
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        val optColor = when (statusOption) {
                                            Project.STATUS_IN_PROGRESS -> DevBlueLight
                                            Project.STATUS_COMPLETED -> DevEmeraldLight
                                            else -> DevAmber
                                        }
                                        Box(
                                            modifier = Modifier
                                                .size(8.dp)
                                                .clip(CircleShape)
                                                .background(optColor)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = statusOption,
                                            fontWeight = if (project.status == statusOption) FontWeight.Bold else FontWeight.Normal
                                        )
                                    }
                                },
                                onClick = {
                                    onUpdateStatus(statusOption)
                                    menuExpanded = false
                                }
                            )
                        }
                        DropdownMenuItem(
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = null,
                                        tint = DevRose,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Projeyi Sil", color = DevRose)
                                }
                            },
                            onClick = {
                                menuExpanded = false
                                onDeleteClick()
                            }
                        )
                    }
                }
            }

            if (project.description.isNotBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = project.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 19.sp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Tech Stack & Lead Chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(DevBlueLight.copy(alpha = 0.12f))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = project.techStack,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            color = DevBlueLight
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "Lider: ${project.lead}",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Text(
                    text = "%${project.completionPercentage}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    color = statusColor
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Progress bar
            LinearProgressIndicator(
                progress = { project.completionPercentage / 100f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = statusColor,
                trackColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f)
            )
        }
    }
}
