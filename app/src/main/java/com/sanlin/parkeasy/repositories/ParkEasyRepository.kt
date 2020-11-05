package com.sanlin.parkeasy.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.sanlin.myarchitecture.network.ApiService
import com.sanlin.myarchitecture.utils.Resource
import com.sanlin.myarchitecture.utils.Status
import com.sanlin.parkeasy.model.ParkingLotModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ParkEasyRepository(private val apiService: ApiService) {

    fun getParkingLots():LiveData<Resource<List<ParkingLotModel>>>{

        val resourceData: MutableLiveData<Resource<List<ParkingLotModel>>> = MutableLiveData()
        var resource:Resource<List<ParkingLotModel>> = Resource(Status.LOADING,null,"")
        resourceData.postValue(resource)

        apiService.getProducts().enqueue(object : Callback<List<ParkingLotModel>> {
            override fun onResponse(call: Call<List<ParkingLotModel>>, response: Response<List<ParkingLotModel>>) {
                resource = Resource(Status.SUCCESS,response.body()!!,"")
                resourceData.postValue(resource)
            }

            override fun onFailure(call: Call<List<ParkingLotModel>>, t: Throwable) {
                resource = Resource(Status.ERROR,null,t.message.toString())
                resourceData.postValue(resource)
            }
        })

        return resourceData

    }


}