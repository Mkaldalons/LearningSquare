package hbv601g.learningsquare.ui

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import hbv601g.learningsquare.R
import hbv601g.learningsquare.models.AssignmentModel
import hbv601g.learningsquare.services.AssignmentService
import hbv601g.learningsquare.services.HttpsService
import hbv601g.learningsquare.services.StudentService
import hbv601g.learningsquare.ui.assignments.AssignmentAdapter
import kotlinx.coroutines.launch

class StudentCourseFragment : Fragment(R.layout.student_course_layout) {
    private lateinit var recyclerView: RecyclerView
    private lateinit var assignmentAdapter: AssignmentAdapter
    private lateinit var noAssignmentFoundMessage: TextView
    private val assignments = mutableListOf<AssignmentModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        noAssignmentFoundMessage = view.findViewById(R.id.noAssignmentsFoundForThisCourseTextView)
        recyclerView = view.findViewById(R.id.recyclerViewAssignments)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val courseId = arguments?.getInt("courseId") ?: -1

        val sharedPref = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val loggedInUser = sharedPref.getString("loggedInUser", null)

        if (loggedInUser != null) {
            val httpsService = HttpsService()

            assignmentAdapter = AssignmentAdapter(
                assignments,
                onViewAssignmentClick = { selectedAssignment ->
                    val bundle = Bundle().apply {
                        selectedAssignment.assignmentId?.let { putInt("assignmentId", it) }
                    }
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container_view, SubmitAssignmentFragment().apply { arguments = bundle })
                        .addToBackStack(null)
                        .commit()
                },
                httpsService = httpsService,
                isInstructor = false // This is the student view
            )

            recyclerView.adapter = assignmentAdapter

            loadAssignments(courseId, loggedInUser, httpsService)
        } else {
            Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadAssignments(courseId: Int, userName: String, httpsService: HttpsService) {
        if (courseId == -1) {
            Toast.makeText(requireContext(), "Error: No courseID found", Toast.LENGTH_SHORT).show()
            assignments.clear()
            assignmentAdapter.notifyDataSetChanged()
            return
        }

        lifecycleScope.launch {
            val assignmentService = AssignmentService(httpsService)
            val studentService = StudentService(httpsService)
            val assignmentList = assignmentService.getPublishedAssignmentsForStudent(courseId)

            val previousAssignmentListSize = assignments.size

            if (assignmentList.isNotEmpty()) {
                noAssignmentFoundMessage.visibility = View.GONE
                assignments.clear()
                assignments.addAll(assignmentList)
                assignmentAdapter.notifyItemRangeRemoved(0, previousAssignmentListSize)
                assignmentAdapter.notifyItemRangeInserted(0, assignments.size)

                for (i in assignments.indices) {
                    val grade = studentService.getAssignmentGrade(assignments[i].assignmentId!!, userName)
                    if (grade != null) {
                        assignments[i] = assignments[i].copy(grade = grade)
                        assignmentAdapter.notifyItemChanged(i)
                    }
                }
            } else {
                assignments.clear()
                assignmentAdapter.notifyDataSetChanged()
                val errorText = "No Published Assignments to show for course with ID: $courseId"
                noAssignmentFoundMessage.visibility = View.VISIBLE
                noAssignmentFoundMessage.text = errorText
            }
        }
    }
}