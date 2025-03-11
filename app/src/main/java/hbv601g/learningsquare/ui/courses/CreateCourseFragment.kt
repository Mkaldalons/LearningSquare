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
import io.ktor.http.HttpStatusCode

class CreateCourseFragment : Fragment(R.layout.fragment_create_course) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val inputCourseName = view.findViewById<EditText>(R.id.inputCourseName)
        val inputInstructor = view.findViewById<EditText>(R.id.inputInstructor)
        val inputDescription = view.findViewById<EditText>(R.id.inputDescription)
        val buttonSubmit = view.findViewById<Button>(R.id.buttonSubmit)
        val buttonCancel = view.findViewById<Button>(R.id.buttonCancel)
        val errorMessageTextView = view.findViewById<TextView>(R.id.errorMessageTextView) // ✅ Add error message display

        var errorText = ""

        buttonSubmit.setOnClickListener {
            val courseName = inputCourseName.text.toString().trim()
            val instructor = inputInstructor.text.toString().trim()
            val description = inputDescription.text.toString().trim()

            val textInputs = listOf(courseName, instructor, description)

            if (textInputs.all { it.isNotEmpty() }) {
                lifecycleScope.launch {
                    val httpsService = HttpsService()

                    try {
                        val response = httpsService.createCourse(courseName, instructor, description)

                        Log.d("CreateCourse", "Response Status: ${response?.status}")

                        if (response != null) {
                            val responseBody = response.bodyAsText()
                            Log.d("CreateCourse", "Response Body: $responseBody")

                            if (response.status == HttpStatusCode.Created) {  // ✅ 201 Created
                                Toast.makeText(requireContext(), "Course Created Successfully!", Toast.LENGTH_SHORT).show()
                                parentFragmentManager.popBackStack() // Navigate back
                            } else {
                                errorText = "Failed to create course: $responseBody"
                                showError(errorMessageTextView, errorText)
                                clearFields(inputCourseName, inputInstructor, inputDescription)
                            }
                        } else {
                            errorText = "No response from server"
                            showError(errorMessageTextView, errorText)
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
            parentFragmentManager.popBackStack() // Navigate back without creating a course
        }
    }

    // ✅ Function to display error messages in the `TextView`
    private fun showError(errorTextView: TextView, message: String) {
        errorTextView.visibility = View.VISIBLE
        errorTextView.text = message
        errorTextView.postDelayed({
            errorTextView.visibility = View.GONE
        }, 5000)
    }

    // ✅ Function to clear input fields
    private fun clearFields(vararg fields: EditText) {
        fields.forEach { it.text.clear() }
    }
}
