package hbv601g.learningsquare.network
import hbv601g.learningsquare.model.LoginRequest
import hbv601g.learningsquare.model.LoginResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>
}