package hbv601g.learningsquare.models

import kotlinx.serialization.Serializable

@Serializable
data class UserModel(
    val userName: String,
    val name: String,
    val email: String,
    val password: String,
    val instructor: Boolean,
    val profileImageData: String?,
    val recoveryEmail: String?
    )
{


}