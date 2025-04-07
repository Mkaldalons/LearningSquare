package hbv601g.learningsquare.services

import android.os.Build
import androidx.annotation.RequiresApi
import hbv601g.learningsquare.models.AssignmentModel
import hbv601g.learningsquare.models.QuestionModel
import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class AssignmentService(private val httpsService: HttpsService) {

    suspend fun createAssignment(assignmentName: String, courseId: Int, published: Boolean, dueDate: String, questionData: List<QuestionModel>) : Int?
    {
        val isoDueDate = dueDate.replace(" ", "T")

        val assignment = AssignmentModel(
            assignmentName = assignmentName,
            courseId = courseId,
            published = published,
            dueDate = kotlinx.datetime.LocalDateTime.parse(isoDueDate),
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

    suspend fun getPublishedAssignmentsForStudent(courseId: Int) : List<AssignmentModel>
    {
        val publishedAssignments: MutableList<AssignmentModel> = mutableListOf()
        val json = Json { ignoreUnknownKeys = true }
        val response = httpsService.getAllAssignmentsForCourse(courseId)
        val assignments = json.decodeFromString<List<AssignmentModel>>(response.body())
        for(assignment in assignments)
        {
            if (assignment.published)
            {
                publishedAssignments.add(assignment)
            }
        }
        return publishedAssignments
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun editAssignmentDetails(id: Int, name: String, dueDate: String, questionData: List<QuestionModel>, published: Boolean): Boolean
    {
        var patchName: String? = null
        var patchDueDate: kotlinx.datetime.LocalDateTime? = null
        var patchQuestionList: List<QuestionModel>? = null

        val isoDueDate = dueDate.replace(" ", "T")

        if (name.isNotBlank())
        {
            patchName = name
        }
        if (dueDate.isNotBlank())
        {
            patchDueDate = kotlinx.datetime.LocalDateTime.parse(isoDueDate)
        }
        if (questionData.isNotEmpty())
        {
            patchQuestionList = questionData
        }

        val response = httpsService.updateAssignment(id, patchName, patchDueDate, patchQuestionList, published)
        return response.status.value == 200
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