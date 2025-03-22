package hbv601g.learningsquare.ui

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import hbv601g.learningsquare.R
import hbv601g.learningsquare.models.CourseModel
import hbv601g.learningsquare.services.HttpsService
import hbv601g.learningsquare.ui.courses.CourseAdapter
import kotlinx.coroutines.launch

class StudentDashboardFragment : Fragment(R.layout.fragment_student_dashboard) {
    private lateinit var recyclerView: RecyclerView
    private lateinit var courseAdapter: CourseAdapter
    private val courses = mutableListOf<CourseModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerViewCourses)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        courseAdapter = CourseAdapter(courses) { selectedCourse ->
            val bundle = Bundle().apply {
                putInt("courseId", selectedCourse.courseId)
            }
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container_view, StudentCourseFragment().apply { arguments = bundle })
                .addToBackStack(null)
                .commit()
        }

        recyclerView.adapter = courseAdapter

        loadCourses()

    }

    private fun loadCourses() {
        val sharedPref = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val loggedInUser = sharedPref.getString("loggedInUser", null)

        if (loggedInUser == null) {
            Toast.makeText(requireContext(), "Error: No logged-in user found", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            val httpsService = HttpsService()
            val coursesList = httpsService.getCourses(loggedInUser)

            if (coursesList.isNotEmpty()) {
                courses.clear()
                courses.addAll(coursesList)
                courseAdapter.notifyDataSetChanged()
            } else {
                Toast.makeText(requireContext(), "No courses found for this user", Toast.LENGTH_SHORT).show()
            }
        }
    }
}