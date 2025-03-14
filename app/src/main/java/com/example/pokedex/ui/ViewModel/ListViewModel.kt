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

    private var currentList = mutableListOf<CustomPokemonListItem>()

    fun searchPokemon(query: String) {
        _pokemonList.postValue(Resource.Loading("Searching Pokémon"))
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = repository.searchPokemonByQuery(query)
                result.data?.let {
                    currentList.clear()
                    currentList.addAll(it)
                    _pokemonList.postValue(Resource.Success(currentList.toList()))
                } ?: _pokemonList.postValue(Resource.Error("No results found"))
            } catch (e: Exception) {
                _pokemonList.postValue(Resource.Error("Search failed: ${e.message}"))
            }
        }
    }

    fun getPokemonList() {
        _pokemonList.postValue(Resource.Loading("Fetching Pokémon"))
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = repository.getPokemonList()
                result.data?.let {
                    currentList.clear()
                    currentList.addAll(it)
                    _pokemonList.postValue(Resource.Success(currentList.toList()))
                } ?: _pokemonList.postValue(Resource.Error("Empty list"))
            } catch (e: Exception) {
                _pokemonList.postValue(Resource.Error("Failed to load Pokémon: ${e.message}"))
            }
        }
    }

    fun getNextPage() {
        _pokemonList.postValue(Resource.Loading("Fetching next page"))
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = repository.getPokemonListNextPage()
                result.data?.let { newItems ->
                    val newBatch = newItems.filter { it !in currentList }
                    currentList.addAll(newBatch)
                    _pokemonList.postValue(Resource.Success(currentList.toList()))
                } ?: _pokemonList.postValue(Resource.Error("Empty next page"))
            } catch (e: Exception) {
                _pokemonList.postValue(Resource.Error("Failed to load next page: ${e.message}"))
            }
        }
    }

    fun savePokemon(pokemon: CustomPokemonListItem) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.savePokemon(pokemon)
        }
    }
}