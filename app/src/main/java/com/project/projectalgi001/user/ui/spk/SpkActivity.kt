package com.project.projectalgi001.user.ui.spk

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
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
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.recyclerview.selection.SelectionPredicates
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StorageStrategy
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.radiobutton.MaterialRadioButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.project.projectalgi001.R
import com.project.projectalgi001.databinding.ActivitySpkBinding
import com.project.projectalgi001.user.MainActivity
import com.project.projectalgi001.user.adapter.ResourceSpkAdapter
import com.project.projectalgi001.user.model.*
import com.project.projectalgi001.user.selection.MyItemDetailsLookupResource
import com.project.projectalgi001.user.selection.MyItemKeyProviderResource
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class SpkActivity : AppCompatActivity() {

    private lateinit var binding : ActivitySpkBinding
    private lateinit var adapter: ResourceSpkAdapter
    private lateinit var storage: FirebaseStorage
    private lateinit var auth: FirebaseAuth
    private lateinit var spinner : Spinner
    private lateinit var model : Cars

    private var selectedFinance : String? = null
    private var arrayNameCustomer : ArrayList<String> = ArrayList()
    private var arrayAddressCustomer : ArrayList<String> = ArrayList()
    private var arrayMobileNumberCustomer : ArrayList<String> = ArrayList()
    private var arrayEmailCustomer : ArrayList<String> = ArrayList()
    private var saveImagesUrlSpk : ArrayList<String> = ArrayList()
    private var mainMenu: Menu? = null
    private var counterSpk: Int = 0
    private val sdf = SimpleDateFormat("EEE, d MMM yyy HH:mm:ss", Locale.getDefault())
    private var gridImagesUploadedResource : ArrayList<ResourceModel> = ArrayList()
    private var selectedPostResource: MutableList<ResourceModel>? = mutableListOf()
    private var trackerResource: SelectionTracker<ResourceModel>? = null
    private var customProggress : Dialog? = null

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
            showRecyclerGridSpk()
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
        const val EXTRA_CARS_SPK = "EXTRA_CARS_SPK"
        const val NAME_CARS = "NAME_CARS"
        private const val READ_PERMISSION_CODE = 16
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivitySpkBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        val toolbar: androidx.appcompat.widget.Toolbar = binding.toolbarSpk
        setSupportActionBar(toolbar)

        val navBarTitle = intent.getStringExtra(NAME_CARS)
        supportActionBar?.title = "SPK $navBarTitle"

        customProggress = Dialog(this)
        customProggress?.setContentView(R.layout.progress_custom)

        storage = Firebase.storage
        auth = Firebase.auth

        adapter = ResourceSpkAdapter(gridImagesUploadedResource)
        showRecyclerGridSpk()
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

        binding.uploadSourceOfDocument.setOnClickListener {
            verifyPermissionAndPickImageResource()
        }
        binding.saveSpkCash.setOnClickListener {
            if (!isNetworkAvailable(this)){
                showDialogConnection()
            }else {
                uploadImageSpkCashToFireStorage()
            }
        }

        binding.saveSpkCredit.setOnClickListener {
            if (!isNetworkAvailable(this)){
                showDialogConnection()
            }else {
                uploadImageSpkCreditToFireStorage()
            }
        }

        spinnerChecked()
        dateNow()
        setDetailCarSpk()
        setCustomer()
        clickNameCustomer()
        disableEdittext()
    }

    private fun clickNameCustomer(){
        val autoComplete = binding.textInputEditTextSpkCustomerName
        val items = arrayNameCustomer
        val itemAdapter = ArrayAdapter(this, R.layout.item_list_name, items)

        autoComplete.setAdapter(itemAdapter)
        autoComplete.setOnItemClickListener { _, _, position, _ ->
            val getAddressCustomer = arrayAddressCustomer[position]
            val addressCustomer = binding.textInputEditTextSpkCustomerAddress as TextView
            addressCustomer.text = getAddressCustomer

            val getMobileNumberCustomer = arrayMobileNumberCustomer[position]
            val mobileNumberCustomer = binding.textInputEditTextSpkCustomerMobileNumber as TextView
            mobileNumberCustomer.text = getMobileNumberCustomer

            val getEmailCustomer = arrayEmailCustomer[position]
            val emailCustomer = binding.textInputEditTextSpkCustomerEmail as TextView
            emailCustomer.text = getEmailCustomer
        }
    }

    private fun setCustomer() {
        val db = Firebase.firestore
        val auth = Firebase.auth
        db.collection("customer")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    Log.d("fetchCars", "${document.id} => ${document.data}" )
                    val customer = FetchCustomerModel()
                    val dataCustomer = document.toObject(CustomerModel::class.java)

                    if (auth.currentUser!!.uid.lowercase() != dataCustomer.idUser.lowercase())
                        continue
                    customer.customerId = dataCustomer.idCustomer
                    customer.userId = dataCustomer.idUser
                    customer.nameCustomer = dataCustomer.customerName
                    customer.addressCustomer = dataCustomer.customerAddress
                    customer.mobileNumberCustomer = dataCustomer.customerMobileNumber
                    customer.emailCustomer = dataCustomer.customerEmail
                    customer.brandCar = dataCustomer.carBrand
                    customer.descriptionCustomer = dataCustomer.description

                    arrayNameCustomer.add(customer.nameCustomer)
                    arrayAddressCustomer.add(customer.addressCustomer)
                    arrayMobileNumberCustomer.add(customer.mobileNumberCustomer)
                    arrayEmailCustomer.add(customer.emailCustomer)
                }
            }
            .addOnFailureListener { exception ->
                Log.d("fetchCars", "Error getting documents: ", exception)
            }
    }

    private fun setDetailCarSpk() {
        val parcelable = intent.getParcelableExtra<Cars>(EXTRA_CARS_SPK) as Cars
        val localeID = Locale("in", "ID")
        val formatRupiah = NumberFormat.getCurrencyInstance(localeID)

        model = parcelable

        val nameCarEdt = binding.textInputEditTextSpkNameCar as TextView
        val policeNumberEdt = binding.textInputEditTextSpkPoliceNumber as TextView
        val machineNumberEdt = binding.textInputEditTextSpkMachineNumber as TextView
        val chassisNumberEdt = binding.textInputEditTextSpkChassisNumber as TextView
        val priceEdt = binding.textInputEditTextSpkPriceCash as TextView

        nameCarEdt.text = parcelable.nameCars
        policeNumberEdt.text = parcelable.numberPolice
        machineNumberEdt.text = parcelable.numberMachine
        chassisNumberEdt.text = parcelable.numberChassis
        priceEdt.text = formatRupiah.format(parcelable.priceCars?.toInt())
    }

    private fun showEditTextCredit(view: Boolean){
        if (view){
            binding.spFinance.visibility = View.VISIBLE
            binding.textInputLayoutSpkDiscountCredit.visibility = View.VISIBLE
            binding.textInputLayoutSpkSoldAt.visibility = View.VISIBLE
            binding.textInputLayoutSpkPrePaymentCredit.visibility = View.VISIBLE
            binding.textInputLayoutSpkDownPayment.visibility = View.VISIBLE
            binding.textInputLayoutSpkRemainingDownPayment.visibility = View.VISIBLE
            binding.textInputLayoutSpkTenor.visibility = View.VISIBLE
            binding.textInputLayoutSpkMonthlyInstallment.visibility = View.VISIBLE
            binding.textViewSpkMoreSelectCredit.visibility = View.VISIBLE
            binding.textInputLayoutSpkAdditionalNotesCredit.visibility = View.VISIBLE
            binding.saveSpkCredit.visibility = View.VISIBLE
        }else{
            binding.spFinance.visibility = View.GONE
            binding.textInputLayoutSpkDiscountCredit.visibility = View.GONE
            binding.textInputLayoutSpkSoldAt.visibility = View.GONE
            binding.textInputLayoutSpkPrePaymentCredit.visibility = View.GONE
            binding.textInputLayoutSpkDownPayment.visibility = View.GONE
            binding.textInputLayoutSpkRemainingDownPayment.visibility = View.GONE
            binding.textInputLayoutSpkTenor.visibility = View.GONE
            binding.textInputLayoutSpkMonthlyInstallment.visibility = View.GONE
            binding.textViewSpkMoreSelectCredit.visibility = View.GONE
            binding.textInputLayoutSpkAdditionalNotesCredit.visibility = View.GONE
            binding.saveSpkCredit.visibility = View.GONE
        }
    }

    private fun showEditTextCash(view: Boolean){
        if (view){
            binding.textInputLayoutSpkDiscount.visibility = View.VISIBLE
            binding.textInputLayoutSpkSoldAtCash.visibility = View.VISIBLE
            binding.textInputLayoutSpkPrePayment.visibility = View.VISIBLE
            binding.textInputLayoutSpkPlanDelivery.visibility = View.VISIBLE
            binding.textInputLayoutSpkRemainingPayment.visibility = View.VISIBLE
            binding.textViewSpkMoreSelectCash.visibility = View.VISIBLE
            binding.textInputLayoutSpkAdditionalNotesCash.visibility = View.VISIBLE
            binding.saveSpkCash.visibility = View.VISIBLE

        }else{
            binding.textInputLayoutSpkDiscount.visibility = View.GONE
            binding.textInputLayoutSpkSoldAtCash.visibility = View.GONE
            binding.textInputLayoutSpkPrePayment.visibility = View.GONE
            binding.textInputLayoutSpkPlanDelivery.visibility = View.GONE
            binding.textInputLayoutSpkRemainingPayment.visibility = View.GONE
            binding.textViewSpkMoreSelectCash.visibility = View.GONE
            binding.textInputLayoutSpkAdditionalNotesCash.visibility = View.GONE
            binding.saveSpkCash.visibility = View.GONE
        }
    }

    fun onRadioButtonClicked(view: View){
        if (view is MaterialRadioButton) {
            val checked = view.isChecked

            when(view.id){
                R.id.rb_cash ->
                    if (checked){
                        showEditTextCash(true)
                        showEditTextCredit(false)
                    }
                R.id.rb_credit ->
                    if (checked){
                        showEditTextCash(false)
                        showEditTextCredit(true)
                    }
            }
        }
    }

   private fun spinnerChecked(){
        spinner = binding.spFinance
        val finance : MutableList<String?> = ArrayList()
        finance.add(0, getString(R.string.choose_finance))
        finance.add("Mandiri Utama Finance")
        finance.add("Clipan Finance")
        finance.add("Oto Finance")
        finance.add("Adira Finance")
        finance.add("BCA Finance")
        finance.add("MPM Finance")
        finance.add("Kredit Plus")

        val arrayAdapter: ArrayAdapter<String?> = ArrayAdapter<String?>(this, android.R.layout.simple_list_item_1, finance)
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = arrayAdapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if (p0?.getItemAtPosition(p2) == getString(R.string.choose_finance)) {
                    return
                }else{
                    selectedFinance = p0?.getItemAtPosition(p2).toString()
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }
    }

    private fun dateNow(){
        val now = Calendar.getInstance()
        binding.textViewSpkDate.text = sdf.format(now.time)
    }

    private fun showRecyclerGridSpk() {
        binding.rvSourceSpk.layoutManager = GridLayoutManager(this, 2)
        binding.rvSourceSpk.setHasFixedSize(true)
        binding.rvSourceSpk.adapter = adapter
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

    private fun selectionTrackerCash() {
        trackerResource = SelectionTracker.Builder(
            "mySelection",
            binding.rvSourceSpk,
            MyItemKeyProviderResource(adapter),
            MyItemDetailsLookupResource(binding.rvSourceSpk),
            StorageStrategy.createParcelableStorage(ResourceModel::class.java)
        ).withSelectionPredicate(SelectionPredicates.createSelectAnything()).build()
    }

    private fun showHideDelete(show: Boolean){
        mainMenu?.findItem(R.id.delete_item)?.isVisible = show
    }

    private fun uploadImageSpkCashToFireStorage() {

        if (gridImagesUploadedResource.isEmpty()) {
            val alertBuilder = AlertDialog.Builder(this)
            alertBuilder.setTitle(getString(R.string.title_failed_spk))
            alertBuilder.setMessage(getString(R.string.upload_proof_spk))
            alertBuilder.setPositiveButton(getString(R.string.oke)) { _, _-> }
            alertBuilder.show()
            return
        }

        if (binding.textInputEditTextSpkCustomerName.text.toString().isEmpty()) {
            binding.textInputEditTextSpkCustomerName.error = getString(R.string.field_empty)
            binding.textInputEditTextSpkCustomerName.requestFocus()
            return
        }

        if (binding.textInputEditTextSpkDiscount.text.toString().isEmpty()) {
            binding.textInputEditTextSpkDiscount.error = getString(R.string.field_empty)
            binding.textInputEditTextSpkDiscount.requestFocus()
            return
        }

        if (binding.textInputEditTextSpkSoldAtCash.text.toString().isEmpty()) {
            binding.textInputEditTextSpkSoldAtCash.error = getString(R.string.field_empty)
            binding.textInputEditTextSpkSoldAtCash.requestFocus()
            return
        }

        if (binding.textInputEditTextSpkPrePayment.text.toString().isEmpty()) {
            binding.textInputEditTextSpkPrePayment.error = getString(R.string.field_empty)
            binding.textInputEditTextSpkPrePayment.requestFocus()
            return
        }

        if (binding.textInputEditTextSpkPlanDelivery.text.toString().isEmpty()) {
            binding.textInputEditTextSpkPlanDelivery.error = getString(R.string.field_empty)
            binding.textInputEditTextSpkPlanDelivery.requestFocus()
            return
        }

        if (binding.textInputEditTextSpkRemainingPayment.text.toString().isEmpty()) {
            binding.textInputEditTextSpkRemainingPayment.error = getString(R.string.field_empty)
            binding.textInputEditTextSpkRemainingPayment.requestFocus()
            return
        }

        if (binding.textInputEditTextSpkAdditionalNotesCash.text.toString().isEmpty()) {
            binding.textInputEditTextSpkAdditionalNotesCash.error = getString(R.string.field_empty)
            binding.textInputEditTextSpkAdditionalNotesCash.requestFocus()
            return
        }

        customProggress?.show()

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
                                saveDataSpkCashToCloudFirestore()
                                updateCustomer()
                            }
                        }
                    } else {
                        customProggress?.dismiss()
                        counterSpk++
                        Toast.makeText(baseContext, "Couldn't Upload" + gridImagesUploadedResource[i], Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    private fun saveDataSpkCashToCloudFirestore() {

        val id = binding.textInputEditTextSpkPoliceNumber.text.toString()
        val uidCash = auth.currentUser?.uid
        val imageResource : HashMap<String, String> = HashMap()
        val spkCash = SpkModel(
            binding.textViewSpkDate.text.toString(),
            imageResource,
            binding.textInputEditTextSpkCustomerName.text.toString(),
            binding.textInputEditTextSpkCustomerAddress.text.toString(),
            binding.textInputEditTextSpkCustomerMobileNumber.text.toString(),
            binding.textInputEditTextSpkCustomerEmail.text.toString(),
            binding.textInputEditTextSpkNameCar.text.toString(),
            binding.textInputEditTextSpkPoliceNumber.text.toString(),
            binding.textInputEditTextSpkMachineNumber.text.toString(),
            binding.textInputEditTextSpkChassisNumber.text.toString(),
            binding.textInputEditTextSpkPriceCash.text.toString(),
            binding.textInputEditTextSpkDiscount.text.toString().toInt(),
            binding.textInputEditTextSpkSoldAtCash.text.toString().toInt(),
            binding.textInputEditTextSpkPrePayment.text.toString().toInt(),
            binding.textInputEditTextSpkPlanDelivery.text.toString(),
            binding.textInputEditTextSpkRemainingPayment.text.toString().toInt(),
            binding.textInputEditTextSpkAdditionalNotesCash.text.toString(),
            uidCash.toString()
        )

        for (i in 0 until saveImagesUrlSpk.size) {
            imageResource["imagesResource$i"] = saveImagesUrlSpk[i]
        }
        val db = Firebase.firestore

        db.document("spkCash/$id")
            .set(spkCash)
            .addOnSuccessListener {
                customProggress?.dismiss()
                val alertBuilder = AlertDialog.Builder(this)
                alertBuilder.setTitle(getString(R.string.success_title))
                alertBuilder.setMessage(getString(R.string.spk_created))
                alertBuilder.setPositiveButton("OK") { _, _->
                    val intent = Intent(this, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                }
                alertBuilder.show()
            }
            .addOnFailureListener {
                customProggress?.dismiss()
                createAlertSpk("Error", "Images Uploaded but we couldn't save them to Database", "OK", "", null, null, null)
            }
    }

    private fun uploadImageSpkCreditToFireStorage() {

        if (gridImagesUploadedResource.isEmpty()) {
            val alertBuilder = AlertDialog.Builder(this)
            alertBuilder.setTitle(getString(R.string.title_failed_spk))
            alertBuilder.setMessage(getString(R.string.upload_proof_spk))
            alertBuilder.setPositiveButton(getString(R.string.oke)) { _, _-> }
            alertBuilder.show()
            return
        }

        if (binding.textInputEditTextSpkCustomerName.text.toString().isEmpty()) {
            binding.textInputEditTextSpkCustomerName.error = getString(R.string.field_empty)
            binding.textInputEditTextSpkCustomerName.requestFocus()
            return
        }

        if (selectedFinance == null) {
            val alertBuilder = AlertDialog.Builder(this)
            alertBuilder.setTitle(getString(R.string.title_failed_spk))
            alertBuilder.setMessage(getString(R.string.choose_finance))
            alertBuilder.setPositiveButton(getString(R.string.oke)) { _, _-> }
            alertBuilder.show()
            return
        }

        if (binding.textInputEditTextSpkDiscountCredit.text.toString().isEmpty()) {
            binding.textInputEditTextSpkDiscountCredit.error = getString(R.string.field_empty)
            binding.textInputEditTextSpkDiscountCredit.requestFocus()
            return
        }

        if (binding.textInputEditTextSpkSoldAt.text.toString().isEmpty()) {
            binding.textInputEditTextSpkSoldAt.error = getString(R.string.field_empty)
            binding.textInputEditTextSpkSoldAt.requestFocus()
            return
        }

        if (binding.textInputEditTextSpkPrePaymentCredit.text.toString().isEmpty()) {
            binding.textInputEditTextSpkPrePaymentCredit.error = getString(R.string.field_empty)
            binding.textInputEditTextSpkPrePaymentCredit.requestFocus()
            return
        }

        if (binding.textInputEditTextSpkDownPayment.text.toString().isEmpty()) {
            binding.textInputEditTextSpkDownPayment.error = getString(R.string.field_empty)
            binding.textInputEditTextSpkDownPayment.requestFocus()
            return
        }

        if (binding.textInputEditTextSpkRemainingDownPayment.text.toString().isEmpty()) {
            binding.textInputEditTextSpkRemainingDownPayment.error = getString(R.string.field_empty)
            binding.textInputEditTextSpkRemainingDownPayment.requestFocus()
            return
        }

        if (binding.textInputEditTextSpkTenor.text.toString().isEmpty()) {
            binding.textInputEditTextSpkTenor.error = getString(R.string.field_empty)
            binding.textInputEditTextSpkTenor.requestFocus()
            return
        }

        if (binding.textInputEditTextSpkMonthlyInstallment.text.toString().isEmpty()) {
            binding.textInputEditTextSpkMonthlyInstallment.error = getString(R.string.field_empty)
            binding.textInputEditTextSpkMonthlyInstallment.requestFocus()
            return
        }

        if (binding.textInputEditTextSpkAdditionalNotesCredit.text.toString().isEmpty()) {
            binding.textInputEditTextSpkAdditionalNotesCredit.error = getString(R.string.field_empty)
            binding.textInputEditTextSpkAdditionalNotesCredit.requestFocus()
            return
        }

        customProggress?.show()
        for (i in 0 until gridImagesUploadedResource.size) {
            val imagesUri: ResourceModel = gridImagesUploadedResource[i]
            val storageRef = storage.reference
            val fileName = UUID.randomUUID().toString()
            val refImages = storageRef.child("images/resourceSpkCredit/$fileName")

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
                                saveDataSpkCreditToCloudFirestore()
                                updateCustomer()
                            }
                        }
                    } else {
                        customProggress?.dismiss()
                        counterSpk++
                        Toast.makeText(baseContext, "Couldn't Upload" + gridImagesUploadedResource[i], Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    private fun saveDataSpkCreditToCloudFirestore() {

        val id = binding.textInputEditTextSpkPoliceNumber.text.toString()
        val uidCredit = auth.currentUser?.uid
        val imageResource : HashMap<String, String> = HashMap()
        val spkCredit = SpkCreditModel(
            binding.textViewSpkDate.text.toString(),
            imageResource,
            binding.textInputEditTextSpkCustomerName.text.toString(),
            binding.textInputEditTextSpkCustomerAddress.text.toString(),
            binding.textInputEditTextSpkCustomerMobileNumber.text.toString(),
            binding.textInputEditTextSpkCustomerEmail.text.toString(),
            binding.textInputEditTextSpkNameCar.text.toString(),
            binding.textInputEditTextSpkPoliceNumber.text.toString(),
            binding.textInputEditTextSpkMachineNumber.text.toString(),
            binding.textInputEditTextSpkChassisNumber.text.toString(),
            binding.textInputEditTextSpkPriceCash.text.toString(),
            selectedFinance,
            binding.textInputEditTextSpkDiscountCredit.text.toString(),
            binding.textInputEditTextSpkSoldAt.text.toString(),
            binding.textInputEditTextSpkPrePaymentCredit.text.toString(),
            binding.textInputEditTextSpkDownPayment.text.toString(),
            binding.textInputEditTextSpkRemainingDownPayment.text.toString(),
            binding.textInputEditTextSpkTenor.text.toString(),
            binding.textInputEditTextSpkMonthlyInstallment.text.toString(),
            binding.textInputEditTextSpkAdditionalNotesCredit.text.toString(),
            uidCredit.toString()
        )

        for (i in 0 until saveImagesUrlSpk.size) {
            imageResource["imagesResource$i"] = saveImagesUrlSpk[i]
        }
        val db = Firebase.firestore

        db.document("spkCredit/$id")
            .set(spkCredit)
            .addOnSuccessListener {
                customProggress?.dismiss()
                val alertBuilder = AlertDialog.Builder(this)
                alertBuilder.setTitle(R.string.success_title)
                alertBuilder.setMessage(R.string.spk_created)
                alertBuilder.setPositiveButton("OK") { _, _->
                    val intent = Intent(this, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                }
                alertBuilder.show()
            }
            .addOnFailureListener {
                customProggress?.dismiss()
                createAlertSpk("Error", "Images Uploaded but we couldn't save them to Database", "OK", "", null, null, null)
            }
    }

    private fun createAlertSpk(alertTitle: String?, alertMessage: String?, positiveButtonText: String?,
                            negativeButtonText: String?, positiveButtonListener: DialogInterface.OnClickListener?,
                            negativeButtonListener: DialogInterface.OnClickListener?,
                            dismissListener: DialogInterface.OnDismissListener?) {
        val builder = android.app.AlertDialog.Builder(this)
        builder.setTitle(alertTitle)
            .setMessage(alertMessage)
            .setPositiveButton(positiveButtonText, positiveButtonListener)
            .setNegativeButton(negativeButtonText, negativeButtonListener)
            .setOnDismissListener(dismissListener)
            .create().show()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        trackerResource?.onSaveInstanceState(outState)
    }

    private fun updateCustomer() {
        val db = Firebase.firestore
        db.document("cars/" + model.numberPolice)
            .update("statusCars", false)
            .addOnSuccessListener {
                Log.d("ProfileActivity", "Update Success")
            }
            .addOnFailureListener {
                Log.d("ProfileActivity", "Update Failure")
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

    private fun disableEdittext(){
        binding.textInputEditTextSpkCustomerAddress.isEnabled = false
        binding.textInputEditTextSpkCustomerMobileNumber.isEnabled = false
        binding.textInputEditTextSpkNameCar.isEnabled = false
        binding.textInputEditTextSpkPoliceNumber.isEnabled = false
        binding.textInputEditTextSpkMachineNumber.isEnabled = false
        binding.textInputEditTextSpkChassisNumber.isEnabled = false
        binding.textInputEditTextSpkPriceCash.isEnabled = false
    }
}