package com.example.pokedex.api

import com.example.pokedex.model.PokemonDetailItem
import retrofit2.Response
import retrofit2.http.Path

interface ApiInterface {

    suspend fun getPokemonDetail(@Path("id") id: Int): Response<PokemonDetailItem>
}