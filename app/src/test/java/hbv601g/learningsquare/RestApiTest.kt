package hbv601g.learningsquare

import org.junit.Test
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.client.engine.cio.*
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class RestApiTest {

    private val client = HttpClient(CIO)

    @Test
    fun testGetCoursesEndpoint() = runBlocking {
        val url = "https://hugbo1-6b15.onrender.com/courses"
        val response: HttpResponse = client.get(url)

        assertEquals(200, response.status.value)
        // Hægt að uncommenta þetta til að sjá response-ið sjálft
        //println("Test Response: ${response.body<String>()}")
    }
}