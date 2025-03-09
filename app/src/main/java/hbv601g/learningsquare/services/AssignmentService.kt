package hbv601g.learningsquare.services

import hbv601g.learningsquare.models.AssignmentModel
import hbv601g.learningsquare.models.MakeQuestion
import io.ktor.client.statement.HttpResponse
import io.ktor.client.call.body
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class AssignmentService(private val httpsService: HttpsService) {

    /**
     * Býr til assignment með multiple choice spurningum.
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

    // Nýr function til að sækja öll assignments úr gagnagrunninum.
    suspend fun getAllAssignments(): List<AssignmentModel>? {
        val response: HttpResponse = httpsService.getAllAssignments()
        return if (response.status.value == 200) {
            // Athugaðu að JSON svarið frá backend verður að vera í samræmi við AssignmentModel.
            Json.decodeFromString(response.body())
        } else {
            null
        }
    }
}
