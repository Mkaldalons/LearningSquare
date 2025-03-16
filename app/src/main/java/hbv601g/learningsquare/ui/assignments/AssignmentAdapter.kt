package hbv601g.learningsquare.ui.assignments

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import hbv601g.learningsquare.R
import hbv601g.learningsquare.models.AssignmentModel
import hbv601g.learningsquare.models.CourseModel

class AssignmentAdapter(private val assignments: List<AssignmentModel>, private val onViewAssignmentClick: (AssignmentModel) -> Unit) : RecyclerView.Adapter<AssignmentAdapter.AssignmentViewHolder>() {

    class AssignmentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val assignmentName: TextView = itemView.findViewById(R.id.assignmentName)
        val assignmentDueDate: TextView = itemView.findViewById(R.id.assignmentDueDate)
        val assignmentPublished: TextView = itemView.findViewById(R.id.published)
        val viewAssignmentButton: Button = itemView.findViewById(R.id.viewAssignmentButton)
    }
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AssignmentViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_assignment, parent, false)
            return AssignmentViewHolder(view)
        }

        override fun onBindViewHolder(holder: AssignmentViewHolder, position: Int)
        {
            val assignment = assignments[position]
            holder.assignmentName.text = assignment.assignmentName
            holder.assignmentDueDate.text = assignment.dueDate.toString()
            holder.assignmentPublished.text = assignment.published.toString()

            holder.viewAssignmentButton.setOnClickListener {
                onViewAssignmentClick(assignment)
            }
        }

        override fun getItemCount(): Int = assignments.size
    }