package hbv601g.learningsquare.ui

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import hbv601g.learningsquare.R
import hbv601g.learningsquare.services.HttpsService
import hbv601g.learningsquare.services.UserService
import kotlinx.coroutines.launch
import androidx.lifecycle.lifecycleScope

class MyInfoFragment : Fragment(R.layout.fragment_my_info) {

    private lateinit var userService: UserService
    private lateinit var httpsService: HttpsService

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val usernameTextView = view.findViewById<TextView>(R.id.usernameTextView)
        val emailTextView = view.findViewById<TextView>(R.id.emailTextView)
        val deleteAccountButton = view.findViewById<Button>(R.id.deleteAccountButton)


        httpsService = HttpsService()
        userService = UserService(httpsService)


        val sharedPref = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val loggedInUser = sharedPref.getString("loggedInInstructor", null)

        if (loggedInUser != null) {
            lifecycleScope.launch {
                val user = userService.getUser(loggedInUser)
                if (user != null) {
                    usernameTextView.text = user.userName
                    emailTextView.text = user.email
                } else {
                    usernameTextView.text = "User not found"
                }
            }
        } else {
            usernameTextView.text = "No user logged in"
        }


        deleteAccountButton.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Confirm Delete")
                .setMessage("Are you sure you want to delete your account? This action cannot be undone.")
                .setPositiveButton("Yes") { dialog, _ ->
                    dialog.dismiss()
                    loggedInUser?.let { username ->
                        lifecycleScope.launch {
                            val success = userService.deleteUser(username)
                            if (success) {
                                Toast.makeText(requireContext(), "Account deleted successfully", Toast.LENGTH_LONG).show()
                                with(sharedPref.edit()) {
                                    clear()
                                    apply()
                                }
                                // Navigate to LoginFragment after deletion
                                parentFragmentManager.beginTransaction()
                                    .replace(R.id.fragment_container_view, LoginFragment())
                                    .commit()
                            } else {
                                Toast.makeText(requireContext(), "Account deletion failed", Toast.LENGTH_LONG).show()
                            }
                        }
                    } ?: run {
                        Toast.makeText(requireContext(), "No logged in user", Toast.LENGTH_LONG).show()
                    }
                }
                .setNegativeButton("No") { dialog, _ ->
                    dialog.dismiss()
                }
                .create()
                .show()
        }
    }
}
