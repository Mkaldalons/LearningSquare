package hbv601g.learningsquare.ui

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import hbv601g.learningsquare.R
import hbv601g.learningsquare.ui.assignments.AssignmentFragment
import hbv601g.learningsquare.ui.courses.CourseFragment
import hbv601g.learningsquare.ui.user_profile.MyInfoFragment

class InstructorNavBarFragment : Fragment(R.layout.fragment_instructor_navbar) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val buttonCourses = view.findViewById<Button>(R.id.buttonCourses)
        val buttonAssignments = view.findViewById<Button>(R.id.buttonAssignments)
        val buttonMyInfo = view.findViewById<Button>(R.id.buttonMyInfo)
        val buttonDashboard = view.findViewById<Button>(R.id.buttonDashboard)

        buttonDashboard.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container_view, InstructorDashboardFragment())
                .addToBackStack(null)
                .commit()
        }

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

        buttonMyInfo.setOnClickListener{
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container_view, MyInfoFragment())
                .addToBackStack(null)
                .commit()
        }
    }
}