import com.google.gson.annotations.SerializedName

//La API del ejemplo nos proporciona mucha información sobre cada elemento, en la siguiente
//data class, tenemos que identificar la información que queremos que nos devuelva
data class SuperHeroDataResponse(
    @SerializedName("response") val response:String,
    @SerializedName("results") val superheroes:List<SuperHeroesItemResponse>)

data class SuperHeroesItemResponse(
    @SerializedName("id") val superheroId:String,
    @SerializedName("name") val name:String,
    @SerializedName("image") val superheroImage:SuperHeroeImageResponse
)

data class SuperHeroeImageResponse(
    @SerializedName("url") val url:String
)

