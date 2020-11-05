package com.sanlin.parkeasy.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.sanlin.myarchitecture.utils.Resource
import com.sanlin.parkeasy.model.ParkingLotModel
import com.sanlin.parkeasy.repositories.ParkEasyRepository

class ParkingLotViewModel(private val parkEasyRepository: ParkEasyRepository):ViewModel() {

    val parkingLots: LiveData<Resource<List<ParkingLotModel>>> by lazy {
        parkEasyRepository.getParkingLots()
    }

}