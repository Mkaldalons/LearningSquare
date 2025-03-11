package hbv601g.learningsquare.services

import com.google.firebase.crashlytics.buildtools.reloc.org.apache.commons.logging.Log
import hbv601g.learningsquare.models.StudentModel
import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class CourseService(private val httpsService: HttpsService) {

    suspend fun registerStudentToCourse(courseId: Int, userName: String) : String
    {
        val response = httpsService.addStudentToCourse(courseId, userName)

        if(response.status.value == 200)
        {
            return "Student $userName registered successfully"
        }
        return "Student already registered or course with id: $courseId not found"
    }

    suspend fun getRegisteredStudents(courseId: Int) : List<StudentModel>
    {
        val response = httpsService.getAllStudentsInCourse(courseId)
        var students: List<StudentModel> = listOf()

        if(response.status.value == 200)
        {
            students = Json.decodeFromString<List<StudentModel>>(response.body())
        }
        return students
    }
    suspend fun removeStudentFromCourse(courseId: Int, userName: String): String {
        val response = httpsService.removeStudentFromCourse(courseId, userName)

        return when (response.status.value) {
            200 -> "Student $userName removed successfully from course $courseId"
            404 -> "Student not found in course $courseId"
            400 -> "Bad request: Invalid input"
            else -> "Failed to remove student. Status: ${response.status.value}"
        }
    }
}