package com.example.pokedex.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(
    tableName = "pokemon", indices = (arrayOf(Index(value = arrayOf("name"), unique = true)))
)
data class CustomPokemonListItem(
    @ColumnInfo(name = "name")
    val name: String,

    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: Int? = null,

    @ColumnInfo(name = "api")
    val apiId: Int,

    @ColumnInfo(name = "image")
    val Image: String? =null,

    @ColumnInfo(name = "positionLeft")
    val positionLeft: Int? = null,

    @ColumnInfo(name = "positionTop")
    val positionTop: Int? = null,

    @ColumnInfo(name = "type")
    val type: String,

    @ColumnInfo(name = "isSaved")
    var isSaved: String = "false"

) : Parcelable


