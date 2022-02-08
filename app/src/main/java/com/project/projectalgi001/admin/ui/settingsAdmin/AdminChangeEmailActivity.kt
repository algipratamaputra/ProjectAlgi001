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
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.project.projectalgi001.R
import com.project.projectalgi001.databinding.ActivityAdminChangeEmailBinding
import com.project.projectalgi001.user.auth.EmailPasswordActivity

class AdminChangeEmailActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityAdminChangeEmailBinding
    private var customProggress : Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityAdminChangeEmailBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val toolbar: Toolbar = binding.toolbarCea
        setSupportActionBar(toolbar)
        supportActionBar?.title = getString(R.string.change_email)

        customProggress = Dialog(this)
        customProggress?.setContentView(R.layout.progress_custom)

        auth = Firebase.auth

        binding.buttonSaveEmailAdmin.setOnClickListener {
            if (!isNetworkAvailable(this)){
                showDialogConnection()
            }else{
                updateEmailAdmin()
                updateEmailAdminToFirestore()
            }
        }

        val email = auth.currentUser?.email
        val changeEmail = binding.textInputEditTextCeEmailAdmin as TextView
        changeEmail.text = email
    }

    private fun updateEmailAdminToFirestore() {

        val uid = auth.currentUser?.uid
        val email = binding.textInputEditTextCeEmailAdmin.text.toString()
        val db = Firebase.firestore
        db.document("users/$uid")
            .update("emailUser", email)
            .addOnSuccessListener {
                Log.d("ProfileActivity", "Update Success")
            }
            .addOnFailureListener {
                Log.d("ProfileActivity", "Update Failure")
            }
    }

    private fun updateEmailAdmin() {

        if (binding.textInputEditTextCePasswordAdmin.text.toString().isNotEmpty() &&
            binding.textInputEditTextCeEmailAdmin.text.toString().isNotEmpty()) {

            val user = auth.currentUser
            if (user != null && user.email !== null) {
                val credential = EmailAuthProvider
                    .getCredential(user.email!!, binding.textInputEditTextCePasswordAdmin.text.toString())
                user.reauthenticate(credential)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            Log.d("ChangeEmail", "User re-authenticated.")

                            customProggress?.show()
                            user.updateEmail(binding.textInputEditTextCeEmailAdmin.text.toString())
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        Log.d("ChangeEmail", "Update Email Success")
                                        user.sendEmailVerification()
                                            .addOnCompleteListener { taskAgain ->
                                                if (taskAgain.isSuccessful) {
                                                    Log.d("ChangeEmail", "Email sent.")
                                                    auth.signOut()
                                                    customProggress?.dismiss()
                                                    val intent = Intent(this, EmailPasswordActivity::class.java)
                                                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(
                                                        Intent.FLAG_ACTIVITY_NEW_TASK)
                                                    startActivity(intent)
                                                    Toast.makeText(baseContext, getString(R.string.email_profile), Toast.LENGTH_LONG).show()
                                                }
                                            }
                                    } else {
                                        customProggress?.dismiss()
                                        Log.w("ChangeEmail", "Update Email : Failure", task.exception)
                                        Toast.makeText(baseContext, getString(R.string.failure_update), Toast.LENGTH_SHORT).show()
                                    }
                                }
                        } else {
                            Toast.makeText(this, getString(R.string.verify_password), Toast.LENGTH_SHORT).show()
                        }
                    }
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