package com.example.pokedex.ui.fragments

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.pokedex.R
import com.example.pokedex.databinding.FragmentDetailBinding
import com.example.pokedex.model.CustomPokemonListItem
import com.example.pokedex.model.PokemonDetailItem
import com.example.pokedex.ui.ViewModel.DetailViewModel
import com.example.pokedex.util.ImageUtils
import com.example.pokedex.util.Resource
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale

@AndroidEntryPoint
class DetailFragment : Fragment(R.layout.fragment_detail) {
    private lateinit var binding: FragmentDetailBinding
    private val viewModel: DetailViewModel by viewModels()
    private lateinit var pokemon: CustomPokemonListItem

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentDetailBinding.bind(view)

        arguments?.let { bundle ->
            bundle.getParcelable<CustomPokemonListItem>("pokemon")?.let { mPokemon ->
                pokemon = mPokemon
                pokemon.type?.let { setType(it) }
                binding.detailFragmentTitleName.text = pokemon.name.replaceFirstChar {
                    if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
                }
                getPokemonDetails(pokemon.apiId)
                initObserver()
                setupSaveButton()
            }
        }
    }

    private fun setupSaveButton() {
        if (!this::pokemon.isInitialized) return

        binding.detailFragmentSaveButton.text = if (pokemon.isSaved) "Saved" else "Save Pokémon"

        binding.detailFragmentSaveButton.setOnClickListener {
            pokemon.isSaved = !pokemon.isSaved
            viewModel.savePokemon(pokemon)
            Toast.makeText(
                requireContext(),
                if (pokemon.isSaved) "El Pokémon se ha guardado en tu Pokédex" else "Pokémon eliminado de tu Pokédex",
                Toast.LENGTH_SHORT
            ).show()
            binding.detailFragmentSaveButton.text = if (pokemon.isSaved) "Saved" else "Save Pokémon"
        }
    }

    private fun setType(type: String) {
        binding.detailFragmentType.text = "Type: ${type.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
        }}"
    }

    private fun getPokemonDetails(id: Int?) {
        id?.let { viewModel.getPokemonDetails(it) }
    }

    private fun initObserver() {
        viewModel.pokemonDetails.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Success -> resource.data?.let { setupView(it) }
                is Resource.Error -> Toast.makeText(
                    requireContext(),
                    "No se encontraron los detalles del Pokémon",
                    Toast.LENGTH_SHORT
                ).show()
                is Resource.Loading -> {  }
                is Resource.Expired -> {
                    resource.data?.let { setupView(it) }
                    Toast.makeText(
                        requireContext(),
                        "Incapaz de obtener los datos, por favor revisa tu conexión a internet",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun setupView(pokemonDetails: PokemonDetailItem) {
        pokemonDetails.sprites.otherSprites.artwork.front_default?.let {
            ImageUtils.loadImage(binding.detailFragmentImage, it)
        }

        binding.abilitiesContainer.removeAllViews()
        for (ability in pokemonDetails.abilities) {
            val textView = TextView(requireContext()).apply {
                text = ability.ability.name.replaceFirstChar {
                    if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
                }
                textSize = 15f
                setTextColor(Color.WHITE)
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            }
            binding.abilitiesContainer.addView(textView)
        }

        binding.statsContainer.removeAllViews()
        val pokemonStats = mutableListOf<Int>()
        for (stat in pokemonDetails.stat) {
            val textView = TextView(requireContext()).apply {
                text = stat.stat.name.replaceFirstChar {
                    if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
                }
                textSize = 15f
                setTextColor(Color.WHITE)
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            }
            val progressBar = ProgressBar(requireContext(), null, android.R.attr.progressBarStyleHorizontal).apply {
                progress = stat.baseStat ?: 0
                progressTintList = ColorStateList.valueOf(Color.WHITE)
            }
            stat.baseStat?.let { pokemonStats.add(it) }
            binding.statsContainer.addView(textView)
            binding.statsContainer.addView(progressBar)
        }

        val pokemonAverage = if (pokemonStats.isNotEmpty()) pokemonStats.sum() / pokemonStats.size else 0
        val dp = (40 * requireContext().resources.displayMetrics.density).toInt()
        binding.detailFragmentStarContainer.removeAllViews()

        addStarToContainer(dp)
        if (pokemonAverage > 60) addStarToContainer(dp)
        if (pokemonAverage > 79) addStarToContainer(dp)

        pokemon.image?.let { ImageUtils.loadImage(binding.mapviewPlot, it) }
        ImageUtils.setMargins(binding.mapviewPlot, viewModel.plotLeft, viewModel.plotTop)
    }

    private fun addStarToContainer(dp: Int) {
        val img = ImageView(requireContext()).apply {
            layoutParams = LinearLayout.LayoutParams(dp, dp)
        }
        ImageUtils.loadImageDrawable(img, R.drawable.star)
        binding.detailFragmentStarContainer.addView(img)
    }
}