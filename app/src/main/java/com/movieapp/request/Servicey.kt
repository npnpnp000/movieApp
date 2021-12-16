package com.movieapp.request

import com.movieapp.Util.MovieApi
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

class Servicey {
    companion object {
        val instances = Servicey()
    }
    private val retrofitBuilder =
        Retrofit.Builder().baseUrl("https://api.themoviedb.org").addConverterFactory(GsonConverterFactory.create())
    private val retrofit = retrofitBuilder.build()
    val movieApi = retrofit.create(MovieApi::class.java)
}