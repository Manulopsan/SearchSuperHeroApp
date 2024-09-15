package com.example.firest_appv2.superheroapp

import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.method.ScrollingMovementMethod
import android.text.style.ForegroundColorSpan
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.firest_appv2.R
import com.example.firest_appv2.databinding.ActivitySuperheroDetailBinding
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.data.AxisValueOverrider
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.columnSeries
import com.patrykandpatrick.vico.core.cartesian.layer.ColumnCartesianLayer
import com.patrykandpatrick.vico.core.common.Dimensions
import com.patrykandpatrick.vico.core.common.component.LineComponent
import com.patrykandpatrick.vico.core.common.shape.Shape
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory



class SuperheroDetailActivity : AppCompatActivity() {

    companion object{
        const val EXTRA_ID = "extra_id"
    }

    private lateinit var binding:ActivitySuperheroDetailBinding
    private lateinit var retrofit:Retrofit
    private lateinit var btnBioDetail:ExtendedFloatingActionButton
    private lateinit var tvsuperheroBioInfo: TextView
    private var statusBtnBiography:Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySuperheroDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        retrofit = getRetrofit()
        val idSuperhero:String = intent.extras?.getString(EXTRA_ID).orEmpty()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        initUI()
        getDetailInfo(idSuperhero)
        updateInfo()
    }

    private fun initUI(){
        btnBioDetail = binding.btnBioDetail
    }

    private fun getDetailInfo(id:String){
        //Hacer las llamadas a la API para obtener la info necesaria
        CoroutineScope(Dispatchers.IO).launch{
            val myResponse = retrofit.create(ApiService::class.java).getSuperheroeInfo(id)
            if(myResponse.isSuccessful){
                val response: SuperHeroInfoResponse? = myResponse.body()
                if(response!=null){
                    withContext(Dispatchers.Main){
                        Picasso.get().load(response.superheroImage.url).into(binding.ivSuperheroDetail)
                        binding.tvSuperheroName.text = response.superheroName
                        generateChart(response.superheroStats)
                        btnBioDetail.setOnClickListener{updateStatusBtnBiography(response.superheroBio)}
                    }
                }
            }
        }

    }

    private fun updateInfo(){
        binding.chartView
    }

    private fun getRetrofit(): Retrofit {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://superheroapi.com/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit
    }

    private fun generateChart(data:SuperheroPowerStats){
        val modelProducer = CartesianChartModelProducer()
        binding.chartView.modelProducer = modelProducer
        CoroutineScope(Dispatchers.IO).launch{
            modelProducer.runTransaction {
                //Values for each column - bar chart
                columnSeries {series(
                    data.combat.toFloat(),
                    data.power.toFloat(),
                    data.speed.toFloat(),
                    data.strength.toFloat(),
                    data.durability.toFloat()
                )
                }
                //Modifiy values axis x
                val labellist:List<String> = listOf("combat","power","speed","strength","durability")
                binding.chartView.chart?.bottomAxis = (binding.chartView.chart?.bottomAxis as HorizontalAxis).copy(
                    valueFormatter = { x, _, _ -> labellist[x.toInt()]}
                )
                //Change color column // thickness column // shape column // margin between columns
                (binding.chartView.chart?.layers?.firstOrNull() as? ColumnCartesianLayer)?.columnProvider =
                    ColumnCartesianLayer.ColumnProvider.series(
                        LineComponent(
                            color=Color.rgb(18,218,199),
                            thicknessDp = 16f,
                            shape = Shape.Pill,
                            margins = Dimensions(-15f)
                            )
                    )
                //Max value axis Y
                (binding.chartView.chart?.layers?.firstOrNull() as? ColumnCartesianLayer)?.axisValueOverrider = AxisValueOverrider.fixed(maxY=100.0)

            }
        }
    }

    private fun updateStatusBtnBiography(superheroBio:SuperheroBiography){
        if (!statusBtnBiography){
            detailBio(superheroBio)
            statusBtnBiography = !statusBtnBiography
        }else{
            //Si antes pulsamos el botón y mostramos la biografía, si pulsamos el botón
            //una segunda vez, hacemos desaparecer el contenido de la biografía
            tvsuperheroBioInfo.text = ""
            statusBtnBiography = !statusBtnBiography
        }
    }

    private fun detailBio(superheroBio:SuperheroBiography){
        tvsuperheroBioInfo = binding.superheroBioInfo
        val text = "Nombre completo: ${superheroBio.fullName}.\n" +
                "Alter egos:${superheroBio.alterEgos} \n" +
                "Listado de alias: ${superheroBio.alias.map{e -> e + " "}}.\n" +
                "Lugar de nacimiento: ${superheroBio.placeBirth}.\n" +
                "Primera aparición: ${superheroBio.firstAppearance}. \n" +
                "Editor: ${superheroBio.publisher}.\n" +
                "Alineación: ${superheroBio.alignment}"
        var superheroBiography = SpannableString(text)

        superheroBiography = colourText(superheroBiography,"Nombre completo")
        superheroBiography = colourText(superheroBiography,"Alter egos")
        superheroBiography = colourText(superheroBiography,"Listado de alias")
        superheroBiography = colourText(superheroBiography,"Lugar de nacimiento")
        superheroBiography = colourText(superheroBiography,"Primera aparición")
        superheroBiography = colourText(superheroBiography,"Editor")
        superheroBiography = colourText(superheroBiography,"Alineación")

        tvsuperheroBioInfo.text = superheroBiography
        tvsuperheroBioInfo.movementMethod = ScrollingMovementMethod()

    }

    private fun colourText(text:SpannableString, textToColour:String):SpannableString{
        text.setSpan(
            ForegroundColorSpan(Color.rgb(98,0,238)),
            text.indexOf(textToColour),
            text.indexOf(textToColour)+textToColour.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        return text
    }

}