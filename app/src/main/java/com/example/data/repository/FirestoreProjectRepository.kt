package com.example.data.repository

import android.util.Log
import com.example.data.model.Project
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class FirestoreProjectRepository(
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) {
    private val TAG = "FirestoreProjectRepo"

    // Initial development projects for rich dashboard experience
    private val initialProjects = listOf(
        Project(
            id = "proj-001",
            name = "DevOS Android Workstation",
            description = "High-performance developer workspace container with Claude Code CLI and instant APK assembly.",
            status = Project.STATUS_IN_PROGRESS,
            techStack = "Jetpack Compose • Room • Coroutines",
            completionPercentage = 75,
            lead = "Alex Rivera",
            createdAt = System.currentTimeMillis() - 86400000L * 5
        ),
        Project(
            id = "proj-002",
            name = "Project IDX Cloud IDE",
            description = "Multiplatform full-stack templates for Next.js, Flutter, and serverless Cloud Run deployments.",
            status = Project.STATUS_COMPLETED,
            techStack = "Next.js • TypeScript • Cloud Run",
            completionPercentage = 100,
            lead = "Sarah Chen",
            createdAt = System.currentTimeMillis() - 86400000L * 12
        ),
        Project(
            id = "proj-003",
            name = "Gemini 3.5 AI Tutor & Studio",
            description = "Interactive prompt engineering sandbox with few-shot evaluation and dynamic quiz generations.",
            status = Project.STATUS_IN_PROGRESS,
            techStack = "Gemini API • Kotlin Coroutines",
            completionPercentage = 60,
            lead = "Devin Miller",
            createdAt = System.currentTimeMillis() - 86400000L * 3
        ),
        Project(
            id = "proj-004",
            name = "Team RBAC & Access Control",
            description = "Fine-grained role-based permissions, resource sharing tokens, and enterprise policy guardrails.",
            status = Project.STATUS_COMPLETED,
            techStack = "Firebase Security Rules • Room SQLite",
            completionPercentage = 100,
            lead = "Elena Rostova",
            createdAt = System.currentTimeMillis() - 86400000L * 18
        ),
        Project(
            id = "proj-005",
            name = "Recharts Analytics & Metrics Engine",
            description = "Visual distribution metrics and status distribution charts for active developer projects.",
            status = Project.STATUS_ON_HOLD,
            techStack = "Recharts • Canvas • Compose",
            completionPercentage = 30,
            lead = "Marcus Vance",
            createdAt = System.currentTimeMillis() - 86400000L * 8
        ),
        Project(
            id = "proj-006",
            name = "Microservices API Gateway",
            description = "High-throughput gRPC and Envoy proxy with automatic rate limiting and telemetry tracing.",
            status = Project.STATUS_ON_HOLD,
            techStack = "Go • Envoy • Docker",
            completionPercentage = 25,
            lead = "Tariq Mansour",
            createdAt = System.currentTimeMillis() - 86400000L * 15
        ),
        Project(
            id = "proj-007",
            name = "Mobile Payment Checkout SDK",
            description = "Secure zero-trust payment processing widget with biometric authentication and localized currencies.",
            status = Project.STATUS_IN_PROGRESS,
            techStack = "Kotlin Multiplatform • Stripe SDK",
            completionPercentage = 45,
            lead = "Zoe Nguyen",
            createdAt = System.currentTimeMillis() - 86400000L * 2
        )
    )

    private val _projects = MutableStateFlow<List<Project>>(initialProjects)
    val projects: StateFlow<List<Project>> = _projects.asStateFlow()

    private val _isFirestoreActive = MutableStateFlow(false)
    val isFirestoreActive: StateFlow<Boolean> = _isFirestoreActive.asStateFlow()

    private val _syncStatusMessage = MutableStateFlow("Firestore listener connected")
    val syncStatusMessage: StateFlow<String> = _syncStatusMessage.asStateFlow()

    private var listenerRegistration: ListenerRegistration? = null

    init {
        setupFirestoreListener()
    }

    private fun setupFirestoreListener() {
        try {
            val firestore = FirebaseFirestore.getInstance()
            listenerRegistration = firestore.collection("projects")
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Log.w(TAG, "Firestore listener warning: ${error.message}")
                        _isFirestoreActive.value = false
                        _syncStatusMessage.value = "Local Cache Mode (Firestore syncing standby)"
                        return@addSnapshotListener
                    }

                    if (snapshot != null && !snapshot.isEmpty) {
                        val fetchedProjects = snapshot.documents.mapNotNull { doc ->
                            try {
                                Project(
                                    id = doc.id,
                                    name = doc.getString("name") ?: "Unnamed Project",
                                    description = doc.getString("description") ?: "",
                                    status = doc.getString("status") ?: Project.STATUS_IN_PROGRESS,
                                    createdAt = doc.getLong("createdAt") ?: System.currentTimeMillis(),
                                    updatedAt = doc.getLong("updatedAt") ?: System.currentTimeMillis(),
                                    techStack = doc.getString("techStack") ?: "Full-Stack",
                                    completionPercentage = (doc.getLong("completionPercentage") ?: 40L).toInt(),
                                    lead = doc.getString("lead") ?: "Project Team"
                                )
                            } catch (e: Exception) {
                                null
                            }
                        }
                        _projects.value = fetchedProjects
                        _isFirestoreActive.value = true
                        _syncStatusMessage.value = "Firestore Cloud Live (${fetchedProjects.size} synced)"
                    } else if (snapshot != null && snapshot.isEmpty) {
                        // If collection is empty in Firestore, seed it with initial projects
                        seedInitialProjectsToFirestore(firestore)
                    }
                }
        } catch (e: Throwable) {
            Log.w(TAG, "Firestore not initialized or offline: ${e.message}")
            _isFirestoreActive.value = false
            _syncStatusMessage.value = "Local In-Memory Cache Active (Firestore fallback)"
        }
    }

    private fun seedInitialProjectsToFirestore(firestore: FirebaseFirestore) {
        scope.launch {
            try {
                for (p in initialProjects) {
                    val data = hashMapOf(
                        "name" to p.name,
                        "description" to p.description,
                        "status" to p.status,
                        "createdAt" to p.createdAt,
                        "updatedAt" to p.updatedAt,
                        "techStack" to p.techStack,
                        "completionPercentage" to p.completionPercentage,
                        "lead" to p.lead
                    )
                    firestore.collection("projects").document(p.id).set(data)
                }
                _isFirestoreActive.value = true
                _syncStatusMessage.value = "Firestore Cloud Seeded & Live"
            } catch (e: Exception) {
                Log.w(TAG, "Seeding Firestore failed: ${e.message}")
            }
        }
    }

    fun addProject(
        name: String,
        description: String,
        status: String = Project.STATUS_IN_PROGRESS,
        techStack: String = "Jetpack Compose"
    ) {
        val newId = "proj-" + UUID.randomUUID().toString().take(8)
        val newProject = Project(
            id = newId,
            name = name.trim(),
            description = description.trim(),
            status = status,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis(),
            techStack = techStack,
            completionPercentage = when (status) {
                Project.STATUS_COMPLETED -> 100
                Project.STATUS_ON_HOLD -> 20
                else -> 40
            },
            lead = "Dev Lead"
        )

        // Optimistically update local state immediately for seamless UX
        _projects.value = listOf(newProject) + _projects.value

        // Persist to Cloud Firestore
        scope.launch {
            try {
                val firestore = FirebaseFirestore.getInstance()
                val data = hashMapOf(
                    "name" to newProject.name,
                    "description" to newProject.description,
                    "status" to newProject.status,
                    "createdAt" to newProject.createdAt,
                    "updatedAt" to newProject.updatedAt,
                    "techStack" to newProject.techStack,
                    "completionPercentage" to newProject.completionPercentage,
                    "lead" to newProject.lead
                )
                firestore.collection("projects").document(newId).set(data)
                _isFirestoreActive.value = true
                _syncStatusMessage.value = "Saved to Firestore Cloud (${newProject.name})"
            } catch (e: Throwable) {
                Log.w(TAG, "Firestore write warning: ${e.message}")
            }
        }
    }

    fun updateProjectStatus(projectId: String, newStatus: String) {
        _projects.value = _projects.value.map { project ->
            if (project.id == projectId) {
                project.copy(
                    status = newStatus,
                    updatedAt = System.currentTimeMillis(),
                    completionPercentage = when (newStatus) {
                        Project.STATUS_COMPLETED -> 100
                        Project.STATUS_ON_HOLD -> project.completionPercentage.coerceAtMost(35)
                        else -> if (project.completionPercentage == 100) 50 else project.completionPercentage
                    }
                )
            } else {
                project
            }
        }

        scope.launch {
            try {
                val firestore = FirebaseFirestore.getInstance()
                val completion = when (newStatus) {
                    Project.STATUS_COMPLETED -> 100
                    Project.STATUS_ON_HOLD -> 30
                    else -> 50
                }
                firestore.collection("projects").document(projectId).update(
                    mapOf(
                        "status" to newStatus,
                        "updatedAt" to System.currentTimeMillis(),
                        "completionPercentage" to completion
                    )
                )
            } catch (e: Throwable) {
                Log.w(TAG, "Firestore update warning: ${e.message}")
            }
        }
    }

    fun deleteProject(projectId: String) {
        _projects.value = _projects.value.filter { it.id != projectId }

        scope.launch {
            try {
                val firestore = FirebaseFirestore.getInstance()
                firestore.collection("projects").document(projectId).delete()
            } catch (e: Throwable) {
                Log.w(TAG, "Firestore delete warning: ${e.message}")
            }
        }
    }
}
