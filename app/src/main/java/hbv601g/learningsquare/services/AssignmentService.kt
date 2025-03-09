package hbv601g.learningsquare.services

import hbv601g.learningsquare.models.AssignmentModel
import hbv601g.learningsquare.models.MakeQuestion
import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class AssignmentService(private val httpsService: HttpsService) {

    /**
     * Calls HttpsService to create an assignment with multiple choice questions.
     *
     * @param courseId Identifier for the course.
     * @param title Title of the assignment.
     * @param description (Not used by backend)
     * @param dueDate Due date.
     * @param questions List of questions.
     * @return AssignmentModel if the POST request is successful, otherwise null.
     */
    suspend fun createMakeAssignment(
        courseId: String,
        title: String,
        description: String,
        dueDate: String,
        questions: List<MakeQuestion>
    ): AssignmentModel? {
        val response: HttpResponse = httpsService.createAssignment(courseId, title, description, dueDate, questions)
        return if (response.status.value == 200) {
            try {
                Json.decodeFromString<AssignmentModel>(response.body())
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        } else {
            null
        }
    }

    // New function to fetch all assignments from the database.
    suspend fun getAllAssignments(): List<AssignmentModel>? {
        val response: HttpResponse = httpsService.getAllAssignments()
        return if (response.status.value == 200) {
            try {
                // Decode the JSON response into a List of AssignmentModel
                Json.decodeFromString<List<AssignmentModel>>(response.body())
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        } else {
            null
        }
    }
}
