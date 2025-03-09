package hbv601g.learningsquare.ui

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import hbv601g.learningsquare.R

class InstructorDashboardFragment : Fragment(R.layout.fragment_instructor_layout) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Find buttons in the layout
        val createAssignmentButton = view.findViewById<Button>(R.id.createAssignmentButton)
        val viewAssignmentsButton = view.findViewById<Button>(R.id.viewAssignmentsButton)

        // Navigate to CreateMakeAssignmentFragment when "Create Assignment" is clicked
        createAssignmentButton.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container_view, CreateMakeAssignmentFragment())
                .addToBackStack(null)
                .commit()
        }

        // Navigate to AssignmentViewFragment when "View Assignments" is clicked
        viewAssignmentsButton.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container_view, AssignmentViewFragment())
                .addToBackStack(null)
                .commit()
        }
    }
}
