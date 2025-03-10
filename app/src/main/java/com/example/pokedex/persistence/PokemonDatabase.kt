package com.example.pokedex.persistence

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.pokedex.model.CustomPokemonListItem
import com.example.pokedex.model.PokemonDetailItem

@Database(entities =[CustomPokemonListItem::class, PokemonDetailItem:: class], version = 1,)
abstract class PokemonDatabase : RoomDatabase() {

}