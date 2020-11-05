package com.sanlin.parkeasy.model


import com.google.gson.annotations.SerializedName

data class Address(
    @SerializedName("city")
    val city: String,
    @SerializedName("country")
    val country: String,
    @SerializedName("countryCode")
    val countryCode: String,
    @SerializedName("postalCode")
    val postalCode: Int,
    @SerializedName("street")
    val street: String,
    @SerializedName("text")
    val text: String
)