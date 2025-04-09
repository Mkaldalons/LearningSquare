package hbv601g.learningsquare.ui.assignments

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import hbv601g.learningsquare.R
import hbv601g.learningsquare.models.AssignmentModel
import hbv601g.learningsquare.models.CourseModel
import hbv601g.learningsquare.services.AssignmentService
import hbv601g.learningsquare.services.HttpsService
import hbv601g.learningsquare.storage.AppDatabase
import hbv601g.learningsquare.storage.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AssignmentFragment : Fragment(R.layout.fragment_assignment){
    private lateinit var recyclerView: RecyclerView
    private lateinit var assignmentAdapter: AssignmentAdapter
    private lateinit var emptyState: ConstraintLayout
    private val assignments = mutableListOf<AssignmentModel>()
    private val courses = mutableListOf<CourseModel>()
    private lateinit var nothingFound: TextView
    private var selectedCourseId: Int = -1

    private lateinit var db: AppDatabase
    private lateinit var userList: List<User>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val coursesDropdown = view.findViewById<Spinner>(R.id.coursesDropdown)
        val buttonCreateAssignment = view.findViewById<Button>(R.id.buttonCreateAssignment)
        emptyState = view.findViewById(R.id.emptyState)
        nothingFound = view.findViewById(R.id.emptyStateMessage)
        recyclerView = view.findViewById(R.id.recyclerViewAssignments)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        loadCourses(coursesDropdown)

        coursesDropdown.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (position in courses.indices)
                {
                    val selectedCourse = courses[position]
                    selectedCourseId = selectedCourse.courseId
                    val header = requireView().findViewById<TextView>(R.id.assignmentHeader)
                    header.text = getString(R.string.assignments_for, selectedCourse.courseName)
                }
                loadAssignments(selectedCourseId)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                selectedCourseId = -1
            }
        }

        val httpsService = HttpsService()

        assignmentAdapter = AssignmentAdapter(
            assignments,
            onViewAssignmentClick = { selectedAssignment ->
                val bundle = Bundle().apply {
                    selectedAssignment.assignmentId?.let { putInt("assignmentId", it) }
                }
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container_view, AssignmentDetailsFragment().apply { arguments = bundle })
                    .addToBackStack(null)
                    .commit()
            },
            httpsService = httpsService,
            isInstructor = true
        )

        recyclerView.adapter = assignmentAdapter

        buttonCreateAssignment.setOnClickListener {
            val bundle = Bundle().apply {
                putInt("selectedCourseId", selectedCourseId)
            }
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container_view, CreateAssignmentFragment().apply {
                    arguments = bundle
                })
                .addToBackStack(null)
                .commit()
        }

    }

    private fun loadAssignments(courseId: Int)
    {
        if (courseId == -1)
        {
            Toast.makeText(requireContext(), "Error: No courseID found", Toast.LENGTH_SHORT).show()
            assignments.clear()
            assignmentAdapter.notifyItemRangeChanged(0, assignments.size)
            return
        }

        lifecycleScope.launch {
            val httpsService = HttpsService()
            val assignmentService = AssignmentService(httpsService)
            val assignmentList = assignmentService.getAllAssignmentsForCourse(courseId)

            val previousAssignmentListSize = assignments.size

            if (assignmentList.isNotEmpty())
            {
                emptyState.visibility = View.GONE
                assignments.clear()
                assignments.addAll(assignmentList)
                assignmentAdapter.notifyItemRangeRemoved(0, previousAssignmentListSize)
                assignmentAdapter.notifyItemRangeInserted(0, assignments.size)
            }
            else
            {
                assignments.clear()
                assignmentAdapter.notifyItemRangeChanged(0, assignments.size)
                val emptyStateMessage = "No Assignments found for the selected course"
                nothingFound.text = emptyStateMessage
                emptyState.visibility = View.VISIBLE
            }
        }

    }

    private fun loadCourses(coursesDropdown: Spinner) {
        lifecycleScope.launch {

            withContext(Dispatchers.IO)
            {
                db = AppDatabase.getDatabase(requireContext())
                userList = db.userDao().getAll()
            }

            val httpsService = HttpsService()
            val coursesList = httpsService.getCourses(userList[0].userName)

            if (coursesList.isNotEmpty()) {
                emptyState.visibility = View.GONE
                courses.clear()
                courses.addAll(coursesList)

                val courseNames = coursesList.map { it.courseName }

                val spinnerAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, courseNames)
                spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                coursesDropdown.adapter = spinnerAdapter

            }
            else
            {
                val emptyStateMessage = "No Courses found for instructor ${userList[0].name}"
                nothingFound.text = emptyStateMessage
                emptyState.visibility = View.VISIBLE
            }
        }
    }

}