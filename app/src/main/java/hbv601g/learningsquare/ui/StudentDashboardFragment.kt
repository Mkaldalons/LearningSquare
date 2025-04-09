package hbv601g.learningsquare.ui

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import hbv601g.learningsquare.R
import hbv601g.learningsquare.models.CourseModel
import hbv601g.learningsquare.services.HttpsService
import hbv601g.learningsquare.storage.AppDatabase
import hbv601g.learningsquare.storage.User
import hbv601g.learningsquare.ui.courses.CourseAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class StudentDashboardFragment : Fragment(R.layout.fragment_student_dashboard) {
    private lateinit var recyclerView: RecyclerView
    private lateinit var courseAdapter: CourseAdapter
    private lateinit var emptyState: ConstraintLayout
    private lateinit var nothingFound: TextView

    private val courses = mutableListOf<CourseModel>()
    private lateinit var db: AppDatabase
    private lateinit var userList: List<User>


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerViewCourses)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        emptyState = view.findViewById(R.id.emptyState)
        nothingFound = view.findViewById(R.id.emptyStateMessage)

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
                courseAdapter.notifyItemRangeChanged(0, courses.size)
            } else {
                val nothingFoundText = "You are currently not registered in any courses, ${userList[0].name}! " +
                        "Please speak with your instructor to be registered."
                nothingFound.text = nothingFoundText
                emptyState.visibility = View.VISIBLE
            }
        }
    }
}