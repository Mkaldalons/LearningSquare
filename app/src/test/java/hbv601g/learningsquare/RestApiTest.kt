package hbv601g.learningsquare

import hbv601g.learningsquare.services.HttpsService
import hbv601g.learningsquare.services.UserService
import kotlinx.coroutines.runBlocking
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class RestApiTest {

    private val httpsService = HttpsService()
    private val userService = UserService(httpsService)

    @Test
    fun testLoginEndpoint(): Unit = runBlocking {
        val userName = "testApp"
        val name = "Test App"
        val email = "test@email.com"
        val password = "lykilord"
        val isInstructor = false

        val user = userService.signupUser(userName, name, email, password, isInstructor)

        if (user != null) {
            assertEquals(user.name, userName)
        }
    }

    @Test
    fun testDeleteUserEndpoint(): Unit = runBlocking {

    }
}