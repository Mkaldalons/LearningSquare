package hbv601g.learningsquare.services

import hbv601g.learningsquare.models.AssignmentModel
import hbv601g.learningsquare.models.MakeQuestion
import io.ktor.client.statement.HttpResponse
import io.ktor.client.call.body
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class AssignmentService(private val httpsService: HttpsService) {

    /**
     * Kallar á HttpsService til að búa til assignment með multiple choice spurningum.
     *
     * @param courseId Auðkenni námskeiðsins.
     * @param title Titill assignmentsins.
     * @param description Lýsing (sendum þó ekki notað af backendinum).
     * @param dueDate Afhendingardagur.
     * @param questions Listi af spurningum.
     * @return AssignmentModel ef POST beiðnin gengur, annars null.
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
            Json.decodeFromString<AssignmentModel>(response.body())
        } else {
            null
        }
    }
}
