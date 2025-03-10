package com.example.pokedex.persistence

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.pokedex.model.PokemonDetailItem
import com.example.pokedex.model.CustomPokemonListItem


@Dao
interface PokemonDAO {

    // Pa buscar en db
    @Query("SELECT * FROM pokemon WHERE name LIKE '%' || :name || '%'")
    suspend fun searchPokemonByName(name: String): List<CustomPokemonListItem>?


    @Query("SELECT * FROM pokemon WHERE type Like :type")
    suspend fun searchPokemonByType(type: String): List<CustomPokemonListItem>?

    @Query("SELECT * FROM pokemon")
    fun getPokemon(): List<CustomPokemonListItem>

    @Query("SELECT * FROM pokemon WHERE isSaved = 'true'")
    suspend fun getSavedPokemon(): List<CustomPokemonListItem>?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertPokemonList(list: List<CustomPokemonListItem>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPokemon(item: CustomPokemonListItem)


    @Query("SELECT * FROM pokemonDetails WHERE id Like :id")
    suspend fun getPokemonDetails(id: Int): PokemonDetailItem?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPokemonDetailsItem(pokemonDetailItem: PokemonDetailItem)

    @Query("SELECT * FROM pokemon ORDER BY id DESC LIMIT 1")
    fun getLastStoredPokemonObject(): CustomPokemonListItem

}