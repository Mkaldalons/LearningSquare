package hbv601g.learningsquare.services

import hbv601g.learningsquare.models.CourseModel
import hbv601g.learningsquare.models.StudentModel
import io.ktor.client.call.body
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

    suspend fun deleteStudentFromCourse(courseId: Int, studentId: Int) : Boolean
    {
        val response = httpsService.deleteStudentFromCourse(courseId, studentId)

        return response.status.value == 200
    }

    suspend fun getCourse(courseId: Int): CourseModel?
    {
        val response = httpsService.getCourse(courseId)

        if(response.status.value == 200)
        {
            return Json.decodeFromString<CourseModel>(response.body())
        }
        return null
    }

    suspend fun editCourseDescription(courseId: Int, courseDescription: String): Boolean
    {
        val response = httpsService.editCourse(courseId, null, courseDescription)

        return response.status.value == 200
    }

    suspend fun editCourseName(courseId: Int, courseName: String): Boolean
    {
        val response = httpsService.editCourse(courseId, courseName, null)

        return response.status.value == 200
    }
}