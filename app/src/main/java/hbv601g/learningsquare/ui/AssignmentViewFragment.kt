package hbv601g.learningsquare.ui

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import hbv601g.learningsquare.R
import hbv601g.learningsquare.models.AssignmentModel
import hbv601g.learningsquare.services.AssignmentService
import hbv601g.learningsquare.services.HttpsService
import kotlinx.coroutines.launch

class AssignmentViewFragment : Fragment(R.layout.fragment_assignmentview) {
    private lateinit var assignmentService: AssignmentService
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        assignmentService = AssignmentService(HttpsService())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = view.findViewById(R.id.assignmentsRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        lifecycleScope.launch {
            val assignments: List<AssignmentModel>? = assignmentService.getAllAssignments()
            if (assignments != null && assignments.isNotEmpty()) {
                recyclerView.adapter = AssignmentAdapter(assignments) { assignment ->
                    // Open the detail fragment when an item is clicked
                    val detailFragment = AssignmentDetailFragment.newInstance(assignment)
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container_view, detailFragment)
                        .addToBackStack(null)
                        .commit()
                }
            } else {
                Toast.makeText(requireContext(), "Engin assignments fundust", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
