package hbv601g.learningsquare.models

import hbv601g.learningsquare.models.utils.QuestionListSerializer
import kotlinx.serialization.Serializable
import kotlinx.datetime.LocalDate
import kotlinx.serialization.SerialName

@Serializable
data class AssignmentModel(
    val assignmentId: Int? = null,
    val assignmentName: String,
    val courseId: Int,
    val dueDate: LocalDate,
    @SerialName("questionRequest")
    @Serializable(with = QuestionListSerializer::class) //Nota til að lesa json listann með valmöguleikunum
    val questionRequest: List<QuestionModel>,
    val published: Boolean
)
