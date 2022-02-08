package com.project.projectalgi001.user.ui.home

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
import com.project.projectalgi001.databinding.ActivityCarsDetailsBinding
import com.project.projectalgi001.user.adapter.GridCarsAdapter
import com.project.projectalgi001.user.adapter.ImageSliderAdapter
import com.project.projectalgi001.user.model.Cars
import com.project.projectalgi001.user.ui.spk.SpkActivity
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class CarsDetailsActivity : AppCompatActivity() {
    private lateinit var binding : ActivityCarsDetailsBinding
    private lateinit var adapter : ImageSliderAdapter
    private lateinit var dots : ArrayList<TextView>
    private var carImages : HashMap<String,String>? = HashMap()
    private var carsModel : Cars? = null


    companion object {
        const val EXTRA_CARS = "extra_cars"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityCarsDetailsBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val toolbar: androidx.appcompat.widget.Toolbar = binding.toolbarCd
        setSupportActionBar(toolbar)

        val navBarTitle = intent.getStringExtra(GridCarsAdapter.VIDEO_TITLE_KEY)
        supportActionBar?.title = navBarTitle

        setDetailCar()
        adapter = ImageSliderAdapter(carImages!!)
        binding.viewPager.adapter = adapter
        dots = ArrayList()

        setIndicator()
        disableButton()
        binding.buttonSPK.setOnClickListener {
            val carParcelable = intent.getParcelableExtra<Cars>(EXTRA_CARS) as Cars
            val intent = Intent(this, SpkActivity::class.java)
            intent.putExtra(SpkActivity.EXTRA_CARS_SPK, carParcelable)
            intent.putExtra(SpkActivity.NAME_CARS, carsModel?.nameCars)
            startActivity(intent)
        }

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                selectedDot(position)
                super.onPageSelected(position)
            }
        })
    }

    private fun setDetailCar() {
        val carParcelable = intent.getParcelableExtra<Cars>(EXTRA_CARS) as Cars
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

    private fun disableButton(){
        val carParcelable = intent.getParcelableExtra<Cars>(EXTRA_CARS) as Cars
        carsModel = carParcelable

        if (carsModel!!.carStatus == false) {
            binding.buttonSPK.isEnabled = false
            binding.buttonSPK.isClickable = false
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                binding.buttonSPK.setTextColor(getColor(android.R.color.transparent))
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                binding.buttonSPK.setBackgroundColor(getColor(android.R.color.transparent))
            }
        }
    }
}