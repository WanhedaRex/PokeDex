package com.example.pokedex.ui.fragments

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pokedex.R
import com.example.pokedex.databinding.FragmentListBinding
import com.example.pokedex.model.CustomPokemonListItem
import com.example.pokedex.ui.ViewModel.ListViewModel
import com.example.pokedex.ui.adapters.PokemonListAdapter
import com.example.pokedex.util.Resource
import com.example.pokedex.ui.dialogs.FilterDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ListFragment : Fragment(R.layout.fragment_list), FilterDialog.FilterListener {
    private lateinit var binding: FragmentListBinding
    private val viewModel: ListViewModel by viewModels()
    private lateinit var pokemonListAdapter: PokemonListAdapter
    private var pokemonList = mutableListOf<CustomPokemonListItem>()
    private var shouldPaginate = true
    private var isLoading = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentListBinding.bind(view)

        setupRv()
        setupClicks()
        setupSearchView()
        setupFabButtons()
        initObserver()

        viewModel.getPokemonList()
    }

    private fun setupFabButtons() {
        binding.listFragmentMapFAB.setOnClickListener {
            findNavController().navigate(R.id.action_listFragment_to_mapViewFragment)
        }
        binding.listFragmentSavedFAB.setOnClickListener {
            findNavController().navigate(R.id.action_listFragment_to_savedViewFragment)
        }
    }

    private fun setupRv() {
        pokemonListAdapter = PokemonListAdapter()
        pokemonListAdapter.setOnClickListener(object : PokemonListAdapter.OnClickListener {
            override fun onClick(item: CustomPokemonListItem) {
                val bundle = Bundle().apply { putParcelable("pokemon", item) }
                findNavController().navigate(R.id.action_listFragment_to_detailFragment, bundle)
            }
        })

        binding.listFragmentRv.apply {
            adapter = pokemonListAdapter
            layoutManager = LinearLayoutManager(requireContext())
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    if (!recyclerView.canScrollVertically(1) &&
                        binding.listFragmentSearchView.query.isEmpty() &&
                        shouldPaginate &&
                        !isLoading &&
                        pokemonList.size < 151) {
                        isLoading = true
                        binding.listFragmentPaginateProgress.visibility = View.VISIBLE
                        viewModel.getNextPage()
                    }
                }
            })
        }

        binding.listFragmentSwipeToRefresh.setOnRefreshListener {
            if (binding.listFragmentSearchView.query.isEmpty()) {
                shouldPaginate = true
                viewModel.getPokemonList()
            } else {
                binding.listFragmentSwipeToRefresh.isRefreshing = false
            }
        }
    }

    private fun setupClicks() {
        binding.listFragmentFilterImg.setOnClickListener {
            FilterDialog(this).show(childFragmentManager, "filter-dialog")
        }
    }

    private fun setupSearchView() {
        binding.listFragmentSearchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    shouldPaginate = false
                    viewModel.searchPokemon(it)
                } ?: run {
                    shouldPaginate = true
                    viewModel.getPokemonList()
                }
                return true
            }

            override fun onQueryTextChange(query: String?): Boolean {
                query?.let {
                    shouldPaginate = false
                    viewModel.searchPokemon(it)
                }
                return true
            }
        })
    }

    override fun onResume() {
        super.onResume()
        if (pokemonList.isEmpty()) {
            viewModel.getPokemonList()
        }
    }

    private fun initObserver() {
        viewModel.pokemonList.observe(viewLifecycleOwner, Observer { resource ->
            when (resource) {
                is Resource.Success -> {
                    resource.data?.let { data ->
                        pokemonList.clear()
                        pokemonList.addAll(data)
                        pokemonListAdapter.submitList(data.toList())
                        showProgressBar(false)
                        binding.listFragmentPaginateProgress.visibility = View.GONE
                        isLoading = false
                        if (binding.listFragmentSwipeToRefresh.isRefreshing) {
                            binding.listFragmentSwipeToRefresh.isRefreshing = false
                        }
                    } ?: run {
                        showEmptyRecyclerViewError()
                    }
                }
                is Resource.Error -> {
                    showProgressBar(false)
                    binding.listFragmentPaginateProgress.visibility = View.GONE
                    isLoading = false
                    Toast.makeText(requireContext(), resource.message ?: "Error loading Pokémon", Toast.LENGTH_SHORT).show()
                }
                is Resource.Loading -> {
                    if (!isLoading) showProgressBar(true)
                }
                is Resource.Expired -> {}
            }
        })
    }

    private fun showEmptyRecyclerViewError() {
        Toast.makeText(requireContext(), "No items found", Toast.LENGTH_SHORT).show()
    }

    private fun showProgressBar(isVisible: Boolean) {
        binding.listFragmentProgress.isVisible = isVisible
        if (!isLoading) binding.listFragmentPaginateProgress.visibility = View.GONE
    }

    override fun typeToSearch(type: String) {
        shouldPaginate = false
        pokemonListAdapter.submitList(filterListByType(type))
    }

    private fun filterListByType(type: String): List<CustomPokemonListItem> {
        return pokemonList.filter { it.type == type }
    }

    private fun filterListByName(name: String): List<CustomPokemonListItem> {
        return pokemonList.filter { it.name.contains(name, ignoreCase = true) }
    }
}