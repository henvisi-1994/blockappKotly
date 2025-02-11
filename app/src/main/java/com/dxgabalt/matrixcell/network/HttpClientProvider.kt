package com.dxgabalt.matrixcell.network

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object HttpClientProvider {

    private val gson: Gson = GsonBuilder()
        .setLenient()
        .create()

    fun getClient(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://matrixcell.onrender.com/devices/") // Cambia esto por la URL de tu API
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }
}
