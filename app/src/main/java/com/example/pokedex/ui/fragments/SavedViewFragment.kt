package com.example.pokedex.ui.fragments

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.pokedex.R
import com.example.pokedex.databinding.FragmentSavedBinding
import com.example.pokedex.model.CustomPokemonListItem
import com.example.pokedex.ui.ViewModel.SavedViewModel
import com.example.pokedex.ui.adapters.PokemonSavedListAdapter
import com.example.pokedex.util.Resource


@AndroidEntryPoint
class SavedViewFragment : Fragment(R.layout.fragment_saved) {
    private lateinit var binding: FragmentSavedBinding
    private val viewModel: SavedViewModel by viewModels()
    private lateinit var pokemonSavedListAdapter: PokemonSavedListAdapter
    private var count = 0
    private var savedList = mutableListOf<CustomPokemonListItem>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSavedBinding.bind(view)
        binding.savedFragmentBack.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.listFragmentSettingsImg.setOnClickListener {
            deleteAllPokemon()
        }
        lifecycleScope.launchWhenStarted {
            setupRv()
            initObserver()
            viewModel.getSavedPokemon()
        }
    }
    private fun setupRv(){
        pokemonSavedListAdapter = PokemonSavedListAdapter()
        pokemonSavedListAdapter.setOnClickListener(object : PokemonSavedListAdapter.OnClickListener{
            override fun onClick(item: CustomPokemonListItem) {
                val bundle = Bundle()
                bundle.putParcelable("pokemon",item)
                findNavController().navigate(R.id.action_savedViewFragment_to_detailFragment, bundle)
            }
        })
        pokemonSavedListAdapter.setOnDeleteListener(object : PokemonSavedListAdapter.OnDeleteListener{
            override fun onDelete(item: CustomPokemonListItem, position: Int) {
                deletePokemon(item,position)
            }
        })
        binding.savedFragmentRv.adapter = pokemonSavedListAdapter
    }
    private fun initObserver(){
        viewModel.pokemonList.observe(viewLifecycleOwner, Observer{ list ->
        when(list){
            is Resource.Success -> {
                if(list.data?.isNotEmpty() == true){
                    count = list.data.size
                    savedList = list.data as  MutableList<CustomPokemonListItem>
                    pokemonSavedListAdapter.setList(list.data)
                    pokemonSavedListAdapter.notifyDataSetChanged()
                }
            }
            is Resource.Error -> {
                binding.savedFragmentPlaceholder.isVisible = true
            }
            is Resource.Expired -> {}
            is Resource.Loading -> {}
        }
        })
    }
    private fun deletePokemon(customPokemonListItem: CustomPokemonListItem, pos: Int){
        val builder = AlertDialog.Builder(requireContext(), R.style.MyDialogTheme)
        builder.setMessage("Estas seguro de eliminar este pokemon?")
            .setCancelable(false)
            .setPositiveButton("Si"){dialog, id ->
                customPokemonListItem.isSaved = false
                pokemonSavedListAdapter.removeItemAtPosition(pos)
                pokemonSavedListAdapter.notifyDataSetChanged()
                count -=1
                if(count == 0){
                    binding.savedFragmentPlaceholder.isVisible = true
                }
                viewModel.deletePokemon(customPokemonListItem)
            }
            .setNegativeButton("No"){dialog, id ->
                dialog.dismiss()
            }
            .create()
            .show()
    }
    private fun deleteAllPokemon() {
        val builder = AlertDialog.Builder(requireContext(), R.style.MyDialogTheme) // using custom theme
        builder.setMessage("Are you sure you want to delete all saved Pokemon ?")
            .setCancelable(false)
            .setPositiveButton("Yes") { dialog, id ->
                if (count > 0) {
                    for (i in savedList) {
                        i.isSaved = false
                        viewModel.deletePokemon(i)
                    }
                    savedList.clear()
                    pokemonSavedListAdapter.setList(savedList)
                    pokemonSavedListAdapter.notifyDataSetChanged()
                    binding.savedFragmentPlaceholder.isVisible = true
                    count = 0 // update count
                } else{
                    Toast.makeText(requireContext(), "No saved pokemon", Toast.LENGTH_SHORT).show()
                }

            }
            .setNegativeButton("No") { dialog, id ->
                dialog.dismiss()
            }
        val alert = builder.create()
        alert.show()
    }

}