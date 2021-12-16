package com.movieapp.Models

class FavoriteModel(var category_name :String, var page: Int, var id : String) {
    override fun toString(): String {
        return "FavoriteModel(category_name='$category_name', page=$page, id='$id')"
    }
}