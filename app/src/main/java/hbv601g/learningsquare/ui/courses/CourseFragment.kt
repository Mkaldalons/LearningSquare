package hbv601g.learningsquare.ui.courses

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import hbv601g.learningsquare.R
import hbv601g.learningsquare.models.CourseModel
import hbv601g.learningsquare.services.HttpsService
import kotlinx.coroutines.launch
import hbv601g.learningsquare.storage.AppDatabase
import hbv601g.learningsquare.storage.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CourseFragment : Fragment(R.layout.fragment_course) {
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
                .replace(R.id.fragment_container_view, CourseDetailsFragment().apply { arguments = bundle })
                .addToBackStack(null)
                .commit()
        }

        recyclerView.adapter = courseAdapter

        loadCourses()

        val buttonCreateCourse = view.findViewById<Button>(R.id.buttonCreateCourse)
        buttonCreateCourse.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container_view, CreateCourseFragment())
                .addToBackStack(null)
                .commit()
        }
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
                val oldSize = courses.size
                courses.clear()
                courses.addAll(coursesList)
                courseAdapter.notifyItemRangeRemoved(0, oldSize)
                courseAdapter.notifyItemRangeInserted(0, coursesList.size)
            } else {
                val emptyStateMessage = "No courses found for instructor ${userList[0].name}"
                nothingFound.text = emptyStateMessage
                emptyState.visibility = View.VISIBLE
            }
        }
    }
}
