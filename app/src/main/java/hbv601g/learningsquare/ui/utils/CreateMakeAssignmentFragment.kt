package hbv601g.learningsquare.ui.utils

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import hbv601g.learningsquare.R
import hbv601g.learningsquare.models.MakeQuestion
import hbv601g.learningsquare.services.AssignmentService
import hbv601g.learningsquare.services.HttpsService
import kotlinx.coroutines.launch

class CreateMakeAssignmentFragment : Fragment(R.layout.fragment_create_make_assignment) {

    private lateinit var assignmentService: AssignmentService
    private var courseId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Sækir courseId úr argumentunum (t.d. "123")
        courseId = arguments?.getString("COURSE_ID")
        assignmentService = AssignmentService(HttpsService())
    }

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
        val correctAnswerEditText = view.findViewById<EditText>(R.id.correctAnswerEditText)
        val createButton = view.findViewById<Button>(R.id.createAssignmentButton)

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
            val correctAnswer = correctAnswerEditText.text.toString().toIntOrNull() ?: -1

            if (title.isNotEmpty() && dueDate.isNotEmpty() && questionText.isNotEmpty() &&
                options.all { it.isNotEmpty() } && correctAnswer in 0..3 && courseId != null) {
                val question = MakeQuestion(questionText, options, correctAnswer)
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
