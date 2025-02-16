package hbv601g.learningsquare.services

import hbv601g.learningsquare.models.UserModel

class UserService(private val httpsService: HttpsService) {

    /** Get the user by username
     *  Return the kind of user
     */
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

    /** Login the user with a given username and password
     *  Return the type of user
     */
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