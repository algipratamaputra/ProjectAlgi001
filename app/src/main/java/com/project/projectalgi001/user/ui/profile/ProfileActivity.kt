package com.project.projectalgi001.user.ui.profile

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.ImageDecoder
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.project.projectalgi001.R
import com.project.projectalgi001.databinding.ActivityProfilBinding
import com.project.projectalgi001.user.MainActivity
import com.project.projectalgi001.user.model.Users
import java.util.*

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfilBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var storage: FirebaseStorage
    private var selectedPhoto: Uri? = null
    private var customProggress : Dialog? = null

    private val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            result -> if (result.resultCode == Activity.RESULT_OK && result.data != null){
        selectedPhoto = result.data!!.data
        try {
            selectedPhoto?.let {
                if (Build.VERSION.SDK_INT < 28) {
                    val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhoto)
                    binding.imageProfile.setImageBitmap(bitmap)
                } else {
                    if (selectedPhoto == null) return@registerForActivityResult
                    val source = ImageDecoder.createSource(contentResolver, selectedPhoto!!)
                    val bitmap = ImageDecoder.decodeBitmap(source)
                    binding.imageProfile.setImageBitmap(bitmap)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfilBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth
        storage = Firebase.storage

        val toolbar: Toolbar = binding.toolbarProfile
        setSupportActionBar(toolbar)
        supportActionBar?.title = getString(R.string.profile)

        customProggress = Dialog(this)
        customProggress?.setContentView(R.layout.progress_custom)

        binding.imageProfile.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
            intent.type = "image/*"
            resultLauncher.launch(intent)
        }

        binding.buttonSaveProfile.setOnClickListener {
            if (!isNetworkAvailable(this)){
                showDialogConnection()
            }else {
                customProggress!!.show()
                updateImageNamesToFireStorage()
            }
        }

        fetchUsers()
    }

    private fun updateImageNamesToFireStorage() {
        if (selectedPhoto == null) {
            customProggress?.dismiss()
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }

        val storageRef = storage.reference
        val fileName = UUID.randomUUID().toString()
        val refImages = storageRef.child("images/user/$fileName")

        selectedPhoto?.let {
            refImages.putFile(it)
                .addOnSuccessListener {uploadTask ->
                    Log.d("ProfileActivity", "successfully uploaded image : ${uploadTask.metadata?.path}")
                    customProggress?.dismiss()
                    val intent = Intent(this, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)

                    refImages.downloadUrl.addOnSuccessListener { urlImages ->
                        Log.d("ProfileActivity", "File Location : $urlImages")
                        updateImagesProfile(urlImages.toString())
                    }
                }
                .addOnFailureListener { failure ->
                    failure.suppressedExceptions
                }
        }

        val uid = auth.currentUser?.uid
        val username = binding.textInputEditTextPName.text.toString()
        val db = Firebase.firestore
        db.document("users/$uid")
            .update("username", username)
            .addOnSuccessListener {
                Log.d("ProfileActivity", "Update Success")
            }
            .addOnFailureListener {
                Log.d("ProfileActivity", "Update Failure")
            }
    }

    private fun updateImagesProfile(profileImageUrl: String){
        val uid = auth.currentUser?.uid
        val db = Firebase.firestore
        db.document("users/$uid")
                .update("profileImageUrl", profileImageUrl)
                .addOnSuccessListener {
                    Log.d("ProfileActivity", "Update Success")
                }
                .addOnFailureListener {
                    Log.d("ProfileActivity", "Update Failure")
                }
    }

    private fun fetchUsers() {

        val nameProfile = binding.textInputEditTextPName as TextView
        val imageHeader = binding.imageProfile as ImageView
        val uid = auth.currentUser?.uid
        val db = Firebase.firestore.document("users/$uid")
        db.addSnapshotListener {snapshot, e ->
            if (e != null) {
                Log.w("ProfileActivity", "Listed Failed", e)
                return@addSnapshotListener
            }
            if (snapshot != null && snapshot.exists()) {
                Log.d("ProfileActivity", "Current data: ${snapshot.data}")
                val user = snapshot.toObject(Users::class.java)

                nameProfile.text = user?.username
                Glide.with(applicationContext).load(user?.profileImageUrl).into(imageHeader)
            }else {
                Log.d("ProfileActivity", "Current Data : Null")
            }
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