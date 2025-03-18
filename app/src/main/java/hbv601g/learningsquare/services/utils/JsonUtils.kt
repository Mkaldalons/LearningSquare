package hbv601g.learningsquare.services.utils

import android.util.Log
import hbv601g.learningsquare.models.QuestionModel
import kotlinx.datetime.LocalDate
import org.json.JSONArray
import org.json.JSONObject

object JsonUtils {

    fun buildAssignmentPatchJson(
        name: String?,
        dueDate: LocalDate?,
        questionRequest: List<QuestionModel>?,
        published: Boolean?
    ): String {
        val jsonObject = JSONObject()

        name?.let {
            jsonObject.put("assignmentName", it)
        }
        dueDate?.let {
            jsonObject.put("dueDate", it.toString())
        }
        if (!questionRequest.isNullOrEmpty()) {
            val jsonArray = JSONArray()
            for (question in questionRequest) {
                val questionJson = JSONObject()
                questionJson.put("question", question.question)

                val optionsArray = JSONArray()
                for (option in question.options) {
                    optionsArray.put(option)
                }
                questionJson.put("options", optionsArray)
                questionJson.put("correctAnswer", question.correctAnswer)
                jsonArray.put(questionJson)
            }
            jsonObject.put("questionRequest", jsonArray)
        }
        published?.let {
            jsonObject.put("published", it)
        }
        Log.d("Assignment", "Submitting: $jsonObject")
        return jsonObject.toString()
    }

}