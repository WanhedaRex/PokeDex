package com.example.pokedex.ui.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.fragment.app.DialogFragment
import com.example.pokedex.R

class FilterDialog (var filterListener: FilterListener) : DialogFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setRetainInstance(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragment_container = container
        val rootview = layoutInflater.inflate(R.layout.dialog_typefilter, fragment_container, false)
        val fireImage = rootview.findViewById<ImageView>(R.id.dialog_fire_img)
        val waterImage = rootview.findViewById<ImageView>(R.id.dialog_water_img)
        val grassImage = rootview.findViewById<ImageView>(R.id.dialog_grass_img)
        val cancelBtn = rootview.findViewById<Button>(R.id.dialog_cancel_button)

        fireImage.setOnClickListener {
            filterListener.typeToSearch("fire")
            this.dismiss()
        }

        waterImage.setOnClickListener {
            filterListener.typeToSearch("water")
            this.dismiss()
        }

        grassImage.setOnClickListener {
            filterListener.typeToSearch("grass")
            this.dismiss()
        }

        cancelBtn.setOnClickListener {
            this.dismiss()
        }


        return rootview

    }

    interface FilterListener{
        fun typeToSearch(type: String)
    }

    override fun onDestroyView() {
        if(dialog != null && retainInstance) dialog!!.setOnDismissListener(null)
        super.onDestroyView()
    }
}