package com.example.pokedex.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(
    tableName = "pokemon",
    indices = [Index(value = ["name"], unique = true)]
)
data class CustomPokemonListItem(
    @ColumnInfo(name = "name")
    val name: String,

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int = 0,

    @ColumnInfo(name = "api")
    val apiId: Int,

    @ColumnInfo(name = "image")
    val image: String? = null,

    @ColumnInfo(name = "positionLeft")
    val positionLeft: Int? = null,

    @ColumnInfo(name = "positionTop")
    val positionTop: Int? = null,

    @ColumnInfo(name = "type")
    val type: String,

    @ColumnInfo(name = "isSaved")
    var isSaved: Boolean = false

) : Parcelable