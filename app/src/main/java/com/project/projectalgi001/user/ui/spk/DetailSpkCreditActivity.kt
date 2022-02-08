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
import com.project.projectalgi001.databinding.ActivityDetailSpkCreditBinding
import com.project.projectalgi001.user.MainActivity
import com.project.projectalgi001.user.adapter.ListSpkCreditAdapter
import com.project.projectalgi001.user.adapter.ResourceSpkAdapter
import com.project.projectalgi001.user.model.FetchSpkCreditModel
import com.project.projectalgi001.user.model.ResourceModel
import com.project.projectalgi001.user.selection.MyItemDetailsLookupResource
import com.project.projectalgi001.user.selection.MyItemKeyProviderResource
import java.text.NumberFormat
import java.util.*

class DetailSpkCreditActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailSpkCreditBinding
    private lateinit var adapter: ResourceSpkAdapter
    private lateinit var storage: FirebaseStorage
    private lateinit var model : FetchSpkCreditModel

    private var trackerResource: SelectionTracker<ResourceModel>? = null
    private var selectedPostResource: MutableList<ResourceModel>? = mutableListOf()
    private var gridImagesUploadedResource : ArrayList<ResourceModel> = ArrayList()
    private var saveImagesUrlSpk : ArrayList<String> = ArrayList()
    private var mainMenu: Menu? = null
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
        const val EXTRA_SPK_CREDIT = "extra_spk_credit"
        private const val READ_PERMISSION_CODE = 32
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityDetailSpkCreditBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val toolbar: androidx.appcompat.widget.Toolbar = binding.toolbarSpkCredit
        setSupportActionBar(toolbar)

        val navBarTitle = intent.getStringExtra(ListSpkCreditAdapter.NAME_CAR)
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

        binding.uploadSourceOfDocumentCredit.setOnClickListener {
            verifyPermissionAndPickImageResource()
        }


        binding.saveSpkCredit.setOnClickListener {
            if (binding.textInputEditTextSpkCreditCustomerName.text.toString().isEmpty()) {
                binding.textInputEditTextSpkCreditCustomerName.error = getString(R.string.field_empty)
                binding.textInputEditTextSpkCreditCustomerName.requestFocus()
                return@setOnClickListener
            }
            if (binding.textInputEditTextSpkCreditCustomerAddress.text.toString().isEmpty()) {
                binding.textInputEditTextSpkCreditCustomerAddress.error = getString(R.string.field_empty)
                binding.textInputEditTextSpkCreditCustomerAddress.requestFocus()
                return@setOnClickListener
            }
            if (binding.textInputEditTextSpkCreditCustomerMobileNumber.text.toString().isEmpty()) {
                binding.textInputEditTextSpkCreditCustomerMobileNumber.error = getString(R.string.field_empty)
                binding.textInputEditTextSpkCreditCustomerMobileNumber.requestFocus()
                return@setOnClickListener
            }
            if (binding.textInputEditTextSpkCreditFinance.text.toString().isEmpty()) {
                binding.textInputEditTextSpkCreditFinance.error = getString(R.string.field_empty)
                binding.textInputEditTextSpkCreditFinance.requestFocus()
                return@setOnClickListener
            }
            if (binding.textInputEditTextSpkCreditTenor.text.toString().isEmpty()) {
                binding.textInputEditTextSpkCreditTenor.error = getString(R.string.field_empty)
                binding.textInputEditTextSpkCreditTenor.requestFocus()
                return@setOnClickListener
            }
            if (binding.textInputEditTextSpkCreditMonthlyInstallment.text.toString().isEmpty()) {
                binding.textInputEditTextSpkCreditMonthlyInstallment.error = getString(R.string.field_empty)
                binding.textInputEditTextSpkCreditMonthlyInstallment.requestFocus()
                return@setOnClickListener
            }
            if (binding.textInputEditTextSpkAdditionalNotesCredit.text.toString().isEmpty()) {
                binding.textInputEditTextSpkAdditionalNotesCredit.error = getString(R.string.field_empty)
                binding.textInputEditTextSpkAdditionalNotesCredit.requestFocus()
                return@setOnClickListener
            }
            if (!isNetworkAvailable(this)){
                showDialogConnection()
            } else{
                updateDataSpk()
                uploadImageSpkCreditToFireStorage()

                val alertBuilder = AlertDialog.Builder(this)
                alertBuilder.setTitle(getString(R.string.success_title))
                alertBuilder.setMessage(getString(R.string.spk_updated))
                alertBuilder.setPositiveButton(getString(R.string.oke)) { _, _->
                    val intent = Intent(this, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()
                }
                alertBuilder.show()
            }
        }

        setDetailSpkCredit()
        disableEditText()
    }

    private fun uploadImageSpkCreditToFireStorage() {

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

        db.document("spkCredit/" + model.policeNumberCarSoldCredit)
            .update("imageResource", imageResource)

            .addOnSuccessListener {
                Log.d("ProfileActivity", "Update Success")
            }
            .addOnFailureListener {
                Log.d("ProfileActivity", "Update Failure")
            }
    }

    private fun updateDataSpk() {

        val customerName = binding.textInputEditTextSpkCreditCustomerName.text.toString()
        val customerAddress = binding.textInputEditTextSpkCreditCustomerAddress.text.toString()
        val customerMobileNumber = binding.textInputEditTextSpkCreditCustomerMobileNumber.text.toString()
        val customerEmail = binding.textInputEditTextSpkCreditCustomerEmail.text.toString()
        val finance = binding.textInputEditTextSpkCreditFinance.text.toString()
        val tenor = binding.textInputEditTextSpkCreditTenor.text.toString()
        val monthlyInstallment = binding.textInputEditTextSpkCreditMonthlyInstallment.text.toString()
        val additionalTextCredit = binding.textInputEditTextSpkAdditionalNotesCredit.text.toString()

        val db = Firebase.firestore

        db.document("spkCredit/" + model.policeNumberCarSoldCredit)
            .update("nameBuyer", customerName,
                "addressBuyer", customerAddress,
                "mobileNumberBuyer", customerMobileNumber,
                "emailBuyer", customerEmail,
                "finance", finance,
                "tenor", tenor,
                "monthlyInstallment", monthlyInstallment,
                "additionalNotes", additionalTextCredit)

            .addOnSuccessListener {
                Log.d("ProfileActivity", "Update Success")
            }
            .addOnFailureListener {
                Log.d("ProfileActivity", "Update Failure")
            }
    }

    private fun setDetailSpkCredit() {
        val spkCreditParcelable = intent.getParcelableExtra<FetchSpkCreditModel>(EXTRA_SPK_CREDIT) as FetchSpkCreditModel
        model = spkCreditParcelable
        val localeID = Locale("in", "ID")
        val formatRupiah = NumberFormat.getCurrencyInstance(localeID)

        val dateCredit = binding.textViewSpkCreditDate
        val buyerNameCredit = binding.textInputEditTextSpkCreditCustomerName as TextView
        val buyerAddressCredit = binding.textInputEditTextSpkCreditCustomerAddress as TextView
        val buyerMobileNumberCredit = binding.textInputEditTextSpkCreditCustomerMobileNumber as TextView
        val buyerEmailCredit = binding.textInputEditTextSpkCreditCustomerEmail as TextView
        val carNameCredit = binding.textInputEditTextSpkCreditNameCar as TextView
        val policeNumberCredit = binding.textInputEditTextSpkCreditPoliceNumber as TextView
        val machineNumberCredit = binding.textInputEditTextSpkCreditMachineNumber as TextView
        val chassisNumberCredit = binding.textInputEditTextSpkCreditChassisNumber as TextView
        val priceCredit = binding.textInputEditTextSpkCreditPrice as TextView
        val finance = binding.textInputEditTextSpkCreditFinance as TextView
        val discountCredit = binding.textInputEditTextSpkCreditDiscount as TextView
        val netOfDiscountCredit = binding.textInputEditTextSpkCreditSoldAt as TextView
        val prepaymentCredit = binding.textInputEditTextSpkCreditPrePayment as  TextView
        val downPaymentCredit = binding.textInputEditTextSpkCreditDownPayment as TextView
        val downPaymentRemainingCredit = binding.textInputEditTextSpkCreditRemainingDownPayment as TextView
        val tenor = binding.textInputEditTextSpkCreditTenor as TextView
        val monthlyInstallment = binding.textInputEditTextSpkCreditMonthlyInstallment as TextView
        val additionalNotesCredit = binding.textInputEditTextSpkAdditionalNotesCredit as TextView

        dateCredit.text = spkCreditParcelable.dateCredit
        spkCreditParcelable.imageResourceCredit
        buyerNameCredit.text = spkCreditParcelable.nameBuyerCredit
        buyerAddressCredit.text = spkCreditParcelable.addressBuyerCredit
        buyerMobileNumberCredit.text = spkCreditParcelable.mobileNumberBuyerCredit
        buyerEmailCredit.text = spkCreditParcelable.emailBuyerCredit
        carNameCredit.text = spkCreditParcelable.nameCarSoldCredit
        policeNumberCredit.text = spkCreditParcelable.policeNumberCarSoldCredit
        machineNumberCredit.text = spkCreditParcelable.machineNumberCarSoldCredit
        chassisNumberCredit.text = spkCreditParcelable.chassisNumberCarSoldCredit
        priceCredit.text = spkCreditParcelable.priceCarSoldCredit
        finance.text = spkCreditParcelable.finance
        discountCredit.text = formatRupiah.format(spkCreditParcelable.creditDiscount?.toInt())
        netOfDiscountCredit.text = formatRupiah.format(spkCreditParcelable.creditNetOfDiscount?.toInt())
        prepaymentCredit.text =  formatRupiah.format(spkCreditParcelable.creditPrepayment?.toInt())
        downPaymentCredit.text = formatRupiah.format(spkCreditParcelable.creditDownPayment?.toInt())
        downPaymentRemainingCredit.text = formatRupiah.format(spkCreditParcelable.downPaymentRemaining?.toInt())
        tenor.text = spkCreditParcelable.creditTenor
        monthlyInstallment.text = spkCreditParcelable.creditMonthlyInstallment
        additionalNotesCredit.text = spkCreditParcelable.creditAdditionalNotes
    }

    private fun showRecyclerGridUpdateResource() {
        binding.rvSourceSpkCredit.layoutManager = GridLayoutManager(this, 2)
        binding.rvSourceSpkCredit.setHasFixedSize(true)
        binding.rvSourceSpkCredit.adapter = adapter
    }

    private fun selectionTrackerCash() {
        trackerResource = SelectionTracker.Builder(
            "mySelection",
            binding.rvSourceSpkCredit,
            MyItemKeyProviderResource(adapter),
            MyItemDetailsLookupResource(binding.rvSourceSpkCredit),
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
        binding.textInputEditTextSpkCreditNameCar.isEnabled = false
        binding.textInputEditTextSpkCreditPoliceNumber.isEnabled = false
        binding.textInputEditTextSpkCreditMachineNumber.isEnabled = false
        binding.textInputEditTextSpkCreditChassisNumber.isEnabled = false
        binding.textInputEditTextSpkCreditPrice.isEnabled = false
        binding.textInputEditTextSpkCreditDiscount.isEnabled = false
        binding.textInputEditTextSpkCreditSoldAt.isEnabled = false
        binding.textInputEditTextSpkCreditPrePayment.isEnabled = false
        binding.textInputEditTextSpkCreditDownPayment.isEnabled = false
        binding.textInputEditTextSpkCreditRemainingDownPayment.isEnabled = false
    }
}