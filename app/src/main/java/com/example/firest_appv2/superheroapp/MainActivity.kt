package com.example.firest_appv2.superheroapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.firest_appv2.R
import com.example.firest_appv2.databinding.ActivityMainBinding
import com.example.firest_appv2.superheroapp.superheroCompare.SuperheroCompareActivity

class MainActivity : AppCompatActivity() {

    private lateinit var binding:ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        binding.btnSearchSuperhero.setOnClickListener{navigateToPage(SuperHeroListActivity())}
        binding.btnCompareSuperHeroes.setOnClickListener{navigateToPage(SuperheroCompareActivity())}
    }

    private fun navigateToPage(targetActivity:Activity){
        val intent = Intent(this,targetActivity::class.java)
        startActivity(intent)
    }

}