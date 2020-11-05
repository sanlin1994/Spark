package com.sanlin.parkeasy.view

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.firebase.database.*
import com.sanlin.myarchitecture.network.RetrofitClient
import com.sanlin.parkeasy.R
import com.sanlin.parkeasy.adapters.ParkingLotRecyclerAdapter
import com.sanlin.parkeasy.model.ParkingLotModel
import com.sanlin.parkeasy.repositories.ParkEasyRepository
import com.sanlin.parkeasy.utilities.Common
import com.sanlin.parkeasy.viewmodel.ParkingLotViewModel
import java.util.*

class ParkEasyMapsActivity : AppCompatActivity(), OnMapReadyCallback,
    GoogleMap.OnMarkerClickListener {

    private lateinit var parkingLotList: List<ParkingLotModel>
    private lateinit var mMap: GoogleMap
    private  var plotList: MutableList<String> = mutableListOf()
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var reference: DatabaseReference
    private lateinit var parkingLotViewModel: ParkingLotViewModel
    private lateinit var repository: ParkEasyRepository
    private val TAG:String = "parkinglot"
    private lateinit var lastKnownLocation:Location
    private  var locationPermissionGranted:Boolean = false
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private val defaultLocation = LatLng(12.9716, 77.5946)
    private  val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1
    private  val DEFAULT_ZOOM = 15

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_park_easy_maps)
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        val apiService = RetrofitClient.getClient()

        repository = ParkEasyRepository(apiService)

        parkingLotViewModel  = getViewModel()

        autoCompleteSearchBar()
    }

    private fun autoCompleteSearchBar(){
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), "your google api key")
        }
        val autocompleteFragment =
            supportFragmentManager.findFragmentById(R.id.autocomplete_fragment)
                    as AutocompleteSupportFragment

        autocompleteFragment.setPlaceFields(listOf(Place.Field.ID, Place.Field.NAME))

        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                Log.i(TAG, "Place: ${place.name}, ${place.id}")
            }

            override fun onError(p0: com.google.android.gms.common.api.Status) {

            }


        })
    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.setOnMarkerClickListener(this)
        getLocationPermission()
        getCurrentLocation()

//        parkingLotViewModel.parkingLots.observe(this, Observer {
//            when(it.status){
//                Status.LOADING -> Log.i(TAG, "onLoading: Loading....")
//                Status.SUCCESS -> Log.i(TAG, "onSuccess: "+it.data?.get(0)?.name)
//                Status.ERROR -> Log.i(TAG, "onError: "+it.message)
//            }
//        })

        val common = Common()
        parkingLotList = common.readJsonAsset(applicationContext)
        val iterator = parkingLotList.iterator()
        iterator.forEach {
            addMarker(latLng = LatLng(it.location.latitude,it.location.longitude),id = it.id)
        }
    }


    private fun getCurrentLocation(){
        try {
            if (locationPermissionGranted) {
                val locationResult = fusedLocationProviderClient.lastLocation
                locationResult.addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Set the map's camera position to the current location of the device.
                        lastKnownLocation = task.result
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                LatLng(defaultLocation.latitude,
                                    defaultLocation.longitude), DEFAULT_ZOOM.toFloat()))
                    } else {
                        Log.d(TAG, "Current location is null. Using defaults.")
                        Log.e(TAG, "Exception: %s", task.exception)
                        mMap.moveCamera(CameraUpdateFactory
                            .newLatLngZoom(defaultLocation, DEFAULT_ZOOM.toFloat()))
                        mMap.uiSettings?.isMyLocationButtonEnabled = false
                    }
                }
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message, e)
        }
    }

    private fun getLocationPermission() {
        if (ContextCompat.checkSelfPermission(this.applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION)
        }
    }

    private fun bitMapFromVector(vectorResID:Int): BitmapDescriptor {
        val vectorDrawable= ContextCompat.getDrawable(applicationContext!!,vectorResID)
        vectorDrawable!!.setBounds(0,0,vectorDrawable!!.intrinsicWidth,vectorDrawable.intrinsicHeight)
        val bitmap= Bitmap.createBitmap(vectorDrawable.intrinsicWidth,vectorDrawable.intrinsicHeight,Bitmap.Config.ARGB_8888)
        val canvas= Canvas(bitmap)
        vectorDrawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    private fun addMarker(latLng: LatLng,id: Int){
        val marker = mMap.addMarker(
            MarkerOptions()
                .position(latLng)
                .icon(bitMapFromVector(R.drawable.ic_parking))
        )
        marker.tag = id
    }

    private fun showParkingInfoAlert(id:Int){

        val parkingLot = parkingLotList.find { it.id == id }

        var availableSpaces = 0

        val dialogView = layoutInflater.inflate(R.layout.parking_spaces_info_window_layout, null)

        val customDialog = AlertDialog
            .Builder(this)
                .setView(dialogView)
                .show()

        customDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            val progressBar = dialogView.findViewById<ProgressBar>(R.id.pBar3)
            val navigate = dialogView.findViewById<Button>(R.id.parkingLotNavigation)
            val recyclerView = dialogView.findViewById<RecyclerView>(R.id.parkingLotRecyclerView)
            val moreDetails = dialogView.findViewById<Button>(R.id.parkingLotMoreDetails)
            val title = dialogView.findViewById<TextView>(R.id.parkingLotName)
            title.setText(parkingLot?.name)
            moreDetails.setOnClickListener {
                val intent = Intent(this@ParkEasyMapsActivity,ParkingDetailsActivity::class.java)
                intent.putExtra("id",id)
                intent.putExtra("AS",availableSpaces)
                startActivity(intent)
            }

            navigate.setOnClickListener {
                val uri = java.lang.String.format(
                    Locale.ENGLISH,
                    "http://maps.google.com/maps?saddr=%f,%f(%s)&daddr=%f,%f (%s)", 12.9716, 77.5946,
                    "Home Sweet Home", parkingLot?.location?.latitude, parkingLot?.location?.longitude,
                    "Where the party is at"
                )
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
                intent.setPackage("com.google.android.apps.maps")
                startActivity(intent)
            }

            val closeButton = dialogView.findViewById<ImageButton>(R.id.closeButton)
            closeButton.setOnClickListener {
                customDialog.dismiss()
            }
            val girdLayoutManager = GridLayoutManager(applicationContext,3)
            recyclerView.layoutManager = girdLayoutManager
            val adapter = ParkingLotRecyclerAdapter(plotList,applicationContext)
            recyclerView.adapter = adapter

            firebaseDatabase = FirebaseDatabase.getInstance()
            reference = firebaseDatabase.getReference(id.toString()).child("1")

            reference.addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    Log.i("space", "onCancelled: "+p0.message)
                    if (progressBar.isShown)
                    progressBar.visibility = View.INVISIBLE
                }

                override fun onDataChange(p0: DataSnapshot) {
                    plotList.clear()
                    val iterator = p0.children.iterator()
                    iterator.forEach {
                        Log.i("space", "onDataChange: "+it.getValue())
                        plotList.add(it.getValue().toString())
                    }
                    availableSpaces = Collections.frequency(plotList,"00")
                    adapter.notifyDataSetChanged()
                    if (progressBar.isShown)
                    progressBar.visibility = View.INVISIBLE
                }
            })

    }

    override fun onMarkerClick(p0: Marker?): Boolean {
        showParkingInfoAlert(p0?.tag as Int)
        return false
    }

    private fun getViewModel(): ParkingLotViewModel{
        return ViewModelProviders.of(this,object: ViewModelProvider.Factory{
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return ParkingLotViewModel(repository) as T
            }
        })[ParkingLotViewModel::class.java]
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        locationPermissionGranted = false
        when (requestCode) {
            PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION -> {
                if (grantResults.isNotEmpty() &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationPermissionGranted = true
                }
            }
        }
    }


}
