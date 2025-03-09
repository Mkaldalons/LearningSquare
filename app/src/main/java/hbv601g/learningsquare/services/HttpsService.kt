package hbv601g.learningsquare.services

import hbv601g.learningsquare.models.MakeQuestion
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.client.engine.cio.*
import io.ktor.http.ContentType
import io.ktor.http.contentType

class HttpsService {
    private val client = HttpClient(CIO)
    private val url = "https://hugbo1-6b15.onrender.com"

    /** Get the User by userName
     * Return the User
     */
    suspend fun getUser(userName: String): HttpResponse {
        val url = "https://hugbo1-6b15.onrender.com/users/${userName}"
        val response: HttpResponse = client.get(url)
        return response
    }

    /** Login the user
     * @param userName: String
     * @param password: String
     * @return HttpResponse The response form the backend in JSON
     */
    suspend fun loginUser(userName: String, password: String): HttpResponse {
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
    suspend fun registerUser(
        userName: String,
        name: String,
        email: String,
        password: String,
        isInstructor: Boolean
    ): HttpResponse {
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
    suspend fun deleteUser(userName: String): HttpResponse {
        val url = "https://hugbo1-6b15.onrender.com/users/${userName}"
        val response: HttpResponse = client.delete(url)

        return response
    }

    /**
     * Býr til assignment með multiple choice spurningum fyrir tiltekið námskeið.
     *
     * @param courseId Auðkenni námskeiðsins (sem strengur; verður breytt í heiltölu).
     * @param title Titill assignmentsins (sent sem "assignmentName").
     * @param description Lýsing (þó backendið noti hana ekki).
     * @param dueDate Afhendingardagur assignmentsins (snið, t.d. "2025-03-08").
     * @param questions Listi af spurningum.
     */
    suspend fun createAssignment(
        courseId: String,
        title: String,
        description: String,
        dueDate: String,
        questions: List<MakeQuestion>
    ): HttpResponse {
        val endpoint = "$url/create"

        // Umbreyting á spurningum í JSON, með réttum lykilum:
        val questionsJson =
            questions.joinToString(separator = ",", prefix = "[", postfix = "]") { questionRequest ->
                val optionsJson = questionRequest.options.joinToString(
                    separator = ",",
                    prefix = "[",
                    postfix = "]"
                ) { "\"$it\"" }
                // Notum "question" lykilinn (ekki "questionText") til að passa backend
                """{"question": "${questionRequest.questionText}", "options": $optionsJson, "correctAnswer": "${questionRequest.correctAnswer}"}"""
            }

        // Breytum courseId í heiltölu; ef það bilar, notum 0.
        val courseIdInt = courseId.toIntOrNull() ?: 0

        // Myndum JSON payloadið sem backendið bíður eftir:
        val jsonBody = """{
            "courseId": $courseIdInt,
            "assignmentName": "$title",
            "dueDate": "$dueDate",
            "questionRequests": $questionsJson
        }""".trimIndent()

        return client.post(endpoint) {
            contentType(ContentType.Application.Json)
            setBody(jsonBody)
        }
    }
}