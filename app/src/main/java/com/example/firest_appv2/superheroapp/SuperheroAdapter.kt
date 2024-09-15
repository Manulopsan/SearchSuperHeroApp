package com.example.firest_appv2.superheroapp

import SuperHeroesItemResponse
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.firest_appv2.R
import com.example.manulopsan.firstApp.superheroapp.SuperheroeViewHolder


class SuperheroAdapter(var superheroesList:List<SuperHeroesItemResponse> = emptyList(),private val detailSuperhero:(String)->Unit):RecyclerView.Adapter<SuperheroeViewHolder>() {

    fun updateListSuperHeroes(superheroeList:List<SuperHeroesItemResponse>){
        //Como lleva delante this. se refiere a la variable superheroeList que se le pasó
        //por parámetro a SuperheroeAdapter
        this.superheroesList = superheroeList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SuperheroeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_superheroes, parent, false)
        return SuperheroeViewHolder(view)
    }

    override fun getItemCount(): Int {
        return  superheroesList.size
    }

    override fun onBindViewHolder(viewHolder: SuperheroeViewHolder, position: Int) {
        viewHolder.renderSuperheroe(superheroesList[position])
        viewHolder.itemView.setOnClickListener{detailSuperhero(superheroesList[position].superheroId)}
    }


}