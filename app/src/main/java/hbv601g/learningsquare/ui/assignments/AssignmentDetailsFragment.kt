package hbv601g.learningsquare.ui.assignments

import android.app.DatePickerDialog
import android.icu.util.Calendar
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.material.switchmaterial.SwitchMaterial
import hbv601g.learningsquare.R
import hbv601g.learningsquare.models.AssignmentModel
import hbv601g.learningsquare.models.QuestionModel
import hbv601g.learningsquare.services.AssignmentService
import hbv601g.learningsquare.services.HttpsService
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.toLocalDate
import java.time.format.DateTimeFormatter

class AssignmentDetailsFragment : Fragment(R.layout.fragment_assignment_details) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val assignmentNameEditText = view.findViewById<EditText>(R.id.editAssignmentName)
        val editAssignmentDueDate = view.findViewById<EditText>(R.id.editTextDueDate)
        val questionsDropDown = view.findViewById<Spinner>(R.id.questionsDropdown)
        val togglePublish = view.findViewById<SwitchMaterial>(R.id.togglePublish)
        val saveChanges = view.findViewById<Button>(R.id.saveChangesButton)
        val discardChanges = view.findViewById<Button>(R.id.clearChangesButton)

        val assignmentId = arguments?.getInt("assignmentId") ?: -1

        Log.d("AssignmentDetails", "Got assignmentId: $assignmentId")

        lifecycleScope.launch {
            val assignment = getAssignment(assignmentId)

            if (assignment != null)
            {
                val questions: List<QuestionModel> = assignment.questionRequest
                val questionNames = questions.map { it.question }

                assignmentNameEditText.setText(assignment.assignmentName)
                editAssignmentDueDate.setText(assignment.dueDate.toString())
                if (assignment.published)
                {
                    togglePublish.toggle()
                }

                val spinnerAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, questionNames)
                spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

                questionsDropDown.adapter = spinnerAdapter

                questionsDropDown.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        if (position in questions.indices)
                        {
                            populateQuestionFields(questions[position])
                        }
                    }
                    override fun onNothingSelected(parent: AdapterView<*>?) {
                        TODO("Not yet implemented")
                    }
                }
            }
        }

        editAssignmentDueDate.setOnClickListener {
            showDatePickerDialog(editAssignmentDueDate)
        }

        saveChanges.setOnClickListener {
            lifecycleScope.launch {
                val httpsService = HttpsService()
                val assignmentService = AssignmentService(httpsService)
                var name = assignmentNameEditText.text.toString()
                var dueDate = editAssignmentDueDate.text.toString()
                val published = togglePublish.isChecked

                val originalAssignment = getAssignment(assignmentId)

                val questionsContainer = view.findViewById<LinearLayout>(R.id.linearLayoutQuestions)
                val questionsList = mutableListOf<QuestionModel>()

                if (questionsContainer != null) {
                    for (i in 0 until questionsContainer.childCount) {
                        val questionView = questionsContainer.getChildAt(i)
                        val questionText = questionView.findViewById<EditText>(R.id.editTextQuestion).text.toString().trim()
                        val option1 = questionView.findViewById<EditText>(R.id.editTextOption1).text.toString().trim()
                        val option2 = questionView.findViewById<EditText>(R.id.editTextOption2).text.toString().trim()
                        val option3 = questionView.findViewById<EditText>(R.id.editTextOption3).text.toString().trim()
                        val option4 = questionView.findViewById<EditText>(R.id.editTextOption4).text.toString().trim()
                        val spinner = questionView.findViewById<Spinner>(R.id.spinnerCorrectAnswer)
                        val correctAnswer = spinner.adapter?.getItem(spinner.selectedItemPosition)?.toString() ?: ""

                        val questionModel = QuestionModel(
                            question = questionText,
                            options = listOf(option1, option2, option3, option4),
                            correctAnswer = correctAnswer
                        )
                        questionsList.add(questionModel)
                    }
                }

                if((name == originalAssignment?.assignmentName) || name.isBlank())
                {
                    name = ""
                }
                if((dueDate == originalAssignment?.dueDate.toString()) || dueDate.isBlank())
                {
                    dueDate = ""
                }
                if((questionsList == originalAssignment?.questionRequest))
                {
                    questionsList.clear()
                }
                val response = assignmentService.editAssignmentDetails(assignmentId, name, dueDate, questionsList, published)

                if(response)
                {
                    Toast.makeText(requireContext(), "Assignment Modified",Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private suspend fun getAssignment(assignmentId: Int): AssignmentModel?
    {
        if (assignmentId == -1) return null

        val httpsService = HttpsService()
        val assignmentService = AssignmentService(httpsService)

        return assignmentService.getAssignment(assignmentId)
    }

    private fun showDatePickerDialog(editTextDueDate: EditText)
    {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(requireContext(), { _, selectedYear, selectedMonth, selectedDay ->
            val formattedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
            editTextDueDate.setText(formattedDate)
        }, year, month, day).show()
    }

    private fun populateQuestionFields(question: QuestionModel) {
        val questionsContainer = view?.findViewById<LinearLayout>(R.id.linearLayoutQuestions)

        questionsContainer?.removeAllViews()

        val questionView = LayoutInflater.from(requireContext())
            .inflate(R.layout.item_question, questionsContainer, false)

        val editTextQuestion = questionView.findViewById<EditText>(R.id.editTextQuestion)
        val editTextOption1 = questionView.findViewById<EditText>(R.id.editTextOption1)
        val editTextOption2 = questionView.findViewById<EditText>(R.id.editTextOption2)
        val editTextOption3 = questionView.findViewById<EditText>(R.id.editTextOption3)
        val editTextOption4 = questionView.findViewById<EditText>(R.id.editTextOption4)
        val spinnerCorrectAnswer = questionView.findViewById<Spinner>(R.id.spinnerCorrectAnswer)

        editTextQuestion.setText(question.question)

        if (question.options.size >= 4) {
            Log.d("AssignmentDetails", "Options: ${question.options}")
            editTextOption1.setText(question.options[0])
            editTextOption2.setText(question.options[1])
            editTextOption3.setText(question.options[2])
            editTextOption4.setText(question.options[3])
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

        questionsContainer?.addView(questionView)

    }
}