package hbv601g.learningsquare.ui

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import hbv601g.learningsquare.R
import hbv601g.learningsquare.services.HttpsService
import hbv601g.learningsquare.services.UserService
import kotlinx.coroutines.launch
import androidx.lifecycle.lifecycleScope

class ChangePasswordFragment : Fragment(R.layout.fragment_change_password) {

    private lateinit var userService: UserService
    private lateinit var httpsService: HttpsService

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val oldPasswordEditText = view.findViewById<EditText>(R.id.oldPasswordEditText)
        val newPasswordEditText = view.findViewById<EditText>(R.id.newPasswordEditText)
        val changePasswordButton = view.findViewById<Button>(R.id.changePasswordButton)

        httpsService = HttpsService()
        userService = UserService(httpsService)

        val sharedPref = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val username = sharedPref.getString("loggedInInstructor", null)

        changePasswordButton.setOnClickListener {
            if (username == null) {
                Toast.makeText(requireContext(), "No logged in user", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            val oldPassword = oldPasswordEditText.text.toString().trim()
            val newPassword = newPasswordEditText.text.toString().trim()

            if (oldPassword.isEmpty() || newPassword.isEmpty()) {
                Toast.makeText(requireContext(), "Please fill in both password fields", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                val success = userService.changePassword(username, oldPassword, newPassword)
                if (success) {
                    Toast.makeText(requireContext(), "Password updated successfully", Toast.LENGTH_LONG).show()
                    parentFragmentManager.popBackStack()  // or navigate as needed
                } else {
                    Toast.makeText(requireContext(), "Password update failed", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
