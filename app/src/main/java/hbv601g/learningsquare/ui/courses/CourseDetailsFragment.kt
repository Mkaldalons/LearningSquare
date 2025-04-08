package hbv601g.learningsquare.ui.courses

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
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
import androidx.core.graphics.toColorInt
import androidx.room.util.convertByteToUUID

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
        val editCourseName = view.findViewById<EditText>(R.id.courseNameEditText)
        val editCourseDescription = view.findViewById<EditText>(R.id.courseDescriptionEditText)
        val editCourseButton = view.findViewById<Button>(R.id.editCourseButton)

        populateCourseDetails(courseId, editCourseName, editCourseDescription)

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
                        showMessage.setTextColor("#023020".toColorInt())
                        showMessage.text = responseString
                        showMessage.postDelayed({
                            showMessage.visibility = View.GONE
                        }, 5_000)
                        studentUsernameInput.text.clear()
                        getStudentList(courseId)
                    }
                    catch (_: Exception)
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
        editCourseButton.setOnClickListener {
            var nameSuccess = false
            var descriptionSuccess = false

            lifecycleScope.launch {
                val httpsService = HttpsService()
                val courseService = CourseService(httpsService)

                val course = courseService.getCourse(courseId)
                val nameValue = editCourseName.text.toString()
                val descriptionValue = editCourseDescription.text.toString()
                if ( nameValue.isNotBlank() && (nameValue != course?.courseName) )
                {
                    nameSuccess = courseService.editCourseName(courseId, nameValue)
                }
                if (descriptionValue.isNotBlank() && (descriptionValue != course?.description) )
                {
                    descriptionSuccess = courseService.editCourseDescription(courseId, descriptionValue)
                }
                if (nameValue.isBlank() || descriptionValue.isBlank())
                {
                    Toast.makeText(requireContext(), "Fields cannot be blank", Toast.LENGTH_SHORT).show()
                }
                if(nameSuccess || descriptionSuccess)
                {
                    Toast.makeText(requireContext(), "Course details modified", Toast.LENGTH_SHORT).show()
                }
                else
                {
                    Toast.makeText(requireContext(), "Could not edit details", Toast.LENGTH_SHORT).show()
                }
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
            studentAdapter.notifyItemRangeChanged(0, students.size)
        }
    }

    private fun populateCourseDetails(courseId: Int, courseName: EditText, courseDescription: EditText)
    {
        lifecycleScope.launch {
            val httpsService = HttpsService()
            val courseService = CourseService(httpsService)

            val course = courseService.getCourse(courseId)
            if (course != null)
            {
                courseName.setText(course.courseName)
                courseDescription.setText(course.description)
            }
            else
            {
                Toast.makeText(requireContext(), "Could not find course for ID: $courseId", Toast.LENGTH_SHORT).show()
            }
        }
    }
}