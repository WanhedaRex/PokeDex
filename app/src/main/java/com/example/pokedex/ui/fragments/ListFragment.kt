package com.example.pokedex.ui.fragments

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.isEmpty
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
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
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentListBinding.bind(view)

    setupRv()
    setupClicks()
    setupSearchView()
    setupFabButtons()
    initObserver()
    }

    private fun setupFabButtons(){
        binding.listFragmentMapFAB.setOnClickListener {
            findNavController().navigate(R.id.action_listFragment_to_mapViewFragment)
        }
        binding.listFragmentSavedFAB.setOnClickListener {
            findNavController().navigate(R.id.action_listFragment_to_savedViewFragment)
        }
    }



    private fun setupRv(){
     pokemonListAdapter = PokemonListAdapter()
     pokemonListAdapter.setOnClickListener(object : PokemonListAdapter.OnClickListener{
         override fun onClick(item: CustomPokemonListItem) {
             val bundle = Bundle()
             bundle.putParcelable("pokemon",item)
             findNavController().navigate(R.id.action_listFragment_to_detailFragment, bundle)
         }
     })
        binding.listFragmentRv.apply {
            adapter = pokemonListAdapter
            addOnScrollListener(object : RecyclerView.OnScrollListener(){
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    if(!recyclerView.canScrollVertically(1) && binding.listFragmentSearchView.query.isEmpty()){
                        binding.listFragmentPaginateProgress.visibility = View.VISIBLE
                         viewModel.getNextPage()
                    }
                }
            })
        }
        binding.listFragmentSwipeToRefresh.setOnRefreshListener{
            if(binding.listFragmentSearchView.query.isEmpty()){
                viewModel.getPokemonList()
            }else{
                binding.listFragmentSwipeToRefresh.isRefreshing = false
            }
        }

    }

private fun setupClicks(){
    binding.listFragmentFilterImg.setOnClickListener{
        val dialog = FilterDialog(this)
        val transaction = childFragmentManager.beginTransaction()
        transaction.add(dialog, "filter-dialog")
        transaction.commit()
    }
}

private fun setupSearchView(){
    binding.listFragmentSearchView.setOnClickListener{
        if(binding.listFragmentSearchView.isEmpty()){
            pokemonListAdapter.submitList(mutableListOf())
            viewModel.getPokemonList()
        }
    }

    binding.listFragmentSearchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener{
        override fun onQueryTextSubmit(query: String?): Boolean {
            if(query != null){
                pokemonListAdapter.submitList(filterListByName(query))
            }else{
                pokemonListAdapter.submitList(mutableListOf())
                viewModel.getPokemonList()
            }
            return false
        }

        override fun onQueryTextChange(query: String?): Boolean {
            if(query != null){
                pokemonListAdapter.submitList(filterListByName(query))
            }
            return false
        }

    })
}



        override fun onResume(){
            super.onResume()
            viewModel.getPokemonList()
            }

    private fun initObserver(){
        viewModel.pokemonList.observe(viewLifecycleOwner, Observer{ list ->
            when(list){
                is Resource.Success -> {
                    if(list.data?.isNotEmpty() == true){
                       pokemonList = list.data as ArrayList<CustomPokemonListItem>
                        pokemonListAdapter.updateList(list.data)
                        pokemonListAdapter.notifyDataSetChanged()
                        showProgressBar(false)
                        if(binding.listFragmentSwipeToRefresh.isRefreshing){
                            binding.listFragmentSwipeToRefresh.isRefreshing = false
                        }
                    }else{
                        showProgressBar(false)
                        showEmptyRecyclerViewError()
                    }
                }
                is Resource.Error -> {
                    showProgressBar(false)
                    showEmptyRecyclerViewError()
                }
                is Resource.Expired -> {}
                is Resource.Loading -> {
                    showProgressBar(true)
                }
            }
        })
    }

    private fun showEmptyRecyclerViewError(){
        Toast.makeText(requireContext(), "No items found", Toast.LENGTH_SHORT).show()
    }
    private fun showProgressBar(isVisible: Boolean){
        binding.listFragmentProgress.isVisible = isVisible
        binding.listFragmentPaginateProgress.visibility = View.GONE
    }

    override fun typeToSearch(type: String) {
        shouldPaginate = false
        pokemonListAdapter.submitList(filterListByType(type))
    }
    private fun filterListByType(type : String) : List<CustomPokemonListItem>{
         return pokemonList.filter{it.type == type}
    }
    private fun filterListByName(name : String) : List<CustomPokemonListItem>{
        return pokemonList.filter{it.name.contains(name) }
    }

}