package com.example.firest_appv2.superheroapp.Utilities

import SuperHeroeImageResponse
import SuperHeroesItemResponse
import com.example.firest_appv2.superheroapp.SuperHeroListActivity
import com.example.firest_appv2.superheroapp.superheroCompare.data.StatsSuperhero
import com.example.firest_appv2.superheroapp.superheroCompare.data.Superhero
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class Utility {
    companion object{
        var firstSuperhero:Superhero = Superhero("","","", StatsSuperhero("","","","","",""))
        var secondSuperhero: Superhero = Superhero("","","", StatsSuperhero("","","","","",""))
        var listSuperheroes:List<SuperHeroesItemResponse> = emptyList()

        fun getRetrofit(): Retrofit {
            val retrofit = Retrofit.Builder()
                .baseUrl("https://superheroapi.com/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            return retrofit
        }

    }


}