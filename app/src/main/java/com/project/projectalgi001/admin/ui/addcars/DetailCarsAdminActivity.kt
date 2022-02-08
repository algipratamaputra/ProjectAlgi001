package com.project.projectalgi001.admin.ui.addcars

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.viewpager2.widget.ViewPager2
import com.project.projectalgi001.R
import com.project.projectalgi001.admin.adapter.ListCarsAdapter
import com.project.projectalgi001.databinding.ActivityDetailCarsAdminBinding
import com.project.projectalgi001.user.adapter.ImageSliderAdapter
import com.project.projectalgi001.user.model.Cars
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class DetailCarsAdminActivity : AppCompatActivity() {

    private lateinit var binding : ActivityDetailCarsAdminBinding
    private lateinit var adapter : ImageSliderAdapter
    private lateinit var dots : ArrayList<TextView>
    private var carsModel : Cars? = null
    private var carImages : HashMap<String,String>? = HashMap()

    companion object {
        const val EXTRA_CARS_ADMIN = "extra_cars"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityDetailCarsAdminBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val toolbar: androidx.appcompat.widget.Toolbar = binding.toolbarDetailCarAdmin
        setSupportActionBar(toolbar)

        val navBarTitle = intent.getStringExtra(ListCarsAdapter.EXTRA_NAME)
        supportActionBar?.title = navBarTitle

        setDetailCar()
        adapter = ImageSliderAdapter(carImages!!)
        binding.viewPager.adapter = adapter
        dots = ArrayList()
        setIndicator()

        binding.buttonUpdateCar.setOnClickListener {
            val carIntent = intent.getParcelableExtra<Cars>(EXTRA_CARS_ADMIN) as Cars
            val intent = Intent(this, UpdateCarActivity::class.java)
            intent.putExtra(UpdateCarActivity.EXTRA_CARS_UPDATE, carIntent)
            intent.putExtra(UpdateCarActivity.NAME_CARS_UPDATE, carsModel?.nameCars)
            startActivity(intent)
        }

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                selectedDot(position)
                super.onPageSelected(position)
            }
        })
    }

    private fun selectedDot(position: Int) {
        for (i in 0 until carImages?.size!!){
            if (i == position)
                dots[i].setTextColor(ContextCompat.getColor(this, R.color.color_three))
            else
                dots[i].setTextColor(ContextCompat.getColor(this, R.color.teal_700))
        }
    }

    private fun setIndicator() {
        for (i in 0 until carImages!!.size) {
            dots.add(TextView(this))
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                dots[i].text = Html.fromHtml("&#9679", Html.FROM_HTML_MODE_LEGACY).toString()
            }else {
                dots[i].text = HtmlCompat.fromHtml("&#9679", HtmlCompat.FROM_HTML_MODE_LEGACY)
            }
            dots[i].textSize = 18f
            binding.dotsIndicator.addView(dots[i])
        }
    }

    private fun setDetailCar() {
        val carParcelable = intent.getParcelableExtra<Cars>(EXTRA_CARS_ADMIN) as Cars
        carsModel = carParcelable
        val localeID = Locale("in", "ID")
        val formatRupiah = NumberFormat.getCurrencyInstance(localeID)

        binding.nameCarDetail.text = carParcelable.nameCars
        binding.priceCarDetail.text = formatRupiah.format(carParcelable.priceCars?.toInt())
        binding.getPoliceNumberDetail.text = carParcelable.numberPolice
        binding.getMachineNumberDetail.text = carParcelable.numberMachine
        binding.getChassisNumberDetail.text = carParcelable.numberChassis
        binding.getBpkbStatusDetail.text = carParcelable.statusBpkb
        binding.getStnkStatusDetail.text = carParcelable.statusStnk
        binding.getCreditPriceDetail.text = formatRupiah.format(carParcelable.creditPriceCars?.toInt())
        binding.getDpMinimumDetail.text = formatRupiah.format(carParcelable.dpMinimum?.toInt())
        carImages = carParcelable.carsImage
    }
}