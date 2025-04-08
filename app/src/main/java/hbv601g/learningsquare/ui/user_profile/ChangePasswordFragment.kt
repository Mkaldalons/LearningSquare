package hbv601g.learningsquare.ui.user_profile

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
import hbv601g.learningsquare.storage.AppDatabase
import hbv601g.learningsquare.storage.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ChangePasswordFragment : Fragment(R.layout.fragment_change_password) {
    private lateinit var userService: UserService
    private lateinit var httpsService: HttpsService
    private lateinit var db: AppDatabase
    private lateinit var userList: List<User>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val oldPasswordEditText = view.findViewById<EditText>(R.id.oldPasswordEditText)
        val newPasswordEditText = view.findViewById<EditText>(R.id.newPasswordEditText)
        val changePasswordButton = view.findViewById<Button>(R.id.changePasswordButton)

        changePasswordButton.setOnClickListener {

            val oldPassword = oldPasswordEditText.text.toString().trim()
            val newPassword = newPasswordEditText.text.toString().trim()

            if (oldPassword.isEmpty() || newPassword.isEmpty()) {
                Toast.makeText(requireContext(), "Please fill in both password fields", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {

                withContext(Dispatchers.IO) {
                    db = AppDatabase.getDatabase(requireContext())
                    userList = db.userDao().getAll()
                }

                httpsService = HttpsService()
                userService = UserService(httpsService)

                val success = userService.changePassword(userList[0].userName, oldPassword, newPassword)
                if (success) {
                    Toast.makeText(requireContext(), "Password updated successfully", Toast.LENGTH_LONG).show()
                    parentFragmentManager.popBackStack()
                } else {
                    Toast.makeText(requireContext(), "Password update failed", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
