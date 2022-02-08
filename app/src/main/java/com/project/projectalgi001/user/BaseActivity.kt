package com.project.projectalgi001.user

import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity

open class BaseActivity : AppCompatActivity() {

    private var progressBar:ProgressBar? = null

    fun setProgressBar(bar: ProgressBar) {
        progressBar = bar
    }

    fun showProgressBar() {
        progressBar?.visibility = View.VISIBLE
    }

    fun hideProgressBar() {
        progressBar?.visibility = View.INVISIBLE
    }

    public override fun onStop() {
        super.onStop()
        hideProgressBar()
    }
}