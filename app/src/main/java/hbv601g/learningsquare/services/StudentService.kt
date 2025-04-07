package hbv601g.learningsquare.services

import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText

class StudentService(private val httpsService: HttpsService) {

    suspend fun submitAssignment(assignmentId: Int, userName: String, answers: List<String>) : Double
    {
        val response = httpsService.submitAssignment(assignmentId, userName, answers)
        return parseGrade(response)
    }

    private suspend fun parseGrade(response: HttpResponse) : Double
    {
        if(response.status.value == 200)
        {
            val id = response.bodyAsText().substringAfterLast(":").substringBefore("}").trim()
            return id.toDouble()
        }
        return -1.0
    }

    suspend fun getAssignmentGrade(assignmentId: Int, userName: String) : Double?
    {
        val response = httpsService.getAssignmentGrade(assignmentId, userName)
        if (response.status.value == 200 && (response.body<Double?>()?.toDouble() != -1.0))
        {
            return response.body<Double?>()?.toDouble()
        }
        return null
    }

    suspend fun getAssignmentAverageGrade(assignmentId: Int): Double?
    {
        val response = httpsService.getAssignmentAverageGrade(assignmentId)
        if(response.status.value == 200 && (response.body<Double?>()?.toDouble() != -1.0))
        {
            return response.body<Double?>()?.toDouble()
        }
        return null
    }

    suspend fun getStudentAverage(courseId: Int, userName: String): Double?
    {
        val response = httpsService.getStudentAverage(courseId, userName)
        if(response.status.value == 200 && (response.body<Double?>()?.toDouble() != -1.0))
        {
            return response.body<Double?>()?.toDouble()
        }
        return null
    }
}