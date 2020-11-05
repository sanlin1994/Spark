package com.sanlin.myarchitecture.network

import com.sanlin.parkeasy.model.ParkingLotModel
import retrofit2.Call
import retrofit2.http.GET

interface ApiService {

    @GET("parking")
    fun getProducts():Call<List<ParkingLotModel>>


}