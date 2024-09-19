package com.example.firest_appv2.superheroapp.superheroCompare

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.firest_appv2.R
import com.example.firest_appv2.databinding.ActivitySuperheroCompareBinding
import com.example.firest_appv2.superheroapp.ApiService
import com.example.firest_appv2.superheroapp.InfoSuperheroCompare
import com.example.firest_appv2.superheroapp.SuperHeroInfoResponse
import com.example.firest_appv2.superheroapp.SuperHeroListActivity
import com.example.firest_appv2.superheroapp.SuperheroPowerStats
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
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SuperheroCompareActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySuperheroCompareBinding
    private lateinit var retrofit: Retrofit
    private lateinit var firstSuperheroe: InfoSuperheroCompare
    private lateinit var secondSuperheroe: InfoSuperheroCompare
    private var deleteFirstSuperhero = false
    private var deleteSecondSuperhero = false
    private lateinit var powerStatsFirstSuperHero: SuperheroPowerStats
    private lateinit var powerStatsSecondSuperHero: SuperheroPowerStats

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
        getInfo()
        updateSuperheroes()
    }

    private fun initUI() {
        firstSuperheroe = InfoSuperheroCompare(id = "", name = "")
        secondSuperheroe = InfoSuperheroCompare(id = "", name = "")
        binding.btnAddFirstSuperHero.setOnClickListener { navigateToListSuperHeroes("FirstSuperhero") }
        binding.btnAddSecondSuperHero.setOnClickListener { navigateToListSuperHeroes("SecondSuperhero") }
        binding.btnDeleteFirstSuperHero.setOnClickListener { deleteSuperhero("FIRST") }
        binding.btnDeleteSecondSuperHero.setOnClickListener() { deleteSuperhero("SECOND") }
        binding.ivSimbolCompare.setOnClickListener {
            compareSuperheroes(
                firstSuperheroe,
                secondSuperheroe
            )
        }
        powerStatsFirstSuperHero = SuperheroPowerStats("0", "0", "0", "0", "0", "0")
        powerStatsSecondSuperHero = SuperheroPowerStats("0", "0", "0", "0", "0", "0")
    }

    private fun navigateToListSuperHeroes(origin: String) {
        //Si pulsamos en el botón añadir de un superheroe, ponemos el status del botón elminar a false
        if (origin == "FirstSuperhero")
            deleteFirstSuperhero = false

        if (origin == "SecondSuperhero")
            deleteSecondSuperhero = false

        val intent = Intent(this, SuperHeroListActivity::class.java)
        intent.putExtra("DESTINATION", origin)
        intent.putExtra("ACTION", "add")
        intent.putExtra("FirstSuperheroe", firstSuperheroe.id)
        intent.putExtra("SecondSuperheroe", secondSuperheroe.id)
        startActivity(intent)
    }

    private fun getInfo() {
        val superheroeIDreturn = intent.extras?.getString("EXTRA_ID").orEmpty()
        val destinatation = intent.extras?.getString("EXTRA_DESTINATION").orEmpty()
        //Si se ha pulsado el botón eliminar entonces no actualizar el id del superheroe
        if (deleteFirstSuperhero) {
            firstSuperheroe.id = ""
        } else {
            firstSuperheroe.id = intent.extras?.getString("EXTRA_FIRST_ID").orEmpty()
        }
        if (deleteSecondSuperhero) {
            secondSuperheroe.id = ""
        } else {
            secondSuperheroe.id = intent.extras?.getString("EXTRA_SECOND_ID").orEmpty()
        }
    }

    private fun updateSuperheroes() {
        if (firstSuperheroe.id != "" || secondSuperheroe.id != "") {
            //Actualizar contenido de los dos superheroes
            //Buscar por ID y obtener el nombre y la url
            if (firstSuperheroe.id != "")
                CoroutineScope(Dispatchers.IO).launch {
                    val myResponseFirstSuperheroe = retrofit.create(ApiService::class.java)
                        .getSuperheroeInfo(firstSuperheroe.id)
                    if (myResponseFirstSuperheroe.isSuccessful) {
                        val responseFirstSuperhero: SuperHeroInfoResponse? =
                            myResponseFirstSuperheroe.body()
                        if (responseFirstSuperhero != null) {
                            withContext(Dispatchers.Main) {
                                Picasso.get().load(responseFirstSuperhero.superheroImage.url)
                                    .into(binding.ivfirstSuperhero)
                                binding.tvNameFirstSuperhero.text =
                                    responseFirstSuperhero.superheroName
                                firstSuperheroe.name = responseFirstSuperhero.superheroName
                                powerStatsFirstSuperHero.combat =
                                    responseFirstSuperhero.superheroStats.combat
                                powerStatsFirstSuperHero.power =
                                    responseFirstSuperhero.superheroStats.power
                                powerStatsSecondSuperHero.speed =
                                    responseFirstSuperhero.superheroStats.speed
                                powerStatsFirstSuperHero.strength =
                                    responseFirstSuperhero.superheroStats.strength
                                powerStatsFirstSuperHero.durability =
                                    responseFirstSuperhero.superheroStats.durability
                                powerStatsFirstSuperHero.intelligence =
                                    responseFirstSuperhero.superheroStats.intelligence

                            }
                        }
                    }
                }
            if (secondSuperheroe.id != "")
                CoroutineScope(Dispatchers.IO).launch {
                    val myResponseSecondSuperheroe = retrofit.create(ApiService::class.java)
                        .getSuperheroeInfo(secondSuperheroe.id)
                    if (myResponseSecondSuperheroe.isSuccessful) {
                        val responseSecondSuperhero: SuperHeroInfoResponse? =
                            myResponseSecondSuperheroe.body()
                        if (responseSecondSuperhero != null) {
                            withContext(Dispatchers.Main) {
                                Picasso.get().load(responseSecondSuperhero.superheroImage.url)
                                    .into(binding.ivSecondSuperhero)
                                binding.tvNameSecondSuperhero.text =
                                    responseSecondSuperhero.superheroName
                                secondSuperheroe.name = responseSecondSuperhero.superheroName
                                powerStatsSecondSuperHero.combat =
                                    responseSecondSuperhero.superheroStats.combat
                                powerStatsSecondSuperHero.power =
                                    responseSecondSuperhero.superheroStats.power
                                powerStatsSecondSuperHero.speed =
                                    responseSecondSuperhero.superheroStats.speed
                                powerStatsSecondSuperHero.strength =
                                    responseSecondSuperhero.superheroStats.strength
                                powerStatsSecondSuperHero.durability =
                                    responseSecondSuperhero.superheroStats.durability
                                powerStatsSecondSuperHero.intelligence =
                                    responseSecondSuperhero.superheroStats.intelligence
                            }
                        }
                    }
                }
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
            getInfo()
            updateSuperheroes()
        }
        if (superheroTarget == "SECOND") {
            deleteSecondSuperhero = true
            binding.tvNameSecondSuperhero.text = ""
            binding.ivSecondSuperhero.setImageDrawable(null)
            getInfo()
            updateSuperheroes()
        }
    }

    private fun compareSuperheroes(
        firstSuperheroe: InfoSuperheroCompare,
        secondSuperheroe: InfoSuperheroCompare
    ) {
        //Primero comprobar si hay dos superheroes
        if (firstSuperheroe.id != "" && secondSuperheroe.id != "") {
            //Hay dos superhéroes y se pueden comprobar
            val scoreTotalFirstSuperheroe =
                powerStatsFirstSuperHero.power.toInt() * 6 +
                        powerStatsFirstSuperHero.combat.toInt() * 5 +
                        powerStatsFirstSuperHero.speed.toInt() * 4 +
                        powerStatsFirstSuperHero.strength.toInt() * 5 +
                        powerStatsFirstSuperHero.durability.toInt() * 3 +
                        powerStatsFirstSuperHero.intelligence.toInt()
            val scoreTotalSecondSuperheroe =
                powerStatsSecondSuperHero.power.toInt() * 6 +
                        powerStatsSecondSuperHero.combat.toInt() * 5 +
                        powerStatsSecondSuperHero.speed.toInt() * 4 +
                        powerStatsSecondSuperHero.strength.toInt() * 5 +
                        powerStatsSecondSuperHero.durability.toInt() * 3 +
                        powerStatsSecondSuperHero.intelligence.toInt()


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
                powerStatsFirstSuperHero,
                powerStatsSecondSuperHero,
                dialog,
                firstSuperheroe,
                secondSuperheroe
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
        powerStatsFirstSuperHero: SuperheroPowerStats,
        powerStatsSecondSuperHero: SuperheroPowerStats,
        dialog: Dialog,
        firstSuperheroe: InfoSuperheroCompare,
        secondSuperheroe: InfoSuperheroCompare
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