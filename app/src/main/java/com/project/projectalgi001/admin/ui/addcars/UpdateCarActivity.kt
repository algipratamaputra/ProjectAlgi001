package com.project.projectalgi001.admin.ui.addcars

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.recyclerview.selection.SelectionPredicates
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StorageStrategy
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.project.projectalgi001.R
import com.project.projectalgi001.admin.MainActivityAdmin
import com.project.projectalgi001.admin.adapter.AddCarsAdapter
import com.project.projectalgi001.admin.model.CarsModel
import com.project.projectalgi001.admin.selection.MyItemDetailsLookup
import com.project.projectalgi001.admin.selection.MyItemKeyProvider
import com.project.projectalgi001.databinding.ActivityUpdateCarBinding
import com.project.projectalgi001.user.model.Cars
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class UpdateCarActivity : AppCompatActivity() {

    private lateinit var binding : ActivityUpdateCarBinding
    private lateinit var model : Cars
    private lateinit var adapter : AddCarsAdapter
    private lateinit var storage: FirebaseStorage
    private var gridImagesUploaded : ArrayList<CarsModel> = ArrayList()
    private var saveImagesUrl : ArrayList<String> = ArrayList()
    private var selectedPostItems: MutableList<CarsModel>? = mutableListOf()
    private var tracker: SelectionTracker<CarsModel>? = null
    private var mainMenu: Menu? = null
    private var counter: Int = 0
    private var customProggress : Dialog? = null

    @SuppressLint("NotifyDataSetChanged")
    private val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            result -> if (result.resultCode == Activity.RESULT_OK && result.data != null){
        Log.d("CreateAccount", "photo was selected")
        if (result.data!!.clipData != null) {
            val count = result.data!!.clipData!!.itemCount
            for (i in 0 until count) {
                val carsModel = CarsModel()
                carsModel.image = result.data!!.clipData!!.getItemAt(i).uri.toString()
                gridImagesUploaded.add(carsModel)
                adapter.notifyDataSetChanged()
            }
            showRecyclerGridUpdate()
        } else {
            val singleImage: Uri = result.data!!.data!!
            val carsModel = CarsModel()
            carsModel.image = singleImage.toString()
            gridImagesUploaded.add(carsModel)
            adapter.notifyDataSetChanged()
        }
    }
    }

    companion object{
        const val EXTRA_CARS_UPDATE = "extra_cars_update"
        const val NAME_CARS_UPDATE = "name_cars_update"
        private const val READ_PERMISSION_CODE = 11
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityUpdateCarBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val toolbar: androidx.appcompat.widget.Toolbar = binding.toolbarUc
        setSupportActionBar(toolbar)

        val navBarTitle = intent.getStringExtra(NAME_CARS_UPDATE)
        supportActionBar?.title = navBarTitle

        customProggress = Dialog(this)
        customProggress?.setContentView(R.layout.progress_custom)

        storage = Firebase.storage

        adapter = AddCarsAdapter(gridImagesUploaded)
        showRecyclerGridUpdate()

        tracker = SelectionTracker.Builder(
            "mySelection",
            binding.rvUpdateCars,
            MyItemKeyProvider(adapter),
            MyItemDetailsLookup(binding.rvUpdateCars),
            StorageStrategy.createParcelableStorage(CarsModel::class.java)
        ).withSelectionPredicate(SelectionPredicates.createSelectAnything()).build()

        if(savedInstanceState != null)
            tracker?.onRestoreInstanceState(savedInstanceState)

        adapter.tracker = tracker

        tracker?.addObserver(object : SelectionTracker.SelectionObserver<CarsModel>() {
            override fun onSelectionChanged() {
                val nItems: Int? = tracker?.selection?.size()
                selectedPostItems = tracker?.selection?.toMutableList()

                if (nItems != null && nItems > 0 && selectedPostItems != null) {
                    showHideDelete(true)
                    supportActionBar?.title = "${selectedPostItems?.size}"
                    supportActionBar?.setBackgroundDrawable(
                        ColorDrawable(Color.parseColor("#006064"))
                    )
                } else {
                    showHideDelete(false)
                    supportActionBar?.title = getString(R.string.title_addCars)
                    supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.parseColor("#07575B")))
                }
            }
        })

        binding.buttonUpdateImages.setOnClickListener {
            verifyPermissionAndPickImage()
        }

        binding.updateCar.setOnClickListener {

            if (binding.textInputEditTextUcNameCar.text.toString().isEmpty()) {
                binding.textInputEditTextUcNameCar.error = getString(R.string.field_empty)
                binding.textInputEditTextUcNameCar.requestFocus()
                return@setOnClickListener
            }

            if (binding.textInputEditTextUcMachineNumber.text.toString().isEmpty()) {
                binding.textInputEditTextUcMachineNumber.error = getString(R.string.field_empty)
                binding.textInputEditTextUcMachineNumber.requestFocus()
                return@setOnClickListener
            }

            if (binding.textInputEditTextUcChassisNumber.text.toString().isEmpty()) {
                binding.textInputEditTextUcChassisNumber.error = getString(R.string.field_empty)
                binding.textInputEditTextUcChassisNumber.requestFocus()
                return@setOnClickListener
            }

            if (binding.textInputEditTextUcBpkb.text.toString().isEmpty()) {
                binding.textInputEditTextUcBpkb.error = getString(R.string.field_empty)
                binding.textInputEditTextUcBpkb.requestFocus()
                return@setOnClickListener
            }

            if (binding.textInputEditTextUcStnk.text.toString().isEmpty()) {
                binding.textInputEditTextUcStnk.error = getString(R.string.field_empty)
                binding.textInputEditTextUcStnk.requestFocus()
                return@setOnClickListener
            }

            if (binding.textInputEditTextUcCashPrice.text.toString().isEmpty()) {
                binding.textInputEditTextUcCashPrice.error = getString(R.string.field_empty)
                binding.textInputEditTextUcCashPrice.requestFocus()
                return@setOnClickListener
            }

            if (binding.textInputEditTextUcCreditPrice.text.toString().isEmpty()) {
                binding.textInputEditTextUcCreditPrice.error = getString(R.string.field_empty)
                binding.textInputEditTextUcCreditPrice.requestFocus()
                return@setOnClickListener
            }

            if (binding.textInputEditTextUcDp.text.toString().isEmpty()) {
                binding.textInputEditTextUcDp.error = getString(R.string.field_empty)
                binding.textInputEditTextUcDp.requestFocus()
                return@setOnClickListener
            }

            if (!isNetworkAvailable(this)){
                showDialogConnection()
            } else{
                updateDataCar()
                updateImageCarsToFireStorage()
                customProggress!!.dismiss()
                val alertBuilder = AlertDialog.Builder(this)
                alertBuilder.setTitle(getString(R.string.success_title))
                alertBuilder.setMessage(getString(R.string.car_updated))
                alertBuilder.setPositiveButton(getString(R.string.oke)) { _, _->
                    val intent = Intent(this, MainActivityAdmin::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()
                }
                alertBuilder.show()
            }
        }

        setDetailCar()
        binding.textInputEditTextUcPoliceNumber.isEnabled = false
    }

    private fun updateImageCarsToFireStorage() {
        customProggress?.show()
        for (i in 0 until gridImagesUploaded.size) {
            val imagesUri: CarsModel = gridImagesUploaded[i]
            val storageRef = storage.reference
            val fileName = UUID.randomUUID().toString()
            val refImages = storageRef.child("images/cars/$fileName")

            imagesUri.image?.let { refImages.putFile(it.toUri()) }
                ?.addOnCompleteListener {
                    if (it.isSuccessful) {
                        refImages.downloadUrl.addOnCompleteListener { task ->
                            counter++
                            if (task.isSuccessful){
                                saveImagesUrl.add(task.result.toString())
                            } else{
                                refImages.delete()
                                Toast.makeText(baseContext, "Couldn't save " + gridImagesUploaded[i], Toast.LENGTH_SHORT).show()
                            }
                            if (counter == gridImagesUploaded.size) {
                                updateImageCar()
                            }
                        }
                    } else {
                        customProggress?.dismiss()
                        counter++
                        Toast.makeText(baseContext, "Couldn't Upload" + gridImagesUploaded[i], Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    private fun updateImageCar() {

        val imageResource : HashMap<String, String> = HashMap()

        for (i in 0 until saveImagesUrl.size) {
            imageResource["imagesCar$i"] = saveImagesUrl[i]
        }

        val db = Firebase.firestore

        db.document("cars/" + model.numberPolice)
            .update("imageCars", imageResource)

            .addOnSuccessListener {
                Log.d("ProfileActivity", "Update Success")
            }
            .addOnFailureListener {
                Log.d("ProfileActivity", "Update Failure")
            }
    }

    private fun updateDataCar() {
        val carName = binding.textInputEditTextUcNameCar.text.toString()
        val machineNumber = binding.textInputEditTextUcMachineNumber.text.toString()
        val chassisNumber = binding.textInputEditTextUcChassisNumber.text.toString()
        val bpkbStatus = binding.textInputEditTextUcBpkb.text.toString()
        val stnkStatus = binding.textInputEditTextUcStnk.text.toString()
        val cashPrice = binding.textInputEditTextUcCashPrice.text.toString()
        val creditPrice = binding.textInputEditTextUcCreditPrice.text.toString()
        val minimumDp = binding.textInputEditTextUcDp.text.toString()

        val db = Firebase.firestore

        db.document("cars/" + model.numberPolice)
            .update("nameCar", carName,
                "machineNumber", machineNumber,
                "chassisNumber", chassisNumber,
                "bpkbStatus", bpkbStatus,
                "stnkStatus", stnkStatus,
                "cashPrice", cashPrice,
                "creditPrice", creditPrice,
                "minimumDp", minimumDp)

            .addOnSuccessListener {
                Log.d("ProfileActivity", "Update Success")
            }
            .addOnFailureListener {
                Log.d("ProfileActivity", "Update Failure")
            }

    }

    private fun setDetailCar() {
        val carIntentExtra = intent.getParcelableExtra<Cars>(EXTRA_CARS_UPDATE) as Cars
        model = carIntentExtra

        val getNameCar = binding.textInputEditTextUcNameCar as TextView
        val getPoliceNumber = binding.textInputEditTextUcPoliceNumber as TextView
        val getMachineNumber = binding.textInputEditTextUcMachineNumber as TextView
        val getChassisNumber = binding.textInputEditTextUcChassisNumber as TextView
        val getBpkbStatus = binding.textInputEditTextUcBpkb as TextView
        val getStnkStatus = binding.textInputEditTextUcStnk as TextView
        val getPriceCash = binding.textInputEditTextUcCashPrice as TextView
        val getPriceCredit = binding.textInputEditTextUcCreditPrice as TextView
        val getMinimumDp = binding.textInputEditTextUcDp as TextView

        getNameCar.text = carIntentExtra.nameCars
        getPoliceNumber.text = carIntentExtra.numberPolice
        getMachineNumber.text = carIntentExtra.numberMachine
        getChassisNumber.text = carIntentExtra.numberChassis
        getBpkbStatus.text = carIntentExtra.statusBpkb
        getStnkStatus.text = carIntentExtra.statusStnk
        getPriceCash.text = carIntentExtra.priceCars
        getPriceCredit.text = carIntentExtra.creditPriceCars.toString()
        getMinimumDp.text = carIntentExtra.dpMinimum.toString()
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
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
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

    private fun showRecyclerGridUpdate() {
        binding.rvUpdateCars.layoutManager = GridLayoutManager(this, 2)
        binding.rvUpdateCars.setHasFixedSize(true)
        binding.rvUpdateCars.adapter = adapter
    }

    private fun showHideDelete(show: Boolean){
        mainMenu?.findItem(R.id.delete_item)?.isVisible = show
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        mainMenu = menu
        menuInflater.inflate(R.menu.delete_menu, menu)
        showHideDelete(false)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.delete_item) {
            delete()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun delete() {
        val alertBuilder = AlertDialog.Builder(this)
        alertBuilder.setTitle("Delete Images")
        alertBuilder.setMessage("Do you want to delete the selected Item ?")
        alertBuilder.setPositiveButton("Delete") { _, _->
            selectedPostItems = tracker?.selection?.toMutableList()
            adapter.deleteImage(selectedPostItems)
            Toast.makeText(this, "Photo Has Been Deleted", Toast.LENGTH_SHORT).show()
            removeMenu()
        }
        alertBuilder.setNegativeButton("Cancel") { _, _->}
        alertBuilder.show()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun removeMenu() {
        showHideDelete(false)
        adapter.tracker?.clearSelection()
        adapter.notifyDataSetChanged()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        tracker?.onSaveInstanceState(outState)
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