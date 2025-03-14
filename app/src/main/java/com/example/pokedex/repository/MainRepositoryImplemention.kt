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
    override suspend fun deleteAllSavedPokemon() {
        pokeDB.deleteAllSavedPokemon() // Delegate to DAO
    }


    override suspend fun searchPokemonByQuery(query: String): Resource<List<CustomPokemonListItem>> {
        val apiId = query.toIntOrNull() // Convert query to Int if possible
        val result = pokeDB.searchPokemonByQuery(query, apiId)
        val filteredResult = result.filter { it.apiId <= FIRST_GEN_LIMIT } // Filter to Gen 1
        return if (filteredResult.isNotEmpty()) {
            Resource.Success(filteredResult)
        } else {
            Resource.Error("No Pokémon found matching query: $query")
        }
    }

    private val fiveMinutesAgo = System.currentTimeMillis() - Constants.CACHE
    private val FIRST_GEN_LIMIT = 151

    override suspend fun getPokemonList(): Resource<List<CustomPokemonListItem>> {
        val responseFromDB = pokeDB.getPokemon()

        if (responseFromDB.isNotEmpty()) {

            val filteredList = responseFromDB.filter { it.apiId <= FIRST_GEN_LIMIT }
            return Resource.Success(filteredList)
        } else {
            val preSeedList = mutableListOf<CustomPokemonListItem>()


            for (i in 1..FIRST_GEN_LIMIT) {
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

        if (nextPokemonId > FIRST_GEN_LIMIT) {
            return Resource.Success(emptyList())
        }

        val pokemonList = mutableListOf<CustomPokemonListItem>()
        val endId = minOf(nextPokemonId + 9, FIRST_GEN_LIMIT)
        for (i in nextPokemonId..endId) {
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
        return Resource.Success(pokemonList)
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