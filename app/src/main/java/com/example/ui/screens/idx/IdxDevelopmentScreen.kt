package com.example.ui.screens.idx

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.DataObject
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.DeploymentTask
import com.example.ui.theme.DevBlueDark
import com.example.ui.theme.DevBlueLight
import com.example.ui.theme.DevEmeraldLight
import com.example.ui.theme.DevIndigoLight
import com.example.ui.viewmodel.TestSuiteItem

data class IdxTabItem(
    val title: String,
    val icon: ImageVector,
    val color: Color
)

@Composable
fun IdxDevelopmentScreen(
    currentSection: Int,
    onSelectSection: (Int) -> Unit,
    testSuite: List<TestSuiteItem>,
    isTestsRunning: Boolean,
    onRunAllTests: () -> Unit,
    deploymentTasks: List<DeploymentTask>,
    onToggleDeploymentTask: (DeploymentTask) -> Unit,
    onSaveSnippet: (String, String, String, String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    val tabs = listOf(
        IdxTabItem("Flutter Web", Icons.Default.Devices, DevBlueLight),
        IdxTabItem("Next.js", Icons.Default.Language, DevIndigoLight),
        IdxTabItem("Firebase", Icons.Default.LocalFireDepartment, Color(0xFFF59E0B)),
        IdxTabItem("Database", Icons.Default.Storage, DevEmeraldLight),
        IdxTabItem("Tests", Icons.Default.Science, Color(0xFF818CF8)),
        IdxTabItem("Deployment", Icons.Default.RocketLaunch, DevEmeraldLight)
    )

    val verticalScrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(verticalScrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Horizontal Scrollable Tabs
        ScrollableTabRow(
            selectedTabIndex = currentSection,
            edgePadding = 0.dp,
            containerColor = Color.Transparent,
            contentColor = DevBlueLight,
            indicator = { tabPositions ->
                if (currentSection < tabPositions.size) {
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[currentSection]),
                        color = tabs[currentSection].color,
                        height = 3.dp
                    )
                }
            },
            divider = {}
        ) {
            tabs.forEachIndexed { index, tabItem ->
                val isSelected = currentSection == index
                Tab(
                    selected = isSelected,
                    onClick = { onSelectSection(index) },
                    text = {
                        Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                            Icon(
                                imageVector = tabItem.icon,
                                contentDescription = null,
                                tint = if (isSelected) tabItem.color else Color(0xFF64748B),
                                modifier = Modifier.padding(end = 6.dp)
                            )
                            Text(
                                text = tabItem.title,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 13.sp,
                                color = if (isSelected) MaterialTheme.colorScheme.onSurface else Color(0xFF94A3B8)
                            )
                        }
                    },
                    modifier = Modifier.testTag("idx_tab_$index")
                )
            }
        }

        // Section Content
        when (currentSection) {
            0 -> FlutterSection(onSaveSnippet = onSaveSnippet)
            1 -> NextJsSection(onSaveSnippet = onSaveSnippet)
            2 -> FirebaseSection(onSaveSnippet = onSaveSnippet)
            3 -> DatabaseSection(onSaveSnippet = onSaveSnippet)
            4 -> TestingSection(
                testSuite = testSuite,
                isRunning = isTestsRunning,
                onRunAllTests = onRunAllTests,
                onSaveSnippet = onSaveSnippet
            )
            5 -> DeploymentSection(
                tasks = deploymentTasks,
                onToggleTask = onToggleDeploymentTask,
                onSaveSnippet = onSaveSnippet
            )
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}
