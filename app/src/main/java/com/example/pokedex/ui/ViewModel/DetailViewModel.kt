package com.example.pokedex.ui.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokedex.model.CustomPokemonListItem
import com.example.pokedex.model.PokemonDetailItem
import com.example.pokedex.repository.MainRepository
import com.example.pokedex.util.Resource
import com.example.pokedex.util.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(private val repository: MainRepository) : ViewModel() {

    private val _pokemonDetails = SingleLiveEvent<Resource<PokemonDetailItem>>()
    val pokemonDetails: LiveData<Resource<PokemonDetailItem>> get() = _pokemonDetails

    private val _saveStatus = SingleLiveEvent<Resource<Unit>>()
    val saveStatus: LiveData<Resource<Unit>> get() = _saveStatus

    val plotLeft = (0..600).random()
    val plotTop = (0..600).random()

    fun getPokemonDetails(id: Int) {
        _pokemonDetails.postValue(Resource.Loading("Loading Pokémon details"))
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = repository.getPokemonDetail(id)
                _pokemonDetails.postValue(result)
            } catch (e: Exception) {
                _pokemonDetails.postValue(Resource.Error("Failed to load details: ${e.message}"))
            }
        }
    }

    fun savePokemon(customPokemonListItem: CustomPokemonListItem) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.savePokemon(customPokemonListItem)
                _saveStatus.postValue(Resource.Success(Unit))
            } catch (e: Exception) {
                _saveStatus.postValue(Resource.Error("Failed to save Pokémon: ${e.message}"))
            }
        }
    }
}