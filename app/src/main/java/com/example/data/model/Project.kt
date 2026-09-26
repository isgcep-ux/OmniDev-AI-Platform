package com.example.data.model

data class Project(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val status: String = "In Progress", // "In Progress", "Completed", "On Hold"
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val techStack: String = "Jetpack Compose",
    val completionPercentage: Int = 40,
    val lead: String = "Lead Engineer"
) {
    companion object {
        const val STATUS_IN_PROGRESS = "In Progress"
        const val STATUS_COMPLETED = "Completed"
        const val STATUS_ON_HOLD = "On Hold"

        val ALL_STATUSES = listOf(STATUS_IN_PROGRESS, STATUS_COMPLETED, STATUS_ON_HOLD)
    }
}
