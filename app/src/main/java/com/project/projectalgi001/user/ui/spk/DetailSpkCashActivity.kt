package com.project.projectalgi001.user.ui.spk

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
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
import com.project.projectalgi001.databinding.ActivityDetailSpkCashBinding
import com.project.projectalgi001.user.MainActivity
import com.project.projectalgi001.user.adapter.ListSpkCashAdapter
import com.project.projectalgi001.user.adapter.ResourceSpkAdapter
import com.project.projectalgi001.user.model.FetchSpkCashModel
import com.project.projectalgi001.user.model.ResourceModel
import com.project.projectalgi001.user.selection.MyItemDetailsLookupResource
import com.project.projectalgi001.user.selection.MyItemKeyProviderResource
import java.text.NumberFormat
import java.util.*

class DetailSpkCashActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailSpkCashBinding
    private lateinit var adapter: ResourceSpkAdapter
    private lateinit var storage: FirebaseStorage
    private lateinit var model : FetchSpkCashModel

    private var trackerResource: SelectionTracker<ResourceModel>? = null
    private var selectedPostResource: MutableList<ResourceModel>? = mutableListOf()
    private var gridImagesUploadedResource : ArrayList<ResourceModel> = ArrayList()
    private var mainMenu: Menu? = null
    private var saveImagesUrlSpk : ArrayList<String> = ArrayList()
    private var counterSpk: Int = 0

    @SuppressLint("NotifyDataSetChanged")
    private val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            result -> if (result.resultCode == Activity.RESULT_OK && result.data != null){
        if (result.data!!.clipData != null) {
            val count = result.data!!.clipData!!.itemCount
            for (i in 0 until count) {
                val resourceModel = ResourceModel()
                resourceModel.imageResource = result.data!!.clipData!!.getItemAt(i).uri.toString()
                gridImagesUploadedResource.add(resourceModel)
                adapter.notifyDataSetChanged()
            }
            showRecyclerGridUpdateResource()
        } else {
            val singleImage: Uri = result.data!!.data!!
            val resourceModel = ResourceModel()
            resourceModel.imageResource = singleImage.toString()
            gridImagesUploadedResource.add(resourceModel)
            adapter.notifyDataSetChanged()
        }
    }
    }

    companion object {
        const val EXTRA_SPK_CASH = "extra_customer"
        private const val READ_PERMISSION_CODE = 16
    }
    override fun onCreate(savedInstanceState: Bundle?) {

        binding = ActivityDetailSpkCashBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val toolbar: androidx.appcompat.widget.Toolbar = binding.toolbarSpkCash
        setSupportActionBar(toolbar)

        val navBarTitle = intent.getStringExtra(ListSpkCashAdapter.NAME_CAR)
        supportActionBar?.title = "SPK $navBarTitle"

        storage = Firebase.storage

        adapter = ResourceSpkAdapter(gridImagesUploadedResource)
        showRecyclerGridUpdateResource()
        selectionTrackerCash()

        if(savedInstanceState != null)
            trackerResource?.onRestoreInstanceState(savedInstanceState)

        adapter.trackerResource = trackerResource

        trackerResource?.addObserver(object : SelectionTracker.SelectionObserver<ResourceModel>() {
            override fun onSelectionChanged() {
                val nItems: Int? = trackerResource?.selection?.size()
                selectedPostResource = trackerResource?.selection?.toMutableList()

                if (nItems != null && nItems > 0 && selectedPostResource != null) {
                    showHideDelete(true)
                    supportActionBar?.title = "${selectedPostResource?.size}"
                    supportActionBar?.setBackgroundDrawable(
                        ColorDrawable(Color.parseColor("#006064"))
                    )
                } else {
                    showHideDelete(false)
                    supportActionBar?.title = "SPK $navBarTitle"
                    supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.parseColor("#07575B")))
                }
            }
        })

        binding.uploadSourceOfDocumentCash.setOnClickListener {
            verifyPermissionAndPickImageResource()
        }

        binding.saveSpkCash.setOnClickListener {
            if (binding.textInputEditTextSpkCashCustomerName.text.toString().isEmpty()) {
                binding.textInputEditTextSpkCashCustomerName.error = getString(R.string.field_empty)
                binding.textInputEditTextSpkCashCustomerName.requestFocus()
                return@setOnClickListener
            }
            if (binding.textInputEditTextSpkCashCustomerAddress.text.toString().isEmpty()) {
                binding.textInputEditTextSpkCashCustomerAddress.error = getString(R.string.field_empty)
                binding.textInputEditTextSpkCashCustomerAddress.requestFocus()
                return@setOnClickListener
            }
            if (binding.textInputEditTextSpkCashCustomerMobileNumber.text.toString().isEmpty()) {
                binding.textInputEditTextSpkCashCustomerMobileNumber.error = getString(R.string.field_empty)
                binding.textInputEditTextSpkCashCustomerMobileNumber.requestFocus()
                return@setOnClickListener
            }
            if (binding.TextInputEditTextSpkCashPlanDelivery.text.toString().isEmpty()) {
                binding.TextInputEditTextSpkCashPlanDelivery.error = getString(R.string.field_empty)
                binding.TextInputEditTextSpkCashPlanDelivery.requestFocus()
                return@setOnClickListener
            }
            if (binding.TextInputEditTextSpkCashAdditionalNotes.text.toString().isEmpty()) {
                binding.TextInputEditTextSpkCashAdditionalNotes.error = getString(R.string.field_empty)
                binding.TextInputEditTextSpkCashAdditionalNotes.requestFocus()
                return@setOnClickListener
            }

            if (!isNetworkAvailable(this)){
                showDialogConnection()
            } else{
                updateDataSpkCash()
                uploadImageSpkCashToFireStorage()

                val alertBuilder = AlertDialog.Builder(this)
                alertBuilder.setTitle(getString(R.string.success_title))
                alertBuilder.setMessage(getString(R.string.spk_updated))
                alertBuilder.setPositiveButton("OK") { _, _->
                    val intent = Intent(this, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()
                }
                alertBuilder.show()
            }
        }

       setDetailSpkCash()
        disableEditText()
    }

    private fun uploadImageSpkCashToFireStorage() {

        for (i in 0 until gridImagesUploadedResource.size) {
            val imagesUri: ResourceModel = gridImagesUploadedResource[i]
            val storageRef = storage.reference
            val fileName = UUID.randomUUID().toString()
            val refImages = storageRef.child("images/resourceSpkCash/$fileName")

            imagesUri.imageResource?.let { refImages.putFile(it.toUri()) }
                ?.addOnCompleteListener {
                    if (it.isSuccessful) {
                        refImages.downloadUrl.addOnCompleteListener { task ->
                            counterSpk++

                            if (task.isSuccessful){
                                saveImagesUrlSpk.add(task.result.toString())
                            } else{
                                refImages.delete()
                                Toast.makeText(baseContext, "Couldn't save " + gridImagesUploadedResource[i], Toast.LENGTH_SHORT).show()
                            }
                            if (counterSpk == gridImagesUploadedResource.size) {
                                updateResourceImage()
                            }
                        }
                    } else {
                        counterSpk++
                        Toast.makeText(baseContext, "Couldn't Upload" + gridImagesUploadedResource[i], Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    private fun updateResourceImage() {

        val imageResource : HashMap<String, String> = HashMap()

        for (i in 0 until saveImagesUrlSpk.size) {
            imageResource["imagesResource$i"] = saveImagesUrlSpk[i]
        }

        val db = Firebase.firestore

        db.document("spkCash/" + model.carPoliceNumberCash)
            .update("imageResource", imageResource)

            .addOnSuccessListener {
                Log.d("ProfileActivity", "Update Success")
            }
            .addOnFailureListener {
                Log.d("ProfileActivity", "Update Failure")
            }
    }

    private fun updateDataSpkCash() {

        val customerName = binding.textInputEditTextSpkCashCustomerName.text.toString()
        val customerAddress = binding.textInputEditTextSpkCashCustomerAddress.text.toString()
        val customerMobileNumber = binding.textInputEditTextSpkCashCustomerMobileNumber.text.toString()
        val customerEmail = binding.textInputEditTextSpkCashCustomerEmail.text.toString()
        val deliveryPlant = binding.TextInputEditTextSpkCashPlanDelivery.text.toString()
        val additionalTextCredit = binding.TextInputEditTextSpkCashAdditionalNotes.text.toString()

        val db = Firebase.firestore

        db.document("spkCash/" + model.carPoliceNumberCash)
            .update("nameBuyer", customerName,
                "addressBuyer", customerAddress,
                "mobileNumberBuyer", customerMobileNumber,
                "emailBuyer", customerEmail,
                "deliveryPlant", deliveryPlant,
                "additionalNotes", additionalTextCredit)

            .addOnSuccessListener {
                Log.d("ProfileActivity", "Update Success")
            }
            .addOnFailureListener {
                Log.d("ProfileActivity", "Update Failure")
            }
    }

    private fun setDetailSpkCash() {
        val spkCashParcelable = intent.getParcelableExtra<FetchSpkCashModel>(EXTRA_SPK_CASH) as FetchSpkCashModel
        model = spkCashParcelable
        val localeID = Locale("in", "ID")
        val formatRupiah = NumberFormat.getCurrencyInstance(localeID)

        val dateCash = binding.textViewSpkCashDate
        val buyerNameCash = binding.textInputEditTextSpkCashCustomerName as TextView
        val buyerAddressCash = binding.textInputEditTextSpkCashCustomerAddress as TextView
        val buyerMobileNumberCash = binding.textInputEditTextSpkCashCustomerMobileNumber as TextView
        val buyerEmailCash = binding.textInputEditTextSpkCashCustomerEmail as TextView
        val carNameCash = binding.textInputEditTextSpkCashNameCar as TextView
        val policeNumberCash = binding.textInputEditTextSpkCashPoliceNumber as TextView
        val machineNumberCash = binding.textInputEditTextSpkCashMachineNumber as TextView
        val chassisNumberCash = binding.textInputEditTextSpkCashChassisNumber as TextView
        val priceCash = binding.textInputEditTextSpkCashPriceCash as TextView
        val discountCash = binding.TextInputEditTextSpkCashDiscount as TextView
        val netOfDiscountCash = binding.TextInputEditTextSpkCashSoldAt as TextView
        val prepaymentCash = binding.TextInputEditTextSpkCashPrePayment as  TextView
        val plantDeliveryCash = binding.TextInputEditTextSpkCashPlanDelivery as TextView
        val paymentRemainingCash = binding.TextInputEditTextSpkCashRemainingPayment as TextView
        val additionalNotesCash = binding.TextInputEditTextSpkCashAdditionalNotes as TextView

        dateCash.text = spkCashParcelable.dateCash
        buyerNameCash.text = spkCashParcelable.buyerNameCash
        buyerAddressCash.text = spkCashParcelable.buyerAddressCash
        buyerMobileNumberCash.text = spkCashParcelable.buyerMobileNumberCash
        buyerEmailCash.text = spkCashParcelable.buyerEmailCash
        carNameCash.text = spkCashParcelable.carNameCash
        policeNumberCash.text = spkCashParcelable.carPoliceNumberCash
        machineNumberCash.text = spkCashParcelable.carMachineNumberCash
        chassisNumberCash.text = spkCashParcelable.carChassisNumberCash
        priceCash.text = spkCashParcelable.carPriceCash
        discountCash.text = formatRupiah.format(spkCashParcelable.cashDiscount)
        netOfDiscountCash.text = formatRupiah.format(spkCashParcelable.netOfDiscountCash)
        prepaymentCash.text =  formatRupiah.format(spkCashParcelable.cashPrepayment)
        plantDeliveryCash.text = spkCashParcelable.plantDelivery
        paymentRemainingCash.text = formatRupiah.format(spkCashParcelable.paymentRemaining)
        additionalNotesCash.text = spkCashParcelable.notesAdditional
    }

    private fun showRecyclerGridUpdateResource() {
        binding.rvSourceSpkCash.layoutManager = GridLayoutManager(this, 2)
        binding.rvSourceSpkCash.setHasFixedSize(true)
        binding.rvSourceSpkCash.adapter = adapter
    }

    private fun selectionTrackerCash() {
        trackerResource = SelectionTracker.Builder(
            "mySelection",
            binding.rvSourceSpkCash,
            MyItemKeyProviderResource(adapter),
            MyItemDetailsLookupResource(binding.rvSourceSpkCash),
            StorageStrategy.createParcelableStorage(ResourceModel::class.java)
        ).withSelectionPredicate(SelectionPredicates.createSelectAnything()).build()
    }

    private fun verifyPermissionAndPickImageResource() {
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
            deleteImagesResource()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun deleteImagesResource() {
        val alertBuilder = AlertDialog.Builder(this)
        alertBuilder.setTitle(getString(R.string.title_deleteImages))
        alertBuilder.setMessage(getString(R.string.sub_deleteImages))
        alertBuilder.setPositiveButton(getString(R.string.title_delete)) { _, _->
            selectedPostResource = trackerResource?.selection?.toMutableList()
            adapter.deleteImage(selectedPostResource)
            Toast.makeText(this, getString(R.string.image_deleted), Toast.LENGTH_SHORT).show()
            removeMenuSpk()
        }
        alertBuilder.setNegativeButton(getString(R.string.title_cancel)) { _, _->}
        alertBuilder.show()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun removeMenuSpk() {
        showHideDelete(false)
        adapter.trackerResource?.clearSelection()
        adapter.notifyDataSetChanged()
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
                Toast.makeText(this, getString(R.string.permission), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showHideDelete(show: Boolean){
        mainMenu?.findItem(R.id.delete_item)?.isVisible = show
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        trackerResource?.onSaveInstanceState(outState)
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

    private fun disableEditText(){
        binding.textInputEditTextSpkCashNameCar.isEnabled = false
        binding.textInputEditTextSpkCashPoliceNumber.isEnabled = false
        binding.textInputEditTextSpkCashMachineNumber.isEnabled = false
        binding.textInputEditTextSpkCashChassisNumber.isEnabled = false
        binding.textInputEditTextSpkCashPriceCash.isEnabled = false
        binding.TextInputEditTextSpkCashDiscount.isEnabled = false
        binding.TextInputEditTextSpkCashSoldAt.isEnabled = false
        binding.TextInputEditTextSpkCashPrePayment.isEnabled = false
        binding.TextInputEditTextSpkCashRemainingPayment.isEnabled = false
    }
}