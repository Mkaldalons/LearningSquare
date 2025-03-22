package hbv601g.learningsquare.models

data class SubmissionModel(
    val submissionId: Int,
    val assignmentId: Int,
    val assignmentGrade: Double,
    val studentId: Int
)