package hbv601g.learningsquare.services

import hbv601g.learningsquare.models.AssignmentModel
import hbv601g.learningsquare.models.MakeQuestion
import io.ktor.client.statement.HttpResponse
import io.ktor.client.call.body
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class AssignmentService(private val httpsService: HttpsService) {

    /**
     * Creates an assignment with multiple choice questions.
     * @param courseId The course ID.
     * @param title Assignment title.
     * @param description Assignment description.
     * @param dueDate Assignment due date.
     * @param questions A list of multiple choice questions.
     * @return The created AssignmentModel if successful, or null otherwise.
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
            Json.decodeFromString(response.body())
        } else {
            null
        }
    }

    // TODO: adding more methods (e.g., getAssignments etc.).
}
