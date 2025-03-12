package com.example.pokedex.ui.fragments

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.example.pokedex.databinding.FragmentMapBinding
import com.example.pokedex.ui.ViewModel.MapViewModel
import dagger.hilt.android.AndroidEntryPoint
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.pokedex.R
import com.example.pokedex.util.ImageUtils
import com.example.pokedex.util.Resource


@AndroidEntryPoint
class MapViewFragment : Fragment(R.layout.fragment_map) {
    private lateinit var binding: FragmentMapBinding
    private val viewModel: MapViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?){
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentMapBinding.bind(view)

        binding.mapFragmentBack.setOnClickListener {
            findNavController().popBackStack()
        }

        initObserver()

        lifecycleScope.launchWhenStarted{
            viewModel.getPokemonList()
        }
    }

    private fun initObserver(){
        viewModel.pokemonList.observe(viewLifecycleOwner, Observer {
            list -> when(list){
                is Resource.Success -> {showProgress(false)
                if(list.data?.isNotEmpty() == true){
                    list.data.forEach {pokemon ->
                        val img = ImageView(requireContext())
                        val lp = RelativeLayout.LayoutParams(200, 200)
                        img.layoutParams = lp
                        pokemon.image?.let { ImageUtils.loadImage(img,it) }
                        pokemon.positionLeft?.let { left ->
                            pokemon.positionTop?.let { top ->
                                ImageUtils.setMargins(img,left, top)

                            }
                        }
                        binding.mapFragmentImgLayout.addView(img)
                    }
                }
                }
                is Resource.Error -> {
                    showProgress(false)
                    Toast.makeText(requireContext(), "no se encontro el pokemon", Toast.LENGTH_SHORT).show()
                }
                is Resource.Expired -> {}
                is Resource.Loading -> {
                    showProgress(true)
                }
            }
        })
    }



    private fun showProgress(isVisible: Boolean){
    binding.mapFragmentProgress.isVisible = isVisible
    }




}