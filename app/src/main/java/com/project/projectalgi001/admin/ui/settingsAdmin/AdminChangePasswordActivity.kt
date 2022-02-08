package com.project.projectalgi001.admin.ui.settingsAdmin

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
import com.project.projectalgi001.databinding.ActivityAdminChangePasswordBinding
import com.project.projectalgi001.user.auth.EmailPasswordActivity
import com.project.projectalgi001.user.ui.setting.ChangePasswordActivity

class AdminChangePasswordActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityAdminChangePasswordBinding
    private var customProggress : Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityAdminChangePasswordBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val toolbar: Toolbar = binding.toolbarCpa
        setSupportActionBar(toolbar)
        supportActionBar?.title = getString(R.string.changePassword_button)

        customProggress = Dialog(this)
        customProggress?.setContentView(R.layout.progress_custom)

        auth = Firebase.auth

        binding.buttonCpa.setOnClickListener {
            if (!isNetworkAvailable(this)){
                showDialogConnection()
            }else{
                changePasswordAdmin()
            }
        }
    }

    private fun changePasswordAdmin() {

        if (binding.textInputEditTextCpCurrentPasswordAdmin.text.toString().isNotEmpty() &&
            binding.textInputEditTextNpNewPasswordAdmin.text.toString().isNotEmpty() &&
            binding.textInputEditTextNpaNewPasswordAgainAdmin.text.toString().isNotEmpty()
        ) {

            if (binding.textInputEditTextNpNewPasswordAdmin.text.toString() == binding.textInputEditTextNpaNewPasswordAgainAdmin.text.toString()) {
                val user = auth.currentUser

                if (binding.textInputEditTextNpNewPasswordAdmin.length() < 8) {
                    binding.textInputEditTextNpNewPasswordAdmin.error =
                        getString(R.string.valid_password)
                    binding.textInputEditTextNpNewPasswordAdmin.requestFocus()
                    return
                }

                if (user != null && user.email !== null) {
                    val credential = EmailAuthProvider
                        .getCredential(
                            user.email!!,
                            binding.textInputEditTextCpCurrentPasswordAdmin.text.toString()
                        )

                    user.reauthenticate(credential)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                Log.d(ChangePasswordActivity.TAG, "User re-authenticated.")

                                customProggress?.show()

                                user.updatePassword(binding.textInputEditTextNpNewPasswordAdmin.text.toString())
                                    .addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            Log.d(ChangePasswordActivity.TAG, "User password updated.")
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