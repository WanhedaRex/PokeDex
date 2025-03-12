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
class DetailViewModel @Inject constructor(private val repository: MainRepository) : ViewModel(){

    private val _pokemonDetails = SingleLiveEvent<Resource<PokemonDetailItem>>()
    val pokemonDetails : LiveData<Resource<PokemonDetailItem>>
        get() = _pokemonDetails


    val plotLeft = (0..600).random()
    val plotTop = (0..600).random()

    fun getPokemonDetails(id: Int){
        _pokemonDetails.postValue(Resource.Loading("Loading"))
        viewModelScope.launch(Dispatchers.IO) {
            _pokemonDetails.postValue(repository.getPokemonDetail(id))
        }
    }


    fun savePokemon(customPokemonListItem: CustomPokemonListItem){
        viewModelScope.launch(Dispatchers.IO) {
            repository.savePokemon(customPokemonListItem)
        }
    }
}