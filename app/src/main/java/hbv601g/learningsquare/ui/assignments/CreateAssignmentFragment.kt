package hbv601g.learningsquare.ui.assignments

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.icu.util.Calendar
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import hbv601g.learningsquare.R
import hbv601g.learningsquare.models.QuestionModel
import hbv601g.learningsquare.services.AssignmentService
import hbv601g.learningsquare.services.HttpsService
import hbv601g.learningsquare.ui.utils.AssignmentReminderScheduler
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter
import java.util.Locale

class CreateAssignmentFragment : Fragment(R.layout.fragment_create_assignment) {
    private var selectedCourseId: Int = -1

    private lateinit var assignmentNameEditText: EditText
    private lateinit var dueDateEditText: EditText
    private lateinit var questionsContainer: LinearLayout
    private lateinit var addQuestionButton: Button
    private lateinit var submitAssignmentButton: Button
    private lateinit var dueTimeEditText: EditText

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        assignmentNameEditText = view.findViewById(R.id.editTextAssignmentName)
        dueDateEditText = view.findViewById(R.id.editTextDueDate)
        addQuestionButton = view.findViewById(R.id.buttonAddQuestion)
        submitAssignmentButton = view.findViewById(R.id.buttonSubmitAssignment)
        questionsContainer = view.findViewById(R.id.linearLayoutQuestions)
        dueTimeEditText = view.findViewById(R.id.editTextDueTime)


        selectedCourseId = arguments?.getInt("selectedCourseId") ?: -1

        dueDateEditText.setOnClickListener {
            showDatePickerDialog()
        }

        dueTimeEditText.setOnClickListener {
            showTimePickerDialog()
        }

        addQuestionButton.setOnClickListener {
            addQuestion(questionsContainer)
        }

        submitAssignmentButton.setOnClickListener {
            if (selectedCourseId != -1) {
                submitAssignment()
            } else {
                Toast.makeText(
                    requireContext(),
                    "Failed to create assignment since no courseId was found",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun addQuestion(container: LinearLayout) {
        val questionView =
            LayoutInflater.from(requireContext()).inflate(R.layout.item_question, container, false)

        val option1EditText = questionView.findViewById<EditText>(R.id.editTextOption1)
        val option2EditText = questionView.findViewById<EditText>(R.id.editTextOption2)
        val option3EditText = questionView.findViewById<EditText>(R.id.editTextOption3)
        val option4EditText = questionView.findViewById<EditText>(R.id.editTextOption4)

        val spinner = questionView.findViewById<Spinner>(R.id.spinnerCorrectAnswer)

        fun updateSpinner() {
            val o1 = option1EditText.text.toString().trim()
            val o2 = option2EditText.text.toString().trim()
            val o3 = option3EditText.text.toString().trim()
            val o4 = option4EditText.text.toString().trim()

            if (o1.isNotEmpty() && o2.isNotEmpty() && o3.isNotEmpty() && o4.isNotEmpty()) {
                val options = listOf(o1, o2, o3, o4)
                val spinnerAdapter =
                    ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, options)
                spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinner.adapter = spinnerAdapter
            } else {
                spinner.adapter = null
            }
        }

        val watcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                updateSpinner()
            }

            override fun afterTextChanged(s: Editable?) {}
        }

        option1EditText.addTextChangedListener(watcher)
        option2EditText.addTextChangedListener(watcher)
        option3EditText.addTextChangedListener(watcher)
        option4EditText.addTextChangedListener(watcher)

        container.addView(questionView)
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            requireContext(), { _, year, month, day ->
                val formattedDate = "$year-${month + 1}-$day"
                dueDateEditText.setText(formattedDate)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun showTimePickerDialog() {
        val calendar = Calendar.getInstance()
        TimePickerDialog(
            requireContext(), { _, hour, minute ->
                val formattedTime = String.format(Locale.US, "%02d:%02d", hour, minute)
                dueTimeEditText.setText(formattedTime)
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        ).show()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun submitAssignment() {
        val assignmentName = assignmentNameEditText.text.toString().trim()
        val assignmentDate = dueDateEditText.text.toString().trim()
        Log.d("Assignment", "Date String: $assignmentDate")
        val assignmentTime = dueTimeEditText.text.toString().trim()

        if (assignmentDate.isEmpty() || assignmentTime.isEmpty()) {
            Toast.makeText(requireContext(), "Please select both date and time", Toast.LENGTH_SHORT)
                .show()
            return
        }

        val dateFormatter = DateTimeFormatter.ofPattern("yyyy-M-d", Locale.US)
        val timeFormatter = DateTimeFormatter.ofPattern("H:mm", Locale.US)

        val javaLocalDate = java.time.LocalDate.parse(assignmentDate, dateFormatter)
        val javaLocalTime = java.time.LocalTime.parse(assignmentTime, timeFormatter)
        val returnDateTime = javaLocalDate.atTime(javaLocalTime)
        val returnDateString = returnDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.US))

        val questionsList = mutableListOf<QuestionModel>()

        for (i in 0 until questionsContainer.childCount) {
            val questionView = questionsContainer.getChildAt(i)
            val questionText =
                questionView.findViewById<EditText>(R.id.editTextQuestion).text.toString().trim()
            val o1 =
                questionView.findViewById<EditText>(R.id.editTextOption1).text.toString().trim()
            val o2 =
                questionView.findViewById<EditText>(R.id.editTextOption2).text.toString().trim()
            val o3 =
                questionView.findViewById<EditText>(R.id.editTextOption3).text.toString().trim()
            val o4 =
                questionView.findViewById<EditText>(R.id.editTextOption4).text.toString().trim()
            val spinner = questionView.findViewById<Spinner>(R.id.spinnerCorrectAnswer)

            if (spinner.adapter == null) continue

            val correctAnswerIndex = spinner.selectedItemPosition
            val options = listOf(o1, o2, o3, o4)

            val questionModel = QuestionModel(
                question = questionText,
                options = options,
                correctAnswer = options[correctAnswerIndex]
            )
            questionsList.add(questionModel)
        }

        lifecycleScope.launch {
            val httpsService = HttpsService()
            val assignmentService = AssignmentService(httpsService)
            val response = assignmentService.createAssignment(
                assignmentName,
                selectedCourseId,
                false,
                returnDateString,
                questionsList
            )

            if (response != null) {
                AssignmentReminderScheduler.scheduleReminder(
                    requireContext(),
                    returnDateString,
                    assignmentName
                )

                Toast.makeText(
                    requireContext(),
                    "Assignment created successfully",
                    Toast.LENGTH_SHORT
                ).show()
                delay(2000)
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container_view, AssignmentFragment())
                    .addToBackStack(null)
                    .commit()
            } else {
                Toast.makeText(requireContext(), "Failed to create assignment", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }
}