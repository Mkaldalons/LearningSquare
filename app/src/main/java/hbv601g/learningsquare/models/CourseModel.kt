package hbv601g.learningsquare.models

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class CourseModel(
        @SerialName("courseId") val courseId: Int,
        @SerialName("courseName") val courseName: String,
        @SerialName("instructor") val instructor: String,
        @SerialName("description") val description: String
)
