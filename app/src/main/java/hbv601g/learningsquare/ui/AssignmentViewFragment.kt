package hbv601g.learningsquare.ui

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import hbv601g.learningsquare.R
import hbv601g.learningsquare.models.AssignmentModel
import hbv601g.learningsquare.services.AssignmentService
import hbv601g.learningsquare.services.HttpsService
import kotlinx.coroutines.launch

class AssignmentViewFragment :Fragment(R.layout.fragment_assignmentview){
    private lateinit var assignmentService: AssignmentService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        assignmentService = AssignmentService(HttpsService())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            val assignments: List<AssignmentModel>? = assignmentService.getAllAssignments()
            if (assignments != null) {
                Toast.makeText(requireContext(), "Fann ${assignments.size} assignments", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Engin assignments fundust", Toast.LENGTH_SHORT).show()
            }
        }
    }
}