package hbv601g.learningsquare.ui.assignments

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import hbv601g.learningsquare.R
import hbv601g.learningsquare.models.AssignmentModel
import hbv601g.learningsquare.services.HttpsService
import hbv601g.learningsquare.services.StudentService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class AssignmentAdapter(
    private val assignments: List<AssignmentModel>,
    private val httpsService: HttpsService,
    private val isInstructor: Boolean,
    private val onViewAssignmentClick: (AssignmentModel) -> Unit
) : RecyclerView.Adapter<AssignmentAdapter.AssignmentViewHolder>() {

    class AssignmentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val assignmentName: TextView = itemView.findViewById(R.id.assignmentName)
        val assignmentDueDate: TextView = itemView.findViewById(R.id.assignmentDueDate)
        val dueAt: TextView = itemView.findViewById(R.id.dueAt)
        val assignmentPublished: TextView = itemView.findViewById(R.id.published)
        val viewAssignmentButton: Button = itemView.findViewById(R.id.viewAssignmentButton)
        val gradeLayout: LinearLayout = itemView.findViewById(R.id.gradeLayout)
        val grade: TextView = itemView.findViewById(R.id.grade)
        val averageGrade: TextView = itemView.findViewById(R.id.averageGrade)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AssignmentViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_assignment, parent, false)
        return AssignmentViewHolder(view)
    }

    override fun onBindViewHolder(holder: AssignmentViewHolder, position: Int) {
        val assignment = assignments[position]

        holder.assignmentName.text = assignment.assignmentName
        holder.assignmentDueDate.text = assignment.dueDate.date.toString()
        holder.dueAt.text = assignment.dueDate.time.toString()


        holder.assignmentPublished.text = if (assignment.published) "Published" else "Not Published"
        val publishedColor = if (assignment.published) {
            android.graphics.Color.parseColor("#2E7D32")
        } else {
            android.graphics.Color.parseColor("#B71C1C")
        }
        holder.assignmentPublished.setTextColor(publishedColor)

        // Student grade
        if (assignment.grade != null) {
            holder.gradeLayout.visibility = View.VISIBLE
            holder.grade.text = assignment.grade.toString()
        } else {
            holder.gradeLayout.visibility = View.GONE
        }

        if (isInstructor && assignment.assignmentId != null) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val studentService = StudentService(httpsService)
                    val avg = studentService.getAssignmentAverageGrade(assignment.assignmentId!!)
                    withContext(Dispatchers.Main) {
                        if (avg != null)
                        {
                            holder.averageGrade.visibility = View.VISIBLE
                            holder.averageGrade.text = holder.itemView.context.getString(R.string.average_grade_label, avg)
                        }
                        else
                        {
                            holder.averageGrade.visibility = View.VISIBLE
                            val noSubmissions = "No submissions"
                            holder.averageGrade.text = noSubmissions
                        }
                    }
                } catch (e: Exception) {
                    Log.e("Adapter", "Error fetching average grade", e)
                    withContext(Dispatchers.Main) {
                        holder.averageGrade.visibility = View.GONE
                    }
                }
            }
        } else {
            holder.averageGrade.visibility = View.GONE
        }

        holder.viewAssignmentButton.setOnClickListener {
            onViewAssignmentClick(assignment)
        }
    }

    override fun getItemCount(): Int = assignments.size
}