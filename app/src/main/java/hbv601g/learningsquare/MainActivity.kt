package hbv601g.learningsquare

import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import hbv601g.learningsquare.ui.utils.Notification

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Create channel
        Notification.createNotificationChannel(this)

        // Request permission on Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS)
                != android.content.pm.PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(
                    arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                    1001 // requestCode, any number you like
                )
            }
        }
    }

}