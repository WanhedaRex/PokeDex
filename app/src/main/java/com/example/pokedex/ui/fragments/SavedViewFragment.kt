package com.example.pokedex.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pokedex.R
import com.example.pokedex.databinding.FragmentSavedBinding
import com.example.pokedex.model.CustomPokemonListItem
import com.example.pokedex.ui.ViewModel.SavedViewModel
import com.example.pokedex.ui.adapters.PokemonSavedListAdapter
import com.example.pokedex.util.Resource
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SavedViewFragment : Fragment(R.layout.fragment_saved) {
    private lateinit var binding: FragmentSavedBinding
    private val viewModel: SavedViewModel by viewModels()
    private lateinit var pokemonSavedListAdapter: PokemonSavedListAdapter
    private var savedList = mutableListOf<CustomPokemonListItem>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSavedBinding.bind(view)

        setupRv()
        initObserver()

        binding.savedFragmentBack.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.savedFragmentSettingsImg.setOnClickListener {
            deleteAllPokemon()
        }

        viewModel.getSavedPokemon()
    }

    private fun setupRv() {
        pokemonSavedListAdapter = PokemonSavedListAdapter()
        binding.savedFragmentRv.apply {
            adapter = pokemonSavedListAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
        pokemonSavedListAdapter.setOnClickListener(object : PokemonSavedListAdapter.OnClickListener {
            override fun onClick(item: CustomPokemonListItem) {
                val bundle = Bundle().apply { putParcelable("pokemon", item) }
                findNavController().navigate(R.id.action_savedViewFragment_to_detailFragment, bundle)
            }
        })
        pokemonSavedListAdapter.setOnDeleteListener(object : PokemonSavedListAdapter.OnDeleteListener {
            override fun onDelete(item: CustomPokemonListItem, position: Int) {
                deletePokemon(item, position)
            }
        })
    }

    private fun initObserver() {
        viewModel.pokemonList.observe(viewLifecycleOwner, Observer { resource ->
            when (resource) {
                is Resource.Success -> {
                    resource.data?.let { pokemonList ->
                        Log.d("SavedViewFragment", "Received ${pokemonList.size} saved Pokémon")
                        savedList.clear()
                        savedList.addAll(pokemonList)
                        pokemonSavedListAdapter.setList(pokemonList)
                        binding.savedFragmentPlaceholder.isVisible = pokemonList.isEmpty()
                    } ?: run {
                        Log.d("SavedViewFragment", "Success but data is null")
                        savedList.clear()
                        pokemonSavedListAdapter.setList(emptyList())
                        binding.savedFragmentPlaceholder.isVisible = true
                    }
                }
                is Resource.Error -> {
                    Log.d("SavedViewFragment", "Error: ${resource.message}")
                    binding.savedFragmentPlaceholder.isVisible = true
                    Toast.makeText(requireContext(), "Error: ${resource.message}", Toast.LENGTH_SHORT).show()
                }
                is Resource.Loading -> {
                    Log.d("SavedViewFragment", "Loading saved Pokémon")

                }
                is Resource.Expired -> {}
            }
        })
    }

    private fun deletePokemon(customPokemonListItem: CustomPokemonListItem, pos: Int) {
        AlertDialog.Builder(requireContext(), R.style.MyDialogTheme)
            .setMessage("¿Estás seguro de eliminar este Pokémon?")
            .setCancelable(false)
            .setPositiveButton("Sí") { _, _ ->
                customPokemonListItem.isSaved = false
                viewModel.deletePokemon(customPokemonListItem)
                pokemonSavedListAdapter.removeItemAtPosition(pos)
                viewModel.getSavedPokemon()
            }
            .setNegativeButton("No") { dialog, _ -> dialog.dismiss() }
            .create()
            .show()
    }

    private fun deleteAllPokemon() {
        AlertDialog.Builder(requireContext(), R.style.MyDialogTheme)
            .setMessage("Are you sure you want to delete all saved Pokémon?")
            .setCancelable(false)
            .setPositiveButton("Yes") { _, _ ->
                viewModel.deleteAllPokemon()
                Toast.makeText(requireContext(), "All Saved Pokémon Deleted", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("No") { dialog, _ -> dialog.dismiss() }
            .create()
            .show()
    }
}