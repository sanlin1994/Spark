package com.sanlin.parkeasy.adapters

import com.sanlin.parkeasy.R
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.GridLayout
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import com.google.firebase.database.*
import kotlin.math.log


class InfoWindowAdapter(private val context: Context):GoogleMap.InfoWindowAdapter {

    private  var plotList: MutableList<String> = mutableListOf()
    private  var recyclerView:RecyclerView
    private  var girdLayoutManager: GridLayoutManager
    private  var adapter: ParkingLotRecyclerAdapter
    private  var view: View
    private  var ref:DatabaseReference

    init {
        view = LayoutInflater.from(context).inflate(R.layout.parking_spaces_info_window_layout,null)
        val firebaseDatabase = FirebaseDatabase.getInstance()
        ref = firebaseDatabase.getReference("Plot")

        recyclerView = view.findViewById<RecyclerView>(R.id.parkingLotRecyclerView)
        girdLayoutManager = GridLayoutManager(context,2)
        recyclerView.layoutManager = girdLayoutManager
        adapter = ParkingLotRecyclerAdapter(plotList,context)
        recyclerView.adapter = adapter
    }

    override fun getInfoContents(p0: Marker?): View {
        TODO("Not yet implemented")
    }

    override fun getInfoWindow(p0: Marker?): View {

        val latlng = p0?.position

        ref.addValueEventListener(object :ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                Log.i("space", "onCancelled: "+p0.message)
            }

            override fun onDataChange(p0: DataSnapshot) {
                plotList.clear()
                val iterator = p0.children.iterator()
                iterator.forEach {
                    Log.i("space", "onDataChange: "+it.getValue())
                    plotList.add(it.getValue().toString())
                }

                adapter.notifyDataSetChanged()
            }

        })

        return view

    }

}