package com.example.pokedex.persistence

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.pokedex.model.PokemonDetailItem
import com.example.pokedex.model.CustomPokemonListItem


@Dao
interface PokemonDAO {
    @Query("SELECT * FROM pokemon WHERE (name LIKE '%' || :query || '%' OR api = :apiId) AND api <= 151")
    suspend fun searchPokemonByQuery(query: String, apiId: Int? = null): List<CustomPokemonListItem>

    @Query("SELECT * FROM pokemon WHERE type LIKE :type")
    suspend fun searchPokemonByType(type: String): List<CustomPokemonListItem>

    @Query("SELECT * FROM pokemon WHERE api <= 151")
    suspend fun getPokemon(): List<CustomPokemonListItem>

    @Query("SELECT * FROM pokemon WHERE isSaved = 1 AND api <= 151")
    suspend fun getSavedPokemon(): List<CustomPokemonListItem>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPokemonList(list: List<CustomPokemonListItem>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPokemon(item: CustomPokemonListItem)

    @Query("SELECT * FROM pokemonDetails WHERE id LIKE :id")
    suspend fun getPokemonDetails(id: Int): PokemonDetailItem?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPokemonDetailsItem(pokemonDetailItem: PokemonDetailItem)

    @Query("SELECT * FROM pokemon ORDER BY id DESC LIMIT 1")
    suspend fun getLastStoredPokemonObject(): CustomPokemonListItem?

    @Query("UPDATE pokemon SET isSaved = 0 WHERE isSaved = 1")
    suspend fun deleteAllSavedPokemon()
}
