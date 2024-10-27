package com.example.firest_appv2.superheroapp.Utilities

import com.example.firest_appv2.superheroapp.superheroCompare.data.StatsSuperhero
import com.example.firest_appv2.superheroapp.superheroCompare.data.Superhero

class Utility {
    companion object{
        var firstSuperhero:Superhero = Superhero("","","", StatsSuperhero("","","","","",""))
        var secondSuperhero: Superhero = Superhero("","","", StatsSuperhero("","","","","",""))
    }
}