package hbv601g.learningsquare.ui

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import hbv601g.learningsquare.R
import hbv601g.learningsquare.models.MakeQuestion
import hbv601g.learningsquare.services.AssignmentService
import hbv601g.learningsquare.services.HttpsService
import kotlinx.coroutines.launch
import java.util.*

class CreateMakeAssignmentFragment : Fragment(R.layout.fragment_create_make_assignment) {

    private lateinit var assignmentService: AssignmentService
    private var courseId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Retrieve courseId from arguments if provided
        courseId = arguments?.getString("COURSE_ID")
        assignmentService = AssignmentService(HttpsService())
    }

    @SuppressLint("DefaultLocale")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val titleEditText = view.findViewById<EditText>(R.id.assignmentTitleEditText)
        val descriptionEditText = view.findViewById<EditText>(R.id.assignmentDescriptionEditText)
        val dueDateEditText = view.findViewById<EditText>(R.id.assignmentDueDateEditText)
        val questionEditText = view.findViewById<EditText>(R.id.questionEditText)
        val option1EditText = view.findViewById<EditText>(R.id.option1EditText)
        val option2EditText = view.findViewById<EditText>(R.id.option2EditText)
        val option3EditText = view.findViewById<EditText>(R.id.option3EditText)
        val option4EditText = view.findViewById<EditText>(R.id.option4EditText)
        // Use the spinner for selecting the correct answer (1-4)
        val correctAnswerSpinner = view.findViewById<Spinner>(R.id.correctAnswerSpinner)
        val createButton = view.findViewById<Button>(R.id.createAssignmentButton)

        // Setup the spinner with values 1 to 4
        val spinnerValues = listOf("1", "2", "3", "4")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, spinnerValues)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        correctAnswerSpinner.adapter = adapter

        // Set up date picker on dueDateEditText click
        dueDateEditText.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            DatePickerDialog(requireContext(), { _, selectedYear, selectedMonth, selectedDay ->
                // Format the date as YYYY-MM-DD (month is zero-based so add 1)
                val formattedDate = String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay)
                dueDateEditText.setText(formattedDate)
            }, year, month, day).show()
        }

        createButton.setOnClickListener {
            val title = titleEditText.text.toString().trim()
            val description = descriptionEditText.text.toString().trim()
            val dueDate = dueDateEditText.text.toString().trim()
            val questionText = questionEditText.text.toString().trim()
            val options = listOf(
                option1EditText.text.toString().trim(),
                option2EditText.text.toString().trim(),
                option3EditText.text.toString().trim(),
                option4EditText.text.toString().trim()
            )
            // Retrieve the selected correct answer from the spinner (as integer)
            val selectedValue = correctAnswerSpinner.selectedItem.toString().toIntOrNull() ?: -1

            // Log current values for debugging
            Log.d("CreateAssignment", "title: '$title'")
            Log.d("CreateAssignment", "dueDate: '$dueDate'")
            Log.d("CreateAssignment", "questionText: '$questionText'")
            Log.d("CreateAssignment", "options: $options")
            Log.d("CreateAssignment", "selected correctAnswer: $selectedValue")
            Log.d("CreateAssignment", "courseId: $courseId")

            // Validate that all fields are correctly filled and selected value is between 1 and 4
            if (title.isNotEmpty() && dueDate.isNotEmpty() && questionText.isNotEmpty() &&
                options.all { it.isNotEmpty() } && selectedValue in 1..4 && courseId != null) {

                val question = MakeQuestion(questionText, options, selectedValue)
                lifecycleScope.launch {
                    val assignment = assignmentService.createMakeAssignment(courseId!!, title, description, dueDate, listOf(question))
                    if (assignment != null) {
                        Toast.makeText(requireContext(), "Assignment created!", Toast.LENGTH_SHORT).show()
                        parentFragmentManager.popBackStack()
                    } else {
                        Toast.makeText(requireContext(), "Failed to create assignment.", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(requireContext(), "Please fill all fields correctly.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}