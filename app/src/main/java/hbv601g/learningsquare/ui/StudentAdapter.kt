package hbv601g.learningsquare.ui

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import hbv601g.learningsquare.R
import hbv601g.learningsquare.models.StudentModel
import java.util.Locale

class StudentAdapter(private val students: List<StudentModel>) : RecyclerView.Adapter<StudentAdapter.StudentViewHolder>() {

    class StudentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val studentName: TextView = itemView.findViewById(R.id.studentUserName)
        val averageGrade: TextView = itemView.findViewById(R.id.studentAverageGrade)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_student, parent, false)
        return StudentViewHolder(view)
    }

    override fun onBindViewHolder(holder: StudentViewHolder, position: Int) {
        val student = students[position]
        holder.studentName.text = student.userName

        val avgGrade = student.averageGrade
        holder.averageGrade.text = if (avgGrade != null) {
            val formatted = String.format(Locale.US, "%.1f", avgGrade)
            holder.averageGrade.setTextColor(getColorForGrade(avgGrade))
            "Average Grade: $formatted"
        } else {
            holder.averageGrade.setTextColor(Color.GRAY)
            "Average Grade: N/A"
        }
    }

    override fun getItemCount(): Int = students.size
}

private fun getColorForGrade(grade: Double): Int {
    return when {
        grade >= 9 -> Color.parseColor("#2E7D32") // Green
        grade >= 7 -> Color.parseColor("#F9A825") // Yellow
        grade >= 5 -> Color.parseColor("#EF6C00") // Orange
        else -> Color.parseColor("#B71C1C")       // Red
    }
}