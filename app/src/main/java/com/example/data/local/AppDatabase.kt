package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.local.dao.DevStudioDao
import com.example.data.local.entity.ChatMessageEntity
import com.example.data.local.entity.DeploymentTask
import com.example.data.local.entity.ISGContent
import com.example.data.local.entity.PromptTemplate
import com.example.data.local.entity.QuizResult
import com.example.data.local.entity.ResourcePolicy
import com.example.data.local.entity.SavedSnippet
import com.example.data.local.entity.ShareInviteToken
import com.example.data.local.entity.TeamMember
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        SavedSnippet::class,
        PromptTemplate::class,
        QuizResult::class,
        ChatMessageEntity::class,
        DeploymentTask::class,
        TeamMember::class,
        ResourcePolicy::class,
        ShareInviteToken::class,
        ISGContent::class
    ],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun devStudioDao(): DevStudioDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "dev_studio_hub.db"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(DatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateInitialData(database.devStudioDao())
                    }
                }
            }

            private suspend fun populateInitialData(dao: DevStudioDao) {
                // Initial Default Snippets
                dao.insertSnippet(
                    SavedSnippet(
                        title = "Flutter Web Responsive Layout",
                        language = "dart",
                        category = "Flutter",
                        code = "LayoutBuilder(\n  builder: (context, constraints) {\n    if (constraints.maxWidth > 800) {\n      return WideScreenDashboard();\n    } else {\n      return MobileDrawerNav();\n    }\n  },\n)",
                        description = "Adaptive layout builder pattern for Flutter on Project IDX Web."
                    )
                )
                dao.insertSnippet(
                    SavedSnippet(
                        title = "Next.js App Router Server Action",
                        language = "typescript",
                        category = "Next.js",
                        code = "'use server'\n\nexport async function createUser(formData: FormData) {\n  const email = formData.get('email') as string;\n  await db.user.create({ data: { email } });\n  revalidatePath('/dashboard');\n}",
                        description = "Type-safe server mutation in Next.js 15+ App Router."
                    )
                )
                dao.insertSnippet(
                    SavedSnippet(
                        title = "Firebase Firestore Security Rules",
                        language = "json",
                        category = "Firebase",
                        code = "rules_version = '2';\nservice cloud.firestore {\n  match /databases/{database}/documents {\n    match /users/{userId} {\n      allow read, write: if request.auth != null && request.auth.uid == userId;\n    }\n  }\n}",
                        description = "Per-user strict Firestore security rules template."
                    )
                )

                // Initial Prompt Templates
                dao.insertPrompt(
                    PromptTemplate(
                        title = "Full-Stack Code Reviewer",
                        systemInstruction = "You are a Principal Software Architect. Review the given code for performance, security vulnerabilities, edge cases, and modern idioms across Flutter, Next.js, and Firebase.",
                        userPrompt = "Please analyze this component architecture and suggest optimizations with code diffs:",
                        tag = "Code Review"
                    )
                )
                dao.insertPrompt(
                    PromptTemplate(
                        title = "Structured API Schema Generator",
                        systemInstruction = "You generate production-ready TypeScript types and database migration schemas from business requirements.",
                        userPrompt = "Design a schema for a real-time collaborative workspace with users, projects, and AI prompt runs.",
                        tag = "Architecture"
                    )
                )

                // Initial Deployment Tasks
                val initialTasks = listOf(
                    DeploymentTask(
                        platform = "Cloud Run",
                        taskName = "Containerize Next.js / Flutter App",
                        description = "Create optimized multi-stage Dockerfile with standalone output.",
                        command = "docker build -t gcr.io/\$PROJECT_ID/app:latest ."
                    ),
                    DeploymentTask(
                        platform = "Firebase Hosting",
                        taskName = "Configure Firebase Web Frameworks",
                        description = "Enable experimental web frameworks support in firebase.json.",
                        command = "firebase experiments:enable webframeworks"
                    ),
                    DeploymentTask(
                        platform = "Vercel",
                        taskName = "Link Git Repository & Environment Secrets",
                        description = "Set GEMINI_API_KEY and NEXT_PUBLIC_FIREBASE_CONFIG in environment.",
                        command = "vercel env pull .env.production.local"
                    ),
                    DeploymentTask(
                        platform = "Cloud Build",
                        taskName = "Set up Automated CI/CD Trigger",
                        description = "Build cloudbuild.yaml with linting, unit test execution, and deployment.",
                        command = "gcloud builds submit --config cloudbuild.yaml"
                    )
                )
                dao.insertDeploymentTasks(initialTasks)

                // Initial Team Members
                val initialTeam = listOf(
                    TeamMember(
                        name = "Alex Vance",
                        email = "alex.vance@devstudio.io",
                        role = "OWNER",
                        department = "Full-Stack Architect",
                        avatarColorHex = "#F59E0B",
                        status = "ACTIVE"
                    ),
                    TeamMember(
                        name = "Sarah Chen",
                        email = "sarah.chen@devstudio.io",
                        role = "ADMIN",
                        department = "AI / ML Researcher",
                        avatarColorHex = "#EC4899",
                        status = "ACTIVE"
                    ),
                    TeamMember(
                        name = "Marcus Thorne",
                        email = "marcus.t@devstudio.io",
                        role = "EDITOR",
                        department = "Frontend & Flutter Lead",
                        avatarColorHex = "#38BDF8",
                        status = "ACTIVE"
                    ),
                    TeamMember(
                        name = "Elena Rostova",
                        email = "elena.r@devstudio.io",
                        role = "EDITOR",
                        department = "Cloud / DevOps",
                        avatarColorHex = "#818CF8",
                        status = "ACTIVE"
                    ),
                    TeamMember(
                        name = "Liam O'Connor",
                        email = "liam.oc@devstudio.io",
                        role = "VIEWER",
                        department = "QA & Security Auditor",
                        avatarColorHex = "#34D399",
                        status = "ACTIVE"
                    )
                )
                dao.insertTeamMembers(initialTeam)

                // Initial Resource Policies
                val initialPolicies = listOf(
                    ResourcePolicy(
                        resourceType = "SNIPPET",
                        resourceId = 1,
                        resourceTitle = "Flutter Web Responsive Layout",
                        visibility = "PUBLIC",
                        minRequiredRole = "VIEWER",
                        lastModifiedBy = "alex.vance@devstudio.io"
                    ),
                    ResourcePolicy(
                        resourceType = "SNIPPET",
                        resourceId = 2,
                        resourceTitle = "Next.js App Router Server Action",
                        visibility = "TEAM_ONLY",
                        minRequiredRole = "EDITOR",
                        lastModifiedBy = "marcus.t@devstudio.io"
                    ),
                    ResourcePolicy(
                        resourceType = "SNIPPET",
                        resourceId = 3,
                        resourceTitle = "Firebase Firestore Security Rules",
                        visibility = "ADMIN_ONLY",
                        minRequiredRole = "ADMIN",
                        lastModifiedBy = "sarah.chen@devstudio.io"
                    ),
                    ResourcePolicy(
                        resourceType = "PROMPT",
                        resourceId = 1,
                        resourceTitle = "Full-Stack Code Reviewer",
                        visibility = "TEAM_ONLY",
                        minRequiredRole = "VIEWER",
                        lastModifiedBy = "alex.vance@devstudio.io"
                    ),
                    ResourcePolicy(
                        resourceType = "PROMPT",
                        resourceId = 2,
                        resourceTitle = "Structured API Schema Generator",
                        visibility = "TEAM_ONLY",
                        minRequiredRole = "EDITOR",
                        lastModifiedBy = "sarah.chen@devstudio.io"
                    )
                )
                dao.insertResourcePolicies(initialPolicies)

                // Initial Active Invite Link
                dao.insertInviteToken(
                    ShareInviteToken(
                        tokenCode = "STUDIO-DEV-98X7A",
                        targetRole = "EDITOR",
                        creatorEmail = "alex.vance@devstudio.io",
                        expiresAt = System.currentTimeMillis() + (7L * 24 * 60 * 60 * 1000),
                        maxUses = 15,
                        usedCount = 3,
                        isRevoked = false
                    )
                )

                // Initial Health and Safety (ISG) Lessons
                val initialISGLessons = listOf(
                    ISGContent(
                        title = "6331 Sayılı İSG Kanunu Temel İlkeleri",
                        category = "Mevzuat & Hukuk",
                        content = "6331 sayılı İş Sağlığı ve Güvenliği Kanunu; kamu ve özel sektöre ait bütün işlere ve işyerlerine, çırak ve stajyerler dahil tüm çalışanlara faaliyet konularına bakılmaksızın uygulanır. Temel amaç, iş kazaları ve meslek hastalıklarını önleyici yaklaşım ile proaktif olarak bertaraf etmektir."
                    ),
                    ISGContent(
                        title = "5x5 L-Tipi Risk Değerlendirme Metodolojisi",
                        category = "Risk Değerlendirmesi",
                        content = "Risk Skoru = Olasılık (1-5) x Şiddet (1-5). 1-6 arası Düşük Risk (Kabul edilebilir), 8-12 arası Orta Risk (Önlem planlanmalı), 15-25 arası Yüksek/Katlanılamaz Risk (İş derhal durdurulmalı ve acil eylemler alınmalıdır)."
                    ),
                    ISGContent(
                        title = "Kişisel Koruyucu Donanım (KKD) Hiyerarşisi",
                        category = "İş Hijyeni & Güvenlik",
                        content = "Tehlikelerle mücadelede öncelik sırası: 1. Tehlikeyi kaynağında yok etme (Eliminasyon), 2. Tehlikeli olanı daha az tehlikeli ile değiştirme (İkame), 3. Mühendislik kontrolleri & izolasyon, 4. İdari kontroller & eğitim, 5. En son çare olarak Kişisel Koruyucu Donanım (KKD) kullanımı."
                    ),
                    ISGContent(
                        title = "Fiziksel Risk Etmenleri: Gürültü Maruziyet Sınırları",
                        category = "Fiziksel Riskler",
                        content = "Gürültü Yönetmeliği uyarınca 8 saatlik çalışma için: En düşük maruziyet eylem değeri 80 dB(A), En yüksek maruziyet eylem değeri 85 dB(A), Maruziyet sınır değeri 87 dB(A) olarak belirlenmiştir. Sınır değer uygulanırken kulak koruyucularının etkisi dikkate alınır."
                    )
                )
                dao.insertAllISGContent(initialISGLessons)
            }
        }
    }
}
