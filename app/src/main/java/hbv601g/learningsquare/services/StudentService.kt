package hbv601g.learningsquare.services

import hbv601g.learningsquare.models.CourseModel
import hbv601g.learningsquare.models.UserModel

class StudentService(private val httpsService: HttpsService) {

    suspend fun getStudentCourses(userName: String) : List<CourseModel>
    {
        val courses: List<CourseModel> = httpsService.getCourses(userName)
        return courses
    }
}