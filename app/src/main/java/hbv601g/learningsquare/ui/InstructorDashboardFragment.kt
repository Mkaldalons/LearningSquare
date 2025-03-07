package hbv601g.learningsquare.ui


import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import hbv601g.learningsquare.R
import android.widget.Button

class InstructorDashboardFragment : Fragment(R.layout.fragment_instructor_layout) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val buttonCourses = view.findViewById<Button>(R.id.buttonCourses)
        buttonCourses.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container_view, CoursesFragment())
                .addToBackStack(null)
                .commit()
        }
    }
}