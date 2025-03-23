package hbv601g.learningsquare.ui

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import hbv601g.learningsquare.R
import hbv601g.learningsquare.services.HttpsService
import hbv601g.learningsquare.services.UserService
import kotlinx.coroutines.launch
import androidx.lifecycle.lifecycleScope
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class MyInfoFragment : Fragment(R.layout.fragment_my_info) {

    private lateinit var userService: UserService
    private lateinit var httpsService: HttpsService

    private lateinit var profileImageView: ImageView
    private lateinit var takePictureButton: Button
    private var currentPhotoPath: String? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        profileImageView = view.findViewById(R.id.profileImageView)
        takePictureButton = view.findViewById(R.id.takePictureButton)
        val usernameTextView = view.findViewById<TextView>(R.id.usernameTextView)
        val emailTextView = view.findViewById<TextView>(R.id.emailTextView)

        httpsService = HttpsService()
        userService = UserService(httpsService)

        val sharedPref = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val loggedInUser = sharedPref.getString("loggedInInstructor", null)

        val takePictureLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) {
                currentPhotoPath?.let { path ->
                    val file = File(path)
                    profileImageView.setImageURI(Uri.fromFile(file))
                    uploadProfileImage(file, loggedInUser)
                }
            }
        }

        if (!loggedInUser.isNullOrEmpty()) {
            lifecycleScope.launch {
                val user = userService.getUser(loggedInUser)
                if (user != null) {
                    usernameTextView.text = user.userName
                    emailTextView.text = user.email

                    if (!user.profileImagePath.isNullOrEmpty()) {
                        val imageUrl = "https://hugbo1-6b15.onrender.com/${user.profileImagePath}"
                        Glide.with(this@MyInfoFragment)
                            .load(imageUrl)
                            .placeholder(R.drawable.ic_profile_placeholder)
                            .into(profileImageView)
                    } else {
                        profileImageView.setImageResource(R.drawable.ic_profile_placeholder)
                    }
                } else {
                    usernameTextView.text = "User not found"
                }
            }
        } else {
            usernameTextView.text = "No user logged in"
        }

        takePictureButton.setOnClickListener {
            openCamera(takePictureLauncher)
        }
    }

    private fun openCamera(takePictureLauncher: androidx.activity.result.ActivityResultLauncher<Uri>) {
        try {
            val photoFile = createImageFile()
            currentPhotoPath = photoFile.absolutePath
            val photoUri: Uri = FileProvider.getUriForFile(
                requireContext(),
                "${requireContext().packageName}.fileprovider",
                photoFile
            )
            takePictureLauncher.launch(photoUri)
        } catch (ex: IOException) {
            Toast.makeText(requireContext(), "Error creating image file", Toast.LENGTH_SHORT).show()
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val imageFileName = "JPEG_${timeStamp}_"
        val storageDir: File = requireContext().cacheDir
        return File.createTempFile(imageFileName, ".jpg", storageDir)
    }

    private fun uploadProfileImage(file: File, username: String?) {
        if (username == null) {
            Toast.makeText(requireContext(), "No logged in user", Toast.LENGTH_LONG).show()
            return
        }

        lifecycleScope.launch {
            val success = userService.uploadProfileImage(username, file)
            if (success) {
                Toast.makeText(requireContext(), "Profile image uploaded successfully", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(requireContext(), "Failed to upload profile image", Toast.LENGTH_LONG).show()
            }
        }
    }
}