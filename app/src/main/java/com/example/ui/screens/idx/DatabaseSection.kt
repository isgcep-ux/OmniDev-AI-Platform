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
import androidx.compose.material.icons.filled.Dataset
import androidx.compose.material.icons.filled.Storage
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

@Composable
fun DatabaseSection(
    onSaveSnippet: (String, String, String, String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedDbType by remember { mutableStateOf("Room / SQLite") }

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
                            imageVector = Icons.Default.Storage,
                            contentDescription = null,
                            tint = DevEmeraldLight,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Database & Persistence",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    StatusBadge(text = "Multi-Engine", color = DevEmeraldLight)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "High-performance data layer strategies: Local offline caching with Android Room / SQLite, Server-side PostgreSQL on Cloud SQL, and Real-time NoSQL on Firestore.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // DB Engine Selector
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("Room / SQLite", "PostgreSQL", "Firestore NoSQL").forEach { dbType ->
                val isSelected = selectedDbType == dbType
                Button(
                    onClick = { selectedDbType = dbType },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isSelected) DevEmeraldLight else MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = if (isSelected) Color(0xFF064E3B) else MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f).height(40.dp).testTag("db_tab_$dbType")
                ) {
                    Text(dbType, fontSize = 10.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                }
            }
        }

        // Schema & Indexing Benchmark Card
        GlassmorphicCard(
            borderColor = Color(0xFF475569),
            backgroundColor = MaterialTheme.colorScheme.surface
        ) {
            Column {
                Text(
                    text = "Schema Characteristics & Performance",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF0F172A))
                            .border(1.dp, Color(0xFF334155), RoundedCornerShape(8.dp))
                            .padding(10.dp)
                    ) {
                        Text("Latency Profile", fontSize = 11.sp, color = DevBlueLight, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = when (selectedDbType) {
                                "Room / SQLite" -> "< 1ms (Local memory mapped)"
                                "PostgreSQL" -> "~15ms (Indexed b-tree scan)"
                                else -> "~35ms (Distributed WAN replica)"
                            },
                            fontSize = 11.sp,
                            color = Color(0xFFCBD5E1),
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF0F172A))
                            .border(1.dp, Color(0xFF334155), RoundedCornerShape(8.dp))
                            .padding(10.dp)
                    ) {
                        Text("ACID Guarantee", fontSize = 11.sp, color = DevEmeraldLight, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = when (selectedDbType) {
                                "Room / SQLite" -> "Full ACID (WAL mode)"
                                "PostgreSQL" -> "Strict ACID + Serializability"
                                else -> "Document-level ACID + Eventual"
                            },
                            fontSize = 11.sp,
                            color = Color(0xFFCBD5E1),
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }

        val codeSnippet = when (selectedDbType) {
            "Room / SQLite" -> """
// Room Database Entity & Flow DAO Pattern
@Entity(tableName = "projects")
data class ProjectEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val framework: String, // 'Flutter' | 'Next.js'
    val updatedAt: Long = System.currentTimeMillis()
)

@Dao
interface ProjectDao {
    @Query("SELECT * FROM projects ORDER BY updatedAt DESC")
    fun observeAllProjects(): Flow<List<ProjectEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertProject(project: ProjectEntity): Long
}
""".trimIndent()

            "PostgreSQL" -> """
-- PostgreSQL Relational Schema with Foreign Keys & Indexes
CREATE TABLE workspaces (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL,
    owner_id VARCHAR(128) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE deployments (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    workspace_id UUID REFERENCES workspaces(id) ON DELETE CASCADE,
    commit_sha VARCHAR(40) NOT NULL,
    status VARCHAR(32) DEFAULT 'QUEUED',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_deployments_workspace ON deployments(workspace_id, created_at DESC);
""".trimIndent()

            else -> """
// Firestore Multi-tenant NoSQL Document Structure
interface WorkspaceDoc {
  name: string;
  plan: 'free' | 'pro' | 'enterprise';
  createdAt: FirebaseFirestore.Timestamp;
  members: Record<string, 'owner' | 'editor' | 'viewer'>;
  stats: {
    totalBuilds: number;
    activeDeployments: number;
  };
}

// Subcollection: /workspaces/{id}/deployments/{depId}
interface DeploymentDoc {
  status: 'PENDING' | 'BUILDING' | 'LIVE' | 'FAILED';
  artifactUrl: string;
  logsUrl: string;
}
""".trimIndent()
        }

        CodeBlockView(
            code = codeSnippet,
            language = if (selectedDbType == "PostgreSQL") "sql" else if (selectedDbType == "Room / SQLite") "kotlin" else "typescript",
            title = "$selectedDbType Schema",
            onSaveSnippet = {
                onSaveSnippet(
                    "$selectedDbType Schema",
                    if (selectedDbType == "PostgreSQL") "sql" else if (selectedDbType == "Room / SQLite") "kotlin" else "typescript",
                    "Database",
                    codeSnippet,
                    "Database schema pattern for $selectedDbType"
                )
            }
        )
    }
}
