package hbv601g.learningsquare.ui

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import hbv601g.learningsquare.R
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import hbv601g.learningsquare.services.HttpsService
import hbv601g.learningsquare.services.UserService
import kotlinx.coroutines.launch

class LoginFragment : Fragment(R.layout.fragment_login) {

     override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?)
     {
         super.onViewCreated(view, savedInstanceState)

        val inputUsername = view.findViewById<EditText>(R.id.inputUsername)
        val inputPassword = view.findViewById<EditText>(R.id.inputPassword)
        val loginButton = view.findViewById<Button>(R.id.loginButton)

        Log.d("LoginFragment", "onCreateViewExecuted")

        loginButton.setOnClickListener {
            val username = inputUsername?.text.toString()
            val password = inputPassword?.text.toString()
            Log.d("LoginFragment", "ButtonClicked")
            lifecycleScope.launch {
                try {
                    val httpsService = HttpsService()
                    val userService = UserService(httpsService)

                    val response = userService.loginUser(username, password)
                    Log.d("LoginFragment", "Validated tag: $response")
                    if( response == "instructor")
                    {
                        parentFragmentManager.beginTransaction()
                            .replace(R.id.fragment_container_view, InstructorDashboardFragment())
                            .addToBackStack(null)
                            .commit()
                    }
                    else {
                        Toast.makeText(requireContext(), "Error: Only instructors have access to the dashboard.", Toast.LENGTH_SHORT).show()
                    }
                }catch (e: Exception){
                    Log.e("LoginFragment", "Error fetching user: ${e.localizedMessage}")
                    Toast.makeText(requireContext(), "Error fetching user data.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

}