package hbv601g.learningsquare.ui.user_profile

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import hbv601g.learningsquare.R
import hbv601g.learningsquare.services.HttpsService
import hbv601g.learningsquare.services.UserService
import kotlinx.coroutines.launch
import androidx.lifecycle.lifecycleScope
import hbv601g.learningsquare.storage.AppDatabase
import hbv601g.learningsquare.storage.User
import hbv601g.learningsquare.ui.LoginFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

class MyInfoFragment : Fragment(R.layout.fragment_my_info) {
    private lateinit var db: AppDatabase
    private lateinit var userList: List<User>

    @RequiresApi(Build.VERSION_CODES.O)
        val getContentLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            if (uri != null) {
                newProfileImageData = getByteArrayFromUri(uri, requireContext())

                val userName = userList[0].userName

                if (userName.isNotEmpty())
                {
                    uploadAndDisplayImage(userName)
                }
                else
                {
                    Toast.makeText(requireContext(), "Could not find user", Toast.LENGTH_SHORT).show()
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

            val userName = userList[0].userName

            if (userName.isNotEmpty())
            {
                uploadAndDisplayImage(userName)
            }
            else
            {
                Toast.makeText(requireContext(), "Could not find user", Toast.LENGTH_SHORT).show()
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

        val updatePasswordButton = view.findViewById<Button>(R.id.updatePasswordButton)
        val updateRecoveryEmailButton = view.findViewById<Button>(R.id.updateRecoveryEmailButton)
        val deleteAccountButton = view.findViewById<Button>(R.id.deleteAccountButton)

        httpsService = HttpsService()
        userService = UserService(httpsService)

        var errorText: String

        lifecycleScope.launch {

            withContext(Dispatchers.IO)
            {
                db = AppDatabase.getDatabase(requireContext())
                userList = db.userDao().getAll()
            }

            if (userList.isNotEmpty()) {
                val user = userList[0]
                usernameTextView.text = user.userName
                emailTextView.text = user.email
                displayProfileImage(user)

            } else {
                errorText = "No user logged in"
                usernameTextView.text = errorText
            }
        }

        uploadProfilePicture.setOnClickListener {
            getContentLauncher.launch("image/*")
        }

        capturePicture.setOnClickListener {
            cameraLauncher.launch(null)
        }

        updatePasswordButton.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container_view, ChangePasswordFragment())
                .addToBackStack(null)
                .commit()
        }

        updateRecoveryEmailButton.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container_view, UpdateRecoveryEmailFragment())
                .addToBackStack(null)
                .commit()
        }

        deleteAccountButton.setOnClickListener {
            showDeleteAccountDialog()
        }

    }

    private fun displayProfileImage(user: User)
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

    private fun showDeleteAccountDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Account")
            .setMessage("Are you sure you want to delete your account ${userList[0].userName}?")
            .setPositiveButton("Confirm") { dialog, _ ->
                deleteAccount()
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

    private fun deleteAccount()
    {
        lifecycleScope.launch {
            val deleted = userService.deleteUser(userList[0].userName)

            if(deleted)
            {
                withContext(Dispatchers.IO) {
                    db.userDao().delete(userList[0])
                }

                Toast.makeText(requireContext(), "Account Deleted. Redirecting to homepage", Toast.LENGTH_SHORT).show()
                delay(2000)
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container_view, LoginFragment())
                    .addToBackStack(null)
                    .commit()
            }
            else
            {
                Toast.makeText(requireContext(), "Could not delete account! Please email us.",
                    Toast.LENGTH_SHORT).show()
            }
        }
    }

}
