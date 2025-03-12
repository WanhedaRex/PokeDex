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
class MapViewModel @Inject constructor(private val repository: MainRepository) : ViewModel(){

        private val _pokemonList = MutableLiveData<Resource<List<CustomPokemonListItem>>>()
        val pokemonList : LiveData<Resource<List<CustomPokemonListItem>>>
        get() = _pokemonList


    fun getPokemonList(){
            _pokemonList.postValue(Resource.Loading(""))
            viewModelScope.launch(Dispatchers.IO) {
            _pokemonList.postValue(repository.getPokemonList())
            }
    }


}