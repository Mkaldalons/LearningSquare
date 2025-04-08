package hbv601g.learningsquare.ui.user_profile

import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import hbv601g.learningsquare.R
import hbv601g.learningsquare.services.HttpsService
import hbv601g.learningsquare.services.UserService
import kotlinx.coroutines.launch
import androidx.lifecycle.lifecycleScope
import hbv601g.learningsquare.storage.AppDatabase
import hbv601g.learningsquare.storage.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UpdateRecoveryEmailFragment : Fragment(R.layout.fragment_update_recovery_email) {
    private lateinit var userService: UserService
    private lateinit var httpsService: HttpsService
    private lateinit var db: AppDatabase
    private lateinit var userList: List<User>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recoveryEmailEditText = view.findViewById<EditText>(R.id.recoveryEmailEditText)
        val updateRecoveryEmailButton = view.findViewById<Button>(R.id.updateRecoveryEmailButton)
        val currentRecovery = view.findViewById<TextView>(R.id.currentRecovery)

        var currentRecoveryText: String

        lifecycleScope.launch {

            withContext(Dispatchers.IO)
            {
                db = AppDatabase.getDatabase(requireContext())
                userList = db.userDao().getAll()
            }

            if (userList[0].recoveryEmail.isNullOrEmpty())
            {
                currentRecoveryText = "No recovery email yet"
                currentRecovery.text = currentRecoveryText
            }
            else
            {
                currentRecoveryText = "Current recovery email: ${userList[0].recoveryEmail.toString()}"
                currentRecovery.text = currentRecoveryText
            }
        }

        updateRecoveryEmailButton.setOnClickListener {

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

                httpsService = HttpsService()
                userService = UserService(httpsService)

                val success = userService.updateRecoveryEmail(userList[0].userName, recoveryEmail)

                withContext(Dispatchers.IO) {
                    db.userDao().updateRecoveryEmail(recoveryEmail, userList[0].userName)
                }

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
