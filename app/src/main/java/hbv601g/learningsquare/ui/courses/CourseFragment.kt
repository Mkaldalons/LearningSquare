package hbv601g.learningsquare.ui.courses

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import hbv601g.learningsquare.R
import hbv601g.learningsquare.models.CourseModel
import hbv601g.learningsquare.services.HttpsService
import kotlinx.coroutines.launch
import android.content.Context
import hbv601g.learningsquare.ui.assignments.AssignmentFragment
import hbv601g.learningsquare.ui.user_profile.MyInfoFragment

class CourseFragment : Fragment(R.layout.fragment_course) {
    private lateinit var recyclerView: RecyclerView
    private lateinit var courseAdapter: CourseAdapter
    private val courses = mutableListOf<CourseModel>() // List to store courses

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerViewCourses)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        courseAdapter = CourseAdapter(courses) { selectedCourse ->
            val bundle = Bundle().apply {
                putInt("courseId", selectedCourse.courseId)
            }
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container_view, CourseDetailsFragment().apply { arguments = bundle })
                .addToBackStack(null)
                .commit()
        }

        recyclerView.adapter = courseAdapter

        // Load courses from API
        loadCourses()

        // Button to Create a New Course
        val buttonCreateCourse = view.findViewById<Button>(R.id.buttonCreateCourse)
        buttonCreateCourse.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container_view, CreateCourseFragment())
                .addToBackStack(null)
                .commit()
        }

        val buttonAssignments = view.findViewById<Button>(R.id.buttonAssignments)
        val buttonMyInfo = view.findViewById<Button>(R.id.myInfoButton)

        buttonAssignments.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container_view, AssignmentFragment())
                .addToBackStack(null)
                .commit()
        }

        buttonMyInfo.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container_view, MyInfoFragment())
                .addToBackStack(null)
                .commit()
        }
    }

    private fun loadCourses() {
        val sharedPref = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val loggedInInstructor = sharedPref.getString("loggedInUser", null)

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
                courseAdapter.notifyDataSetChanged()
            } else {
                Toast.makeText(requireContext(), "No courses found for this instructor", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
