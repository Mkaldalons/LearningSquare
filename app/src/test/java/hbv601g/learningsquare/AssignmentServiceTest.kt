package hbv601g.learningsquare

import hbv601g.learningsquare.models.QuestionModel
import hbv601g.learningsquare.services.AssignmentService
import hbv601g.learningsquare.services.HttpsService
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.LocalDate
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.Assert.*


class AssignmentServiceTest {

    private val httpsService = HttpsService()
    private val assignmentService = AssignmentService(httpsService)

    @Test
    fun testCreateAssignment(): Unit = runBlocking {
        val assignmentName = "Test Assignment endpoint Again"
        val courseId = 0
        val published = false
        val dueDate = LocalDate(2025, 3, 9)
        val question = "Who am I?"
        val options: List<String> = listOf("a", "b", "c")
        val correctAnswer = "a"

        val questionItem = QuestionModel(question, options, correctAnswer)

        val questionData = listOf(questionItem)

        val assignmentId = assignmentService.createAssignment(assignmentName, courseId, published, dueDate, questionData)

        if(assignmentId != null)
        {
            val assignment = assignmentService.getAssignment(assignmentId)
            assertNotNull(assignment)
            assertEquals(assignment.assignmentId, assignmentId)
            assertEquals(assignment.courseId, courseId)
        }
        assertNotNull(assignmentId)
    }

    @Test
    fun testGetAssignment(): Unit = runBlocking {
        val assignmentId = 17

        val assignment = assignmentService.getAssignment(assignmentId)
        assertNotNull(assignment)
        assertEquals(assignment.assignmentId, assignmentId)
    }

    @Test
    fun testGetAllAssignmentsForCourse(): Unit = runBlocking {
        // Breyta courseId og expected size eftir þörfum
        val courseId = 0
        val expectedSize = 10

        val assignments = assignmentService.getAllAssignmentsForCourse(courseId)
        assertNotNull(assignments)
        assertTrue(assignments.size == expectedSize)
    }
}