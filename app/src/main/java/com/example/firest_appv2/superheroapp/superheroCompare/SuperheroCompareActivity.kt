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
        retrofit = Utility.getRetrofit()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initUI()

        updateSuperheroes()
    }

    private fun initUI() {
        // firstSuperhero Utility.firstSuperhero
        // secondSuperhero Utility.secondsuperhero
        binding.btnAddFirstSuperHero.setOnClickListener { navigateToListSuperHeroes("FirstSuperhero") }
        binding.btnAddSecondSuperHero.setOnClickListener { navigateToListSuperHeroes("SecondSuperhero") }
        binding.btnDeleteFirstSuperHero.setOnClickListener { deleteSuperhero(Utility.firstSuperhero) }
        binding.btnDeleteSecondSuperHero.setOnClickListener() { deleteSuperhero(Utility.secondSuperhero) }
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
            Picasso.get().load(Utility.firstSuperhero.urlImage)
                .into(binding.ivfirstSuperhero)
            //2º cargamos el nombre del superhéroe
            binding.tvNameFirstSuperhero.text =
                Utility.firstSuperhero.name
        }

        if(Utility.secondSuperhero.id!=""){
            //Ya hay un segundo superhéroe elegido
            //1º Cargamos la imagen
            Picasso.get().load(Utility.secondSuperhero.urlImage)
                .into(binding.ivSecondSuperhero)
            //2º cargamos el nombre del superhéroe
            binding.tvNameSecondSuperhero.text =
                Utility.secondSuperhero.name
        }
    }


    private fun deleteSuperhero(superheroe:Superhero) {
        superheroe.id =""
        superheroe.name=""
        superheroe.stats.combat=""
        superheroe.stats.power=""
        superheroe.stats.speed=""
        superheroe.stats.strength=""
        superheroe.stats.durability=""
        superheroe.stats.intelligence=""

        if(superheroe==Utility.firstSuperhero) {
            binding.tvNameFirstSuperhero.text = ""
            binding.ivfirstSuperhero.setImageDrawable(null)
            updateSuperheroes()
        }else{
            binding.tvNameSecondSuperhero.text = ""
            binding.ivSecondSuperhero.setImageDrawable(null)
        }
    }

    private fun compareSuperheroes(
        firstSuperheroe: Superhero,
        secondSuperheroe: Superhero
    ) {
        //Primero comprobar si hay dos superheroes
        if (firstSuperheroe.id != "" && secondSuperheroe.id != "") {
            //Hay dos superhéroes y se pueden comprobar
            val scoreTotalFirstSuperheroe = returnPuntuation(Utility.firstSuperhero)
            val scoreTotalSecondSuperheroe = returnPuntuation(Utility.secondSuperhero)

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

    private fun returnPuntuation(superheroe:Superhero):Int{
        return superheroe.stats.power.toInt() * 6 +
                superheroe.stats.combat.toInt() * 5 +
                superheroe.stats.speed.toInt() * 4 +
                superheroe.stats.strength.toInt() * 5 +
                superheroe.stats.durability.toInt() * 3 +
                superheroe.stats.intelligence.toInt()
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