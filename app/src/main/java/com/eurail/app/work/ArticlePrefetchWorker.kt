package com.eurail.app.work

import android.content.Context
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.eurail.app.EurailApplication
import com.eurail.app.domain.Result
import java.util.concurrent.TimeUnit

/**
 * Background worker that prefetches article data once per day.
 *
 * Scheduling Strategy:
 * - Runs approximately once every 24 hours (WorkManager's minimum periodic interval is 15 minutes)
 * - Only executes when:
 *   1. Device has network connectivity (CONNECTED, not METERED required)
 *   2. Device battery is not low
 * - Uses KeepExistingPeriodicWork policy to avoid duplicate schedules
 * - Runs as expedited work if possible for faster completion
 *
 * This ensures users have fresh content available when they open the app,
 * while being respectful of battery and data usage.
 */
class ArticlePrefetchWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val app = applicationContext as? EurailApplication ?: return Result.failure()
        val repository = app.articleRepository

        return when (val result = repository.refreshArticles()) {
            is com.eurail.app.domain.Result.Success -> {
                Result.success()
            }
            is com.eurail.app.domain.Result.Error -> {
                if (runAttemptCount < MAX_RETRY_COUNT) {
                    Result.retry()
                } else {
                    Result.failure()
                }
            }
        }
    }

    companion object {
        const val WORK_NAME = "article_prefetch_work"
        private const val MAX_RETRY_COUNT = 3

        fun schedule(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .setRequiresBatteryNotLow(true)
                .build()

            val workRequest = PeriodicWorkRequestBuilder<ArticlePrefetchWorker>(
                repeatInterval = 24,
                repeatIntervalTimeUnit = TimeUnit.HOURS
            )
                .setConstraints(constraints)
                .setInitialDelay(1, TimeUnit.HOURS)
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                workRequest
            )
        }
    }
}
