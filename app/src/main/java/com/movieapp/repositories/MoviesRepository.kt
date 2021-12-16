package com.movieapp.repositories

import androidx.lifecycle.LiveData
import com.movieapp.Models.FavoriteModel
import com.movieapp.Models.MovieModel
import com.movieapp.request.FavoriteApiClient
import com.movieapp.request.MovieApiClient

class MoviesRepository(){
    companion object{
        val instances = MoviesRepository()
        }
    fun getMovies() : LiveData<ArrayList<MovieModel?>> =MovieApiClient.instances.mMovies

    fun getFavorites() : LiveData<HashMap<String,FavoriteModel>> = FavoriteApiClient.instances.mFavorites

    fun searchMovieByTag(key: String, language: String, tag: String, page: Int) {
        MovieApiClient.instances.searchMoviesApiByTag(key, language,tag,page)
    }
    fun searchMovieById(key: String, language: String, tags: ArrayList<String>, pages: ArrayList<Int>, listIds: ArrayList<String>) {
        MovieApiClient.instances.searchMoviesApiById(key, language, tags, pages, listIds )
    }
    fun addToFavorites(category_name: String, page: Int, id: String) {
        FavoriteApiClient.instances.addToFavorites(category_name, page,id)
    }
    fun removeFavorites(id: String) {
        FavoriteApiClient.instances.removeFavorites(id)
    }
    fun requestFavorites() {
        FavoriteApiClient.instances.requestFavorites()
    }
}