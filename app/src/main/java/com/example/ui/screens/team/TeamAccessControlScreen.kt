package com.example.ui.screens.team

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.GroupAdd
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.PromptTemplate
import com.example.data.local.entity.ResourcePolicy
import com.example.data.local.entity.SavedSnippet
import com.example.data.local.entity.ShareInviteToken
import com.example.data.local.entity.TeamMember
import com.example.ui.components.StatusBadge
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkCodeBg
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.DarkTextMuted
import com.example.ui.theme.DarkTextSecondary
import com.example.ui.theme.DevAmber
import com.example.ui.theme.DevBlueLight
import com.example.ui.theme.DevEmeraldLight
import com.example.ui.theme.DevIndigoLight
import com.example.ui.theme.DevRose
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TeamAccessControlScreen(
    teamMembers: List<TeamMember>,
    resourcePolicies: List<ResourcePolicy>,
    activeInviteTokens: List<ShareInviteToken>,
    savedSnippets: List<SavedSnippet>,
    promptTemplates: List<PromptTemplate>,
    currentUserRole: String,
    canManageTeam: Boolean,
    onInviteMember: (name: String, email: String, role: String, department: String) -> Unit,
    onChangeMemberRole: (memberId: Long, newRole: String) -> Unit,
    onRemoveMember: (memberId: Long) -> Unit,
    onCreateInviteLink: (targetRole: String, validDays: Int, maxUses: Int) -> String,
    onRevokeInvite: (tokenId: Long) -> Unit,
    onUpdateResourcePolicy: (resourceId: Long, resourceType: String, title: String, visibility: String, minRole: String) -> Unit,
    onCopyText: (text: String, label: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Members, 1: RBAC Matrix & Policies, 2: Invite Links
    var searchQuery by remember { mutableStateOf("") }
    var selectedRoleFilter by remember { mutableStateOf("ALL") }

    // Dialog States
    var showInviteDialog by remember { mutableStateOf(false) }
    var showCreateLinkDialog by remember { mutableStateOf(false) }
    var memberToEditRole by remember { mutableStateOf<TeamMember?>(null) }
    var memberToDelete by remember { mutableStateOf<TeamMember?>(null) }
    var newlyCreatedLinkCode by remember { mutableStateOf<String?>(null) }

    val filteredMembers = teamMembers.filter { member ->
        val matchesSearch = member.name.contains(searchQuery, ignoreCase = true) ||
                member.email.contains(searchQuery, ignoreCase = true) ||
                member.department.contains(searchQuery, ignoreCase = true)
        val matchesRole = if (selectedRoleFilter == "ALL") true else member.role == selectedRoleFilter
        matchesSearch && matchesRole
    }

    val totalMembers = teamMembers.size
    val adminCount = teamMembers.count { it.role == "OWNER" || it.role == "ADMIN" }
    val editorCount = teamMembers.count { it.role == "EDITOR" }
    val activeLinksCount = activeInviteTokens.count { !it.isRevoked && it.expiresAt > System.currentTimeMillis() }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        // Workspace Security Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            DarkSurfaceElevated,
                            DarkSurface
                        )
                    )
                )
                .border(1.dp, DarkCardBorder)
                .padding(16.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(DevIndigoLight.copy(alpha = 0.2f))
                                .border(1.dp, DevIndigoLight.copy(alpha = 0.4f), RoundedCornerShape(10.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = null,
                                tint = DevIndigoLight,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Team & Access Control",
                                    color = Color.White,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                StatusBadge(text = "RBAC Active", color = DevEmeraldLight)
                            }
                            Text(
                                text = "Role-Based Access Control • Project IDX & AI Studio Security",
                                color = DarkTextSecondary,
                                fontSize = 12.sp
                            )
                        }
                    }

                    // Action Buttons for Admins
                    if (canManageTeam) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(
                                onClick = { showCreateLinkDialog = true },
                                modifier = Modifier.testTag("btn_create_invite_link"),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = DevBlueLight),
                                border = ButtonDefaults.outlinedButtonBorder(enabled = true).copy(
                                    brush = Brush.linearGradient(listOf(DevBlueLight.copy(alpha = 0.6f), DevBlueLight.copy(alpha = 0.6f)))
                                )
                            ) {
                                Icon(Icons.Default.Link, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Share Link", fontSize = 12.sp)
                            }

                            Button(
                                onClick = { showInviteDialog = true },
                                modifier = Modifier.testTag("btn_invite_member_top"),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = DevEmeraldLight, contentColor = Color(0xFF040814))
                            ) {
                                Icon(Icons.Default.GroupAdd, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Invite Member", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Metric Cards Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    WorkspaceStatCard(
                        title = "Team Members",
                        value = "$totalMembers",
                        icon = Icons.Default.Group,
                        accentColor = DevBlueLight,
                        modifier = Modifier.weight(1f)
                    )
                    WorkspaceStatCard(
                        title = "Admins / Leads",
                        value = "$adminCount",
                        icon = Icons.Default.AdminPanelSettings,
                        accentColor = DevAmber,
                        modifier = Modifier.weight(1f)
                    )
                    WorkspaceStatCard(
                        title = "Editors",
                        value = "$editorCount",
                        icon = Icons.Default.Edit,
                        accentColor = DevIndigoLight,
                        modifier = Modifier.weight(1f)
                    )
                    WorkspaceStatCard(
                        title = "Active Links",
                        value = "$activeLinksCount",
                        icon = Icons.Default.Link,
                        accentColor = DevEmeraldLight,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Sub Navigation Tabs
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = DarkSurface,
            contentColor = DevBlueLight,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = DevBlueLight
                )
            },
            divider = { HorizontalDivider(color = DarkCardBorder) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Group, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Team Members (${teamMembers.size})", fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal)
                    }
                }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Shield, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("RBAC & Policies", fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal)
                    }
                }
            )
            Tab(
                selected = selectedTab == 2,
                onClick = { selectedTab = 2 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Link, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Invite Links (${activeInviteTokens.size})", fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Normal)
                    }
                }
            )
        }

        // Main Tab Content
        when (selectedTab) {
            0 -> TeamMembersTab(
                members = filteredMembers,
                searchQuery = searchQuery,
                onSearchQueryChange = { searchQuery = it },
                selectedRoleFilter = selectedRoleFilter,
                onRoleFilterChange = { selectedRoleFilter = it },
                canManageTeam = canManageTeam,
                onChangeRole = { memberToEditRole = it },
                onRemove = { memberToDelete = it }
            )
            1 -> RbacPolicyTab(
                savedSnippets = savedSnippets,
                promptTemplates = promptTemplates,
                resourcePolicies = resourcePolicies,
                canManage = canManageTeam,
                onUpdatePolicy = onUpdateResourcePolicy
            )
            2 -> InviteLinksTab(
                inviteTokens = activeInviteTokens,
                canManage = canManageTeam,
                onCreateLink = { showCreateLinkDialog = true },
                onRevoke = onRevokeInvite,
                onCopy = onCopyText
            )
        }
    }

    // Dialog: Invite Member
    if (showInviteDialog) {
        InviteMemberDialog(
            onDismiss = { showInviteDialog = false },
            onConfirm = { name, email, role, dept ->
                onInviteMember(name, email, role, dept)
                showInviteDialog = false
            }
        )
    }

    // Dialog: Create Share Link
    if (showCreateLinkDialog) {
        CreateInviteLinkDialog(
            onDismiss = { showCreateLinkDialog = false },
            onConfirm = { role, days, maxUses ->
                val code = onCreateInviteLink(role, days, maxUses)
                newlyCreatedLinkCode = code
                showCreateLinkDialog = false
            }
        )
    }

    // Dialog: Newly Created Link Confirmation
    if (newlyCreatedLinkCode != null) {
        AlertDialog(
            onDismissRequest = { newlyCreatedLinkCode = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = DevEmeraldLight)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Shareable Invite Created", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column {
                    Text(
                        text = "Share this secure access token with your team member. They can redeem it directly to join with preset permissions.",
                        color = DarkTextSecondary,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(DarkCodeBg)
                            .border(1.dp, DevBlueLight.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                            .padding(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = newlyCreatedLinkCode ?: "",
                                color = DevBlueLight,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                            IconButton(
                                onClick = {
                                    newlyCreatedLinkCode?.let { onCopyText(it, "Invite Token") }
                                },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = DevBlueLight, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        newlyCreatedLinkCode?.let { onCopyText(it, "Invite Token") }
                        newlyCreatedLinkCode = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DevBlueLight, contentColor = Color(0xFF040814))
                ) {
                    Text("Copy & Close", fontWeight = FontWeight.Bold)
                }
            },
            containerColor = DarkSurface,
            shape = RoundedCornerShape(14.dp)
        )
    }

    // Dialog: Edit Member Role
    memberToEditRole?.let { member ->
        EditMemberRoleDialog(
            member = member,
            onDismiss = { memberToEditRole = null },
            onConfirm = { newRole ->
                onChangeMemberRole(member.id, newRole)
                memberToEditRole = null
            }
        )
    }

    // Dialog: Confirm Remove Member
    memberToDelete?.let { member ->
        AlertDialog(
            onDismissRequest = { memberToDelete = null },
            title = { Text("Revoke Member Access", color = Color.White, fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    text = "Are you sure you want to remove ${member.name} (${member.email}) from the workspace? They will immediately lose access to all shared snippets, prompt templates, and AI Studio environments.",
                    color = DarkTextSecondary,
                    fontSize = 13.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onRemoveMember(member.id)
                        memberToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DevRose, contentColor = Color.White)
                ) {
                    Text("Revoke Access", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { memberToDelete = null }) {
                    Text("Cancel", color = Color.White)
                }
            },
            containerColor = DarkSurface,
            shape = RoundedCornerShape(14.dp)
        )
    }
}

// ----------------------------------------------------
// TAB 1: TEAM MEMBERS LIST
// ----------------------------------------------------
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun TeamMembersTab(
    members: List<TeamMember>,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    selectedRoleFilter: String,
    onRoleFilterChange: (String) -> Unit,
    canManageTeam: Boolean,
    onChangeRole: (TeamMember) -> Unit,
    onRemove: (TeamMember) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Search & Role Filter Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                placeholder = { Text("Search by name, email, department...", color = Color(0xFF64748B), fontSize = 13.sp) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = DevBlueLight, modifier = Modifier.size(18.dp)) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { onSearchQueryChange("") }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear", tint = Color.Gray, modifier = Modifier.size(16.dp))
                        }
                    }
                },
                singleLine = true,
                modifier = Modifier
                    .weight(1f)
                    .testTag("input_team_search"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = DevBlueLight,
                    unfocusedBorderColor = DarkCardBorder,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedContainerColor = DarkSurface,
                    unfocusedContainerColor = DarkSurface
                ),
                shape = RoundedCornerShape(10.dp)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Role Filter Chips
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val roles = listOf("ALL" to "All Members", "OWNER" to "Owners", "ADMIN" to "Admins", "EDITOR" to "Editors", "VIEWER" to "Viewers")
            roles.forEach { (roleKey, label) ->
                val isSelected = selectedRoleFilter == roleKey
                FilterChip(
                    selected = isSelected,
                    onClick = { onRoleFilterChange(roleKey) },
                    label = { Text(label, fontSize = 12.sp) },
                    leadingIcon = if (isSelected) {
                        { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp)) }
                    } else null,
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = DarkSurface,
                        labelColor = DarkTextSecondary,
                        selectedContainerColor = DevBlueLight.copy(alpha = 0.2f),
                        selectedLabelColor = DevBlueLight
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = isSelected,
                        borderColor = if (isSelected) DevBlueLight else DarkCardBorder
                    ),
                    shape = RoundedCornerShape(8.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Members List
        if (members.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(DarkSurface)
                    .border(1.dp, DarkCardBorder, RoundedCornerShape(12.dp))
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Group, contentDescription = null, tint = DarkTextMuted, modifier = Modifier.size(48.dp))
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("No matching team members found", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                    Text("Try adjusting your search or role filter query", color = DarkTextSecondary, fontSize = 12.sp)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .testTag("list_team_members"),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(members, key = { it.id }) { member ->
                    TeamMemberCard(
                        member = member,
                        canManage = canManageTeam,
                        onChangeRole = { onChangeRole(member) },
                        onRemove = { onRemove(member) }
                    )
                }
            }
        }
    }
}

@Composable
private fun TeamMemberCard(
    member: TeamMember,
    canManage: Boolean,
    onChangeRole: () -> Unit,
    onRemove: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    val roleColor = when (member.role) {
        "OWNER" -> DevAmber
        "ADMIN" -> DevRose
        "EDITOR" -> DevBlueLight
        else -> DevEmeraldLight
    }

    val roleDescription = when (member.role) {
        "OWNER" -> "Full Administrative Control • Workspace Billing • Security"
        "ADMIN" -> "Can Invite/Manage Members • Deploy • Full AI Studio Access"
        "EDITOR" -> "Can Create & Edit Code Snippets • Run AI Studio Prompts"
        else -> "Read-Only Access to Snippets, Docs & Architecture Guides"
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.horizontalGradient(listOf(DarkCardBorder, DarkCardBorder.copy(alpha = 0.5f)))
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Member Avatar with Initials
            val initials = member.name.split(" ").take(2).mapNotNull { it.firstOrNull()?.toString() }.joinToString("")
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                roleColor.copy(alpha = 0.25f),
                                roleColor.copy(alpha = 0.08f)
                            )
                        )
                    )
                    .border(1.5.dp, roleColor.copy(alpha = 0.7f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = initials.ifEmpty { "DEV" },
                    color = roleColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    fontFamily = FontFamily.Monospace
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Info Column
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = member.name,
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    RoleBadge(role = member.role, color = roleColor)
                }

                Text(
                    text = member.email,
                    color = DarkTextSecondary,
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = member.department,
                        color = Color(0xFF94A3B8),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = " • ",
                        color = DarkTextMuted,
                        fontSize = 11.sp
                    )
                    Text(
                        text = roleDescription,
                        color = DarkTextMuted,
                        fontSize = 11.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // Options Dropdown Menu for Management
            if (canManage && member.role != "OWNER") {
                Box {
                    IconButton(onClick = { showMenu = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Options", tint = Color.Gray)
                    }

                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false },
                        modifier = Modifier.background(DarkSurfaceElevated)
                    ) {
                        DropdownMenuItem(
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = DevBlueLight, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Change Role", color = Color.White, fontSize = 13.sp)
                                }
                            },
                            onClick = {
                                showMenu = false
                                onChangeRole()
                            }
                        )
                        HorizontalDivider(color = DarkCardBorder)
                        DropdownMenuItem(
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Delete, contentDescription = null, tint = DevRose, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Revoke Access", color = DevRose, fontSize = 13.sp)
                                }
                            },
                            onClick = {
                                showMenu = false
                                onRemove()
                            }
                        )
                    }
                }
            }
        }
    }
}

// ----------------------------------------------------
// TAB 2: RBAC MATRIX & RESOURCE POLICIES
// ----------------------------------------------------
@Composable
private fun RbacPolicyTab(
    savedSnippets: List<SavedSnippet>,
    promptTemplates: List<PromptTemplate>,
    resourcePolicies: List<ResourcePolicy>,
    canManage: Boolean,
    onUpdatePolicy: (resourceId: Long, resourceType: String, title: String, visibility: String, minRole: String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // RBAC Permissions Matrix Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = Brush.horizontalGradient(listOf(DarkCardBorder, DarkCardBorder.copy(alpha = 0.5f)))
                ),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Security, contentDescription = null, tint = DevIndigoLight, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Role-Based Access Control (RBAC) Matrix", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }
                    Text(
                        text = "Standard enterprise authorization boundaries across DevStudio Hub modules",
                        color = DarkTextSecondary,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 2.dp, bottom = 12.dp)
                    )

                    RbacMatrixRow(role = "OWNER", title = "Workspace Owner", color = DevAmber, permissions = listOf("Full Administrative Access", "Manage Billing & Secrets", "Manage All Team Roles", "Deploy to Production"))
                    HorizontalDivider(color = DarkCardBorder.copy(alpha = 0.5f), modifier = Modifier.padding(vertical = 8.dp))
                    RbacMatrixRow(role = "ADMIN", title = "Lead Architect / Admin", color = DevRose, permissions = listOf("Invite / Remove Members", "Configure Project IDX & Cloud Run", "Edit Protected Prompts", "Execute Test Suites"))
                    HorizontalDivider(color = DarkCardBorder.copy(alpha = 0.5f), modifier = Modifier.padding(vertical = 8.dp))
                    RbacMatrixRow(role = "EDITOR", title = "Core Developer", color = DevBlueLight, permissions = listOf("Create / Edit Code Snippets", "Run Gemini AI Studio Prompts", "Save Custom Templates", "Execute Sandbox Builds"))
                    HorizontalDivider(color = DarkCardBorder.copy(alpha = 0.5f), modifier = Modifier.padding(vertical = 8.dp))
                    RbacMatrixRow(role = "VIEWER", title = "Auditor / Junior Dev", color = DevEmeraldLight, permissions = listOf("Read-Only Code Explorer", "Inspect Architectural Guides", "View Quiz & Test Logs", "Run Pre-Approved Prompts"))
                }
            }
        }

        // Section: Resource Security Policy Manager
        item {
            Text(
                text = "Protected Resource Policies",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Configure sharing visibility and minimum required access role for code snippets and AI prompts",
                color = DarkTextSecondary,
                fontSize = 12.sp
            )
        }

        // Snippet Policies
        item {
            Text("Code Snippets (${savedSnippets.size})", color = DevBlueLight, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
        }

        items(savedSnippets, key = { "snip_${it.id}" }) { snippet ->
            val policy = resourcePolicies.find { it.resourceType == "SNIPPET" && it.resourceId == snippet.id }
            val currentVisibility = policy?.visibility ?: "TEAM_ONLY"
            val currentMinRole = policy?.minRequiredRole ?: "VIEWER"

            ResourcePolicyItemCard(
                title = snippet.title,
                subtitle = "${snippet.category} • ${snippet.language.uppercase()}",
                resourceType = "SNIPPET",
                currentVisibility = currentVisibility,
                currentMinRole = currentMinRole,
                canManage = canManage,
                onVisibilityChange = { newVis ->
                    onUpdatePolicy(snippet.id, "SNIPPET", snippet.title, newVis, currentMinRole)
                },
                onMinRoleChange = { newRole ->
                    onUpdatePolicy(snippet.id, "SNIPPET", snippet.title, currentVisibility, newRole)
                }
            )
        }

        // Prompt Policies
        item {
            Spacer(modifier = Modifier.height(6.dp))
            Text("AI Studio Prompt Templates (${promptTemplates.size})", color = DevIndigoLight, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
        }

        items(promptTemplates, key = { "prompt_${it.id}" }) { prompt ->
            val policy = resourcePolicies.find { it.resourceType == "PROMPT" && it.resourceId == prompt.id }
            val currentVisibility = policy?.visibility ?: "TEAM_ONLY"
            val currentMinRole = policy?.minRequiredRole ?: "VIEWER"

            ResourcePolicyItemCard(
                title = prompt.title,
                subtitle = "${prompt.tag} • ${prompt.targetModel}",
                resourceType = "PROMPT",
                currentVisibility = currentVisibility,
                currentMinRole = currentMinRole,
                canManage = canManage,
                onVisibilityChange = { newVis ->
                    onUpdatePolicy(prompt.id, "PROMPT", prompt.title, newVis, currentMinRole)
                },
                onMinRoleChange = { newRole ->
                    onUpdatePolicy(prompt.id, "PROMPT", prompt.title, currentVisibility, newRole)
                }
            )
        }
    }
}

@Composable
private fun RbacMatrixRow(
    role: String,
    title: String,
    color: Color,
    permissions: List<String>
) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            RoleBadge(role = role, color = color)
            Spacer(modifier = Modifier.width(8.dp))
            Text(title, color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
        }
        Spacer(modifier = Modifier.height(4.dp))
        permissions.forEach { perm ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(start = 4.dp, top = 2.dp)
            ) {
                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = color, modifier = Modifier.size(12.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(perm, color = Color(0xFFCBD5E1), fontSize = 11.sp)
            }
        }
    }
}

@Composable
private fun ResourcePolicyItemCard(
    title: String,
    subtitle: String,
    resourceType: String,
    currentVisibility: String,
    currentMinRole: String,
    canManage: Boolean,
    onVisibilityChange: (String) -> Unit,
    onMinRoleChange: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.horizontalGradient(listOf(DarkCardBorder, DarkCardBorder.copy(alpha = 0.5f)))
        ),
        shape = RoundedCornerShape(10.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(title, color = Color.White, fontWeight = FontWeight.Medium, fontSize = 14.sp)
                Text(subtitle, color = DarkTextSecondary, fontSize = 11.sp)
            }

            // Controls
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Visibility Toggle Button
                val (visIcon, visLabel, visColor) = when (currentVisibility) {
                    "PUBLIC" -> Triple(Icons.Default.Public, "Public", DevEmeraldLight)
                    "ADMIN_ONLY" -> Triple(Icons.Default.Lock, "Admin Only", DevRose)
                    else -> Triple(Icons.Default.Group, "Team Only", DevBlueLight)
                }

                Box {
                    var showVisMenu by remember { mutableStateOf(false) }
                    OutlinedButton(
                        onClick = { if (canManage) showVisMenu = true },
                        modifier = Modifier.height(34.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = visColor),
                        border = ButtonDefaults.outlinedButtonBorder(enabled = true).copy(
                            brush = Brush.linearGradient(listOf(visColor.copy(alpha = 0.5f), visColor.copy(alpha = 0.5f)))
                        )
                    ) {
                        Icon(visIcon, contentDescription = null, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(visLabel, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    if (canManage) {
                        DropdownMenu(
                            expanded = showVisMenu,
                            onDismissRequest = { showVisMenu = false },
                            modifier = Modifier.background(DarkSurfaceElevated)
                        ) {
                            DropdownMenuItem(
                                text = { Text("Public (Anyone with link)", color = DevEmeraldLight, fontSize = 12.sp) },
                                onClick = {
                                    showVisMenu = false
                                    onVisibilityChange("PUBLIC")
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Team Only (Workspace members)", color = DevBlueLight, fontSize = 12.sp) },
                                onClick = {
                                    showVisMenu = false
                                    onVisibilityChange("TEAM_ONLY")
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Admin Only (Restricted)", color = DevRose, fontSize = 12.sp) },
                                onClick = {
                                    showVisMenu = false
                                    onVisibilityChange("ADMIN_ONLY")
                                }
                            )
                        }
                    }
                }

                // Minimum Role Selector
                Box {
                    var showRoleMenu by remember { mutableStateOf(false) }
                    OutlinedButton(
                        onClick = { if (canManage) showRoleMenu = true },
                        modifier = Modifier.height(34.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                        border = ButtonDefaults.outlinedButtonBorder(enabled = true).copy(
                            brush = Brush.linearGradient(listOf(DarkCardBorder, DarkCardBorder))
                        )
                    ) {
                        Icon(Icons.Default.Shield, contentDescription = null, tint = DevIndigoLight, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Min: $currentMinRole", fontSize = 11.sp)
                    }

                    if (canManage) {
                        DropdownMenu(
                            expanded = showRoleMenu,
                            onDismissRequest = { showRoleMenu = false },
                            modifier = Modifier.background(DarkSurfaceElevated)
                        ) {
                            listOf("VIEWER", "EDITOR", "ADMIN").forEach { role ->
                                DropdownMenuItem(
                                    text = { Text("Requires $role+", color = Color.White, fontSize = 12.sp) },
                                    onClick = {
                                        showRoleMenu = false
                                        onMinRoleChange(role)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// ----------------------------------------------------
// TAB 3: SHAREABLE INVITE LINKS
// ----------------------------------------------------
@Composable
private fun InviteLinksTab(
    inviteTokens: List<ShareInviteToken>,
    canManage: Boolean,
    onCreateLink: () -> Unit,
    onRevoke: (Long) -> Unit,
    onCopy: (String, String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "Active Shareable Invites",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Generate and manage one-time or team join links with preset roles",
                    color = DarkTextSecondary,
                    fontSize = 12.sp
                )
            }

            if (canManage) {
                Button(
                    onClick = onCreateLink,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = DevBlueLight, contentColor = Color(0xFF040814))
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("New Link", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        if (inviteTokens.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(DarkSurface)
                    .border(1.dp, DarkCardBorder, RoundedCornerShape(12.dp))
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Link, contentDescription = null, tint = DarkTextMuted, modifier = Modifier.size(48.dp))
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("No active share links created yet", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                    Text("Click 'New Link' above to generate team access invitations", color = DarkTextSecondary, fontSize = 12.sp)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(inviteTokens, key = { it.id }) { token ->
                    InviteLinkCard(
                        token = token,
                        canManage = canManage,
                        onRevoke = { onRevoke(token.id) },
                        onCopy = { onCopy(token.tokenCode, "Share Token") }
                    )
                }
            }
        }
    }
}

@Composable
private fun InviteLinkCard(
    token: ShareInviteToken,
    canManage: Boolean,
    onRevoke: () -> Unit,
    onCopy: () -> Unit
) {
    val isExpired = token.expiresAt < System.currentTimeMillis()
    val isMaxedOut = token.usedCount >= token.maxUses

    val roleColor = when (token.targetRole) {
        "ADMIN" -> DevRose
        "EDITOR" -> DevBlueLight
        else -> DevEmeraldLight
    }

    val dateFormat = remember { SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault()) }
    val expirationText = dateFormat.format(Date(token.expiresAt))

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.horizontalGradient(listOf(DarkCardBorder, DarkCardBorder.copy(alpha = 0.5f)))
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(DarkCodeBg)
                            .border(1.dp, DevBlueLight.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = token.tokenCode,
                            color = DevBlueLight,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    RoleBadge(role = token.targetRole, color = roleColor)
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onCopy, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = DevBlueLight, modifier = Modifier.size(16.dp))
                    }
                    if (canManage) {
                        IconButton(onClick = onRevoke, modifier = Modifier.size(32.dp)) {
                            Icon(Icons.Default.Delete, contentDescription = "Revoke", tint = DevRose, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Timer, contentDescription = null, tint = if (isExpired) DevRose else DarkTextSecondary, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isExpired) "Expired on $expirationText" else "Expires $expirationText",
                        color = if (isExpired) DevRose else DarkTextSecondary,
                        fontSize = 11.sp
                    )
                }

                Text(
                    text = "Used ${token.usedCount}/${token.maxUses} times",
                    color = if (isMaxedOut) DevAmber else DevEmeraldLight,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
        }
    }
}

// ----------------------------------------------------
// HELPER DIALOGS & COMPONENTS
// ----------------------------------------------------
@Composable
private fun WorkspaceStatCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(DarkSurfaceElevated)
            .border(1.dp, DarkCardBorder, RoundedCornerShape(10.dp))
            .padding(10.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(title, color = DarkTextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Medium)
                Icon(icon, contentDescription = null, tint = accentColor, modifier = Modifier.size(14.dp))
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
        }
    }
}

@Composable
private fun RoleBadge(role: String, color: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(color.copy(alpha = 0.15f))
            .border(1.dp, color.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(
            text = role,
            color = color,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp,
            fontFamily = FontFamily.Monospace
        )
    }
}

@Composable
private fun InviteMemberDialog(
    onDismiss: () -> Unit,
    onConfirm: (name: String, email: String, role: String, department: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var department by remember { mutableStateOf("Full-Stack Engineer") }
    var selectedRole by remember { mutableStateOf("EDITOR") }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.GroupAdd, contentDescription = null, tint = DevEmeraldLight)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Invite Team Member", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column {
                Text(
                    text = "Add a collaborator to this workspace. They will receive role-appropriate permissions for Project IDX environments and Google AI Studio models.",
                    color = DarkTextSecondary,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                if (errorMsg != null) {
                    Text(errorMsg ?: "", color = DevRose, fontSize = 12.sp, modifier = Modifier.padding(bottom = 6.dp))
                }

                // Name
                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                        errorMsg = null
                    },
                    label = { Text("Full Name", fontSize = 12.sp) },
                    placeholder = { Text("e.g. Elena Rostova") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = DevEmeraldLight,
                        unfocusedBorderColor = DarkCardBorder,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedContainerColor = DarkSurfaceElevated,
                        unfocusedContainerColor = DarkSurfaceElevated
                    ),
                    shape = RoundedCornerShape(8.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Email
                OutlinedTextField(
                    value = email,
                    onValueChange = {
                        email = it
                        errorMsg = null
                    },
                    label = { Text("Email Address", fontSize = 12.sp) },
                    placeholder = { Text("developer@devstudio.io") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = DevEmeraldLight,
                        unfocusedBorderColor = DarkCardBorder,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedContainerColor = DarkSurfaceElevated,
                        unfocusedContainerColor = DarkSurfaceElevated
                    ),
                    shape = RoundedCornerShape(8.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Department
                OutlinedTextField(
                    value = department,
                    onValueChange = { department = it },
                    label = { Text("Department / Specialization", fontSize = 12.sp) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = DevEmeraldLight,
                        unfocusedBorderColor = DarkCardBorder,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedContainerColor = DarkSurfaceElevated,
                        unfocusedContainerColor = DarkSurfaceElevated
                    ),
                    shape = RoundedCornerShape(8.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Role Picker
                Text("Select Role & Access Level", color = Color(0xFFCBD5E1), fontSize = 12.sp, fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("ADMIN" to DevRose, "EDITOR" to DevBlueLight, "VIEWER" to DevEmeraldLight).forEach { (role, color) ->
                        val isSelected = selectedRole == role
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) color.copy(alpha = 0.2f) else DarkSurfaceElevated)
                                .border(1.dp, if (isSelected) color else DarkCardBorder, RoundedCornerShape(8.dp))
                                .clickable { selectedRole = role }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = role,
                                color = if (isSelected) color else DarkTextSecondary,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isBlank() || email.isBlank()) {
                        errorMsg = "Please fill in both name and email"
                    } else if (!email.contains("@")) {
                        errorMsg = "Please enter a valid email address"
                    } else {
                        onConfirm(name, email, selectedRole, department)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = DevEmeraldLight, contentColor = Color(0xFF040814))
            ) {
                Text("Send Invitation", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancel", color = Color.White)
            }
        },
        containerColor = DarkSurface,
        shape = RoundedCornerShape(14.dp)
    )
}

@Composable
private fun CreateInviteLinkDialog(
    onDismiss: () -> Unit,
    onConfirm: (targetRole: String, validDays: Int, maxUses: Int) -> Unit
) {
    var selectedRole by remember { mutableStateOf("EDITOR") }
    var selectedDays by remember { mutableIntStateOf(7) }
    var selectedUses by remember { mutableIntStateOf(10) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Link, contentDescription = null, tint = DevBlueLight)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Generate Shareable Link", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column {
                Text(
                    text = "Create an instant-join access token for team members with predefined roles.",
                    color = DarkTextSecondary,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Text("Target Role", color = Color(0xFFCBD5E1), fontSize = 12.sp, fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("ADMIN" to DevRose, "EDITOR" to DevBlueLight, "VIEWER" to DevEmeraldLight).forEach { (role, color) ->
                        val isSelected = selectedRole == role
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) color.copy(alpha = 0.2f) else DarkSurfaceElevated)
                                .border(1.dp, if (isSelected) color else DarkCardBorder, RoundedCornerShape(8.dp))
                                .clickable { selectedRole = role }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = role,
                                color = if (isSelected) color else DarkTextSecondary,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 11.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text("Expiration Period", color = Color(0xFFCBD5E1), fontSize = 12.sp, fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(1 to "24 Hours", 7 to "7 Days", 30 to "30 Days").forEach { (days, label) ->
                        val isSelected = selectedDays == days
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) DevBlueLight.copy(alpha = 0.2f) else DarkSurfaceElevated)
                                .border(1.dp, if (isSelected) DevBlueLight else DarkCardBorder, RoundedCornerShape(8.dp))
                                .clickable { selectedDays = days }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                color = if (isSelected) DevBlueLight else DarkTextSecondary,
                                fontSize = 11.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text("Maximum Uses", color = Color(0xFFCBD5E1), fontSize = 12.sp, fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(1 to "1 Use", 10 to "10 Uses", 50 to "50 Uses").forEach { (uses, label) ->
                        val isSelected = selectedUses == uses
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) DevIndigoLight.copy(alpha = 0.2f) else DarkSurfaceElevated)
                                .border(1.dp, if (isSelected) DevIndigoLight else DarkCardBorder, RoundedCornerShape(8.dp))
                                .clickable { selectedUses = uses }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                color = if (isSelected) DevIndigoLight else DarkTextSecondary,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(selectedRole, selectedDays, selectedUses) },
                colors = ButtonDefaults.buttonColors(containerColor = DevBlueLight, contentColor = Color(0xFF040814))
            ) {
                Text("Generate Token", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancel", color = Color.White)
            }
        },
        containerColor = DarkSurface,
        shape = RoundedCornerShape(14.dp)
    )
}

@Composable
private fun EditMemberRoleDialog(
    member: TeamMember,
    onDismiss: () -> Unit,
    onConfirm: (newRole: String) -> Unit
) {
    var selectedRole by remember { mutableStateOf(member.role) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Update Role for ${member.name}", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold) },
        text = {
            Column {
                Text(
                    text = "Assign a new security role for ${member.email}. Permissions take effect immediately.",
                    color = DarkTextSecondary,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                listOf(
                    Triple("ADMIN", "Lead / Admin", DevRose),
                    Triple("EDITOR", "Core Developer (Editor)", DevBlueLight),
                    Triple("VIEWER", "Auditor / Viewer", DevEmeraldLight)
                ).forEach { (role, label, color) ->
                    val isSelected = selectedRole == role
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) color.copy(alpha = 0.2f) else DarkSurfaceElevated)
                            .border(1.dp, if (isSelected) color else DarkCardBorder, RoundedCornerShape(8.dp))
                            .clickable { selectedRole = role }
                            .padding(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(label, color = if (isSelected) color else Color.White, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                            if (isSelected) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = color, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(selectedRole) },
                colors = ButtonDefaults.buttonColors(containerColor = DevBlueLight, contentColor = Color(0xFF040814))
            ) {
                Text("Save Role", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancel", color = Color.White)
            }
        },
        containerColor = DarkSurface,
        shape = RoundedCornerShape(14.dp)
    )
}
