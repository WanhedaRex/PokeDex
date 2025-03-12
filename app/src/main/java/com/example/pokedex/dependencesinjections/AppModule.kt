package com.example.pokedex.dependencesinjections

import android.content.Context
import com.example.pokedex.api.ApiInterface
import com.example.pokedex.persistence.PokemonDAO
import com.example.pokedex.persistence.PokemonDatabase
import com.example.pokedex.repository.MainRepository
import com.example.pokedex.repository.MainRepositoryImplemention
import com.example.pokedex.util.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun providePokeApi() : ApiInterface = Retrofit.Builder()
        .baseUrl(Constants.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(ApiInterface::class.java)

    @Provides
    fun providePokemonDao(@ApplicationContext applicationContext: Context) : PokemonDAO{
         return PokemonDatabase.getDatabase(applicationContext)!!.pokemonDao()
    }


    @Singleton
    @Provides
    fun provideMainRepository(api: ApiInterface, dao: PokemonDAO): MainRepository = MainRepositoryImplemention(api, dao)
}