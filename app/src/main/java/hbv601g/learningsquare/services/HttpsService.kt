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

    suspend fun getUser(user: String): UserModel
    {
        val url = "https://hugbo1-6b15.onrender.com/users/${user}"
        Log.d("UserService", "Requesting URL: $url")
        val response: HttpResponse = client.get(url)
        return parseUserResponse(response)
    }

    suspend fun loginUser(userName: String, password: String): String
    {
        val url = "https://hugbo1-6b15.onrender.com/login"
        val jsonBody = """{"username": "$userName", "password": "$password"}"""
        Log.d("UserService", "POSTing to URL: $url with data: $jsonBody")

        val response: HttpResponse = client.post(url)
        {
            contentType(ContentType.Application.Json)
            setBody(jsonBody)
        }
        return if(parseLoginResponse(response)) {
            "instructor"
        } else {
            "student"
        }
    }


    private suspend fun parseUserResponse(response: HttpResponse): UserModel
    {
        if(response.status.value == 200)
        {
            return Json.decodeFromString<UserModel>(response.body())
        }
        else
        {
            throw Exception("Failed to retrieve user data. Status code: ${response.status}")
        }
    }

    private suspend fun parseLoginResponse(response: HttpResponse): Boolean
    {
        val jsonObject = Json{ ignoreUnknownKeys = true }
        val returnResponse = jsonObject.decodeFromString<JsonObject>(response.body())
        if(response.status.value == 200)
        {
            return returnResponse["isInstructor"]?.jsonPrimitive?.booleanOrNull ?: false
        }
        else
        {
            throw Exception("Failed to log in user. Status code: ${response.status}")
        }
    }
}