package hbv601g.learningsquare.models

import hbv601g.learningsquare.models.utils.QuestionListSerializer
import kotlinx.serialization.Serializable
import kotlinx.datetime.LocalDate
import kotlinx.serialization.SerialName

@Serializable
data class AssignmentModel(
    var assignmentId: Int? = null,
    val assignmentName: String,
    val courseId: Int,
    val dueDate: LocalDate,
    val questionRequest: List<QuestionModel>,
    val published: Boolean,
    val grade: Double? = null
)
