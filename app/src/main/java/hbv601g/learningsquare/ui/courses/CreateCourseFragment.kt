package hbv601g.learningsquare.ui.courses

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import hbv601g.learningsquare.R
import hbv601g.learningsquare.services.HttpsService
import kotlinx.coroutines.launch
import io.ktor.client.statement.bodyAsText
import android.content.Context

class CreateCourseFragment : Fragment(R.layout.fragment_create_course) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val inputCourseName = view.findViewById<EditText>(R.id.inputCourseName)
        val inputDescription = view.findViewById<EditText>(R.id.inputDescription)
        val buttonSubmit = view.findViewById<Button>(R.id.buttonSubmit)
        val buttonCancel = view.findViewById<Button>(R.id.buttonCancel)
        val errorMessageTextView = view.findViewById<TextView>(R.id.errorMessageTextView)

        val sharedPref = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val loggedInInstructor = sharedPref.getString("loggedInUser", null)

        var errorText: String

        buttonSubmit.setOnClickListener {
            val courseName = inputCourseName.text.toString().trim()
            val description = inputDescription.text.toString().trim()

            val textInputs = listOf(courseName, description)

            if (textInputs.all { it.isNotEmpty() }) {
                lifecycleScope.launch {
                    val httpsService = HttpsService()

                    try {
                        val response = httpsService.createCourse(courseName, loggedInInstructor!!, description)

                        Log.d("CreateCourse", "Response Status: ${response.status}")

                            val responseBody = response.bodyAsText()
                            Log.d("CreateCourse", "Response Body: $responseBody")

                            if (response.status.value == 200) {
                                Toast.makeText(requireContext(), "Course Created Successfully!", Toast.LENGTH_SHORT).show()
                                parentFragmentManager.popBackStack()
                            } else {
                                errorText = "Failed to create course: $responseBody"
                                showError(errorMessageTextView, errorText)
                                clearFields(inputCourseName, inputDescription)
                            }

                    } catch (e: Exception) {
                        Log.e("CreateCourse", "Error creating course: ${e.message}")
                        errorText = "Error: ${e.message}"
                        showError(errorMessageTextView, errorText)
                    }
                }
            } else {
                errorText = "All fields are required!"
                showError(errorMessageTextView, errorText)
            }
        }

        buttonCancel.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private fun showError(errorTextView: TextView, message: String) {
        errorTextView.visibility = View.VISIBLE
        errorTextView.text = message
        errorTextView.postDelayed({
            errorTextView.visibility = View.GONE
        }, 5000)
    }

    private fun clearFields(vararg fields: EditText) {
        fields.forEach { it.text.clear() }
    }
}
