package com.example.data.repository

import com.example.data.local.dao.DevStudioDao
import com.example.data.local.entity.ChatMessageEntity
import com.example.data.local.entity.DeploymentTask
import com.example.data.local.entity.PromptTemplate
import com.example.data.local.entity.QuizResult
import com.example.data.local.entity.SavedSnippet
import com.example.data.remote.GeminiApiClient
import kotlinx.coroutines.flow.Flow

class DevStudioRepository(
    private val dao: DevStudioDao,
    private val geminiClient: GeminiApiClient = GeminiApiClient()
) {
    // Snippets
    val allSnippets: Flow<List<SavedSnippet>> = dao.getAllSnippets()
    suspend fun saveSnippet(snippet: SavedSnippet): Long = dao.insertSnippet(snippet)
    suspend fun deleteSnippet(id: Long) = dao.deleteSnippet(id)

    // Prompts
    val allPrompts: Flow<List<PromptTemplate>> = dao.getAllPrompts()
    suspend fun savePrompt(prompt: PromptTemplate): Long = dao.insertPrompt(prompt)
    suspend fun deletePrompt(id: Long) = dao.deletePrompt(id)

    // Quiz Results
    val allQuizResults: Flow<List<QuizResult>> = dao.getAllQuizResults()
    suspend fun saveQuizResult(result: QuizResult): Long = dao.insertQuizResult(result)
    suspend fun clearQuizHistory() = dao.clearQuizHistory()

    // Chat
    fun getChatMessages(persona: String): Flow<List<ChatMessageEntity>> = dao.getChatMessages(persona)
    suspend fun sendChatMessage(userMsg: String, persona: String, customApiKey: String? = null): Result<String> {
        // Save user message to database
        dao.insertChatMessage(
            ChatMessageEntity(
                role = "user",
                persona = persona,
                content = userMsg
            )
        )

        val systemInstruction = getPersonaSystemInstruction(persona)
        val result = geminiClient.generateContent(
            prompt = userMsg,
            systemInstruction = systemInstruction,
            modelName = "gemini-3.5-flash",
            customApiKey = customApiKey
        )

        result.onSuccess { replyText ->
            dao.insertChatMessage(
                ChatMessageEntity(
                    role = "model",
                    persona = persona,
                    content = replyText
                )
            )
        }

        return result
    }

    suspend fun clearChat(persona: String) = dao.clearChatForPersona(persona)

    // Deployment tasks
    val deploymentTasks: Flow<List<DeploymentTask>> = dao.getDeploymentTasks()
    suspend fun updateDeploymentTask(task: DeploymentTask) = dao.updateDeploymentTask(task)

    // Team Members & Access Control
    val allTeamMembers: Flow<List<com.example.data.local.entity.TeamMember>> = dao.getAllTeamMembers()
    suspend fun addTeamMember(member: com.example.data.local.entity.TeamMember): Long = dao.insertTeamMember(member)
    suspend fun updateTeamMember(member: com.example.data.local.entity.TeamMember) = dao.updateTeamMember(member)
    suspend fun updateMemberRole(id: Long, newRole: String) = dao.updateMemberRole(id, newRole)
    suspend fun removeTeamMember(id: Long) = dao.deleteTeamMember(id)

    // Resource Policies
    val allResourcePolicies: Flow<List<com.example.data.local.entity.ResourcePolicy>> = dao.getAllResourcePolicies()
    suspend fun saveResourcePolicy(policy: com.example.data.local.entity.ResourcePolicy): Long = dao.insertResourcePolicy(policy)
    suspend fun updateResourcePolicy(policy: com.example.data.local.entity.ResourcePolicy) = dao.updateResourcePolicy(policy)
    suspend fun deleteResourcePolicy(id: Long) = dao.deleteResourcePolicy(id)

    // Share Invite Tokens
    val allActiveInviteTokens: Flow<List<com.example.data.local.entity.ShareInviteToken>> = dao.getAllActiveInviteTokens()
    suspend fun createInviteToken(token: com.example.data.local.entity.ShareInviteToken): Long = dao.insertInviteToken(token)
    suspend fun revokeInviteToken(id: Long) = dao.revokeInviteToken(id)
    suspend fun useInviteToken(tokenCode: String) = dao.incrementTokenUsage(tokenCode)

    // ISGContent - Health and Safety Lessons
    val allISGContent: Flow<List<com.example.data.local.entity.ISGContent>> = dao.getAllISGContent()
    fun getISGContentByCategory(category: String): Flow<List<com.example.data.local.entity.ISGContent>> = dao.getISGContentByCategory(category)
    suspend fun getISGContentById(id: Long): com.example.data.local.entity.ISGContent? = dao.getISGContentById(id)
    suspend fun saveISGContent(content: com.example.data.local.entity.ISGContent): Long = dao.insertISGContent(content)
    suspend fun updateISGContent(content: com.example.data.local.entity.ISGContent) = dao.updateISGContent(content)
    suspend fun deleteISGContent(content: com.example.data.local.entity.ISGContent) = dao.deleteISGContent(content)
    suspend fun deleteISGContentById(id: Long) = dao.deleteISGContentById(id)

    // Generic AI Execution
    suspend fun askGemini(
        prompt: String,
        systemInstruction: String? = null,
        temperature: Float = 0.7f,
        modelName: String = "gemini-3.5-flash",
        customApiKey: String? = null
    ): Result<String> {
        return geminiClient.generateContent(
            prompt = prompt,
            systemInstruction = systemInstruction,
            temperature = temperature,
            modelName = modelName,
            customApiKey = customApiKey
        )
    }

    private fun getPersonaSystemInstruction(persona: String): String {
        return when (persona) {
            "Flutter Master" -> "You are an expert Google Flutter & Dart architect for Project IDX. Explain state management, widget composition, WASM rendering, responsive design, and animations with clear code snippets."
            "Next.js Pro" -> "You are a senior Next.js & React full-stack engineer. Specialize in App Router, React Server Components (RSC), Server Actions, Next.js Middleware, streaming SSR, and edge deployment."
            "Firebase Specialist" -> "You are a Google Cloud & Firebase architect. Provide production-grade Firestore data modeling, Security Rules, Firebase Auth, Cloud Functions, and Firebase Extensions configurations."
            "AI & Prompt Engineer" -> "You are an AI research & Prompt Engineering specialist for Google AI Studio. Teach zero-shot, few-shot, chain-of-thought, structured JSON schema outputs, and multimodal Gemini 3.5 integrations."
            else -> "You are a Principal Full-Stack Architect guiding developers on building end-to-end applications across Project IDX and Google AI Studio with Flutter, Next.js, Firebase, Databases, Testing, and Deployment."
        }
    }
}
