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
class SavedViewModel @Inject constructor(private val repository: MainRepository) : ViewModel(){

    private val _pokemonList = MutableLiveData<Resource<List<CustomPokemonListItem>>>()
    val pokemonList : LiveData<Resource<List<CustomPokemonListItem>>>
        get() = _pokemonList


    fun getSavedPokemon(){
        _pokemonList.postValue(Resource.Loading(""))
        viewModelScope.launch(Dispatchers.IO) {
            _pokemonList.postValue(repository.getSavedPokemon())
        }
    }


    fun deletePokemon(customPokemonListItem: CustomPokemonListItem) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.savePokemon(customPokemonListItem)
            _pokemonList.postValue(repository.getSavedPokemon())
        }
    }

    fun deleteAllPokemon() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.deleteAllSavedPokemon()
                _pokemonList.postValue(Resource.Success(emptyList()))
            } catch (e: Exception) {
                _pokemonList.postValue(Resource.Error("Failed to delete all Pokémon: ${e.message}"))
            }
        }
    }
}