package hbv601g.learningsquare

import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import hbv601g.learningsquare.ui.InstructorDashboardFragment
import hbv601g.learningsquare.ui.InstructorNavBarFragment
import hbv601g.learningsquare.ui.StudentDashboardFragment
import hbv601g.learningsquare.ui.StudentNavBarFragment
import hbv601g.learningsquare.ui.utils.Notification

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Notification.createNotificationChannel(this)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS)
                != android.content.pm.PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(
                    arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                    1001
                )
            }
        }
    }

    enum class DashboardType {
        INSTRUCTOR, STUDENT
    }

    fun showDashboardAndNavBar(type: DashboardType) {
        findViewById<FrameLayout>(R.id.navBarContainer).visibility = View.VISIBLE

        val navBarFragment = when (type) {
            DashboardType.INSTRUCTOR -> InstructorNavBarFragment()
            DashboardType.STUDENT -> StudentNavBarFragment()
        }

        supportFragmentManager.beginTransaction()
            .replace(R.id.navBarContainer, navBarFragment)
            .commit()

        val dashboardFragment = when (type) {
            DashboardType.INSTRUCTOR -> InstructorDashboardFragment()
            DashboardType.STUDENT -> StudentDashboardFragment()
        }
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container_view, dashboardFragment)
            .commit()
    }
}