package com.example.firest_appv2.superheroapp.superheroCompare

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.firest_appv2.R
import com.example.firest_appv2.databinding.ActivitySuperheroCompareBinding
import com.example.firest_appv2.superheroapp.SuperHeroListActivity
import com.example.firest_appv2.superheroapp.Utilities.Utility
import com.example.firest_appv2.superheroapp.superheroCompare.data.StatsSuperhero
import com.example.firest_appv2.superheroapp.superheroCompare.data.Superhero
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.data.AxisValueOverrider
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.columnSeries
import com.patrykandpatrick.vico.core.cartesian.layer.ColumnCartesianLayer
import com.patrykandpatrick.vico.core.common.Dimensions
import com.patrykandpatrick.vico.core.common.HorizontalLegend
import com.patrykandpatrick.vico.core.common.LegendItem
import com.patrykandpatrick.vico.core.common.component.LineComponent
import com.patrykandpatrick.vico.core.common.component.ShapeComponent
import com.patrykandpatrick.vico.core.common.component.TextComponent
import com.patrykandpatrick.vico.core.common.shape.Shape
import com.patrykandpatrick.vico.views.cartesian.CartesianChartView
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SuperheroCompareActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySuperheroCompareBinding
    private lateinit var retrofit: Retrofit
    private var deleteFirstSuperhero = false
    private var deleteSecondSuperhero = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySuperheroCompareBinding.inflate(layoutInflater)
        setContentView(binding.root)
        retrofit = getRetrofit()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initUI()
        // getInfo()  **** BAJO REVISIÓN - ¿PARA QUE SE NECESITA?
        updateSuperheroes()
    }

    private fun initUI() {
        // firstSuperhero Utility.firstSuperhero
        // secondSuperhero Utility.secondsuperhero
        binding.btnAddFirstSuperHero.setOnClickListener { navigateToListSuperHeroes("FirstSuperhero") }
        binding.btnAddSecondSuperHero.setOnClickListener { navigateToListSuperHeroes("SecondSuperhero") }
        binding.btnDeleteFirstSuperHero.setOnClickListener { deleteSuperhero("FIRST") }
        binding.btnDeleteSecondSuperHero.setOnClickListener() { deleteSuperhero("SECOND") }
        binding.ivSimbolCompare.setOnClickListener {
            compareSuperheroes(
                Utility.firstSuperhero,
                Utility.secondSuperhero
            )
        }
    }

    private fun navigateToListSuperHeroes(origin: String) {
        //Si pulsamos en el botón añadir de un superheroe, ponemos el status del botón elminar a false
        if (origin == "FirstSuperhero")
            deleteFirstSuperhero = false

        if (origin == "SecondSuperhero")
            deleteSecondSuperhero = false

        val intent = Intent(this, SuperHeroListActivity::class.java)
        intent.putExtra("DESTINATION", origin)
        startActivity(intent)
    }

    private fun updateSuperheroes() {
        if(Utility.firstSuperhero.id != ""){
            //Ya hay un primer superhéroe elegido
            //1º Cargamos la imagen
            Log.d("PRUEBA","ID - ${Utility.firstSuperhero.id}")
            Log.d("PRUEBA","NOMBRE ${Utility.firstSuperhero.name}")
            Log.d("PRUEBA","URL IMAGE ${Utility.firstSuperhero.urlImage}")
            Picasso.get().load(Utility.firstSuperhero.urlImage)
                .into(binding.ivfirstSuperhero)
            //2º cargamos el nombre del superhéroe
            binding.tvNameFirstSuperhero.text =
                Utility.firstSuperhero.name
        }

        if(Utility.secondSuperhero.id!=""){
            //Ya hay un segundo superhéroe elegido
            //1º Cargamos la imagen
            Log.d("PRUEBA","ID - SECOND SUPERHERO ${Utility.secondSuperhero.id}")
            Log.d("PRUEBA","URL IMAGE - BEFORE PICASSO ${Utility.secondSuperhero.urlImage}")
            Picasso.get().load(Utility.secondSuperhero.urlImage)
                .into(binding.ivSecondSuperhero)
            //2º cargamos el nombre del superhéroe
            binding.tvNameSecondSuperhero.text =
                Utility.secondSuperhero.name
        }
    }

    private fun getRetrofit(): Retrofit {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://superheroapi.com/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit
    }

    private fun deleteSuperhero(superheroTarget: String) {
        if (superheroTarget == "FIRST") {
            deleteFirstSuperhero = true
            binding.tvNameFirstSuperhero.text = ""
            binding.ivfirstSuperhero.setImageDrawable(null)
            Utility.firstSuperhero.id =""
            Utility.firstSuperhero.name=""
            Utility.firstSuperhero.stats.combat=""
            Utility.firstSuperhero.stats.power=""
            Utility.firstSuperhero.stats.speed=""
            Utility.firstSuperhero.stats.strength=""
            Utility.firstSuperhero.stats.durability=""
            Utility.firstSuperhero.stats.intelligence=""
            updateSuperheroes()
        }
        if (superheroTarget == "SECOND") {
            deleteSecondSuperhero = true
            binding.tvNameSecondSuperhero.text = ""
            binding.ivSecondSuperhero.setImageDrawable(null)
            Utility.secondSuperhero.id =""
            Utility.secondSuperhero.name=""
            Utility.secondSuperhero.stats.combat=""
            Utility.secondSuperhero.stats.power=""
            Utility.secondSuperhero.stats.speed=""
            Utility.secondSuperhero.stats.strength=""
            Utility.secondSuperhero.stats.durability=""
            Utility.secondSuperhero.stats.intelligence=""
            updateSuperheroes()
        }
    }

    private fun compareSuperheroes(
        firstSuperheroe: Superhero,
        secondSuperheroe: Superhero
    ) {
        //Primero comprobar si hay dos superheroes
        if (firstSuperheroe.id != "" && secondSuperheroe.id != "") {
            //Hay dos superhéroes y se pueden comprobar
            val scoreTotalFirstSuperheroe =
                Utility.firstSuperhero.stats.power.toInt() * 6 +
                        Utility.firstSuperhero.stats.combat.toInt() * 5 +
                        Utility.firstSuperhero.stats.speed.toInt() * 4 +
                        Utility.firstSuperhero.stats.strength.toInt() * 5 +
                        Utility.firstSuperhero.stats.durability.toInt() * 3 +
                        Utility.firstSuperhero.stats.intelligence.toInt()
            val scoreTotalSecondSuperheroe =
                Utility.secondSuperhero.stats.power.toInt() * 6 +
                        Utility.secondSuperhero.stats.combat.toInt() * 5 +
                        Utility.secondSuperhero.stats.speed.toInt() * 4 +
                        Utility.secondSuperhero.stats.strength.toInt() * 5 +
                        Utility.secondSuperhero.stats.durability.toInt() * 3 +
                        Utility.secondSuperhero.stats.intelligence.toInt()


            val superHeroeWin: String

            if (scoreTotalFirstSuperheroe > scoreTotalSecondSuperheroe) {
                superHeroeWin = firstSuperheroe.name
            } else {
                superHeroeWin = secondSuperheroe.name
            }

            val dialog = Dialog(this)
            dialog.setContentView(R.layout.item_dialog_compare_superheroes)
            //Generar el gráfico
            generateChartCompare(
                Utility.firstSuperhero.stats,
                Utility.secondSuperhero.stats,
                dialog,
                Utility.firstSuperhero,
                Utility.secondSuperhero
            )

            //Generar el texto
            var textResult = dialog.findViewById<TextView>(R.id.textResult)
            textResult.text = "El superhéroe ganador de la batalla es $superHeroeWin"

            //Mostrar el resultado
            dialog.show()


        } else {
            AlertDialog.Builder(this)
                .setTitle("Comparar dos superhéroes")
                .setMessage("Tiene que haber dos superhéroes seleccionados para poder realizar la comparación")
                .setPositiveButton("Ok", null)
                .show()
        }
    }

    private fun generateChartCompare(
        powerStatsFirstSuperHero: StatsSuperhero,
        powerStatsSecondSuperHero: StatsSuperhero,
        dialog: Dialog,
        firstSuperheroe: Superhero,
        secondSuperheroe: Superhero
    ) {
        val modelProducer = CartesianChartModelProducer()
        val chartCompare: CartesianChartView = dialog.findViewById(R.id.chart_view_result)
        chartCompare.modelProducer = modelProducer
        CoroutineScope(Dispatchers.IO).launch {
            modelProducer.runTransaction {
                columnSeries {
                    series(
                        powerStatsFirstSuperHero.power.toFloat(),
                        powerStatsFirstSuperHero.combat.toFloat(),
                        powerStatsFirstSuperHero.speed.toFloat(),
                        powerStatsFirstSuperHero.strength.toFloat(),
                        powerStatsFirstSuperHero.durability.toFloat(),
                        powerStatsFirstSuperHero.intelligence.toFloat()
                    );series(
                    powerStatsSecondSuperHero.power.toFloat(),
                    powerStatsSecondSuperHero.combat.toFloat(),
                    powerStatsSecondSuperHero.speed.toFloat(),
                    powerStatsSecondSuperHero.strength.toFloat(),
                    powerStatsSecondSuperHero.durability.toFloat(),
                    powerStatsSecondSuperHero.intelligence.toFloat()
                )
                }
                //Modifiy values axis x
                val labellist: List<String> =
                    listOf("power", "combat", "speed", "strength", "durability", "intelligence")
                chartCompare.chart?.bottomAxis =
                    (chartCompare.chart?.bottomAxis as HorizontalAxis).copy(
                        valueFormatter = { x, _, _ -> labellist[x.toInt()] }
                    )
                //Change color column // thickness column // shape column // margin between columns
                (chartCompare.chart?.layers?.firstOrNull() as? ColumnCartesianLayer)?.columnProvider =
                    ColumnCartesianLayer.ColumnProvider.series(
                        LineComponent(
                            color = Color.rgb(18, 218, 199),
                            thicknessDp = 4f,
                            shape = Shape.Rectangle,
                            margins = Dimensions(-5f)
                        ),
                        LineComponent(
                            color = Color.rgb(50, 154, 98),
                            thicknessDp = 4f,
                            shape = Shape.Rectangle,
                            margins = Dimensions(-5f)
                        )
                    )
                //Max value axis Y
                (chartCompare.chart?.layers?.firstOrNull() as? ColumnCartesianLayer)?.axisValueOverrider =
                    AxisValueOverrider.fixed(maxY = 100.0)
                //Add legend
                chartCompare.chart?.legend = HorizontalLegend(
                    items = {
                        add(
                            LegendItem(
                                icon = ShapeComponent(Color.rgb(18, 218, 199), Shape.Rectangle),
                                labelComponent = TextComponent(
                                    color = Color.BLACK,
                                    textSizeSp = 14f,
                                    typeface = Typeface.MONOSPACE
                                ),
                                label = firstSuperheroe.name,
                            ),
                        )
                        add(
                            LegendItem(
                                icon = ShapeComponent(Color.rgb(50, 154, 98), Shape.Rectangle),
                                labelComponent = TextComponent(
                                    color = Color.BLACK,
                                    textSizeSp = 14f,
                                    typeface = Typeface.MONOSPACE
                                ),
                                label = secondSuperheroe.name,
                            ),
                        )
                    },
                    iconPaddingDp = 4f,
                    iconSizeDp = 14f,
                    spacingDp = 25f
                )
            }
        }
    }
}