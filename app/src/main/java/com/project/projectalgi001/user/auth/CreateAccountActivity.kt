package com.project.projectalgi001.user.auth

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.ImageDecoder
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.util.Patterns
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.project.projectalgi001.R
import com.project.projectalgi001.databinding.ActivityCreateAccountBinding
import com.project.projectalgi001.user.model.Users
import java.util.*

class CreateAccountActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var auth: FirebaseAuth
    private lateinit var storage: FirebaseStorage
    private lateinit var binding: ActivityCreateAccountBinding
    private var selectedPhoto: Uri? = null
    private var customProggress : Dialog? = null

    private val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        result -> if (result.resultCode == Activity.RESULT_OK && result.data != null){
        Log.d("CreateAccount", "photo was selected")
        selectedPhoto = result.data?.data
        try {
            selectedPhoto?.let {
                if (Build.VERSION.SDK_INT < 28) {
                    val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhoto)
                    binding.buttonSelectPhoto.setImageBitmap(bitmap)
                } else {
                    if (selectedPhoto == null) return@registerForActivityResult
                    val source = ImageDecoder.createSource(contentResolver, selectedPhoto!!)
                    val bitmap = ImageDecoder.decodeBitmap(source)
                    binding.buttonSelectPhoto.setImageBitmap(bitmap)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    }

    companion object {
        private const val TAG = "CreateAccount"
        private const val READ_PERMISSION_CODE = 20
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateAccountBinding.inflate(layoutInflater)
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
        storage = Firebase.storage

        binding.buttonSignUp.setOnClickListener(this)
        binding.buttonSelectPhoto.setOnClickListener(this)

    }

    @Suppress("NAME_SHADOWING")
    private fun signUpUser() {
        if (binding.textInputEditTextCaEmail.text.toString().isEmpty()) {
            binding.textInputEditTextCaEmail.error = getString(R.string.enter_email)
            binding.textInputEditTextCaEmail.requestFocus()
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(binding.textInputEditTextCaEmail.text.toString()).matches()) {
            binding.textInputEditTextCaEmail.error = getString(R.string.valid_email)
            binding.textInputEditTextCaEmail.requestFocus()
            return
        }

        if (binding.textInputEditTextCaPassword.text.toString().isEmpty()) {
            val alertBuilder = androidx.appcompat.app.AlertDialog.Builder(this)
            alertBuilder.setTitle(getString(R.string.failed_title))
            alertBuilder.setMessage(getString(R.string.enter_password))
            alertBuilder.setPositiveButton(getString(R.string.oke)) { _, _-> }
            alertBuilder.show()
            return
        }

        if (binding.textInputEditTextCaPassword.length() < 8) {
            val alertBuilder = androidx.appcompat.app.AlertDialog.Builder(this)
            alertBuilder.setTitle(getString(R.string.failed_title))
            alertBuilder.setMessage(getString(R.string.valid_password))
            alertBuilder.setPositiveButton(getString(R.string.oke)) { _, _-> }
            alertBuilder.show()
            return
        }

        if (binding.textInputEditTextCaName.text.toString().isEmpty()) {
            binding.textInputEditTextCaName.error = getString(R.string.enter_name)
            binding.textInputEditTextCaName.requestFocus()
            return
        }

        if (selectedPhoto == null) {
            val alertBuilder = androidx.appcompat.app.AlertDialog.Builder(this)
            alertBuilder.setTitle(getString(R.string.failed_title))
            alertBuilder.setMessage(getString(R.string.upload_images))
            alertBuilder.setPositiveButton(getString(R.string.oke)) { _, _-> }
            alertBuilder.show()
            return
        }

        customProggress?.show()

        auth.createUserWithEmailAndPassword(binding.textInputEditTextCaEmail.text.toString(), binding.textInputEditTextCaPassword.text.toString())
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser

                    user!!.sendEmailVerification()
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Log.d(TAG, "Email sent.")
                                uploadImageToFireStorage()
                                customProggress?.dismiss()
                                val alertBuilder = androidx.appcompat.app.AlertDialog.Builder(this)
                                alertBuilder.setTitle(getString(R.string.success_title))
                                alertBuilder.setMessage(getString(R.string.email_verification))
                                alertBuilder.setPositiveButton(getString(R.string.oke)) { _, _->
                                    val intent = Intent(this, EmailPasswordActivity::class.java)
                                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    startActivity(intent)
                                    finish()
                                }
                                alertBuilder.show()
                            }
                        }
                } else {

                    if (!isNetworkAvailable(this)){
                        showDialogConnection()
                    } else{
                        customProggress?.dismiss()
                        val alertBuilder = androidx.appcompat.app.AlertDialog.Builder(this)
                        alertBuilder.setTitle(getString(R.string.failed_title))
                        alertBuilder.setMessage(getString(R.string.signUp_failed))
                        alertBuilder.setPositiveButton(getString(R.string.oke)) { _, _-> }
                        alertBuilder.show()
                    }
                }
            }
    }

    private fun showDialogConnection() {
        val alertBuilder = androidx.appcompat.app.AlertDialog.Builder(this)
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

    private fun uploadImageToFireStorage() {
        val storageRef = storage.reference
        val fileName = UUID.randomUUID().toString()
        val refImages = storageRef.child("images/user/$fileName")

        refImages.putFile(selectedPhoto!!)
                .addOnSuccessListener {
                    Log.d("CreateAccount", "successfully uploaded image : ${it.metadata?.path}")

                    refImages.downloadUrl.addOnSuccessListener { urlImages ->
                        Log.d("CreateAccount", "File Location : $urlImages")
                        saveUserToCloudFirestore(urlImages.toString())
                    }
                }
                .addOnFailureListener {
                    it.suppressedExceptions
                }
    }

    private fun saveUserToCloudFirestore(profileImageUrl: String) {

        val uid = auth.currentUser?.uid
        val user = Users(binding.textInputEditTextCaName.text.toString(), auth.currentUser?.email, "User", profileImageUrl)
        val db = Firebase.firestore
        db.document("users/$uid")
                .set(user)
                .addOnSuccessListener { documentReference ->
                    Log.d("CreateAccount", "written with  ID : $documentReference")
                }
                .addOnFailureListener {
                    Log.w("CreateAccount", "Error Adding Document")
                }
    }

    private fun verifyPermissionAndPickImage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                pickImage()
            } else {
                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), READ_PERMISSION_CODE)
            }
        } else {
            pickImage()
        }
    }

    private fun pickImage() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        resultLauncher.launch(intent)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == READ_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                pickImage()
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.buttonSignUp -> {
                signUpUser()
            }

            R.id.button_selectPhoto -> {
                verifyPermissionAndPickImage()
            }
        }
    }
}
