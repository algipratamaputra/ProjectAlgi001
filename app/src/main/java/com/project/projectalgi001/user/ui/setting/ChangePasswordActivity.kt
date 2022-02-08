package com.project.projectalgi001.user.ui.setting

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.project.projectalgi001.R
import com.project.projectalgi001.databinding.ActivityChangePasswordBinding
import com.project.projectalgi001.user.auth.EmailPasswordActivity

class ChangePasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChangePasswordBinding
    private lateinit var auth: FirebaseAuth
    private var customProggress : Dialog? = null

    companion object {
        const val TAG = "changePassword"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangePasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar: Toolbar = binding.toolbarCp
        setSupportActionBar(toolbar)
        supportActionBar?.title = getString(R.string.changePassword_button)

        customProggress = Dialog(this)
        customProggress?.setContentView(R.layout.progress_custom)

        auth = Firebase.auth

        binding.buttonCp.setOnClickListener {
            if (!isNetworkAvailable(this)){
                showDialogConnection()
            }else{
                changePassword()
            }
        }
    }

    private fun changePassword() {

        if (binding.textInputEditTextCpCurrentPassword.text.toString().isNotEmpty() &&
            binding.textInputEditTextNpNewPassword.text.toString().isNotEmpty() &&
            binding.textInputEditTextNpaNewPasswordAgain.text.toString().isNotEmpty()
        ) {

            if (binding.textInputEditTextNpNewPassword.text.toString() == binding.textInputEditTextNpaNewPasswordAgain.text.toString()) {
                val user = auth.currentUser

                if (binding.textInputEditTextNpNewPassword.length() < 8) {
                    binding.textInputEditTextNpNewPassword.error =
                        getString(R.string.valid_password)
                    binding.textInputEditTextNpNewPassword.requestFocus()
                    return
                }

                if (user != null && user.email !== null) {
                    val credential = EmailAuthProvider
                        .getCredential(
                            user.email!!,
                            binding.textInputEditTextCpCurrentPassword.text.toString()
                        )

                    user.reauthenticate(credential)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                Log.d(TAG, "User re-authenticated.")

                                customProggress?.show()

                                user.updatePassword(binding.textInputEditTextNpNewPassword.text.toString())
                                    .addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            Log.d(TAG, "User password updated.")
                                            Toast.makeText(
                                                this,
                                                getString(R.string.success_changePassword),
                                                Toast.LENGTH_LONG
                                            ).show()
                                            auth.signOut()
                                            customProggress?.dismiss()
                                            val intent =
                                                Intent(this, EmailPasswordActivity::class.java)
                                            intent.flags =
                                                Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                                            startActivity(intent)
                                        }
                                    }
                            } else {
                                Toast.makeText(
                                    this,
                                    getString(R.string.verify_password),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                }
            } else {
                Toast.makeText(this, getString(R.string.passwordMiss), Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, getString(R.string.allPassword), Toast.LENGTH_SHORT).show()
        }
    }

    private fun showDialogConnection() {
        val alertBuilder = AlertDialog.Builder(this)
        alertBuilder.setTitle(getString(R.string.no_connection_title))
        alertBuilder.setMessage(getString(R.string.no_connection))
        alertBuilder.setPositiveButton(getString(R.string.oke)) { _, _ ->
            startActivity(Intent(Settings.ACTION_WIRELESS_SETTINGS))
        }
        alertBuilder.show()
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
}