package com.project.projectalgi001.admin.ui.listspk

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.project.projectalgi001.admin.MainActivityAdmin
import com.project.projectalgi001.admin.adapter.ListSpkCashAdminAdapter
import com.project.projectalgi001.admin.model.DoModel
import com.project.projectalgi001.databinding.ActivityDetailSpkCashAdminBinding
import com.project.projectalgi001.user.model.FetchSpkCashModel
import com.project.projectalgi001.utils.DataToPDF
import java.text.NumberFormat
import java.util.*


class DetailSpkCashAdminActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_SPK_CASH_ADMIN = "extra_spk_cash_admin"
        private const val REQUEST_EXTERNAL_STORAGE = 1
        private val PERMISSIONS_STORAGE = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    }

    private lateinit var binding: ActivityDetailSpkCashAdminBinding
    private lateinit var model : FetchSpkCashModel
    private var idUser : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityDetailSpkCashAdminBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val toolbar: androidx.appcompat.widget.Toolbar = binding.toolbarSpkCash
        setSupportActionBar(toolbar)

        val navBarTitle = intent.getStringExtra(ListSpkCashAdminAdapter.NAME_CAR)
        supportActionBar?.title = "SPK $navBarTitle"

        setDetailSpkCash()
        disableEditText()
        binding.createDo.setOnClickListener {
            val alertBuilder = AlertDialog.Builder(this)
            alertBuilder.setTitle("Success")
            alertBuilder.setMessage("DO Created !")
            alertBuilder.setPositiveButton("OK") { _, _->
                this.verifyStoragePermissions(this)
            }
            alertBuilder.show()
            createDataDo()
            deleteSpk()
            deleteCar()
            }

        binding.cancelSpk.setOnClickListener {
            updateCar()
            deleteSpk()
            val alertBuilder = AlertDialog.Builder(this)
            alertBuilder.setTitle("Success")
            alertBuilder.setMessage("SPK Deleted")
            alertBuilder.setPositiveButton("OK") { _, _->
                val intent = Intent(this, MainActivityAdmin::class.java)
                startActivity(intent)
            }
            alertBuilder.show()
        }
    }


    private fun verifyStoragePermissions(activity: Activity?) {

        val permission = ActivityCompat.checkSelfPermission(activity!!, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE)
        }
        else{
            val displayMetrics = DisplayMetrics()
            activity.windowManager.defaultDisplay.getRealMetrics(displayMetrics)
            DataToPDF(intent.getParcelableExtra<FetchSpkCashModel>(EXTRA_SPK_CASH_ADMIN) as FetchSpkCashModel,this,displayMetrics.widthPixels,displayMetrics.heightPixels).generatePDF()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        val dis = DisplayMetrics()
        this.windowManager.defaultDisplay.getRealMetrics(dis)
        DataToPDF(intent.getParcelableExtra<FetchSpkCashModel>(EXTRA_SPK_CASH_ADMIN) as FetchSpkCashModel,this,dis.widthPixels,dis.heightPixels).generatePDF()

    }

    private fun setDetailSpkCash() {
        val spkCashParcelable = intent.getParcelableExtra<FetchSpkCashModel>(EXTRA_SPK_CASH_ADMIN) as FetchSpkCashModel
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
        val prepaymentCash = binding.TextInputEditTextSpkCashPrePayment as TextView
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
        idUser = spkCashParcelable.userId

    }

    private fun createDataDo(){
        val dataDo = DoModel(
            model.dateCash,
            model.resourceImageCash,
            model.buyerNameCash,
            model.buyerAddressCash,
            model.buyerMobileNumberCash,
            model.buyerEmailCash,
            model.carNameCash,
            model.carPoliceNumberCash,
            model.carMachineNumberCash,
            model.carChassisNumberCash,
            model.carPriceCash,
            model.cashDiscount,
            model.netOfDiscountCash,
            model.cashPrepayment,
            model.plantDelivery,
            model.paymentRemaining,
            model.notesAdditional,
            model.userId
        )

        val db = Firebase.firestore
        db.document("CarDo/${model.carPoliceNumberCash}")
            .set(dataDo)
            .addOnSuccessListener {

            }
            .addOnFailureListener {
            }
    }

    private fun deleteSpk(){
        val idSpk = model.carPoliceNumberCash
        val db =Firebase.firestore

        db.collection("spkCash").document("$idSpk")
            .delete()
            .addOnSuccessListener {
                Log.d("Delete", "Berhasil")
            }
            .addOnFailureListener {
                Log.d("Delete", "gagal")
            }
    }

    private fun deleteCar(){
        val idSpk = model.carPoliceNumberCash
        val db =Firebase.firestore

        db.collection("cars").document("$idSpk")
            .delete()
            .addOnSuccessListener {
                Log.d("Delete", "Berhasil")
            }
            .addOnFailureListener {
                Log.d("Delete", "gagal")
            }
    }

    private fun updateCar() {

        val db = Firebase.firestore
        db.document("cars/" + model.carPoliceNumberCash)
            .update("statusCars", true)
            .addOnSuccessListener {
            }
            .addOnFailureListener {
                Log.d("ProfileActivity", "Update Failure")
            }
    }

    private fun disableEditText(){
        binding.textInputEditTextSpkCashCustomerName.isEnabled = false
        binding.textInputEditTextSpkCashCustomerAddress.isEnabled = false
        binding.textInputEditTextSpkCashCustomerMobileNumber.isEnabled = false
        binding.textInputEditTextSpkCashCustomerEmail.isEnabled = false
        binding.textInputEditTextSpkCashNameCar.isEnabled = false
        binding.textInputEditTextSpkCashPoliceNumber.isEnabled = false
        binding.textInputEditTextSpkCashMachineNumber.isEnabled = false
        binding.textInputEditTextSpkCashChassisNumber.isEnabled = false
        binding.textInputEditTextSpkCashPriceCash.isEnabled = false
        binding.TextInputEditTextSpkCashDiscount.isEnabled = false
        binding.TextInputEditTextSpkCashSoldAt.isEnabled = false
        binding.TextInputEditTextSpkCashPrePayment.isEnabled = false
        binding.TextInputEditTextSpkCashPlanDelivery.isEnabled = false
        binding.TextInputEditTextSpkCashRemainingPayment.isEnabled = false
        binding.TextInputEditTextSpkCashAdditionalNotes.isEnabled = false
    }
}