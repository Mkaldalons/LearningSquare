package hbv601g.learningsquare.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import hbv601g.learningsquare.R
import hbv601g.learningsquare.models.AssignmentModel
import hbv601g.learningsquare.models.QuestionModel
import hbv601g.learningsquare.services.AssignmentService
import hbv601g.learningsquare.services.HttpsService
import hbv601g.learningsquare.services.StudentService
import hbv601g.learningsquare.storage.AppDatabase
import hbv601g.learningsquare.storage.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SubmitAssignmentFragment : Fragment(R.layout.fragment_submit_assignment) {
    private lateinit var db: AppDatabase
    private lateinit var userList: List<User>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val assignmentNameText = view.findViewById<TextView>(R.id.assignmentName)
        val submitAssignmentButton = view.findViewById<Button>(R.id.submitAssignment)
        val assignmentGrade = view.findViewById<TextView>(R.id.assignmentGrade)

        var assignment: AssignmentModel?
        val assignmentId = arguments?.getInt("assignmentId") ?: -1

        lifecycleScope.launch {

            withContext(Dispatchers.IO)
            {
                db = AppDatabase.getDatabase(requireContext())
                userList = db.userDao().getAll()
            }

            assignment = getAssignment(assignmentId)
            if (assignment != null)
            {
                val grade = getAssignmentGrade(assignmentId, userList[0].userName)
                if(grade != null)
                {
                    assignmentGrade.text = grade
                }
                assignmentNameText.text = assignment!!.assignmentName
                populateQuestionContainer(assignment!!.questionRequest)
            }
        }

        submitAssignmentButton.setOnClickListener {
            lifecycleScope.launch {
                val answersList = mutableListOf<String>()

                val questionsContainer = view.findViewById<LinearLayout>(R.id.linearLayoutQuestions)

                questionsContainer?.let { container ->
                    for (i in 0 until container.childCount) {
                        val questionView = container.getChildAt(i)
                        val spinner = questionView.findViewById<Spinner>(R.id.spinnerCorrectAnswer)
                        val selectedAnswer = spinner.adapter?.getItem(spinner.selectedItemPosition)?.toString() ?: ""
                        answersList.add(selectedAnswer)
                    }
                }
                val httpsService = HttpsService()
                val studentService = StudentService(httpsService)
                val response = studentService.submitAssignment(assignmentId, userList[0].userName, answersList)
                Log.d("Submit", "Grade: $response")
                if (response >= 0.0)
                {
                    Toast.makeText(requireContext(), "Assignment Submitted with grade $response", Toast.LENGTH_SHORT).show()
                    delay(2000)
                    parentFragmentManager.popBackStack()
                }
                else
                {
                    Toast.makeText(requireContext(), "Could not submit assignment.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun populateQuestionContainer(questions: List<QuestionModel>) {
        val container = view?.findViewById<LinearLayout>(R.id.linearLayoutQuestions)
        container?.removeAllViews()

        for (question in questions) {
            val questionView = LayoutInflater.from(requireContext())
                .inflate(R.layout.item_question, container, false)

            val editTextQuestion = questionView.findViewById<TextView>(R.id.editTextQuestion)
            val editTextOption1 = questionView.findViewById<TextView>(R.id.editTextOption1)
            val editTextOption2 = questionView.findViewById<TextView>(R.id.editTextOption2)
            val editTextOption3 = questionView.findViewById<TextView>(R.id.editTextOption3)
            val editTextOption4 = questionView.findViewById<TextView>(R.id.editTextOption4)
            val spinnerCorrectAnswer = questionView.findViewById<Spinner>(R.id.spinnerCorrectAnswer)

            editTextQuestion.text = question.question
            if (question.options.size >= 4) {
                editTextOption1.text = question.options[0]
                editTextOption2.text = question.options[1]
                editTextOption3.text = question.options[2]
                editTextOption4.text = question.options[3]
            }

            val spinnerAdapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item,
                question.options
            )
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerCorrectAnswer.adapter = spinnerAdapter

            container?.addView(questionView)
        }
    }

    private suspend fun getAssignment(assignmentId: Int): AssignmentModel?
    {
        if (assignmentId == -1) return null

        val httpsService = HttpsService()
        val assignmentService = AssignmentService(httpsService)

        return assignmentService.getAssignment(assignmentId)
    }

    private suspend fun getAssignmentGrade(assignmentId: Int, userName: String) : String?
    {
        if (assignmentId == -1 || userName.isEmpty()) return null

        val httpsService = HttpsService()
        val studentService = StudentService(httpsService)

        val grade = studentService.getAssignmentGrade(assignmentId, userName)
        if (grade != null)
        {
            return grade.toString()
        }
        return null
    }
}