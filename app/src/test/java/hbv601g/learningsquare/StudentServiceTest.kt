package hbv601g.learningsquare

import hbv601g.learningsquare.services.HttpsService
import hbv601g.learningsquare.services.StudentService
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test

class StudentServiceTest {
    private val httpsService = HttpsService()
    private val studentService = StudentService(httpsService)

    @Test
    fun testGetAssignmentGrade(): Unit = runBlocking {
        val userName = "mariaros"
        val assignmentId = 38

        val response = studentService.getAssignmentGrade(assignmentId, userName)

        assertTrue(response == 10.0)
    }

    @Test
    fun testGetStudentAverage(): Unit = runBlocking {
        // Test user with no submissions, thus no average
        val userName = "tedi"
        val courseId = 0

        val response = studentService.getStudentAverage(courseId, userName)

        assertNull(response)

        // Test user with submissions
        val userNameWithAverage = "mariaros"

        val responseWithAverage = studentService.getStudentAverage(courseId, userNameWithAverage)

        assertEquals(5.0, responseWithAverage)
    }
}