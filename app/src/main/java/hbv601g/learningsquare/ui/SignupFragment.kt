package hbv601g.learningsquare.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import hbv601g.learningsquare.R
import hbv601g.learningsquare.services.HttpsService
import hbv601g.learningsquare.services.UserService
import kotlinx.coroutines.launch

class SignupFragment : Fragment(R.layout.fragment_signup_layout) {
    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        val inputUsername = view.findViewById<EditText>(R.id.userNameInput)
        val nameInput = view.findViewById<EditText>(R.id.nameInput)
        val emailInput = view.findViewById<EditText>(R.id.emailInput)
        val passwordInput = view.findViewById<EditText>(R.id.passwordInput)
        val confirmPasswordInput = view.findViewById<EditText>(R.id.confirmPasswordInput)
        val isInstructorCheckBox = view.findViewById<CheckBox>(R.id.isInstructorCheckbox)
        val confirmSignupButton = view.findViewById<Button>(R.id.confirmSignupButton)
        val errorMessageTextView = view.findViewById<TextView>(R.id.errorMessageTextView)

        var errorText = ""

        confirmSignupButton.setOnClickListener{
            val textInputs = mutableListOf(inputUsername.text.toString(), nameInput.text.toString(),
                emailInput.text.toString(), passwordInput.text.toString(), confirmPasswordInput.text.toString())
            val isInstructor = isInstructorCheckBox.isChecked
            if(textInputs.all { it.isNotEmpty() })
            {
                if(passwordInput.text.toString() == confirmPasswordInput.text.toString())
                {
                    lifecycleScope.launch {
                        val httpsService = HttpsService()
                        val userService = UserService(httpsService)

                        val user = userService.signupUser(inputUsername.text.toString(),
                            nameInput.text.toString(), emailInput.text.toString(), passwordInput.text.toString(),
                            isInstructor)

                        if(user != null)
                        {
                            Log.d("SignupFragment", "The user ${user.userName} has been created")
                            if (user.instructor) {
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
                        else
                        {
                            errorText = "User with username ${textInputs[0]} already exists"
                            errorMessageTextView.visibility = View.VISIBLE
                            errorMessageTextView.text = errorText
                            errorMessageTextView.postDelayed({
                                errorMessageTextView.visibility = View.GONE
                            }, 5_000)
                            inputUsername.text.clear()
                            emailInput.text.clear()
                        }
                    }
                }
                else
                {
                    errorText = "Passwords do not match"
                    errorMessageTextView.visibility = View.VISIBLE
                    errorMessageTextView.text = errorText
                    errorMessageTextView.postDelayed({
                        errorMessageTextView.visibility = View.GONE
                    }, 5_000)
                    passwordInput.text.clear()
                    confirmPasswordInput.text.clear()
                }
            }
            else
            {
                errorText = "Please fill all text fields"
                errorMessageTextView.visibility = View.VISIBLE
                errorMessageTextView.text = errorText
                errorMessageTextView.postDelayed({
                    errorMessageTextView.visibility = View.GONE
                }, 5_000)
            }
        }
    }
}