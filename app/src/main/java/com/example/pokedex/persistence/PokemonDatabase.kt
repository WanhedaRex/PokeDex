package com.example.pokedex.persistence

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.pokedex.model.Converters
import com.example.pokedex.model.CustomPokemonListItem
import com.example.pokedex.model.PokemonDetailItem


@Database(entities =[CustomPokemonListItem::class, PokemonDetailItem:: class], version = 1)
@TypeConverters(Converters::class)
abstract class PokemonDatabase : RoomDatabase() {

    abstract fun pokemonDao(): PokemonDAO

    companion object{

        @Volatile
        private var instance: PokemonDatabase? = null

        fun getDatabase(context: Context) :PokemonDatabase?{
            return instance ?: synchronized(this){
                val _instance = Room.databaseBuilder(context.applicationContext, PokemonDatabase::class.java,"Pokemon").fallbackToDestructiveMigration().build()
                instance = _instance
                instance
            }
        }
    }

}