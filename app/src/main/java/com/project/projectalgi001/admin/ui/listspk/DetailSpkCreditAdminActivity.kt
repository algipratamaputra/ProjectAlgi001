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
import com.project.projectalgi001.R
import com.project.projectalgi001.admin.MainActivityAdmin
import com.project.projectalgi001.admin.model.DoModelCredit
import com.project.projectalgi001.databinding.ActivityDetailSpkCreditAdminBinding
import com.project.projectalgi001.user.model.FetchSpkCashModel
import com.project.projectalgi001.user.model.FetchSpkCreditModel
import com.project.projectalgi001.utils.DataToPDF
import com.project.projectalgi001.utils.DataToPDFCredit
import java.text.NumberFormat
import java.util.*

class DetailSpkCreditAdminActivity : AppCompatActivity() {

    private lateinit var binding : ActivityDetailSpkCreditAdminBinding
    private lateinit var model : FetchSpkCreditModel
    private var idUser : String? = null

    companion object{
        const val EXTRA_SPK_CREDIT_ADMIN = "extra_spk_credit_admin"
        const val NAME_CAR_ADMIN = "name_car_admin"
        private const val REQUEST_EXTERNAL_STORAGE = 1
        private val PERMISSIONS_STORAGE = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityDetailSpkCreditAdminBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val toolbar: androidx.appcompat.widget.Toolbar = binding.toolbarSpkCreditAdm
        setSupportActionBar(toolbar)

        val navBarTitle = intent.getStringExtra(NAME_CAR_ADMIN)
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
            setDataDoCredit()
            deleteSpk()
            deleteCar()
        }
        binding.cancelSpk.setOnClickListener {
            deleteSpk()
            updateCar()
            val alertBuilder = AlertDialog.Builder(this)
            alertBuilder.setTitle(getString(R.string.success_title))
            alertBuilder.setMessage("SPK Deleted")
            alertBuilder.setPositiveButton("OK") { _, _->
                val intent = Intent(this, MainActivityAdmin::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
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
            DataToPDFCredit(intent.getParcelableExtra<FetchSpkCreditModel>(EXTRA_SPK_CREDIT_ADMIN) as FetchSpkCreditModel,this,displayMetrics.widthPixels,displayMetrics.heightPixels).generatePDF()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        val dis = DisplayMetrics()
        this.windowManager.defaultDisplay.getRealMetrics(dis)
        DataToPDF(intent.getParcelableExtra<FetchSpkCashModel>(EXTRA_SPK_CREDIT_ADMIN) as FetchSpkCashModel,this,dis.widthPixels,dis.heightPixels).generatePDF()

    }

    private fun disableEditText() {
        binding.textInputEditTextSpkCreditCustomerNameAdm.isEnabled = false
        binding.textInputEditTextSpkCreditCustomerAddressAdm.isEnabled = false
        binding.textInputEditTextSpkCreditCustomerMobileNumberAdm.isEnabled = false
        binding.textInputEditTextSpkCreditCustomerEmailAdm.isEnabled = false
        binding.textInputEditTextSpkCreditNameCarAdm.isEnabled = false
        binding.textInputEditTextSpkCreditPoliceNumberAdm.isEnabled = false
        binding.textInputEditTextSpkCreditMachineNumber.isEnabled = false
        binding.textInputEditTextSpkCreditChassisNumberAdm.isEnabled = false
        binding.textInputEditTextSpkCreditPriceAdm.isEnabled = false
        binding.textInputEditTextSpkCreditFinanceAdm.isEnabled = false
        binding.textInputEditTextSpkCreditDiscountAdm.isEnabled = false
        binding.textInputEditTextSpkCreditSoldAtAdm.isEnabled = false
        binding.textInputEditTextSpkCreditPrePaymentAdm.isEnabled = false
        binding.textInputEditTextSpkCreditDownPaymentAdm.isEnabled = false
        binding.textInputEditTextSpkCreditRemainingDownPaymentAdm.isEnabled = false
        binding.textInputEditTextSpkCreditTenorAdm.isEnabled = false
        binding.textInputEditTextSpkCreditMonthlyInstallmentAdm.isEnabled = false
        binding.textInputEditTextSpkAdditionalNotesCreditAdm.isEnabled = false
    }

    private fun setDetailSpkCash() {
        val parcelableSpkCredit = intent.getParcelableExtra<FetchSpkCreditModel>(EXTRA_SPK_CREDIT_ADMIN) as FetchSpkCreditModel
        model = parcelableSpkCredit
        val localeID = Locale("in", "ID")
        val formatRupiah = NumberFormat.getCurrencyInstance(localeID)

        val date = binding.textViewSpkCreditDateAdm
        val customerName = binding.textInputEditTextSpkCreditCustomerNameAdm as TextView
        val customerAddress = binding.textInputEditTextSpkCreditCustomerAddressAdm as TextView
        val customerMobileNumber = binding.textInputEditTextSpkCreditCustomerMobileNumberAdm as TextView
        val customerEmail = binding.textInputEditTextSpkCreditCustomerEmailAdm as TextView
        val nameCar = binding.textInputEditTextSpkCreditNameCarAdm as TextView
        val policeNumber = binding.textInputEditTextSpkCreditPoliceNumberAdm as TextView
        val machineNumber = binding.textInputEditTextSpkCreditMachineNumber as TextView
        val chassisNumber = binding.textInputEditTextSpkCreditChassisNumberAdm as TextView
        val price = binding.textInputEditTextSpkCreditPriceAdm as TextView
        val finance = binding.textInputEditTextSpkCreditFinanceAdm as TextView
        val discount = binding.textInputEditTextSpkCreditDiscountAdm as TextView
        val netOfDiscount = binding.textInputEditTextSpkCreditSoldAtAdm as TextView
        val prepayment = binding.textInputEditTextSpkCreditPrePaymentAdm as TextView
        val downPayment = binding.textInputEditTextSpkCreditDownPaymentAdm as TextView
        val remainingDownPayment = binding.textInputEditTextSpkCreditRemainingDownPaymentAdm as TextView
        val tenor = binding.textInputEditTextSpkCreditTenorAdm as TextView
        val monthlyInstallment = binding.textInputEditTextSpkCreditMonthlyInstallmentAdm as TextView
        val additionalNotes = binding.textInputEditTextSpkAdditionalNotesCreditAdm as TextView

        date.text = parcelableSpkCredit.dateCredit
        customerName.text = parcelableSpkCredit.nameBuyerCredit
        customerAddress.text = parcelableSpkCredit.addressBuyerCredit
        customerMobileNumber.text = parcelableSpkCredit.mobileNumberBuyerCredit
        customerEmail.text = parcelableSpkCredit.emailBuyerCredit
        nameCar.text = parcelableSpkCredit.nameCarSoldCredit
        policeNumber.text = parcelableSpkCredit.policeNumberCarSoldCredit
        machineNumber.text = parcelableSpkCredit.machineNumberCarSoldCredit
        chassisNumber.text = parcelableSpkCredit.chassisNumberCarSoldCredit
        price.text = parcelableSpkCredit.priceCarSoldCredit
        finance.text = parcelableSpkCredit.finance
        discount.text = formatRupiah.format(parcelableSpkCredit.creditDiscount?.toInt())
        netOfDiscount.text = formatRupiah.format(parcelableSpkCredit.creditNetOfDiscount?.toInt())
        prepayment.text = formatRupiah.format(parcelableSpkCredit.creditPrepayment?.toInt())
        downPayment.text = formatRupiah.format(parcelableSpkCredit.creditDownPayment?.toInt())
        remainingDownPayment.text = formatRupiah.format(parcelableSpkCredit.downPaymentRemaining?.toInt())
        tenor.text = parcelableSpkCredit.creditTenor
        monthlyInstallment.text = parcelableSpkCredit.creditMonthlyInstallment
        additionalNotes.text = parcelableSpkCredit.creditAdditionalNotes
        model.userId = parcelableSpkCredit.userId
    }

    private fun setDataDoCredit(){
        val doCredit = DoModelCredit(
            model.dateCredit,
            model.imageResourceCredit,
            model.nameBuyerCredit,
            model.addressBuyerCredit,
            model.mobileNumberBuyerCredit,
            model.emailBuyerCredit,
            model.nameCarSoldCredit,
            model.policeNumberCarSoldCredit,
            model.machineNumberCarSoldCredit,
            model.chassisNumberCarSoldCredit,
            model.priceCarSoldCredit,
            model.finance,
            model.creditDiscount,
            model.creditNetOfDiscount,
            model.creditPrepayment,
            model.creditDownPayment,
            model.downPaymentRemaining,
            model.creditTenor,
            model.creditMonthlyInstallment,
            model.creditAdditionalNotes,
            model.userId,
        )

        val db = Firebase.firestore
        db.document("CarDo/${model.policeNumberCarSoldCredit}")
            .set(doCredit)
            .addOnSuccessListener {

            }
            .addOnFailureListener {
            }
    }

    private fun deleteSpk(){
        val idSpk = model.policeNumberCarSoldCredit
        val db =Firebase.firestore

        db.collection("spkCredit").document("$idSpk")
            .delete()
            .addOnSuccessListener {
            }
            .addOnFailureListener {
                Log.d("Delete", "gagal")
            }
    }

    private fun updateCar() {

        val db = Firebase.firestore
        db.document("cars/" + model.policeNumberCarSoldCredit)
            .update("statusCars", true)
            .addOnSuccessListener {
            }
            .addOnFailureListener {
                Log.d("ProfileActivity", "Update Failure")
            }
    }

    private fun deleteCar(){
        val idSpk = model.policeNumberCarSoldCredit
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
}