package com.example.firest_appv2.superheroapp

import com.google.gson.annotations.SerializedName

data class SuperHeroInfoResponse(
    @SerializedName("name") val superheroName:String,
    @SerializedName("powerstats") val superheroStats: SuperheroPowerStats,
    @SerializedName("biography") val superheroBio:SuperheroBiography,
    @SerializedName("image") val superheroImage: SuperHeroDetailImage
)

data class SuperheroPowerStats(
    @SerializedName("intelligence") var intelligence:String,
    @SerializedName("strength") var strength:String,
    @SerializedName("speed") var speed:String,
    @SerializedName("durability") var durability:String,
    @SerializedName("power") var power:String,
    @SerializedName("combat") var combat:String)

data class SuperHeroDetailImage(
    @SerializedName("url") val url: String)

data class SuperheroBiography(
    @SerializedName("full-name") val fullName:String,
    @SerializedName("alter-egos") val alterEgos:String,
    @SerializedName("aliases") val alias:List<String>,
    @SerializedName("place-of-birth") val placeBirth:String,
    @SerializedName("first-appearance") val firstAppearance:String,
    @SerializedName("publisher") val publisher:String,
    @SerializedName("alignment") val alignment:String
)