package hbv601g.learningsquare.ui

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import hbv601g.learningsquare.R
import hbv601g.learningsquare.models.AssignmentModel
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class AssignmentDetailFragment : Fragment(R.layout.fragment_assignment_detail) {

    companion object {
        private const val ARG_ASSIGNMENT = "assignment"

        fun newInstance(assignment: AssignmentModel): AssignmentDetailFragment {
            val fragment = AssignmentDetailFragment()
            val bundle = Bundle()
            val jsonString = Json.encodeToString(assignment)
            bundle.putString(ARG_ASSIGNMENT, jsonString)
            fragment.arguments = bundle
            return fragment
        }
    }

    private lateinit var assignment: AssignmentModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.getString(ARG_ASSIGNMENT)?.let {
            assignment = Json.decodeFromString(it)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Bind assignment details to TextViews in the layout
        view.findViewById<TextView>(R.id.assignmentTitleTextView)?.text = assignment.title
        view.findViewById<TextView>(R.id.assignmentDueDateTextView)?.text = "Due: ${assignment.dueDate}"
        view.findViewById<TextView>(R.id.assignmentJsonDataTextView)?.text = "Questions: ${assignment.jsonData}"
    }
}
