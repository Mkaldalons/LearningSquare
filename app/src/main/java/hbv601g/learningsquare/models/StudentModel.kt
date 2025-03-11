package hbv601g.learningsquare.models

import kotlinx.serialization.Serializable

@Serializable
data class StudentModel(
    val studentId: Int? = null,
    val userName: String,
    val name: String = ""
)