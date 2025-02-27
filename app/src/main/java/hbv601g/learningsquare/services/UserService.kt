package hbv601g.learningsquare.services

import android.util.Log
import hbv601g.learningsquare.models.UserModel
import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.jsonPrimitive


class UserService(private val httpsService: HttpsService) {

    /** Get the user by username
     *  Return the kind of user
     */
    private suspend fun getUser(userName: String): UserModel
    {
        val response = httpsService.getUser(userName)

        val user = parseUserResponse(response)

        return user
    }

    /** Login the user with a given username and password
     *  Return the type of user
     */
    suspend fun loginUser(userName: String, password: String): UserModel?
    {
        val response = httpsService.loginUser(userName, password)
        val isInstructor = parseLoginResponse(response)
        if (isInstructor != null)
        {
            return getUser(userName)
        }
        return null
    }

    suspend fun signupUser(userName: String, name: String, email: String, password: String, isInstructor: Boolean): UserModel?
    {
        val httpsResponse = httpsService.registerUser(userName, name, email, password, isInstructor)
        if(parseSignupResponse(httpsResponse))
        {
            val user = getUser(userName)
            Log.d("SignupFragment", "returning user ${user.userName}")
            return user
        }
        return null
    }

    /** Parse a user response
     *  Return a User
     */
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

    /** Parse the login response
     *  Return true if isInstructor field is true, otherwise false
     */
    private suspend fun parseLoginResponse(response: HttpResponse): Boolean?
    {
        val jsonObject = Json{ ignoreUnknownKeys = true }
        val returnResponse = jsonObject.decodeFromString<JsonObject>(response.body())
        return if(response.status.value == 200) {
            returnResponse["isInstructor"]?.jsonPrimitive?.booleanOrNull ?: false
        } else {
            null
        }
    }

    private suspend fun parseSignupResponse(response: HttpResponse): Boolean
    {
        if(response.body<String>().toString() == "User registered successfully")
        {
            Log.d("SignupFragment", "User registered successfully response")
            return true
        }
        Log.d("SignupFragment", "Could not register student")
        return false
    }
}