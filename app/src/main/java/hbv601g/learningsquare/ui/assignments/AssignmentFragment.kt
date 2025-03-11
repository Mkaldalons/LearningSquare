package hbv601g.learningsquare.ui.assignments

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import hbv601g.learningsquare.R
import hbv601g.learningsquare.models.AssignmentModel
import hbv601g.learningsquare.models.CourseModel
import hbv601g.learningsquare.services.AssignmentService
import hbv601g.learningsquare.services.HttpsService
import kotlinx.coroutines.launch

class AssignmentFragment : Fragment(R.layout.fragment_assignment){
    private lateinit var recyclerView: RecyclerView
    private lateinit var assignmentAdapter: AssignmentAdapter
    private val assignments = mutableListOf<AssignmentModel>()
    private val courses = mutableListOf<CourseModel>()
    private var selectedCourseId: Int = -1

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val coursesDropdown = view.findViewById<Spinner>(R.id.coursesDropdown)
        val buttonCreateAssignment = view.findViewById<Button>(R.id.buttonCreateAssignment)
        // noAssignmentsToShowTextView = view.findViewById<TextView>(R.id.noAssignmentsToShow)
        recyclerView = view.findViewById(R.id.recyclerViewAssignments)

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
                }
                //loadAssignments(selectedCourseId) // Þetta virkar ekki atm
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                selectedCourseId = -1
            }
        }

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

        buttonCreateAssignment.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container_view, CreateAssignmentFragment().apply { selectedCourseId })
                .addToBackStack(null)
                .commit()
        }

    }

    private fun loadAssignments(courseId: Int)
    {
        if (courseId == -1)
        {
            Toast.makeText(requireContext(), "Error: No courseID found", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            val httpsService = HttpsService()
            val assignmentService = AssignmentService(httpsService)
            val assignmentList = assignmentService.getAllAssignmentsForCourse(courseId)

            val previousAssignmentListSize = assignments.size

            if (assignmentList.isNotEmpty())
            {
                assignments.clear()
                assignments.addAll(assignmentList)
                assignmentAdapter.notifyItemRangeRemoved(0, previousAssignmentListSize)
                assignmentAdapter.notifyItemRangeInserted(0, assignments.size)
                //assignmentAdapter.notifyDataSetChanged()
                //assignmentAdapter.notifyItemRangeInserted(previousAssignmentListSize, assignments.size)
            }
            else
            {
                Toast.makeText(requireContext(), "No Assignments found for this course", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun loadCourses(coursesDropdown: Spinner) {
        val sharedPref = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val loggedInInstructor = sharedPref.getString("loggedInInstructor", null)

        if (loggedInInstructor == null) {
            Toast.makeText(requireContext(), "Error: No logged-in user found", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            val httpsService = HttpsService()
            val coursesList = httpsService.getCourses(loggedInInstructor)

            if (coursesList.isNotEmpty()) {
                courses.clear()
                courses.addAll(coursesList)

                val courseNames = coursesList.map { it.courseName }

                val spinnerAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, courseNames)
                spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                coursesDropdown.adapter = spinnerAdapter

                coursesDropdown.setSelection(1)
                selectedCourseId = courses[1].courseId

            } else {
                Toast.makeText(requireContext(), "No courses found for this instructor", Toast.LENGTH_SHORT).show()
            }
        }
    }

}