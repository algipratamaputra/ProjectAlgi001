package com.project.projectalgi001

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.WindowInsets
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.project.projectalgi001.admin.MainActivityAdmin
import com.project.projectalgi001.databinding.ActivitySplashScreenBinding
import com.project.projectalgi001.user.MainActivity
import com.project.projectalgi001.user.auth.EmailPasswordActivity

@SuppressLint("CustomSplashScreen")
class SplashScreen : AppCompatActivity() {

    private lateinit var binding : ActivitySplashScreenBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }

        auth = Firebase.auth

        Handler(mainLooper).postDelayed({
            val user = auth.currentUser
            if (user != null){
                val uid = auth.currentUser?.uid
                val db = Firebase.firestore
                db.document("users/$uid")
                    .get().addOnSuccessListener {
                        if (it.getString("levelAdmin") != null) {
                            emailVerificationAdmin()
                            return@addOnSuccessListener
                        }
                        if (it.getString("levelUser") != null) {
                            emailVerificationUser()
                            return@addOnSuccessListener
                        }
                    }
                    .addOnFailureListener { exception ->
                        auth.signOut()
                        Log.d("onStart", "Start Failed, Re-Login", exception)
                        return@addOnFailureListener
                    }
            } else {
                val intentSplash = Intent(this@SplashScreen, EmailPasswordActivity::class.java)
                startActivity(intentSplash)
                finish()
            }
        }, 2000)
    }

    private fun emailVerificationUser(){
        val user = auth.currentUser
        if (user != null) {
            if (user.isEmailVerified) {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else {
                startActivity(Intent(this, EmailPasswordActivity::class.java))
                finish()
            }
        }
    }

    private fun emailVerificationAdmin() {
        val user = auth.currentUser
        if (user != null) {
            if (user.isEmailVerified) {
                val intent = Intent(this, MainActivityAdmin::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            } else {
                startActivity(Intent(this, EmailPasswordActivity::class.java))
                finish()
            }
        }
    }
}