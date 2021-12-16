package com.movieapp.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.movieapp.Models.MovieModel

// this class getting movies by the filter
class MoviesSearchResponse {
    @SerializedName("total_results")
    @Expose()
    private var total_cunt :Int = 0


    @SerializedName("results")
    @Expose()
    private var movies = arrayListOf<MovieModel?>()

    fun getTotal_cunt(): Int{
        return total_cunt
    }

    fun getMovies(): ArrayList<MovieModel?>{
        return movies
    }

    override fun toString(): String {
        return "MoviesSearchResponse(total_cunt=$total_cunt, movies=$movies)"
    }


}