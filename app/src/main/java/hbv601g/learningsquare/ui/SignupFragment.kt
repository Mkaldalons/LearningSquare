package hbv601g.learningsquare.ui

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import androidx.fragment.app.Fragment
import hbv601g.learningsquare.R

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

        confirmSignupButton.setOnClickListener{
            val isInstructor = isInstructorCheckBox.isChecked
        }
    }
}