package hbv601g.learningsquare.ui.user_profile

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import hbv601g.learningsquare.R
import hbv601g.learningsquare.services.HttpsService
import hbv601g.learningsquare.services.UserService
import kotlinx.coroutines.launch
import androidx.lifecycle.lifecycleScope
import hbv601g.learningsquare.models.UserModel
import hbv601g.learningsquare.ui.assignments.AssignmentFragment
import hbv601g.learningsquare.ui.courses.CourseFragment
import java.io.ByteArrayOutputStream

class MyInfoFragment : Fragment(R.layout.fragment_my_info) {

    @RequiresApi(Build.VERSION_CODES.O)
    private val getContentLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            newProfileImageData = getByteArrayFromUri(uri, requireContext())

            val sharedPref = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
            val loggedInUser = sharedPref.getString("loggedInUser", null)

            if (loggedInUser != null) {
                uploadAndDisplayImage(loggedInUser)
            }

        } else {
            Toast.makeText(requireContext(), "No image selected", Toast.LENGTH_SHORT).show()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private val cameraLauncher = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap: Bitmap? ->
        if (bitmap != null)
        {
            newProfileImageData = getByteArrayFromBitmap(bitmap)
            val sharedPref = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
            val loggedInUser = sharedPref.getString("loggedInUser", null)

            if (loggedInUser != null) {
                uploadAndDisplayImage(loggedInUser)
            }
        }
        else
        {
            Toast.makeText(requireContext(), "No photo captured", Toast.LENGTH_SHORT).show()
        }
    }


    private var newProfileImageData: ByteArray? = null

    private lateinit var userService: UserService
    private lateinit var httpsService: HttpsService

    private lateinit var profileImageView: ImageView

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        profileImageView = view.findViewById(R.id.profileImageView)
        val usernameTextView = view.findViewById<TextView>(R.id.usernameTextView)
        val emailTextView = view.findViewById<TextView>(R.id.emailTextView)
        val uploadProfilePicture = view.findViewById<Button>(R.id.uploadPictureButton)
        val capturePicture = view.findViewById<Button>(R.id.takePictureButton)

        httpsService = HttpsService()
        userService = UserService(httpsService)

        val sharedPref = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val loggedInUser = sharedPref.getString("loggedInUser", null)

        if (!loggedInUser.isNullOrEmpty()) {
            lifecycleScope.launch {
                val user = userService.getUser(loggedInUser)
                if (user != null) {
                    usernameTextView.text = user.userName
                    emailTextView.text = user.email
                    displayProfileImage(user)
                }
                else {
                    usernameTextView.text = "User not found"
                }
            }
        } else {
            usernameTextView.text = "No user logged in"
        }

        uploadProfilePicture.setOnClickListener {
            getContentLauncher.launch("image/*")
        }

        capturePicture.setOnClickListener {
            cameraLauncher.launch(null)
        }

        val buttonAssignments = view.findViewById<Button>(R.id.buttonAssignments)
        val buttonCourses = view.findViewById<Button>(R.id.buttonCourses)

        buttonAssignments.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container_view, AssignmentFragment())
                .addToBackStack(null)
                .commit()
        }

        buttonCourses.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container_view, CourseFragment())
                .addToBackStack(null)
                .commit()
        }

    }

    private fun displayProfileImage(user: UserModel)
    {
        lifecycleScope.launch {
            if (user.profileImageData.isNullOrEmpty())
            {
                profileImageView.setImageResource(R.drawable.ic_profile_placeholder)
            }
            else
            {
                val profileImage = userService.getProfilePicture(user.userName)
                if(profileImage != null)
                {
                    val profilePictureBitmap =
                        profileImage.let { BitmapFactory.decodeByteArray(profileImage, 0, it.size) }
                    profileImageView.setImageBitmap(profilePictureBitmap)
                }
            }
        }
    }

    private fun getByteArrayFromUri(uri: Uri, context: Context): ByteArray? {
        return context.contentResolver.openInputStream(uri)?.use { inputStream ->
            inputStream.readBytes()
        }
    }

    private fun getByteArrayFromBitmap(bitmap: Bitmap): ByteArray
    {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        return stream.toByteArray()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun uploadAndDisplayImage(loggedInUser: String) {
        lifecycleScope.launch {
            if (newProfileImageData != null) {
                val profileImage = userService.uploadProfileImage(loggedInUser, newProfileImageData!!)
                if (profileImage != null) {
                    val bitmap = BitmapFactory.decodeByteArray(profileImage, 0, profileImage.size)
                    profileImageView.setImageBitmap(bitmap)
                } else {
                    Toast.makeText(requireContext(), "Could not upload photo", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

}
