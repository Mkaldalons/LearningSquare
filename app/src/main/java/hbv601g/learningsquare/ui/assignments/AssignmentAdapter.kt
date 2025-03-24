package hbv601g.learningsquare.ui.assignments

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import hbv601g.learningsquare.R
import hbv601g.learningsquare.models.AssignmentModel


class AssignmentAdapter(private val assignments: List<AssignmentModel>, private val onViewAssignmentClick: (AssignmentModel) -> Unit) : RecyclerView.Adapter<AssignmentAdapter.AssignmentViewHolder>() {

    class AssignmentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val assignmentName: TextView = itemView.findViewById(R.id.assignmentName)
        val assignmentDueDate: TextView = itemView.findViewById(R.id.assignmentDueDate)
        val assignmentPublished: TextView = itemView.findViewById(R.id.published)
        val viewAssignmentButton: Button = itemView.findViewById(R.id.viewAssignmentButton)
        var gradeLayout: LinearLayout = itemView.findViewById(R.id.gradeLayout) // The layout for the label and the grade (currently set to hidden)
        val grade: TextView = itemView.findViewById(R.id.grade)

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
            if (assignment.grade != null) {
                holder.gradeLayout.visibility = View.VISIBLE
                holder.grade.text = assignment.grade.toString()
            } else {
                holder.gradeLayout.visibility = View.GONE
            }

            holder.viewAssignmentButton.setOnClickListener {
                onViewAssignmentClick(assignment)
            }
        }

        override fun getItemCount(): Int = assignments.size
    }