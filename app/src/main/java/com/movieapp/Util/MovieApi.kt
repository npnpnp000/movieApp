package com.movieapp.Util

import com.movieapp.response.MoviesSearchResponse
import retrofit2.http.GET
import retrofit2.Call
import retrofit2.http.Path
import retrofit2.http.Query

interface MovieApi {

    //search for movies by tag
    @GET("3/movie/{tag}?")
     fun searchMovisByTag(
        @Path("tag") tag: String,
        @Query("api_key") key: String,
        @Query("language") language: String,
        @Query("page") page: String,
    ): Call<MoviesSearchResponse>

    //search for movie by ID
    @GET("3/movie/{id}?")
     fun searchMovieByID(
        @Path("id") id: String,
        @Query("api_key") key: String,
        @Query("language") language: String,
    ): Call<MoviesSearchResponse>

}