package hbv601g.learningsquare.services

import android.util.Log
import android.os.Build
import androidx.annotation.RequiresApi
import hbv601g.learningsquare.models.UserModel
import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.jsonPrimitive
import org.json.JSONObject

class UserService(private val httpsService: HttpsService) {

    /** Get the user by username
     *  @param userName
     *  @return UserModel The user if it exists, otherwise null
     */
    suspend fun getUser(userName: String): UserModel?
    {
        val response = httpsService.getUser(userName)

        val user = parseUserResponse(response)

        return user
    }

    /** Login the user with a given username and password
     *  @param userName
     *  @param password
     *  @return UserModel The user if the login was successful, otherwise null
     */
    suspend fun loginUser(userName: String, password: String): UserModel?
    {
        val response = httpsService.loginUser(userName, password)
        val isInstructor = parseLoginResponse(response)
        if (isInstructor != null) {
            return getUser(userName)
        }
        return null
    }

    /** Register the user
     * @param userName
     * @param name
     * @param email
     * @param password
     * @param isInstructor
     * @return UserModel The user if it was successfully registered, otherwise null
     */
    suspend fun signupUser(
        userName: String,
        name: String,
        email: String,
        password: String,
        isInstructor: Boolean
    ): UserModel?
    {
        val httpsResponse = httpsService.registerUser(userName, name, email, password, isInstructor)
        val user = parseUserResponse(httpsResponse)
        if (user != null) {
            return user
        }
        return null
    }

    /** Delete the user
     * @param userName
     * @return response Return true if the user was deleted, otherwise false
     */
    suspend fun deleteUser(userName: String): Boolean
    {
        val httpResponse = httpsService.deleteUser(userName)
        val response = parseDeleteResponse(httpResponse)

        return response
    }

    /** Parse a user response from json
     * @param response
     * @return UserModel Return a user if HttpResponse value is 200, otherwise null
     */
    private suspend fun parseUserResponse(response: HttpResponse): UserModel?
    {
        return if (response.status.value == 200) {
            Json.decodeFromString<UserModel>(response.body())
        } else {
            null
        }
    }

    /** Parse the login response
     *  @param response
     *  @return Boolean? Return true/false based on the type of user, return null if none is found
     */
    private suspend fun parseLoginResponse(response: HttpResponse): Boolean?
    {
        val jsonObject = Json { ignoreUnknownKeys = true }
        val returnResponse = jsonObject.decodeFromString<JsonObject>(response.body())
        return if (response.status.value == 200) {
            returnResponse["isInstructor"]?.jsonPrimitive?.booleanOrNull ?: false
        } else {
            null
        }
    }

    /** Parse the delete response
     * @param response
     * @return Boolean Return true if the status value is 200, otherwise false
     */
    private fun parseDeleteResponse(response: HttpResponse): Boolean
    {
        return response.status.value == 200
    }

    suspend fun changePassword(username: String, oldPassword: String, newPassword: String): Boolean
    {
        val response = httpsService.changePassword(username, oldPassword, newPassword)
        return response.status.value == 200
    }

    suspend fun updateRecoveryEmail(username: String, recoveryEmail: String): Boolean
    {
        val response = httpsService.updateRecoveryEmail(username, recoveryEmail)
        return response.status.value == 200
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun uploadProfileImage(userName: String, imageByteArray: ByteArray): ByteArray?
    {
        val response = httpsService.uploadProfileImage(userName, imageByteArray)
        if (response.status.value == 200)
        {
            val jsonObject = JSONObject(response.bodyAsText())
            val base64Image = jsonObject.getString("status")

            return android.util.Base64.decode(base64Image, android.util.Base64.DEFAULT)
        }
        return null
    }

    suspend fun getProfilePicture(userName: String): ByteArray?
    {
        val response = httpsService.getProfileImage(userName)
        if(response.status.value == 200)
        {
            val jsonObject = JSONObject(response.bodyAsText())
            val base64Image = jsonObject.getString("imagePath")

            return android.util.Base64.decode(base64Image, android.util.Base64.DEFAULT)
        }
        return null
    }
}