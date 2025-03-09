package hbv601g.learningsquare.ui

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
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.client.call.body
import android.content.Context

class CourseFragment : Fragment(R.layout.fragment_course) {
    private lateinit var recyclerView: RecyclerView
    private lateinit var courseAdapter: CourseAdapter
    private val courses = mutableListOf<CourseModel>() // List to store courses

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerViewCourses)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Initialize Adapter
        courseAdapter = CourseAdapter(courses)
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
    }

    private fun loadCourses() {
        val sharedPref = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val loggedInInstructor = sharedPref.getString("loggedInInstructor", null)

        if (loggedInInstructor == null) {
            Toast.makeText(requireContext(), "Error: No logged-in user found", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            val httpsService = HttpsService()
            val coursesList = httpsService.getCourses(loggedInInstructor) // ✅ Fetching as a List<CourseModel>

            if (coursesList.isNotEmpty()) {  // ✅ Now `isNotEmpty()` will work
                courses.clear()
                courses.addAll(coursesList)
                courseAdapter.notifyDataSetChanged()
            } else {
                Toast.makeText(requireContext(), "No courses found for this instructor", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
