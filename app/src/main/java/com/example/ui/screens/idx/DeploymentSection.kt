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
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
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
import com.example.data.local.entity.DeploymentTask
import com.example.ui.components.CodeBlockView
import com.example.ui.components.GlassmorphicCard
import com.example.ui.components.StatusBadge
import com.example.ui.theme.DevBlueLight
import com.example.ui.theme.DevEmeraldLight
import com.example.ui.theme.DevRose

@Composable
fun DeploymentSection(
    tasks: List<DeploymentTask>,
    onToggleTask: (DeploymentTask) -> Unit,
    onSaveSnippet: (String, String, String, String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    val completedCount = tasks.count { it.isCompleted }

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
                            imageVector = Icons.Default.RocketLaunch,
                            contentDescription = null,
                            tint = DevEmeraldLight,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Cloud Deployment & CI/CD",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    StatusBadge(text = "$completedCount/${tasks.size} Ready", color = DevEmeraldLight)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "One-click deployment pipelines from Project IDX: Google Cloud Run containerization, Firebase Hosting Web Frameworks, Vercel Edge networks, and automated Cloud Build stages.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Interactive Deployment Checklist Card (Backed by Room)
        GlassmorphicCard(
            borderColor = Color(0xFF475569),
            backgroundColor = MaterialTheme.colorScheme.surface
        ) {
            Column {
                Text(
                    text = "Production Release Checklist (Room Persistent)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(10.dp))

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    tasks.forEach { task ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (task.isCompleted) Color(0xFF064E3B).copy(alpha = 0.3f) else Color(0xFF0F172A))
                                .border(1.dp, if (task.isCompleted) DevEmeraldLight.copy(alpha = 0.4f) else Color(0xFF334155), RoundedCornerShape(8.dp))
                                .padding(8.dp)
                                .testTag("deploy_task_${task.id}"),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = task.isCompleted,
                                onCheckedChange = { onToggleTask(task) },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = DevEmeraldLight,
                                    checkmarkColor = Color(0xFF064E3B)
                                )
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "[${task.platform}] ${task.taskName}",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (task.isCompleted) DevEmeraldLight else Color(0xFFF8FAFC)
                                )
                                Text(
                                    text = task.description,
                                    fontSize = 11.sp,
                                    color = Color(0xFF94A3B8)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Live Build Terminal Simulator
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF090D16))
                .border(1.dp, Color(0xFF334155), RoundedCornerShape(12.dp))
                .padding(14.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Terminal, contentDescription = null, tint = DevBlueLight, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Cloud Build Log Stream", color = Color(0xFFE2E8F0), fontSize = 12.sp, fontFamily = FontFamily.Monospace)
                    }
                    StatusBadge(text = "LIVE", color = DevEmeraldLight)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "$ gcloud builds submit --tag gcr.io/project-idx-cloud/app:v1.0\n" +
                            "[1/4] STEP: Building multi-stage Docker container... [DONE in 14.2s]\n" +
                            "[2/4] STEP: Running Next.js build & Flutter WASM export... [DONE in 21.0s]\n" +
                            "[3/4] STEP: Deploying container image to Cloud Run (europe-west2)... [DONE in 8.4s]\n" +
                            "[4/4] SUCCESS: URL: https://idx-app-live-340572404376.a.run.app (Healthy 200 OK)",
                    color = Color(0xFF38BDF8),
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    lineHeight = 18.sp
                )
            }
        }

        val cloudbuildYaml = """
# cloudbuild.yaml (Google Cloud Build CI/CD Pipeline)
steps:
  # 1. Install & Build Full-Stack Next.js & Flutter assets
  - name: 'node:20'
    entrypoint: 'npm'
    args: ['ci']
  - name: 'node:20'
    entrypoint: 'npm'
    args: ['run', 'build']

  # 2. Build Container Image
  - name: 'gcr.io/cloud-builders/docker'
    args: ['build', '-t', 'gcr.io/${'$'}PROJECT_ID/devstudio-hub:${'$'}COMMIT_SHA', '.']

  # 3. Push to Google Container Registry
  - name: 'gcr.io/cloud-builders/docker'
    args: ['push', 'gcr.io/${'$'}PROJECT_ID/devstudio-hub:${'$'}COMMIT_SHA']

  # 4. Deploy to Google Cloud Run
  - name: 'gcr.io/google.com/cloudsdktool/cloud-sdk'
    entrypoint: 'gcloud'
    args:
      - 'run'
      - 'deploy'
      - 'devstudio-service'
      - '--image=gcr.io/${'$'}PROJECT_ID/devstudio-hub:${'$'}COMMIT_SHA'
      - '--region=europe-west2'
      - '--platform=managed'
      - '--allow-unauthenticated'
""".trimIndent()

        CodeBlockView(
            code = cloudbuildYaml,
            language = "json",
            title = "Cloud Build Pipeline (cloudbuild.yaml)",
            onSaveSnippet = {
                onSaveSnippet(
                    "Cloud Build Pipeline",
                    "json",
                    "Deployment",
                    cloudbuildYaml,
                    "Automated Cloud Build CI/CD script for Cloud Run"
                )
            }
        )
    }
}
