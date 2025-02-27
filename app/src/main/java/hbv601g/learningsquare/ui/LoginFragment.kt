package hbv601g.learningsquare.ui

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import hbv601g.learningsquare.R
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.collection.emptyLongSet
import androidx.lifecycle.lifecycleScope
import hbv601g.learningsquare.services.HttpsService
import hbv601g.learningsquare.services.UserService
import kotlinx.coroutines.launch

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

        //Log.d("LoginFragment", "onCreateViewExecuted")

        loginButton.setOnClickListener {
            val username = inputUsername?.text.toString()
            val password = inputPassword?.text.toString()
            //Log.d("LoginFragment", "ButtonClicked")
            if (username.isNotBlank() && password.isNotBlank()) {
                lifecycleScope.launch {
                    val httpsService = HttpsService()
                    val userService = UserService(httpsService)

                    val user = userService.loginUser(username, password)
                    if (user != null)
                    {
                        //Log.d("LoginFragment", "Validated tag: ${user.isInstructor}")
                        if (user.isInstructor) {
                            parentFragmentManager.beginTransaction()
                                .replace(R.id.fragment_container_view, InstructorDashboardFragment())
                                .addToBackStack(null)
                                .commit()
                        }
                        else
                        {
                            errorTextView.visibility = View.VISIBLE
                            errorTextView.text = "Dashboard is currently only available to instructors"
                        }
                    }
                    else {
                        errorTextView.visibility = View.VISIBLE
                        errorTextView.text = "Wrong username or password"
                    }
                }
            }
            else {
                errorTextView.visibility = View.VISIBLE
                errorTextView.text = "Please enter username and password"
            }
        }
        signupButton.setOnClickListener {
            //Log.d("LoginFragment", "Signup button clicked")
            lifecycleScope.launch {
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container_view, SignupFragment())
                    .addToBackStack(null)
                    .commit()
            }
        }

    }
}