package com.movieapp.request

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.movieapp.Models.FavoriteModel

class FavoriteApiClient(var mFavorites: MutableLiveData<HashMap<String, FavoriteModel>> = MutableLiveData()) {
    companion object {
        val instances = FavoriteApiClient()
    }
    fun addToFavorites(category_name: String, page: Int, id: String) {
        Log.e("addToFavorites_api",mFavorites.value.toString())
            var new_favorites = hashMapOf<String, FavoriteModel>()
        if (mFavorites.value!= null){
            new_favorites = mFavorites.value!!
        }
            new_favorites.put(id, FavoriteModel(category_name, page, id))
            mFavorites.postValue(new_favorites)
    }
    fun removeFavorites(id: String) {
        Log.e("removeFavorites",mFavorites.value.toString())
        if (mFavorites.value!!.containsKey(id)) {
            var new_favorites = hashMapOf<String, FavoriteModel>()
            new_favorites = mFavorites.value!!
            new_favorites.remove(id)
            mFavorites.postValue(new_favorites)
        }
    }
    fun requestFavorites() {
        Log.e("requestFavorites_api",mFavorites.value.toString())
        if(mFavorites.value != null && mFavorites.value!!.isNotEmpty()){
            mFavorites.postValue(mFavorites.value)
        }else{
            mFavorites.postValue(hashMapOf<String, FavoriteModel>())
        }

    }
}