package hbv601g.learningsquare.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import hbv601g.learningsquare.R
import hbv601g.learningsquare.models.StudentModel

class StudentAdapter(
    private val students: MutableList<StudentModel>,
    private val onRemoveStudent: (String) -> Unit
) : RecyclerView.Adapter<StudentAdapter.StudentViewHolder>() {


    inner class StudentViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val studentName: TextView = view.findViewById(R.id.studentUserName)
        val removeButton: Button = view.findViewById(R.id.removeStudentButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_student, parent, false)
        return StudentViewHolder(view)
    }

    override fun onBindViewHolder(holder: StudentViewHolder, position: Int) {
        val student = students[position]
        holder.studentName.text = student.userName

        holder.removeButton.text = holder.itemView.context.getString(R.string.remove)

        holder.removeButton.setOnClickListener {
            onRemoveStudent(student.userName)
        }
    }

    override fun getItemCount() = students.size

    fun removeStudent(userName: String) {
        val index = students.indexOfFirst { it.userName == userName }
        if (index != -1) {
            students.removeAt(index)
            notifyItemRemoved(index)
        }
    }

    fun addStudent(student: StudentModel) {
        students.add(student)
        notifyItemInserted(students.size - 1)
    }
}