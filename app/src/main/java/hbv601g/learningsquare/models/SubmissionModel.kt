package hbv601g.learningsquare.models

data class SubmissionModel(
    val submissionId: Int,
    val assignmentId: Int,
    var assignmentGrade: Double,
    val studentId: Int
)