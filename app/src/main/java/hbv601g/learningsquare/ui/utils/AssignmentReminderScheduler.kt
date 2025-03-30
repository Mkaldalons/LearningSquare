package hbv601g.learningsquare.ui.utils

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import java.time.Duration
import java.time.LocalDateTime

object AssignmentReminderScheduler {

    @RequiresApi(Build.VERSION_CODES.O)
    fun scheduleReminder(context: Context, deadline: String, assignmentName: String) {
        val isoDate = deadline.replace(" ", "T")
        val dateTimeIso = LocalDateTime.parse(isoDate)
        val reminderTime = dateTimeIso.minusHours(24)
        val durationUntilReminder = Duration.between(LocalDateTime.now(), reminderTime).seconds

        if (durationUntilReminder <= 0) {
            return
        }

        val data = workDataOf("assignmentName" to assignmentName)

        val reminderRequest = OneTimeWorkRequestBuilder<AssignmentReminderWorker>()
            .setInitialDelay(durationUntilReminder, java.util.concurrent.TimeUnit.SECONDS)
            .setInputData(data)
            .build()

        WorkManager.getInstance(context).enqueue(reminderRequest)
    }
}