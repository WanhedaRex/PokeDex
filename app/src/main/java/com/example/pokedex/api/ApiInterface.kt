package com.example.pokedex.api

import com.example.pokedex.model.PokemonDetailItem
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiInterface {

    @GET("/api/v2/pokemon/{id}")
    suspend fun getPokemonDetail(@Path("id") id: Int): Response<PokemonDetailItem>
}