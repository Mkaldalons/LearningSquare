package hbv601g.learningsquare.ui.user_profile

import android.content.Context
import android.os.Bundle
import android.util.Patterns
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

class UpdateRecoveryEmailFragment : Fragment(R.layout.fragment_update_recovery_email) {

    private lateinit var userService: UserService
    private lateinit var httpsService: HttpsService

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recoveryEmailEditText = view.findViewById<EditText>(R.id.recoveryEmailEditText)
        val updateRecoveryEmailButton = view.findViewById<Button>(R.id.updateRecoveryEmailButton)

        httpsService = HttpsService()
        userService = UserService(httpsService)

        val sharedPref = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val username = sharedPref.getString("loggedInUser", null)

        updateRecoveryEmailButton.setOnClickListener {
            if (username == null) {
                Toast.makeText(requireContext(), "No logged in user", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            val recoveryEmail = recoveryEmailEditText.text?.toString()?.trim()

            if (recoveryEmail.isNullOrEmpty()) {
                Toast.makeText(requireContext(), "Please enter a recovery email", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(recoveryEmail).matches()) {
                Toast.makeText(requireContext(), "Please enter a valid email address", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                val success = userService.updateRecoveryEmail(username, recoveryEmail)
                if (success) {
                    Toast.makeText(requireContext(), "Recovery email updated successfully", Toast.LENGTH_LONG).show()
                    parentFragmentManager.popBackStack()
                } else {
                    Toast.makeText(requireContext(), "Recovery email update failed", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
