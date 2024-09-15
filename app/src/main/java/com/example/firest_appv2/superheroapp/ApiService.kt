package com.example.firest_appv2.superheroapp

import SuperHeroDataResponse
import com.example.firest_appv2.superheroapp.SuperHeroInfoResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {

    //En GET ponemos el final de la ruta de la API que estamos usando
    @GET("4e4b684a88728637b951c889924f7449/search/{name}")

    //La función getSuperheroes va recibir un path, una parte de la url a la que vamos acceder
    //va buscar name en la ruta y lo va sustituir por el contenido de superheroName
    //De todos los datos que tenga, solo nos va devolver los que definamos en SuperHeroDataResponse

    suspend fun getSuperheroes(@Path("name") superheroName:String):Response<SuperHeroDataResponse>


    @GET("4e4b684a88728637b951c889924f7449/{id}")
    suspend fun getSuperheroeInfo(@Path("id") superheroId:String):Response<SuperHeroInfoResponse>
}