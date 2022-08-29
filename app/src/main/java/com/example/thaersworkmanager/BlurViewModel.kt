package com.example.thaersworkmanager

import android.app.Application
import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class BlurViewModel @Inject constructor(
    application: Application,
    private val workManager: WorkManager
) : ViewModel() {

    //private val workManager = WorkManager.getInstance(application)

    internal var imageUri: Uri? = null
    internal var outputUri: Uri? = null

    init {
        imageUri = getImageUri(application.applicationContext)
    }

    private fun createInputDataForUri(): Data {
        val builder = Data.Builder()
        imageUri?.let {
            builder.putString(KEY_IMAGE_URI, imageUri.toString())
        }
        builder.putString("ss", imageUri.toString())
        return builder.build()
    }

    /**
     * Create the WorkRequest to apply the blur and save the resulting image
     * @param blurLevel The amount to blur the image
     */
    internal fun applyBlur(lifecycleOwner: LifecycleOwner, blurLevel: Int) {
        val workRequest =
            OneTimeWorkRequestBuilder<BlurWorker>().setInputData(createInputDataForUri()).build()
        workManager.enqueue(workRequest)
        workManager.getWorkInfoByIdLiveData(workRequest.id).observe(lifecycleOwner, Observer { workInfo ->

        })
    }

    private fun uriOrNull(uriString: String?): Uri? {
        return if (!uriString.isNullOrEmpty()) {
            Uri.parse(uriString)
        } else {
            null
        }
    }

    private fun getImageUri(context: Context): Uri {
        val resources = context.resources

        val imageUri = Uri.Builder()
            .scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
//            .authority(resources.getResourcePackageName(R.drawable.android_cupcake))
//            .appendPath(resources.getResourceTypeName(R.drawable.android_cupcake))
//            .appendPath(resources.getResourceEntryName(R.drawable.android_cupcake))
            .build()

        return imageUri
    }

    internal fun setOutputUri(outputImageUri: String?) {
        outputUri = uriOrNull(outputImageUri)
    }
}