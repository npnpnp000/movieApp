package com.movieapp.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.movieapp.Models.FavoriteModel
import com.movieapp.Models.MovieModel
import com.movieapp.repositories.MoviesRepository
import com.movieapp.request.FavoriteApiClient
import com.movieapp.request.MovieApiClient

//ViewModel Class
class MoviesListViewModel() : ViewModel() {


    fun getMovies(): LiveData<ArrayList<MovieModel?>> = MoviesRepository.instances.getMovies()

    fun getFavorites() : LiveData<HashMap<String,FavoriteModel>> = MoviesRepository.instances.getFavorites()

    fun searchMovieByTag(key: String, language: String, tag: String, page: Int) {
        MoviesRepository.instances.searchMovieByTag(key, language,tag,page)
    }
    fun searchMovieByid(key: String, language: String, tags: ArrayList<String>, pages: ArrayList<Int>, listIds: ArrayList<String>) {
        MoviesRepository.instances.searchMovieById(key, language, tags, pages, listIds )
    }
    fun addToFavorites(category_name: String, page: Int, id: String) {
        MoviesRepository.instances.addToFavorites(category_name, page,id)
    }
    fun removeFavorites(id: String) {
        MoviesRepository.instances.removeFavorites(id)
    }
    fun requestFavorites() {
        MoviesRepository.instances.requestFavorites()
    }
}