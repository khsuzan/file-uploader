package com.example.pdfuploader

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.Callback
import retrofit2.Call
import java.io.File
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val apiService: ApiService) : ViewModel() {

    private val TAG = "Response_Test"

    private val _eventChannel = Channel<MainEvent>()
    val mainEvent = _eventChannel.receiveAsFlow()

    var file: ByteArray? = null
        set(value) {
            field = value
            selectedFile()
        }


    fun uploadPdf() = viewModelScope.launch {

        if (file == null) {
            _eventChannel.send(MainEvent.Error("File is empty"))
            return@launch
        }
        val reqBody = RequestBody.create(MediaType.parse("application/pdf"),file)
        val formData = MultipartBody.Part.createFormData("file","amarfile.pdf",reqBody)
        apiService.uploadPdf(formData).enqueue(object :
            Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if(response.isSuccessful){

                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                Log.d(TAG, "onResponse: Error ${t.toString()}")
            }
        })
    }

    private fun selectedFile() = viewModelScope.launch {
        if (file != null)
            _eventChannel.send(MainEvent.FileSelected)
    }

    sealed class MainEvent {
        object FileSelected : MainEvent()
        data class Error(val error: String) : MainEvent()
        data class Uploading(val progress: Int) : MainEvent()
        object UploadSuccess : MainEvent()
    }
}