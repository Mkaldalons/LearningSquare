package hbv601g.learningsquare.ui.utils

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters

class AssignmentReminderWorker(
    context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {
    override fun doWork(): Result {
        val assignmentName = inputData.getString("assignmentName") ?: return Result.failure()

        Notification.showNotification(
            applicationContext,
            "Assignment Due Soon",
            "Your assignment \"$assignmentName\" is due in 24 hours!"
        )

        return Result.success()
    }
}