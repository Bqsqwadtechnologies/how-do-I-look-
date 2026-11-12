package com.bqsqwad.howdoilook.notifications

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

class StyleReminderWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {
    override suspend fun doWork(): Result {
        val shown = HowDoILookNotifications.showReminder(
            applicationContext,
            "A little wardrobe check-in",
            "Review an outfit plan, care task or packing list in How Do I Look?"
        )
        return if (shown) Result.success() else Result.success()
    }
}
