package hbv601g.learningsquare.services

import hbv601g.learningsquare.models.UserModel

class UserService(private val httpsService: HttpsService) {

    public suspend fun getUser(userName: String): String
    {
        val user: UserModel = httpsService.getUser(userName)
        if( user != null )
        {
            if( user.isInstructor )
            {
                return "instructor"
            }
            return "student"
        }
        return "none"
    }
    public suspend fun loginUser(userName: String, password: String): String
    {
        val userType: String = httpsService.loginUser(userName, password)

        if( userType != null)
        {
            return userType
        }
        return "none"
    }
}