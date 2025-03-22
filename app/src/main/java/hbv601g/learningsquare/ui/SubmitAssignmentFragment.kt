package hbv601g.learningsquare.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import hbv601g.learningsquare.R
import hbv601g.learningsquare.models.AssignmentModel
import hbv601g.learningsquare.models.QuestionModel
import hbv601g.learningsquare.services.AssignmentService
import hbv601g.learningsquare.services.HttpsService
import kotlinx.coroutines.launch

class SubmitAssignmentFragment : Fragment(R.layout.fragment_submit_assignment) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val assignmentNameText = view.findViewById<TextView>(R.id.assignmentName)

        var assignment: AssignmentModel?
        val assignmentId = arguments?.getInt("assignmentId") ?: -1

        lifecycleScope.launch {
            assignment = getAssignment(assignmentId)
            if (assignment != null)
            {
                assignmentNameText.text = assignment!!.assignmentName
                populateQuestionContainer(assignment!!.questionRequest)
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

            val correctIndex = question.options.indexOf(question.correctAnswer)
            if (correctIndex >= 0) {
                spinnerCorrectAnswer.setSelection(correctIndex)
            }

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
}