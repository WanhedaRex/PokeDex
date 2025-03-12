package com.example.pokedex.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.pokedex.model.CustomPokemonListItem
import com.example.pokedex.util.ImageUtils
import com.example.pokedex.databinding.ListSavedRowItemBinding
import java.util.Locale

class PokemonSavedListAdapter() : RecyclerView.Adapter<PokemonSavedListAdapter.PokemonViewHolder>() {

    private var onClickListener: OnClickListener? = null
    private var onDeleteListener: OnDeleteListener? = null

    private var pokemonList = mutableListOf<CustomPokemonListItem>()

    class PokemonViewHolder(private val binding: ListSavedRowItemBinding) : RecyclerView.ViewHolder(binding.root){

        fun bind(item: CustomPokemonListItem, onClickListener: OnClickListener?, onDeleteListener: OnDeleteListener?, position: Int){
            binding.rowCardTitle.text = item.name.replaceFirstChar { if(it .isLowerCase()) it.titlecase(
                Locale.ROOT) else it.toString() }

            binding.rowCardType.text = "Type: ${item.type.replaceFirstChar { if(it.isLowerCase()) it.titlecase(
                Locale.ROOT) else it.toString()}}"

            binding.cardView.setOnClickListener{
                onClickListener?.onClick(item)
            }

            item.image?.let{ ImageUtils.loadImage(binding.rowCardImage, it) }

            binding.rowDeleteImg.setOnClickListener {
                onDeleteListener?.onDelete(item, position)
            }
        }

        companion object{
            fun inflateLayout(parent: ViewGroup) : PokemonViewHolder{
                parent.apply{
                    val inflater = LayoutInflater.from(parent.context)
                    val binding = ListSavedRowItemBinding.inflate(inflater, parent, false)
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

    interface OnDeleteListener{
        fun onDelete(item: CustomPokemonListItem, position: Int)
    }

    fun setOnDeleteListener(onDeleteListener: OnDeleteListener){
        this.onDeleteListener = onDeleteListener
    }



    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PokemonSavedListAdapter.PokemonViewHolder {
        return PokemonViewHolder.inflateLayout((parent))
    }

    override fun onBindViewHolder(holder: PokemonSavedListAdapter.PokemonViewHolder, position: Int) {
        holder.bind(pokemonList[position], onClickListener, onDeleteListener, position)
    }

    override fun getItemCount(): Int {
        return pokemonList.size
    }

    fun removeItemAtPosition(position: Int){
        pokemonList.removeAt(position)
    } 

    fun setList(list: List<CustomPokemonListItem>){
        pokemonList.clear()
        pokemonList = list as MutableList<CustomPokemonListItem>
    }




}