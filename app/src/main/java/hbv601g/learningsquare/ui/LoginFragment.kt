package hbv601g.learningsquare.ui

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import hbv601g.learningsquare.R
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import hbv601g.learningsquare.services.HttpsService
import hbv601g.learningsquare.services.UserService
import kotlinx.coroutines.launch
import hbv601g.learningsquare.storage.AppDatabase
import hbv601g.learningsquare.storage.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import android.util.Log
import hbv601g.learningsquare.MainActivity

class LoginFragment : Fragment(R.layout.fragment_login) {

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        val inputUsername = view.findViewById<EditText>(R.id.inputUsername)
        val inputPassword = view.findViewById<EditText>(R.id.inputPassword)
        val loginButton = view.findViewById<Button>(R.id.loginButton)
        val errorTextView = view.findViewById<TextView>(R.id.errorTextView)
        val signupButton = view.findViewById<Button>(R.id.signupButton)

        var errorTextString: String

        loginButton.setOnClickListener {
            val username = inputUsername?.text.toString()
            val password = inputPassword?.text.toString()

            if (username.isNotBlank() && password.isNotBlank()) {
                lifecycleScope.launch {
                    val httpsService = HttpsService()
                    val userService = UserService(httpsService)

                    val user = userService.loginUser(username, password)
                    if (user != null)
                    {
                        val userToSave = User(0, user.userName, user.name ,user.email, user.password, user.instructor, user.profileImageData, user.recoveryEmail)
                        val db = AppDatabase.getDatabase(requireContext())
                        withContext(Dispatchers.IO) {
                            db.userDao().insert(userToSave)
                        }

                        if (user.instructor) {
                            (activity as MainActivity).showDashboardAndNavBar(MainActivity.DashboardType.INSTRUCTOR)
                        }
                        else
                        {
                            (activity as MainActivity).showDashboardAndNavBar(MainActivity.DashboardType.STUDENT)
                        }
                    }
                    else {
                        errorTextString = "Wrong username or password"
                        errorTextView.visibility = View.VISIBLE
                        errorTextView.text = errorTextString
                        errorTextView.text = errorTextString
                        errorTextView.postDelayed({
                            errorTextView.visibility = View.GONE
                        }, 5_000)
                        inputUsername.text.clear()
                        inputPassword.text.clear()
                    }
                }
            }
            else {
                errorTextString = "Please enter username and password"
                errorTextView.visibility = View.VISIBLE
                errorTextView.text = errorTextString
                errorTextView.text = errorTextString
                errorTextView.postDelayed({
                    errorTextView.visibility = View.GONE
                }, 5_000)
            }
        }
        signupButton.setOnClickListener {
            lifecycleScope.launch {
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container_view, SignupFragment())
                    .addToBackStack(null)
                    .commit()
            }
        }

    }
}