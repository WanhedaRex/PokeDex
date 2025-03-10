package com.example.pokedex.util

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.Target.SIZE_ORIGINAL
import com.example.pokedex.R

object ImageUtils {

    fun loadImage(imageView: ImageView, url: String){
        Glide.with(imageView.context)
            .load(url)
            .override(SIZE_ORIGINAL, SIZE_ORIGINAL)
            .placeholder(R.drawable.place_holder_img)
            .error(R.drawable.place_holder_img)
            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
            .into(imageView)
    }

    fun loadImageDrawable(imageView: ImageView, drawable: Int){
        Glide.with(imageView.context)
            .load(drawable)
            .override(SIZE_ORIGINAL, SIZE_ORIGINAL)
            .placeholder(R.drawable.place_holder_img)
            .error(R.drawable.place_holder_img)
            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
            .into(imageView)
    }

    fun setMargins(view: View, left: Int, top: Int){
        if(view.layoutParams is ViewGroup.MarginLayoutParams){
            val layoutParams = view.layoutParams as ViewGroup.MarginLayoutParams
            layoutParams.setMargins(left, top, 0,0)
            view.requestLayout()
        }
    }
}