package hbv601g.learningsquare.ui

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
import hbv601g.learningsquare.ui.assignments.AssignmentAdapter
import hbv601g.learningsquare.ui.assignments.AssignmentDetailsFragment
import kotlinx.coroutines.launch

class StudentCourseFragment : Fragment(R.layout.student_course_layout){
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

        assignmentAdapter = AssignmentAdapter(assignments) { selectedAssignment ->
            val bundle = Bundle().apply {
                selectedAssignment.assignmentId?.let { putInt("assignmentId", it) }
            }
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container_view, AssignmentDetailsFragment().apply { arguments = bundle })
                .addToBackStack(null)
                .commit()
        }

        recyclerView.adapter = assignmentAdapter

        loadAssignments(courseId)
    }

    private fun loadAssignments(courseId: Int)
    {
        if (courseId == -1)
        {
            Toast.makeText(requireContext(), "Error: No courseID found", Toast.LENGTH_SHORT).show()
            assignments.clear()
            assignmentAdapter.notifyDataSetChanged()
            return
        }

        lifecycleScope.launch {
            val httpsService = HttpsService()
            val assignmentService = AssignmentService(httpsService)
            val assignmentList = assignmentService.getAllAssignmentsForCourse(courseId)

            val previousAssignmentListSize = assignments.size

            if (assignmentList.isNotEmpty())
            {
                noAssignmentFoundMessage.visibility = View.GONE
                assignments.clear()
                assignments.addAll(assignmentList)
                assignmentAdapter.notifyItemRangeRemoved(0, previousAssignmentListSize)
                assignmentAdapter.notifyItemRangeInserted(0, assignments.size)
            }
            else
            {
                assignments.clear()
                assignmentAdapter.notifyDataSetChanged() // Viljum mögulega nota DiffUtil hér
                val errorText = "No Assignments to show for course with ID: $courseId"
                noAssignmentFoundMessage.visibility = View.VISIBLE
                noAssignmentFoundMessage.text = errorText
            }
        }

    }
}