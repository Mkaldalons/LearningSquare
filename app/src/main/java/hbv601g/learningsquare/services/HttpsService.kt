package hbv601g.learningsquare.services

import android.util.Log
import hbv601g.learningsquare.models.UserModel
import io.ktor.client.*
import io.ktor.client.call.body
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.client.engine.cio.*
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.json.Json
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.jsonPrimitive

class HttpsService {
    private val client = HttpClient(CIO)
    private val url = "https://hugbo1-6b15.onrender.com"

    /** Get the User by userName
     * Return the User
     */
    suspend fun getUser(userName: String): HttpResponse
    {
        val url = "https://hugbo1-6b15.onrender.com/users/${userName}"
        Log.d("UserService", "Requesting URL: $url")
        val response: HttpResponse = client.get(url)
        return response
    }

    /** Login the user
     * @param userName: String
     * @param password: String
     * @return user: UserModel?
     *  Return the user if the login was successful, otherwise null.
     */
    suspend fun loginUser(userName: String, password: String): HttpResponse
    {
        val url = "https://hugbo1-6b15.onrender.com/login"
        val jsonBody = """{"username": "$userName", "password": "$password"}"""
        Log.d("UserService", "POSTing to URL: $url with data: $jsonBody")

        val response: HttpResponse = client.post(url)
        {
            contentType(ContentType.Application.Json)
            setBody(jsonBody)
        }
        return response
    }

//    /** Parse a user response
//     *  Return a User
//     */
//    private suspend fun parseUserResponse(response: HttpResponse): UserModel
//    {
//        if(response.status.value == 200)
//        {
//            return Json.decodeFromString<UserModel>(response.body())
//        }
//        else
//        {
//            throw Exception("Failed to retrieve user data. Status code: ${response.status}")
//        }
//    }
//
//    /** Parse the login response
//     *  Return true if isInstructor field is true, otherwise false
//     */
//    private suspend fun parseLoginResponse(response: HttpResponse): Boolean?
//    {
//        val jsonObject = Json{ ignoreUnknownKeys = true }
//        val returnResponse = jsonObject.decodeFromString<JsonObject>(response.body())
//        return if(response.status.value == 200) {
//            returnResponse["isInstructor"]?.jsonPrimitive?.booleanOrNull ?: false
//        } else {
//            null
//        }
//    }
}