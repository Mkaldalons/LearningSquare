package hbv601g.learningsquare.ui

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import hbv601g.learningsquare.R
import hbv601g.learningsquare.ui.user_profile.MyInfoFragment

class StudentNavBarFragment : Fragment(R.layout.fragment_student_navbar) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val buttonMyInfo = view.findViewById<Button>(R.id.myInfoButton)
        val buttonDashboard = view.findViewById<Button>(R.id.buttonDashboard)

        buttonDashboard.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container_view, StudentDashboardFragment())
                .addToBackStack(null)
                .commit()
        }

        buttonMyInfo.setOnClickListener{
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container_view, MyInfoFragment())
                .addToBackStack(null)
                .commit()
        }
    }
}