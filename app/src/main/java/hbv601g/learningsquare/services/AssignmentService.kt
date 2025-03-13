package hbv601g.learningsquare.services

import hbv601g.learningsquare.models.AssignmentModel
import hbv601g.learningsquare.models.QuestionModel
import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import kotlinx.datetime.LocalDate
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class AssignmentService(private val httpsService: HttpsService) {

    suspend fun createAssignment(assignmentName: String, courseId: Int, published: Boolean, dueDate: LocalDate, questionData: List<QuestionModel>) : Int?
    {
        val assignment = AssignmentModel(
            assignmentName = assignmentName,
            courseId = courseId,
            published = published,
            dueDate = dueDate,
            questionRequest = questionData
        )

        val response = httpsService.createAssignment(assignment)
        val returnResponse = parseAssignment(response)

        if (returnResponse.isNotEmpty())
        {
            return returnResponse.toInt()
        }
        return null

    }

    suspend fun getAssignment(assignmentId: Int) : AssignmentModel
    {
        val json = Json { ignoreUnknownKeys = true }
        val response = httpsService.getAssignment(assignmentId)
        val assignment = json.decodeFromString<AssignmentModel>(response.body())
        assignment.assignmentId = assignmentId
        return assignment
    }

    suspend fun getAllAssignmentsForCourse(courseId: Int) : List<AssignmentModel>
    {
        val json = Json { ignoreUnknownKeys = true }
        val response = httpsService.getAllAssignmentsForCourse(courseId)
        val assignments = json.decodeFromString<List<AssignmentModel>>(response.body())
        return assignments
    }

    private suspend fun parseAssignment(response: HttpResponse) : String
    {
        if(response.status.value == 200 )
        {
            val id = response.bodyAsText().substringAfterLast(":").substringBefore("}").trim()
            return id
        }
        return ""
    }
}