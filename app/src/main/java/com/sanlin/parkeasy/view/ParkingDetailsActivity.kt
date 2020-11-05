package com.sanlin.parkeasy.view

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.sanlin.parkeasy.R
import com.sanlin.parkeasy.adapters.ImageSliderAdapter
import com.sanlin.parkeasy.utilities.Common

class ParkingDetailsActivity : AppCompatActivity() {

    private lateinit var pagerAdapter: PagerAdapter
    private  var imageList: MutableList<String> = mutableListOf()
    private lateinit var availableSpaces:TextView
    private lateinit var CPH:TextView
    private lateinit var capacity:TextView
    private lateinit var address:TextView
    private lateinit var parkingType:TextView
    private lateinit var floors:TextView
    private lateinit var facilities:TextView
    private lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_parking_details)
        setToolBar()
        UIinit()

        //get id from intent
        val id = intent.getIntExtra("id",127701)
        val AS = intent.getIntExtra("AS",0)

        //load data from json file
        val common = Common()
        val parkingLot = common.readJsonAsset(applicationContext).find { it.id == id }
        imageList = parkingLot!!.images as MutableList
        availableSpaces.setText(AS.toString())
        CPH.setText(parkingLot.cPH.toString())
        capacity.setText(parkingLot.capacity.toString())
        address.setText(parkingLot.address.text)
        parkingType.setText(parkingLot.parkingType)
        floors.setText(parkingLot.parkingLevelType)
        val iterator = parkingLot.facilities
        var feature = ""
        iterator.forEach {
            feature = feature + it+","
        }
        facilities.setText(feature)
        toolbar.setTitle(parkingLot.name)
        setViewPager()
    }

    private fun setViewPager() {
        val viewPager = findViewById<ViewPager>(R.id.detailsViewPager)
        pagerAdapter = ImageSliderAdapter(applicationContext,imageList)
        viewPager.adapter = pagerAdapter
    }

    private fun UIinit() {
        availableSpaces = findViewById(R.id.detailsActivityAS)
        CPH = findViewById(R.id.detailsActivityCPH)
        capacity = findViewById(R.id.detailsActivityCapacity)
        address = findViewById(R.id.detailsActivityAddress)
        parkingType = findViewById(R.id.detailsActivityParkingType)
        floors = findViewById(R.id.detailsActivityFloorsNo)
        facilities = findViewById(R.id.detailsActivityFacilities)

    }

    private fun setToolBar(){
        toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.setTitle("Forum mall parking")
        setSupportActionBar(toolbar)

            getSupportActionBar()?.setDisplayHomeAsUpEnabled(true);
            getSupportActionBar()?.setDisplayShowHomeEnabled(true);

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}