package com.sanlin.parkeasy.utilities

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.sanlin.parkeasy.model.ParkingLotModel
import java.io.IOException

class Common {

    fun readJsonAsset(context: Context):List<ParkingLotModel>{
        val jsonFileString = getJsonDataFromAsset(context, "parkingLotData.json")

        val gson = Gson()
        val listPersonType = object : TypeToken<List<ParkingLotModel>>() {}.type

        val parkinglots: List<ParkingLotModel> = gson.fromJson(jsonFileString, listPersonType)
        return  parkinglots
    }

     fun getJsonDataFromAsset(context: Context, fileName: String): String? {
        val jsonString: String
        try {
            jsonString = context.assets.open(fileName).bufferedReader().use { it.readText() }
        } catch (ioException: IOException) {
            ioException.printStackTrace()
            return null
        }
        return jsonString
    }


}