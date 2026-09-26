package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_snippets")
data class SavedSnippet(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val language: String, // "dart", "typescript", "kotlin", "sql", "json", "python"
    val category: String, // "Flutter", "Next.js", "Firebase", "Database", "Testing", "Deployment", "AI Studio"
    val code: String,
    val description: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "prompt_templates")
data class PromptTemplate(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val systemInstruction: String,
    val userPrompt: String,
    val targetModel: String = "gemini-3.5-flash",
    val temperature: Float = 0.7f,
    val tag: String = "Engineering",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "quiz_results")
data class QuizResult(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val topic: String, // "Flutter", "Next.js", "Firebase", "Gemini AI", "Architecture"
    val difficulty: String, // "Junior", "Mid", "Senior", "Lead"
    val score: Int,
    val totalQuestions: Int,
    val feedback: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val role: String, // "user" or "model"
    val persona: String, // "Full-Stack Architect", "Flutter Master", "Next.js Pro", "Firebase Specialist", "Prompt Engineer"
    val content: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "deployment_tasks")
data class DeploymentTask(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val platform: String, // "Cloud Run", "Firebase Hosting", "Vercel", "Cloud Build"
    val taskName: String,
    val description: String,
    val isCompleted: Boolean = false,
    val command: String = ""
)

@Entity(tableName = "team_members")
data class TeamMember(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val workspaceId: String = "devstudio_main",
    val name: String,
    val email: String,
    val role: String, // "OWNER", "ADMIN", "EDITOR", "VIEWER"
    val department: String, // "Full-Stack", "AI / ML", "Frontend", "Cloud / DevOps", "QA & Security"
    val avatarColorHex: String = "#38BDF8",
    val status: String = "ACTIVE", // "ACTIVE", "INVITED", "SUSPENDED"
    val joinedTimestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "resource_policies")
data class ResourcePolicy(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val resourceType: String, // "SNIPPET", "PROMPT", "DEPLOYMENT"
    val resourceId: Long,
    val resourceTitle: String,
    val visibility: String = "TEAM_ONLY", // "PUBLIC", "TEAM_ONLY", "ADMIN_ONLY"
    val minRequiredRole: String = "VIEWER", // "VIEWER", "EDITOR", "ADMIN"
    val lastModifiedBy: String = "admin@devstudio.io",
    val updatedTimestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "share_invite_tokens")
data class ShareInviteToken(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val tokenCode: String,
    val targetRole: String = "EDITOR", // "VIEWER", "EDITOR", "ADMIN"
    val creatorEmail: String,
    val expiresAt: Long,
    val maxUses: Int = 10,
    val usedCount: Int = 0,
    val isRevoked: Boolean = false,
    val createdTimestamp: Long = System.currentTimeMillis()
)

