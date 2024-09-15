package com.example.firest_appv2.superheroapp

import com.google.gson.annotations.SerializedName

data class SuperHeroInfoResponse(
    @SerializedName("name") val superheroName:String,
    @SerializedName("powerstats") val superheroStats: SuperheroPowerStats,
    @SerializedName("biography") val superheroBio:SuperheroBiography,
    @SerializedName("image") val superheroImage: SuperHeroDetailImage
)

data class SuperheroPowerStats(
    @SerializedName("intelligence") val intelligence:String,
    @SerializedName("strength") val strength:String,
    @SerializedName("speed") val speed:String,
    @SerializedName("durability") val durability:String,
    @SerializedName("power") val power:String,
    @SerializedName("combat") val combat:String)

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