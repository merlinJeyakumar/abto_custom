package com.support.utills.retrofit

import okhttp3.RequestBody
import io.reactivex.FlowableEmitter
import okhttp3.MediaType
import java.io.File
import okhttp3.MultipartBody




object RetrofitUtill {


    private fun createCountingRequestBody(
        file: File,
        emitter: FlowableEmitter<Double>
    ): RequestBody {
        val requestBody = createRequestForImage(file)
        return CountingRequestBody(requestBody, object : CountingRequestBody.Listener {
            override fun onRequestProgress(bytesWritten: Long, contentLength: Long) {
                val progress = 1.0 * bytesWritten / contentLength
                emitter.onNext(progress)
            }

        })
    }



    private fun createRequestForImage(file: File): RequestBody {
        return RequestBody.create(MediaType.parse("application/octet-stream"), file)
    }

    fun createMultipartBody(
        filePath: String,
        emitter: FlowableEmitter<Double>
    ): MultipartBody.Part {
        val file = File(filePath)
        return MultipartBody.Part.createFormData(
            "media_file",
            file.name,
            createCountingRequestBody(file, emitter)
        )
    }

}