package hbv601g.learningsquare.ui


import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import hbv601g.learningsquare.R
import android.widget.Button
import hbv601g.learningsquare.ui.assignments.AssignmentFragment
import hbv601g.learningsquare.ui.courses.CourseFragment

class InstructorDashboardFragment : Fragment(R.layout.fragment_instructor_layout) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val buttonCourses = view.findViewById<Button>(R.id.buttonCourses)
        val buttonAssignments = view.findViewById<Button>(R.id.buttonAssignments)
        val buttonMyInfoFragment = view.findViewById<Button>(R.id.buttonMyInfo)

        buttonCourses.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container_view, CourseFragment())
                .addToBackStack(null)
                .commit()
        }

        buttonAssignments.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container_view, AssignmentFragment())
                .addToBackStack(null)
                .commit()
        }

        buttonMyInfoFragment.setOnClickListener{
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container_view, MyInfoFragment())
                .addToBackStack(null)
                .commit()
        }
    }
}