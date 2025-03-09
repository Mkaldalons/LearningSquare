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
import android.content.Context

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

        var errorTextString = ""

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
                        val sharedPref = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
                        with(sharedPref.edit()) {
                            putString("loggedInInstructor", user.userName)
                            apply()
                        }
                        if (user.isInstructor) {
                            parentFragmentManager.beginTransaction()
                                .replace(R.id.fragment_container_view, InstructorDashboardFragment())
                                .addToBackStack(null)
                                .commit()
                        }
                        else
                        {
                            parentFragmentManager.beginTransaction()
                                .replace(R.id.fragment_container_view, StudentDashboardFragment())
                                .addToBackStack(null)
                                .commit()
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