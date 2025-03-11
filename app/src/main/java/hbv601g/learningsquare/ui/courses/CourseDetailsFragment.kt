package hbv601g.learningsquare.ui.courses

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import hbv601g.learningsquare.R
import hbv601g.learningsquare.models.StudentModel
import hbv601g.learningsquare.services.CourseService
import hbv601g.learningsquare.services.HttpsService
import hbv601g.learningsquare.ui.StudentAdapter
import kotlinx.coroutines.launch

class CourseDetailsFragment : Fragment(R.layout.fragment_course_details) {

    private val students = mutableListOf<StudentModel>()
    private lateinit var studentAdapter: StudentAdapter
    private lateinit var courseService: CourseService
    private var courseId: Int = -1

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        courseService = CourseService(HttpsService())
        courseId = arguments?.getInt("courseId") ?: -1

        val recyclerViewStudents = view.findViewById<RecyclerView>(R.id.recyclerViewStudents)
        recyclerViewStudents.layoutManager = LinearLayoutManager(requireContext())

        studentAdapter = StudentAdapter(students, onRemoveStudent = { studentUserName ->
            showRemoveStudentDialog(studentUserName)
        })
        recyclerViewStudents.adapter = studentAdapter

        val studentUsernameInput = view.findViewById<EditText>(R.id.studentUserNameInput)
        val addStudentButton = view.findViewById<Button>(R.id.addStudentButton)
        val showMessage = view.findViewById<TextView>(R.id.notifyUserTextView)

        addStudentButton.setOnClickListener {
            val studentUserName = studentUsernameInput.text.toString()
            if (studentUserName.isNotBlank()) {
                addStudentToCourse(studentUserName, showMessage, studentUsernameInput)
            } else {
                showMessage(getString(R.string.enter_username), false)
            }
        }

        getStudentList()
    }

    private fun getStudentList() {
        lifecycleScope.launch {
            val studentsInCourse = courseService.getRegisteredStudents(courseId)
            students.clear()
            students.addAll(studentsInCourse)
            studentAdapter.notifyDataSetChanged()
        }
    }

    private fun addStudentToCourse(studentUserName: String, showMessage: TextView, studentUsernameInput: EditText) {
        lifecycleScope.launch {
            val response = courseService.registerStudentToCourse(courseId, studentUserName)
            val success = response.contains("successfully")

            showMessage(response, success)

            if (success) {
                val newStudent = StudentModel(studentId = 0, userName = studentUserName, name = studentUserName)
                studentAdapter.addStudent(newStudent)
                studentUsernameInput.text.clear()
            }
        }
    }

    private fun showRemoveStudentDialog(studentUserName: String) {
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.remove_student_title))
            .setMessage(getString(R.string.remove_student_message, studentUserName))
            .setPositiveButton(getString(R.string.yes)) { _, _ ->
                removeStudentFromCourse(studentUserName)
            }
            .setNegativeButton(getString(R.string.cancel), null)
            .show()
    }

    private fun removeStudentFromCourse(studentUserName: String) {
        lifecycleScope.launch {
            val response = courseService.removeStudentFromCourse(courseId, studentUserName)
            val success = response.contains("removed successfully")

            showMessage(response, success)

            if (success) {
                studentAdapter.removeStudent(studentUserName)
            }
        }
    }

    private fun showMessage(message: String, success: Boolean) {
        val showMessage = view?.findViewById<TextView>(R.id.notifyUserTextView)
        showMessage?.visibility = View.VISIBLE
        showMessage?.setTextColor(if (success) Color.parseColor("#023020") else Color.RED)
        showMessage?.text = message
        showMessage?.postDelayed({ showMessage.visibility = View.GONE }, 5000)
    }
}