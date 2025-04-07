package hbv601g.learningsquare.models

import kotlinx.serialization.Serializable

@Serializable
data class StudentModel(
    val studentId: Int,
    val userName: String,
    val name: String,
    val averageGrade: Double? = null
)