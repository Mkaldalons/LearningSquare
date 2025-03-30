package hbv601g.learningsquare.services

import hbv601g.learningsquare.models.AssignmentModel
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.encodeToString
import android.util.Log
import hbv601g.learningsquare.models.CourseModel
import hbv601g.learningsquare.models.QuestionModel
import hbv601g.learningsquare.services.utils.JsonUtils
import io.ktor.http.HttpStatusCode
import io.ktor.client.call.body
import io.ktor.serialization.kotlinx.json.json
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.json.Json
import org.json.JSONArray

class HttpsService {
    private val client = HttpClient {
        install(io.ktor.client.plugins.contentnegotiation.ContentNegotiation) {
            json(Json {ignoreUnknownKeys = true})
        }
    }
    //private val url = "https://hugbo1-6b15.onrender.com"
    private val url = "http://10.0.2.2:8080" // Nota þetta til að keyra locally með emulator
    //private val url = "http://localhost:8080" // Nota þetta til að keyra test locally

    /** Get the User by userName
     * Return the User
     */
    suspend fun getUser(userName: String): HttpResponse
    {
        val url = "$url/users/${userName}"
        val response: HttpResponse = client.get(url)
        return response
    }

    /** Login the user
     * @param userName: String
     * @param password: String
     * @return HttpResponse The response form the backend in JSON
     */
    suspend fun loginUser(userName: String, password: String): HttpResponse
    {
        val url = "$url/login"
        val jsonBody = """{"username": "$userName", "password": "$password"}"""

        val response: HttpResponse = client.post(url)
        {
            contentType(ContentType.Application.Json)
            setBody(jsonBody)
        }
        return response
    }

    /** Register a user
     * @param userName: String
     * @param name: String
     * @param email: String
     * @param password: String
     * @param isInstructor: boolean
     * @return HttpResponse The response from the backend in JSON
     */
    suspend fun registerUser(userName: String, name: String, email: String, password: String, isInstructor: Boolean): HttpResponse
    {
        val url = "$url/signup"
        val jsonBody = """
        |{
        |  "username": "$userName",
        |  "name": "$name",
        |  "email": "$email",
        |  "password": "$password",
        |  "confirmPassword": "$password",
        |  "isInstructor": $isInstructor
        |}
        """.trimMargin()

        val response: HttpResponse = client.post(url)
        {
            contentType(ContentType.Application.Json)
            setBody(jsonBody)
        }
        return response
    }

    /** Delete a user by their username
     * @param userName: String
     * @return HttpResponse The response from the backend in JSON
     */
    suspend fun deleteUser(userName: String): HttpResponse
    {
        val url = "$url/users/${userName}"
        val response: HttpResponse = client.delete(url)

        return response
    }

    /**
     *
     */
    suspend fun createAssignment(assignmentModel: AssignmentModel): HttpResponse
    {
        val url = "$url/assignments"
        val jsonBody = Json.encodeToString(assignmentModel)

        val response: HttpResponse = client.post(url)
        {
            contentType(ContentType.Application.Json)
            setBody(jsonBody)
        }
        return response
    }

    suspend fun getAssignment(assignmentId: Int): HttpResponse
    {
        val url = "$url/assignments/$assignmentId"

        val response: HttpResponse = client.get(url)
        return response
    }

    suspend fun updateAssignment(assignmentId: Int, name: String?, dueDate: LocalDateTime?, questionRequest: List<QuestionModel>?, published: Boolean?): HttpResponse
    {
        val url = "$url/assignments/$assignmentId"

        val jsonBody = JsonUtils.buildAssignmentPatchJson(
            name = name,
            dueDate = dueDate,
            questionRequest = questionRequest,
            published = published
        )

        val response: HttpResponse = client.patch(url)
        {
            contentType(ContentType.Application.Json)
            setBody(jsonBody)
        }
        return response
    }
    
    /** Create a course
     *
     */
    suspend fun createCourse(courseName: String, instructor: String, description: String): HttpResponse
    {
        val url = "$url/courses"
        val jsonBody = """
            {
                "courseName": "$courseName",
                "instructor": "$instructor",
                "description": "$description"
            }
        """.trimIndent()

        Log.d("CreateCourse", "POSTing to URL: $url with data: $jsonBody")
        val response: HttpResponse = client.post(url)
        {
            contentType(ContentType.Application.Json)
            setBody(jsonBody)
        }
        return response
    }

    suspend fun getCourses(userName: String): List<CourseModel> {
        val url = "$url/courses/$userName"
        Log.d("HttpsService", "Fetching courses for instructor: $userName")

        return try {
            val response: HttpResponse = client.get(url)

            if (response.status == HttpStatusCode.OK) {
                val coursesList: List<CourseModel> = response.body() 
                Log.d("HttpsService", "Received courses: $coursesList")
                coursesList
            } else {
                Log.e("HttpsService", "Failed to fetch courses: ${response.status}")
                emptyList()
            }
        } catch (e: Exception) {
            Log.e("HttpsService", "Error fetching courses: ${e.message}")
            emptyList()
        }
    }

    suspend fun addStudentToCourse(courseId: Int, userName: String): HttpResponse
    {
        val url = "$url/courses/$courseId/$userName"
        val response: HttpResponse = client.post(url)

        return response
    }

    suspend fun deleteStudentFromCourse(courseId: Int, studentId: Int): HttpResponse
    {
        val url = "$url/courses/$courseId/students/$studentId"
        val response: HttpResponse = client.delete(url)

        return response
    }

    suspend fun getAllStudentsInCourse(courseId: Int): HttpResponse
    {
        val url = "$url/courses/$courseId/students"
        val response: HttpResponse = client.get(url)

        return response
    }

    suspend fun getAllAssignmentsForCourse(courseId: Int): HttpResponse
    {
        val url = "$url/assignments/courses/$courseId"
        val response: HttpResponse = client.get(url)

        return response
    }

    suspend fun submitAssignment(assignmentId: Int, userName: String, answers: List<String>): HttpResponse
    {
        val url = "$url/submissions"

        val answersJson = JSONArray(answers).toString()
        val jsonBody = """
            {
                "assignmentId": "$assignmentId",
                "userName": "$userName",
                "answers": $answersJson
            }
        """.trimIndent()
        val response: HttpResponse = client.post(url)
        {
            contentType(ContentType.Application.Json)
            setBody(jsonBody)
        }
        Log.d("Submit", "Body: $response")

        return response
    }

    suspend fun getAssignmentGrade(assignmentId: Int, userName: String): HttpResponse
    {
        val url = "$url/students/grade/$userName/$assignmentId"
        val response: HttpResponse = client.get(url)

        return response
    }
    suspend fun getAssignmentAverageGrade(assignmentId: Int): HttpResponse {
        val url = "$url/submissions/average/$assignmentId"
        val response: HttpResponse = client.get(url)

        return response
    }

    suspend fun getStudentAverage(courseId: Int, userName: String): HttpResponse {
        val url = "$url/students/average/course/$courseId?userName=$userName"
        val response: HttpResponse = client.get(url)

        return response
    }

    suspend fun changePassword(userName: String, oldPassword: String, newPassword: String): HttpResponse
    {
        val url = "$url/users/$userName"
        val jsonBody = """
            {
                "oldPassword": "$oldPassword",
                "newPassword": "$newPassword"
            }
        """.trimIndent()
        val response: HttpResponse = client.patch(url)
        {
            contentType(ContentType.Application.Json)
            setBody(jsonBody)
        }
        return response
    }

    suspend fun updateRecoveryEmail(userName: String, recoveryEmail: String): HttpResponse
    {
        val url = "$url/users/$userName"
        val jsonBody = """
            {
                "recoveryEmail": "$recoveryEmail"
            }
        """.trimIndent()
        val response: HttpResponse = client.post(url)
        {
            contentType(ContentType.Application.Json)
            setBody(jsonBody)
        }
        return response
    }
}