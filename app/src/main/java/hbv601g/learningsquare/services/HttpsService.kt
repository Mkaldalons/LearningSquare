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
import io.ktor.http.HttpStatusCode
import io.ktor.client.call.body
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class HttpsService {
    // private val client = HttpClient(CIO)
    private val client = HttpClient {
        install(io.ktor.client.plugins.contentnegotiation.ContentNegotiation) {
            json(Json {ignoreUnknownKeys = true})
        }
    }
    private val url = "https://hugbo1-6b15.onrender.com"

    /** Get the User by userName
     * Return the User
     */
    suspend fun getUser(userName: String): HttpResponse
    {
        val url = "https://hugbo1-6b15.onrender.com/users/${userName}"
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
        val url = "https://hugbo1-6b15.onrender.com/login"
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
        val url = "https://hugbo1-6b15.onrender.com/users/${userName}"
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
    
    /** Create a course
     *
     */
    suspend fun createCourse(courseName: String, instructor: String, description: String): HttpResponse
    {
        val url = "https://hugbo1-6b15.onrender.com/courses" 
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

    suspend fun getCourses(instructor: String): List<CourseModel> {
        val url = "https://hugbo1-6b15.onrender.com/courses?instructor=$instructor"
        Log.d("HttpsService", "Fetching courses for instructor: $instructor")

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

    suspend fun getAllStudentsInCourse(courseId: Int): HttpResponse
    {
        val url = "$url/courses/$courseId/students"
        val response: HttpResponse = client.get(url)

        return response
    }
    suspend fun removeStudentFromCourse(courseId: Int, userName: String): HttpResponse {
        val url = "$url/courses/$courseId/students/$userName"

        Log.d("DEBUG", "Sending DELETE request to: $url")

        return client.delete(url) {
            contentType(ContentType.Application.Json)
        }
    }
}