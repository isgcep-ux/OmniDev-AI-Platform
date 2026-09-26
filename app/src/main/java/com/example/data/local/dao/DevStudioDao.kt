package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.ChatMessageEntity
import com.example.data.local.entity.DeploymentTask
import com.example.data.local.entity.ISGContent
import com.example.data.local.entity.PromptTemplate
import com.example.data.local.entity.QuizResult
import com.example.data.local.entity.ResourcePolicy
import com.example.data.local.entity.SavedSnippet
import com.example.data.local.entity.ShareInviteToken
import com.example.data.local.entity.TeamMember
import kotlinx.coroutines.flow.Flow

@Dao
interface DevStudioDao {
    // Snippets
    @Query("SELECT * FROM saved_snippets ORDER BY timestamp DESC")
    fun getAllSnippets(): Flow<List<SavedSnippet>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSnippet(snippet: SavedSnippet): Long

    @Query("DELETE FROM saved_snippets WHERE id = :id")
    suspend fun deleteSnippet(id: Long)

    // Prompts
    @Query("SELECT * FROM prompt_templates ORDER BY timestamp DESC")
    fun getAllPrompts(): Flow<List<PromptTemplate>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPrompt(prompt: PromptTemplate): Long

    @Query("DELETE FROM prompt_templates WHERE id = :id")
    suspend fun deletePrompt(id: Long)

    // Quiz Results
    @Query("SELECT * FROM quiz_results ORDER BY timestamp DESC")
    fun getAllQuizResults(): Flow<List<QuizResult>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuizResult(result: QuizResult): Long

    @Query("DELETE FROM quiz_results")
    suspend fun clearQuizHistory()

    // Chat messages
    @Query("SELECT * FROM chat_messages WHERE persona = :persona ORDER BY timestamp ASC")
    fun getChatMessages(persona: String): Flow<List<ChatMessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChatMessage(message: ChatMessageEntity): Long

    @Query("DELETE FROM chat_messages WHERE persona = :persona")
    suspend fun clearChatForPersona(persona: String)

    // Deployment tasks
    @Query("SELECT * FROM deployment_tasks ORDER BY id ASC")
    fun getDeploymentTasks(): Flow<List<DeploymentTask>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDeploymentTasks(tasks: List<DeploymentTask>)

    @Update
    suspend fun updateDeploymentTask(task: DeploymentTask)

    // Team Members
    @Query("SELECT * FROM team_members ORDER BY CASE role WHEN 'OWNER' THEN 1 WHEN 'ADMIN' THEN 2 WHEN 'EDITOR' THEN 3 ELSE 4 END, joinedTimestamp ASC")
    fun getAllTeamMembers(): Flow<List<TeamMember>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTeamMember(member: TeamMember): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTeamMembers(members: List<TeamMember>)

    @Update
    suspend fun updateTeamMember(member: TeamMember)

    @Query("UPDATE team_members SET role = :newRole WHERE id = :id")
    suspend fun updateMemberRole(id: Long, newRole: String)

    @Query("DELETE FROM team_members WHERE id = :id")
    suspend fun deleteTeamMember(id: Long)

    // Resource Policies
    @Query("SELECT * FROM resource_policies ORDER BY updatedTimestamp DESC")
    fun getAllResourcePolicies(): Flow<List<ResourcePolicy>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertResourcePolicy(policy: ResourcePolicy): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertResourcePolicies(policies: List<ResourcePolicy>)

    @Update
    suspend fun updateResourcePolicy(policy: ResourcePolicy)

    @Query("DELETE FROM resource_policies WHERE id = :id")
    suspend fun deleteResourcePolicy(id: Long)

    // Share Invite Tokens
    @Query("SELECT * FROM share_invite_tokens WHERE isRevoked = 0 ORDER BY createdTimestamp DESC")
    fun getAllActiveInviteTokens(): Flow<List<ShareInviteToken>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInviteToken(token: ShareInviteToken): Long

    @Query("UPDATE share_invite_tokens SET isRevoked = 1 WHERE id = :id")
    suspend fun revokeInviteToken(id: Long)

    @Query("UPDATE share_invite_tokens SET usedCount = usedCount + 1 WHERE tokenCode = :tokenCode")
    suspend fun incrementTokenUsage(tokenCode: String)

    // ISGContent - Health & Safety Lessons
    @Query("SELECT * FROM isg_contents ORDER BY id ASC")
    fun getAllISGContent(): Flow<List<ISGContent>>

    @Query("SELECT * FROM isg_contents WHERE category = :category ORDER BY id ASC")
    fun getISGContentByCategory(category: String): Flow<List<ISGContent>>

    @Query("SELECT * FROM isg_contents WHERE id = :id")
    suspend fun getISGContentById(id: Long): ISGContent?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertISGContent(content: ISGContent): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllISGContent(contents: List<ISGContent>)

    @Update
    suspend fun updateISGContent(content: ISGContent)

    @Delete
    suspend fun deleteISGContent(content: ISGContent)

    @Query("DELETE FROM isg_contents WHERE id = :id")
    suspend fun deleteISGContentById(id: Long)
}
