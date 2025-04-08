package hbv601g.learningsquare

import hbv601g.learningsquare.services.HttpsService
import hbv601g.learningsquare.services.UserService
import kotlinx.coroutines.runBlocking
import org.junit.Test

import org.junit.Assert.*

/**
 * Unit Test for the UserService class.
 *
 */
class UserServiceTest {

    private val httpsService = HttpsService()
    private val userService = UserService(httpsService)


    @Test
    fun testSignupEndpoint(): Unit = runBlocking {
        // Create new user for test
        val userName = "testApp"
        val name = "Test App"
        val email = "test@email.com"
        val password = "lykilord"
        val isInstructor = false

        val user = userService.signupUser(userName, name, email, password, isInstructor)

        assertNotNull(user)
        assertEquals(user?.userName, userName)
        assertEquals(user?.name, name)
        assertEquals(user?.email, email)

        // Cleanup database after test
        userService.deleteUser(userName)
        // Make sure user was deleted
        assertNull(userService.getUser(userName))
    }

    @Test
    fun testLoginEndpoint(): Unit = runBlocking {
        val userName = "kennari"
        val password = "lykilord"

        val user = userService.loginUser(userName, password)

        assertNotNull(user)
        assertEquals(userName, user?.userName)
    }

    @Test
    fun testGetUserEndpoint(): Unit = runBlocking {
        val userName = "kennari"
        val user = userService.getUser(userName)

        assertNotNull(user)
        assertEquals(userName, user?.userName)
    }


    @Test
    fun testSetRecoveryEmail(): Unit = runBlocking {
        val userName = "kennari"
        val newRecoveryEmail = "testRecovery@email.com"

        val success = userService.updateRecoveryEmail(userName, newRecoveryEmail)

        assertTrue(success)
    }

    @Test
    fun testChangePassword(): Unit = runBlocking {
        val userName = "kennari"
        val oldPassword = "lykilord"
        val newPassword = "lykilord123"

        val success = userService.changePassword(userName, oldPassword, newPassword)

        assertTrue(success)
    }
}