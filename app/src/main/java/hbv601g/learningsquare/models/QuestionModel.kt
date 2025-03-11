package hbv601g.learningsquare.models

import kotlinx.serialization.Serializable

@Serializable
data class QuestionModel (
        val question: String,
        val options: List<String>,
        val correctAnswer: String
)