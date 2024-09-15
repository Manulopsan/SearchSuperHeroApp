package com.example.manulopsan.firstApp.superheroapp

import SuperHeroesItemResponse
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.firest_appv2.databinding.ItemSuperheroesBinding
import com.squareup.picasso.Picasso

class SuperheroeViewHolder(view: View):RecyclerView.ViewHolder(view) {

    private val binding = ItemSuperheroesBinding.bind(view)

    fun renderSuperheroe(superheroe:SuperHeroesItemResponse){
        Picasso.get().load(superheroe.superheroImage.url).into(binding.superheroImage)
        binding.nameSuperhero.text = superheroe.name
    }

}