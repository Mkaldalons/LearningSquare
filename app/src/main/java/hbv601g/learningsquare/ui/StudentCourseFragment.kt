package hbv601g.learningsquare.ui

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import hbv601g.learningsquare.R
import hbv601g.learningsquare.models.AssignmentModel
import hbv601g.learningsquare.services.AssignmentService
import hbv601g.learningsquare.services.HttpsService
import hbv601g.learningsquare.services.StudentService
import hbv601g.learningsquare.storage.AppDatabase
import hbv601g.learningsquare.storage.User
import hbv601g.learningsquare.ui.assignments.AssignmentAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class StudentCourseFragment : Fragment(R.layout.student_course_layout) {
    private lateinit var recyclerView: RecyclerView
    private lateinit var assignmentAdapter: AssignmentAdapter
    private lateinit var emptyState: ConstraintLayout
    private lateinit var nothingFound: TextView
    private val assignments = mutableListOf<AssignmentModel>()

    private lateinit var db: AppDatabase
    private lateinit var userList: List<User>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        emptyState = view.findViewById(R.id.emptyState)
        nothingFound = view.findViewById(R.id.emptyStateMessage)
        recyclerView = view.findViewById(R.id.recyclerViewAssignments)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val courseId = arguments?.getInt("courseId") ?: -1

        lifecycleScope.launch {
            withContext(Dispatchers.IO)
            {
                db = AppDatabase.getDatabase(requireContext())
                userList = db.userDao().getAll()
            }


            if (userList[0].userName.isNotEmpty()) {
                val httpsService = HttpsService()

                assignmentAdapter = AssignmentAdapter(
                    assignments,
                    onViewAssignmentClick = { selectedAssignment ->
                        val bundle = Bundle().apply {
                            selectedAssignment.assignmentId?.let { putInt("assignmentId", it) }
                        }
                        parentFragmentManager.beginTransaction()
                            .replace(
                                R.id.fragment_container_view,
                                SubmitAssignmentFragment().apply { arguments = bundle })
                            .addToBackStack(null)
                            .commit()
                    },
                    httpsService = httpsService,
                    isInstructor = false
                )
                recyclerView.adapter = assignmentAdapter

                loadAssignments(courseId, httpsService)
            } else {
                Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadAssignments(courseId: Int, httpsService: HttpsService) {
        if (courseId == -1) {
            Toast.makeText(requireContext(), "Error: No course ID found", Toast.LENGTH_SHORT).show()
            assignments.clear()
            assignmentAdapter.notifyItemRangeRemoved(0, assignments.size)
            return
        }

        lifecycleScope.launch {
            val assignmentService = AssignmentService(httpsService)
            val studentService = StudentService(httpsService)
            val assignmentList = assignmentService.getPublishedAssignmentsForStudent(courseId)

            val previousAssignmentListSize = assignments.size

            if (assignmentList.isNotEmpty()) {
                emptyState.visibility = View.GONE
                assignments.clear()
                assignments.addAll(assignmentList)
                assignmentAdapter.notifyItemRangeRemoved(0, previousAssignmentListSize)
                assignmentAdapter.notifyItemRangeInserted(0, assignments.size)

                for (i in assignments.indices) {
                    val grade = studentService.getAssignmentGrade(assignments[i].assignmentId!!, userList[0].userName)
                    if (grade != null) {
                        assignments[i] = assignments[i].copy(grade = grade)
                        assignmentAdapter.notifyItemChanged(i)
                    }
                }
            } else {
                assignments.clear()
                assignmentAdapter.notifyItemRangeRemoved(0, assignments.size)
                val errorText = "No Published Assignments to show for this course"
                nothingFound.text = errorText
                emptyState.visibility = View.VISIBLE
            }
        }
    }
}