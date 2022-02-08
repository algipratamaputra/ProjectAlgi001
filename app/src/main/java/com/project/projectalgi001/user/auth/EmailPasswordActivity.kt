package com.project.projectalgi001.user.auth

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.util.Patterns
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.project.projectalgi001.R
import com.project.projectalgi001.admin.MainActivityAdmin
import com.project.projectalgi001.databinding.ActivityEmailPasswordBinding
import com.project.projectalgi001.user.MainActivity

class EmailPasswordActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityEmailPasswordBinding
    private var customProggress : Dialog? = null

    companion object {
        private const val TAG = "EmailPassword"
    }

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEmailPasswordBinding.inflate(layoutInflater)
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

        customProggress = Dialog(this)
        customProggress?.setContentView(R.layout.progress_custom)

        auth = Firebase.auth

        binding.buttonEpForgotPassword.setOnClickListener(this)
        binding.buttonLoginEp.setOnClickListener(this)
        binding.buttonRegistEp.setOnClickListener(this)
    }

    private fun signIn() {
        if (binding.textInputEditTextEpEmail.text.toString().isEmpty()) {
            binding.textInputEditTextEpEmail.error = getString(R.string.enter_email)
            binding.textInputEditTextEpEmail.requestFocus()
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(binding.textInputEditTextEpEmail.text.toString()).matches()) {
            binding.textInputEditTextEpEmail.error = getString(R.string.valid_email)
            binding.textInputEditTextEpEmail.requestFocus()
            return
        }

        if (binding.textInputEditTextEpPassword.text.toString().isEmpty()) {
            binding.textInputEditTextEpPassword.error = getString(R.string.enter_password)
            binding.textInputEditTextEpPassword.requestFocus()
            return
        }

        customProggress?.show()

        auth.signInWithEmailAndPassword(binding.textInputEditTextEpEmail.text.toString(), binding.textInputEditTextEpPassword.text.toString())
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "signInWithEmail : Success")
                        checkAccessLevel(auth.currentUser?.uid)
                    } else {
                        if (!isNetworkAvailable(this)){
                            showDialogConnection()
                        }else{
                            Log.w(TAG, "signInWithEmail: Failure", task.exception)
                            customProggress?.dismiss()
                            val alertBuilder = androidx.appcompat.app.AlertDialog.Builder(this)
                            alertBuilder.setTitle(getString(R.string.failed_title))
                            alertBuilder.setMessage(getString(R.string.login_failed))
                            alertBuilder.setPositiveButton(getString(R.string.oke)) { _, _-> }
                            alertBuilder.show()
                        }
                    }
                }
    }

    private fun checkAccessLevel(uid: String?) {
        val db = Firebase.firestore
        db.document("users/$uid")
                .get().addOnSuccessListener {
                    if (it.getString("levelAdmin") != null) {
                        emailVerificationAdmin()
                    }
                    if (it.getString("levelUser") != null) {
                        emailVerificationUser()
                    }
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(baseContext, "Get Data Failed", Toast.LENGTH_LONG).show()
                    Log.d(TAG, "get failed with", exception)
                }
    }

    private fun emailVerificationUser(){
        val user = auth.currentUser
        if (user != null) {
            if (user.isEmailVerified) {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else {
                Toast.makeText(baseContext, getString(R.string.verify_failed), Toast.LENGTH_SHORT).show()
                return signIn()
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
                Toast.makeText(baseContext, getString(R.string.verify_failed), Toast.LENGTH_SHORT).show()
                return signIn()
            }
        }
    }

    private fun forgotPassword(email: com.google.android.material.textfield.TextInputEditText) {
        if (email.text.toString().isEmpty()) {
            val alertBuilder = androidx.appcompat.app.AlertDialog.Builder(this)
            alertBuilder.setTitle(getString(R.string.failed_title))
            alertBuilder.setMessage(getString(R.string.enter_email))
            alertBuilder.setPositiveButton(getString(R.string.oke)) {_, _-> }
            alertBuilder.show()
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email.text.toString()).matches()) {
            val alertBuilder = androidx.appcompat.app.AlertDialog.Builder(this)
            alertBuilder.setTitle(getString(R.string.failed_title))
            alertBuilder.setMessage(getString(R.string.valid_email))
            alertBuilder.setPositiveButton(getString(R.string.oke)) {_, _-> }
            alertBuilder.show()
            return
        }

        auth.sendPasswordResetEmail(email.text.toString())
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val alertBuilder = androidx.appcompat.app.AlertDialog.Builder(this)
                    alertBuilder.setTitle(getString(R.string.success_title))
                    alertBuilder.setMessage(getString(R.string.email_sent))
                    alertBuilder.setPositiveButton(getString(R.string.oke)) { _, _-> }
                    alertBuilder.show()
                } else {
                    if (!isNetworkAvailable(this)){
                        showDialogConnection()
                    } else{
                        val alertBuilder = androidx.appcompat.app.AlertDialog.Builder(this)
                        alertBuilder.setTitle(getString(R.string.failed_title))
                        alertBuilder.setMessage(getString(R.string.email_notRegist))
                        alertBuilder.setPositiveButton(getString(R.string.oke)) { _, _-> }
                        alertBuilder.show()
                    }
                }
            }
    }

    private fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val nw      = connectivityManager.activeNetwork ?: return false
            val actNw = connectivityManager.getNetworkCapabilities(nw) ?: return false
            return when {
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) -> true
                else -> false
            }

        } else {
            val nwInfo = connectivityManager.activeNetworkInfo ?: return false
            return nwInfo.isConnected
        }
    }

    private fun showDialogConnection() {
        val alertBuilder = androidx.appcompat.app.AlertDialog.Builder(this)
        alertBuilder.setTitle(getString(R.string.no_connection_title))
        alertBuilder.setMessage(getString(R.string.no_connection))
        alertBuilder.setPositiveButton(getString(R.string.retry)) { _, _ ->
            startActivity(Intent(Settings.ACTION_WIRELESS_SETTINGS))
        }
        alertBuilder.show()
    }

    public override fun onStart() {
        super.onStart()
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.buttonRegistEp -> {
                val intent = Intent(this, CreateAccountActivity::class.java)
                startActivity(intent)
            }
            R.id.buttonLoginEp -> signIn()

            R.id.buttonEp_forgotPassword -> {
                val builder = AlertDialog.Builder(this)
                builder.setTitle(getString(R.string.forgot_password))

                val view = layoutInflater.inflate(R.layout.dialog_forgot_password, null)
                val email = view.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.dialogEditText_ForgotPassword)
                builder.setView(view)
                builder.setPositiveButton(getString(R.string.reset_password)) { _, _ ->
                    forgotPassword(email)
                }
                builder.setNegativeButton(getString(R.string.close_dialogFP)) { _, _ -> }
                builder.show()
            }
        }
    }
}