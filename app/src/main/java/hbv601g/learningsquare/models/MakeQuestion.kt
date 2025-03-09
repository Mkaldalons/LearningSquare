package hbv601g.learningsquare.models

import kotlinx.serialization.Serializable

@Serializable
data class MakeQuestion(
    val questionText: String,
    val options: List<String>,
    val correctAnswer: Int
)
