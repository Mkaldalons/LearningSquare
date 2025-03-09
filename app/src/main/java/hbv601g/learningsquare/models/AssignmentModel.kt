package hbv601g.learningsquare.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AssignmentModel(
    val assignmentId: Int,
    val courseId: Int,
    @SerialName("assignmentName")
    val title: String,
    val dueDate: String,
    val jsonData: String,
    val published: Boolean = false
)
