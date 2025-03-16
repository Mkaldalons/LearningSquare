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
        questionRequest?.let { questions ->
            val jsonArray = JSONArray()
            for (question in questions) {
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
        Log.d("AssignmentFragment", "JsonObject is now: $jsonObject")
        return jsonObject.toString()
    }

}