package hbv601g.learningsquare.ui.courses

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import hbv601g.learningsquare.R
import hbv601g.learningsquare.models.CourseModel

class CourseAdapter(private val courses: List<CourseModel>, private val onViewCourseClick: (CourseModel) -> Unit) : RecyclerView.Adapter<CourseAdapter.CourseViewHolder>() {

    class CourseViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val courseName: TextView = view.findViewById(R.id.courseName)
        val courseDescription: TextView = view.findViewById(R.id.courseDescription)
        val courseId: TextView = view.findViewById(R.id.courseId)
        val viewCourseButton: Button = view.findViewById(R.id.viewCoursesButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_course, parent, false)
        return CourseViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: CourseViewHolder, position: Int) {
        val course = courses[position]
        holder.courseName.text = course.courseName
        holder.courseDescription.text = course.description
        holder.courseId.text = "ID: ${course.courseId}"

        holder.viewCourseButton.setOnClickListener{
            onViewCourseClick(course)
        }
    }

    override fun getItemCount() = courses.size
}
