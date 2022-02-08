package com.project.projectalgi001.admin.ui.listdo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.github.barteksc.pdfviewer.PDFView
import com.project.projectalgi001.databinding.ActivityDetailDoCashAdminBinding
import java.io.File

class DetailDoCashAdminActivity : AppCompatActivity() {
    private lateinit var binding : ActivityDetailDoCashAdminBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityDetailDoCashAdminBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val v : PDFView = binding.pdfViewer

        val file = File(intent.extras!!.getString("PDF"))

        v.fromFile(file).pages(0)
            .load()
    }
}