package hbv601g.learningsquare.ui.utils

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.concurrent.TimeUnit



object AssignmentReminderScheduler {
    @RequiresApi(Build.VERSION_CODES.O)
    fun scheduleReminder(context: Context, deadline: LocalDateTime, assignmentName: String) {
        val now = LocalDateTime.now()
        val triggerAtMillis = ChronoUnit.MILLIS.between(now, deadline.minusHours(24))

        if (triggerAtMillis <= 0) return

        val data = workDataOf("assignmentName" to assignmentName)

        val request = OneTimeWorkRequestBuilder<AssignmentReminderWorker>()
            .setInitialDelay(triggerAtMillis, TimeUnit.MILLISECONDS)
            .setInputData(data)
            .build()

        WorkManager.getInstance(context).enqueue(request)
    }
}