package com.example.firest_appv2.superheroapp

import SuperHeroDataResponse
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.firest_appv2.R
import com.example.firest_appv2.databinding.ActivitySuperHeroListBinding
import com.example.firest_appv2.superheroapp.SuperheroDetailActivity.Companion.EXTRA_ID
import com.example.firest_appv2.superheroapp.Utilities.Utility
import com.example.firest_appv2.superheroapp.superheroCompare.SuperheroCompareActivity
import com.example.firest_appv2.superheroapp.superheroCompare.data.Superhero
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SuperHeroListActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySuperHeroListBinding
    private lateinit var retrofit:Retrofit
    private lateinit var rvSuperhero:RecyclerView
    private lateinit var superheroeAdapter:SuperheroAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySuperHeroListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        retrofit = getRetrofit()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        initUI()
    }

    private fun initUI() {
        binding.searchview.setOnQueryTextListener(object : SearchView.OnQueryTextListener
        {
            override fun onQueryTextSubmit(query: String?): Boolean {
                searchByName(query.orEmpty())
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean = false
        })
        superheroeAdapter = SuperheroAdapter{superheroID -> applyFunction(superheroID)}
        rvSuperhero = binding.rvSuperhero
        rvSuperhero.layoutManager = LinearLayoutManager(rvSuperhero.context,LinearLayoutManager.VERTICAL,false)
        rvSuperhero.adapter = superheroeAdapter

    }

    private fun searchByName(query:String){
        //Cuando le damos a buscar, mostramos la progressBar
        binding.progressBar.isVisible = true
        CoroutineScope(Dispatchers.IO).launch {
            //con retrofit crea la interface que le pasamos como parámetro
            val myResponse = retrofit.create(ApiService::class.java).getSuperheroes(query)

            if (myResponse.isSuccessful) {
                val response: SuperHeroDataResponse? = myResponse.body()
                if (response != null) {
                    //en response.superheroes está la lista con los datos que se obtienen de la
                    //consulta a la API
                    withContext(Dispatchers.Main){
                        binding.progressBar.isVisible = false
                        superheroeAdapter.updateListSuperHeroes(response.superheroes)
                    }
                }
            } else {
                Log.i("prueba", "fracaso")
            }
        }
    }

    private fun getRetrofit():Retrofit{
        val retrofit = Retrofit.Builder()
            .baseUrl("https://superheroapi.com/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit
    }

    private fun applyFunction(id:String){
        val destination:String = intent.extras?.getString("DESTINATION").orEmpty()

        if(destination.isNotEmpty()){
            //Se ha accedido desde la opción de comparar dos superhéroes

            if(destination=="FirstSuperhero"){
                Utility.firstSuperhero.id = id
                var response: SuperHeroInfoResponse? = SuperHeroInfoResponse("", SuperheroPowerStats("","","","","",""),SuperheroBiography("","",listOf(""),"","","",""),SuperHeroDetailImage(""))
                //Guardar todos los datos del superhéroe
                val job = CoroutineScope(Dispatchers.IO).launch{
                    val myResponse = retrofit.create(ApiService::class.java).getSuperheroeInfo(id)
                    if(myResponse.isSuccessful){
                        response = myResponse.body()
                    }
                }
                while(job.isActive){
                    //Await to finnish the coroutine
                }
                saveDataSuperheroe(Utility.firstSuperhero,response)
                val intent = Intent(this,SuperheroCompareActivity::class.java)
                startActivity(intent)
            }


            if(destination=="SecondSuperhero"){
                Utility.secondSuperhero.id = id
                var response: SuperHeroInfoResponse? = SuperHeroInfoResponse("", SuperheroPowerStats("","","","","",""),SuperheroBiography("","",listOf(""),"","","",""),SuperHeroDetailImage(""))
                //Guardar todos los datos del superhéroe
                val job = CoroutineScope(Dispatchers.IO).launch{
                    val myResponse = retrofit.create(ApiService::class.java).getSuperheroeInfo(id)
                    if(myResponse.isSuccessful){
                        response = myResponse.body()
                    }
                }
                while(job.isActive){
                    //Await to finnish the coroutine
                }
                saveDataSuperheroe(Utility.secondSuperhero,response)
                val intent = Intent(this,SuperheroCompareActivity::class.java)
                startActivity(intent)
            }
        }else{
            //Se ha llegado a la función applyFunction desde la opción de ver la información de
            //un superhéroe
            val intent = Intent(this,SuperheroDetailActivity::class.java)
            intent.putExtra(EXTRA_ID,id)
            startActivity(intent)
        }
    }

    private fun saveDataSuperheroe(superheroe:Superhero,dataSuperheroe:SuperHeroInfoResponse?){
        superheroe.name = dataSuperheroe?.superheroName.toString()
        superheroe.urlImage = dataSuperheroe?.superheroImage?.url.toString()
        superheroe.stats.combat = dataSuperheroe?.superheroStats?.combat.toString()
        superheroe.stats.power = dataSuperheroe?.superheroStats?.power.toString()
        superheroe.stats.speed = dataSuperheroe?.superheroStats?.speed.toString()
        superheroe.stats.durability = dataSuperheroe?.superheroStats?.durability.toString()
        superheroe.stats.intelligence = dataSuperheroe?.superheroStats?.intelligence.toString()
        superheroe.stats.strength = dataSuperheroe?.superheroStats?.strength.toString()

    }

}

//CORREGIR PROBLEMA - SALTA A LA ACTIVITY DE COMPARE Y TODAVÍA NO HA TERMINADO LA CORUTINA POR ESO
//NO SE MUESTRAN LOS DATOS
