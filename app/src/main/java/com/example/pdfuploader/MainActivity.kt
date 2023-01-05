package com.example.pdfuploader

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val chooseBtn: Button = findViewById(R.id.chooseBtn)
        val uploadBtn: Button = findViewById(R.id.uploadBtn)
        val tv: TextView = findViewById(R.id.tv)

        chooseBtn.isEnabled = true
        uploadBtn.isEnabled = false

        lifecycleScope.launchWhenStarted {
            viewModel.mainEvent.collect {
                when (it) {
                    is MainViewModel.MainEvent.Error -> {

                    }
                    is MainViewModel.MainEvent.UploadSuccess -> {

                    }
                    is MainViewModel.MainEvent.Uploading -> {

                    }
                    is MainViewModel.MainEvent.FileSelected -> {
                        chooseBtn.isEnabled = false
                        uploadBtn.isEnabled = true
                        tv.text = "File Selected"
                    }
                }
            }
        }


        chooseBtn.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                type = "application/pdf"
            }
            pdfLauncher.launch(intent)
        }
        uploadBtn.setOnClickListener {
            viewModel.uploadPdf()
        }
    }

    private val pdfLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK && result?.data?.data != null) {
                result?.data?.data?.let {
                    viewModel.file = contentResolver.openInputStream(it)?.readBytes()
                }
            }
        }
}