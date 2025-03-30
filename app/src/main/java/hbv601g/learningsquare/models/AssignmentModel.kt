package hbv601g.learningsquare.models

import kotlinx.serialization.Serializable
import kotlinx.datetime.LocalDateTime

@Serializable
data class AssignmentModel(
    var assignmentId: Int? = null,
    val assignmentName: String,
    val courseId: Int,
    val dueDate: LocalDateTime,
    val questionRequest: List<QuestionModel>,
    val published: Boolean,
    val grade: Double? = null
)
