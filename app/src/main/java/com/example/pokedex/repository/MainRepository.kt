package com.example.pokedex.repository

import com.example.pokedex.model.CustomPokemonListItem
import com.example.pokedex.model.PokemonDetailItem
import com.example.pokedex.util.Resource

interface MainRepository {
    suspend fun getPokemonList() : Resource<List<CustomPokemonListItem>>
    suspend fun getPokemonListNextPage() : Resource<List<CustomPokemonListItem>>
    suspend fun getSavedPokemon() : Resource<List<CustomPokemonListItem>>
    suspend fun getPokemonDetail(id: Int) : Resource<PokemonDetailItem>
    suspend fun getLastStoredPokemon() : CustomPokemonListItem?
    suspend fun savePokemon(pokemonListItem: CustomPokemonListItem)
}