package hbv601g.learningsquare.ui.courses

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import hbv601g.learningsquare.R
import hbv601g.learningsquare.models.StudentModel
import hbv601g.learningsquare.services.CourseService
import hbv601g.learningsquare.services.HttpsService
import hbv601g.learningsquare.services.StudentService
import hbv601g.learningsquare.ui.StudentAdapter
import kotlinx.coroutines.launch

class CourseDetailsFragment : Fragment(R.layout.fragment_course_details) {

    private val students = mutableListOf<StudentModel>()
    private lateinit var studentAdapter: StudentAdapter

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerViewStudents = view.findViewById<RecyclerView>(R.id.recyclerViewStudents)
        recyclerViewStudents.layoutManager = LinearLayoutManager(requireContext())

        studentAdapter = StudentAdapter(students)
        recyclerViewStudents.adapter = studentAdapter

        val courseId = arguments?.getInt("courseId") ?: -1

        getStudentList(courseId)

        val studentUsernameInput = view.findViewById<EditText>(R.id.studentUserNameInput)
        val addStudentButton = view.findViewById<Button>(R.id.addStudentButton)
        val showMessage = view.findViewById<TextView>(R.id.notifyUserTextView)

        var responseString = ""

        addStudentButton.setOnClickListener{
            val studentUserNameInputString = studentUsernameInput.text.toString()

            if(studentUserNameInputString.isNotBlank() && courseId != -1)
            {
                lifecycleScope.launch {
                    val httpsService = HttpsService()
                    val courseService = CourseService(httpsService)

                    try
                    {
                        responseString = courseService.registerStudentToCourse(courseId, studentUserNameInputString)
                        showMessage.visibility = View.VISIBLE
                        showMessage.setTextColor(Color.parseColor("#023020"))
                        showMessage.text = responseString
                        showMessage.postDelayed({
                            showMessage.visibility = View.GONE
                        }, 5_000)
                        studentUsernameInput.text.clear()
                        getStudentList(courseId)
                    }
                    catch (e: Exception)
                    {
                        responseString = "Could not register student to course."
                        showMessage.visibility = View.VISIBLE
                        showMessage.setTextColor(Color.RED)
                        showMessage.text = responseString
                        showMessage.postDelayed({
                            showMessage.visibility = View.GONE
                        }, 10_000)
                        studentUsernameInput.text.clear()
                    }
                }
            }
            else
            {
                responseString = "Please enter a username"
                showMessage.visibility = View.VISIBLE
                showMessage.setTextColor(Color.RED)
                showMessage.text = responseString
                showMessage.postDelayed({
                    showMessage.visibility = View.GONE
                }, 10_000)
                studentUsernameInput.text.clear()
            }
        }
    }

    private fun getStudentList(courseId: Int) {
        lifecycleScope.launch {
            val httpsService = HttpsService()
            val courseService = CourseService(httpsService)

            val studentsInCourse = courseService.getRegisteredStudents(courseId)
            val updatedStudents = mutableListOf<StudentModel>()

            for (student in studentsInCourse) {
                val avg = try {
                    val studentService = StudentService(httpsService)
                    studentService.getStudentAverage(courseId, student.userName)
                } catch (e: Exception) {
                    null
                }
                val studentWithAverage = student.copy(averageGrade = avg)
                updatedStudents.add(studentWithAverage)
            }

            students.clear()
            students.addAll(
                updatedStudents.sortedBy { it.averageGrade ?: Double.MAX_VALUE }
            )
            studentAdapter.notifyDataSetChanged()
        }
    }
}