package com.example.pokedex.repository

import com.example.pokedex.api.ApiInterface
import com.example.pokedex.model.CustomPokemonListItem
import com.example.pokedex.model.PokemonDetailItem
import com.example.pokedex.persistence.PokemonDAO
import com.example.pokedex.util.Constants
import com.example.pokedex.util.Resource
import javax.inject.Inject

class MainRepositoryImplemention @Inject constructor(
    private val pokeApi: ApiInterface,
    private val pokeDB: PokemonDAO
) : MainRepository {

    private val fiveMinutesAgo = System.currentTimeMillis() - Constants.CACHE

    override suspend fun getPokemonList(): Resource<List<CustomPokemonListItem>> {
        val responseFromDB = pokeDB.getPokemon()

        if (responseFromDB.isNotEmpty()) {
            return Resource.Success(responseFromDB)
        } else {
            val preSeedList = mutableListOf<CustomPokemonListItem>()

            for (i in 1..10) {
                when (val apiResult = getPokemonDetail(i)) {
                    is Resource.Success -> {
                        apiResult.data?.let { newPokemon ->
                            val newPokemonObj = CustomPokemonListItem(
                                name = newPokemon.name,
                                image = newPokemon.sprites.front_default,
                                type = newPokemon.types?.get(0)?.type?.name.toString(),
                                positionLeft = (0..1500).random(),
                                positionTop = (0..1500).random(),
                                apiId = newPokemon.id
                            )
                            preSeedList.add(newPokemonObj)
                        }
                    }
                    else -> return Resource.Error("Unable to retrieve Items")
                }
            }

            pokeDB.insertPokemonList(preSeedList)
            return Resource.Success(pokeDB.getPokemon())
        }
    }

    override suspend fun getPokemonListNextPage(): Resource<List<CustomPokemonListItem>> {
        val lastStoredPokemon = getLastStoredPokemon()
        val nextPokemonId = lastStoredPokemon?.apiId?.plus(1) ?: return Resource.Error("No stored Pokémon found")

        val pokemonList = mutableListOf<CustomPokemonListItem>()

        for (i in nextPokemonId..(nextPokemonId + 9)) {
            when (val apiResult = getPokemonDetail(i)) {
                is Resource.Success -> {
                    apiResult.data?.let { newPokemon ->
                        val newPokemonObj = CustomPokemonListItem(
                            name = newPokemon.name,
                            image = newPokemon.sprites.front_default,
                            type = newPokemon.types?.get(0)?.type?.name.toString(),
                            positionLeft = (0..1500).random(),
                            positionTop = (0..1500).random(),
                            apiId = newPokemon.id
                        )
                        pokemonList.add(newPokemonObj)
                    }
                }
                else -> return Resource.Error("Unable to retrieve Items")
            }
        }
        pokeDB.insertPokemonList(pokemonList)
        return Resource.Success(pokeDB.getPokemon()) // Return full list, not just new batch
    }

    override suspend fun getSavedPokemon(): Resource<List<CustomPokemonListItem>> {
        val dbResult = pokeDB.getSavedPokemon()
        return if (dbResult.isNullOrEmpty()) {
            Resource.Error("Saved Pokémon list is empty")
        } else {
            Resource.Success(dbResult)
        }
    }

    override suspend fun getPokemonDetail(id: Int): Resource<PokemonDetailItem> {
        val dbResult = pokeDB.getPokemonDetails(id)
        if (dbResult != null) {
            return if (dbResult.timestamp?.toLong()!! < fiveMinutesAgo) {
                getPokemonDetailFromApi(id)
            } else {
                Resource.Success(dbResult)
            }
        } else {
            return getPokemonDetailFromApi(id)
        }
    }

    private suspend fun getPokemonDetailFromApi(id: Int): Resource<PokemonDetailItem> {
        try {
            val apiResult = pokeApi.getPokemonDetail(id)
            if (apiResult.isSuccessful && apiResult.body() != null) {
                val newPokemon = apiResult.body()!!
                newPokemon.timestamp = System.currentTimeMillis().toString()
                pokeDB.insertPokemonDetailsItem(newPokemon)
                return Resource.Success(pokeDB.getPokemonDetails(id)!!)
            } else {
                return Resource.Error(apiResult.message())
            }
        } catch (e: Exception) {
            return Resource.Error("Error retrieving items")
        }
    }

    override suspend fun getLastStoredPokemon(): CustomPokemonListItem? {
        return pokeDB.getLastStoredPokemonObject()
    }

    override suspend fun savePokemon(pokemonListItem: CustomPokemonListItem) {
        pokeDB.insertPokemon(pokemonListItem)
    }
}