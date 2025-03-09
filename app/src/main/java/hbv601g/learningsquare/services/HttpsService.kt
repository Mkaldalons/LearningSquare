package hbv601g.learningsquare.services

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
}