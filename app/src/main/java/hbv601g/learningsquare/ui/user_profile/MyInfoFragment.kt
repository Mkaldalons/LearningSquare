package hbv601g.learningsquare.ui.user_profile

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import hbv601g.learningsquare.R
import hbv601g.learningsquare.services.HttpsService
import hbv601g.learningsquare.services.UserService
import kotlinx.coroutines.launch
import androidx.lifecycle.lifecycleScope

class MyInfoFragment : Fragment(R.layout.fragment_my_info) {

    private lateinit var userService: UserService
    private lateinit var httpsService: HttpsService

    private lateinit var profileImageView: ImageView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        profileImageView = view.findViewById(R.id.profileImageView)
        val usernameTextView = view.findViewById<TextView>(R.id.usernameTextView)
        val emailTextView = view.findViewById<TextView>(R.id.emailTextView)

        httpsService = HttpsService()
        userService = UserService(httpsService)

        val sharedPref = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val loggedInUser = sharedPref.getString("loggedInUser", null)

        if (!loggedInUser.isNullOrEmpty()) {
            lifecycleScope.launch {
                val user = userService.getUser(loggedInUser)
                if (user != null) {
                    usernameTextView.text = user.userName
                    emailTextView.text = user.email
                    profileImageView.setImageResource(R.drawable.ic_profile_placeholder)
                }
                else {
                    usernameTextView.text = "User not found"
                }
            }
        } else {
            usernameTextView.text = "No user logged in"
        }
    }
}