package hbv601g.learningsquare

import hbv601g.learningsquare.services.CourseService
import hbv601g.learningsquare.services.HttpsService
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.Assert.*

class CourseServiceTest {
    private val httpsService = HttpsService()
    private val courseService = CourseService(httpsService)

    @Test
    fun testRegisterStudentInCourse(): Unit = runBlocking {
        val courseId = 0
        val studentUserName = "lena"
        val successMessage = "Student $studentUserName registered successfully"
        val response = courseService.registerStudentToCourse(courseId, studentUserName)

        assertEquals(successMessage, response)

    }

    @Test
    fun testGetAllStudentsInCourse(): Unit = runBlocking {
        val courseId = 1
        val expectedUserName = "lena"
        val expectedId = 15
        val indexToCheck = 0

        val users = courseService.getRegisteredStudents(courseId)

        assertTrue(users.isNotEmpty())
        assertEquals(3, users.size)
        assertEquals(expectedId, users[indexToCheck].studentId)
        assertEquals(expectedUserName, users[indexToCheck].userName)

    }

    @Test
    fun testDeleteStudentFromCourse(): Unit = runBlocking {
        // Breyta eftir þörfum
        val courseId = 1
        val studentToDelete = 2

        val students = courseService.getRegisteredStudents(courseId)
        val before = students.size
        val isDeleted = courseService.deleteStudentFromCourse(courseId, studentToDelete)
        assertTrue(isDeleted)
        val studentAfter = courseService.getRegisteredStudents(courseId)
        val after = studentAfter.size
        assertNotSame(before, after)
    }

    @Test
    fun testUpdateName(): Unit = runBlocking {
        val courseId = 0
        val newName = "Stærðfræðigreining I"

        val success = courseService.editCourseName(courseId, newName)

        assertTrue(success)
    }

    @Test
    fun testUpdateDescription(): Unit = runBlocking {
        val courseId = 0
        val newDescription = "Uppáhalds áfanginn hennar Maríu"

        val success = courseService.editCourseDescription(courseId, newDescription)

        assertTrue(success)
    }

}