package com.example.pokedex.ui.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokedex.model.CustomPokemonListItem
import com.example.pokedex.repository.MainRepository
import com.example.pokedex.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ListViewModel @Inject constructor(private val repository: MainRepository) : ViewModel() {

    private val _pokemonList = MutableLiveData<Resource<List<CustomPokemonListItem>>>()
    val pokemonList: LiveData<Resource<List<CustomPokemonListItem>>> get() = _pokemonList

    private var isFetching = false // Prevents concurrent fetches
    private val allPokemon = mutableListOf<CustomPokemonListItem>() // Accumulates unique Pokémon

    init {
        getPokemonList() // Initial fetch
    }

    fun getPokemonList() {
        if (isFetching) return // Avoid duplicate fetches
        isFetching = true
        allPokemon.clear() // Clear previous data for fresh start
        _pokemonList.postValue(Resource.Loading("Fetching Pokémon list..."))
        viewModelScope.launch(Dispatchers.IO) {
            val result = repository.getPokemonList()
            handleFetchResult(result)
            isFetching = false
        }
    }

    fun getNextPage() {
        if (isFetching) return // Avoid duplicate fetches
        isFetching = true
        _pokemonList.postValue(Resource.Loading("Fetching next page..."))
        viewModelScope.launch(Dispatchers.IO) {
            val result = repository.getPokemonListNextPage()
            handleFetchResult(result)
            isFetching = false
        }
    }

    private fun handleFetchResult(result: Resource<List<CustomPokemonListItem>>) {
        when (result) {
            is Resource.Success -> {
                result.data?.let { newPokemon ->
                    // Add only unique Pokémon based on apiId
                    val uniqueNewPokemon = newPokemon.filter { newItem ->
                        allPokemon.none { it.apiId == newItem.apiId }
                    }
                    allPokemon.addAll(uniqueNewPokemon)
                    _pokemonList.postValue(Resource.Success(allPokemon.toList()))
                } ?: _pokemonList.postValue(Resource.Error("No data received"))
            }
            is Resource.Error -> _pokemonList.postValue(result)
            is Resource.Loading -> _pokemonList.postValue(result)
            is Resource.Expired -> _pokemonList.postValue(result)
        }
    }
}