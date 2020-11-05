package com.sanlin.parkeasy.model


import com.google.gson.annotations.SerializedName

data class ParkingLotModel(
    @SerializedName("address")
    val address: Address,
    @SerializedName("CPH")
    val cPH: Int,
    @SerializedName("capacity")
    val capacity: Int,
    @SerializedName("facilities")
    val facilities: List<String>,
    @SerializedName("id")
    val id: Int,
    @SerializedName("images")
    val images: List<String>,
    @SerializedName("location")
    val location: Location,
    @SerializedName("name")
    val name: String,
    @SerializedName("parkingLevelType")
    val parkingLevelType: String,
    @SerializedName("parkingLotID")
    val parkingLotID: Int,
    @SerializedName("parkingType")
    val parkingType: String
)