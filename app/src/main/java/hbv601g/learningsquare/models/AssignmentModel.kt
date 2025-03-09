package hbv601g.learningsquare.models

import kotlinx.serialization.Serializable

@Serializable
data class AssignmentModel(
    val assignmentId: String,
    val courseId: String,
    val title: String,
    val description: String,
    val dueDate: String,
    val questions: List<MakeQuestion> = emptyList()
)
