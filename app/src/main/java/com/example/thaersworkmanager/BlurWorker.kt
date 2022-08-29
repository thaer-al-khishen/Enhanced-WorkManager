package com.example.thaersworkmanager

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.text.TextUtils
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class BlurWorker  @AssistedInject constructor(
    @Assisted ctx: Context,
    @Assisted params: WorkerParameters
) : CoroutineWorker(
    ctx,
    params
) {
    override suspend fun doWork(): Result {
        val applicationContext = applicationContext

        val resourceUri = inputData.getString(KEY_IMAGE_URI)

//        makeStatusNotification("Sit tight, Image getting blurred", applicationContext)
        return try {
            if (TextUtils.isEmpty(resourceUri)) {
                Log.e("Debug for Uri", "Invalid input uri")
                throw IllegalArgumentException("Invalid input uri")
            }

            val picture =
                BitmapFactory.decodeStream(
                    applicationContext.contentResolver.openInputStream(
                        Uri.parse(
                            resourceUri
                        )
                    )
                )
//            val output = blurBitmap(picture, applicationContext)

//            val blurredImageUri = writeBitmapToFile(applicationContext, output)

            val outputData = workDataOf(KEY_IMAGE_URI to blurredImageUri.toString())

//            makeStatusNotification("$blurredImageUri", applicationContext)
            Result.success(outputData)
        } catch (e: Throwable) {
            Log.e("BlurWorker", "Error blurring the image")

            Result.failure()
        }
    }
}