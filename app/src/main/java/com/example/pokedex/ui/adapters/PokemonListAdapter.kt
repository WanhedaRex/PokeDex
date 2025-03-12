package com.example.pokedex.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.pokedex.model.CustomPokemonListItem
import com.example.pokedex.util.ImageUtils
import com.example.pokedex.databinding.ListRowItemBinding
import java.util.Locale

class PokemonListAdapter() : RecyclerView.Adapter<PokemonListAdapter.PokemonViewHolder>() {

    private var onClickListener: OnClickListener? = null
    private var pokemonList = mutableListOf<CustomPokemonListItem>()

    class PokemonViewHolder(private val binding: ListRowItemBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(item: CustomPokemonListItem, onClickListener: OnClickListener?){
            binding.rowCardTitle.text = item.name.replaceFirstChar { if(it .isLowerCase()) it.titlecase(
                Locale.ROOT) else it.toString() }

            binding.rowCardType.text = "Type: ${item.type.replaceFirstChar { if(it.isLowerCase()) it.titlecase(
                Locale.ROOT) else it.toString()}}"

            binding.cardView.setOnClickListener{
                onClickListener?.onClick(item)
            }

            item.image?.let{ ImageUtils.loadImage(binding.rowCardImage, it) }
        }

        companion object{
            fun inflateLayout(parent: ViewGroup) : PokemonViewHolder{
                parent.apply{
                    val inflater = LayoutInflater.from(parent.context)
                    val binding = ListRowItemBinding.inflate(inflater, parent, false)
                    return PokemonViewHolder(binding)
                }
            }
        }
    }

    interface OnClickListener{
       fun onClick(item: CustomPokemonListItem)
    }

    fun setOnClickListener(onClickListener: OnClickListener){
        this.onClickListener = onClickListener
    }



    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PokemonListAdapter.PokemonViewHolder {
       return PokemonViewHolder.inflateLayout((parent))
    }

    override fun onBindViewHolder(holder: PokemonListAdapter.PokemonViewHolder, position: Int) {
       holder.bind(pokemonList[position],onClickListener)
    }

    override fun getItemCount(): Int {
        return pokemonList.size
    }

    fun updateList(list: List<CustomPokemonListItem>){
        pokemonList.addAll(list)
    }

    fun submitList(list: List<CustomPokemonListItem>){
        pokemonList = list as MutableList<CustomPokemonListItem>
        notifyDataSetChanged()
    }



}