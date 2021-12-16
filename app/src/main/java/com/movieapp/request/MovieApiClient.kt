package com.movieapp.request

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.movieapp.Models.MovieModel
import com.movieapp.response.MoviesSearchResponse
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

// Live Data
class MovieApiClient(var mMovies: MutableLiveData<ArrayList<MovieModel?>> = MutableLiveData()) {
    companion object {
        val instances = MovieApiClient()
    }

    var jobToTagSearch: Job? = null
    var jobToIdSearch: Job? = null

    fun searchMoviesApiByTag(key: String, language: String, tag: String, page: Int) {
        val movieApi = Servicey.instances.movieApi
        val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
            throwable.printStackTrace()
        }
        var responseCall: Call<MoviesSearchResponse>? = null
        jobToTagSearch = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
//           withContext(Dispatchers.IO + exceptionHandler,{
            Log.e("jobToTagSearch", "key: $key language: $language tag: $tag page: $page")
//                val responseCall: Call<MoviesSearchResponse> = movieApi
            responseCall = movieApi
                .searchMovisByTag(
                    tag,
                    key,
                    language,
                    page.toString()
                )
        }
//           )
        jobToTagSearch!!.invokeOnCompletion {
            Log.e("jobToTagSearch", responseCall!!.request().url().url().toString())
            responseCall?.enqueue(object : Callback<MoviesSearchResponse> {
                override fun onResponse(
                    call: Call<MoviesSearchResponse>,
                    response: Response<MoviesSearchResponse>
                ) {
                    Log.e("responseTag", "key: $key language: $language tag: $tag page: $page")
                    if (response.code() == 200) {
                        val movies = response.body()!!.getMovies()
                        Log.e("responseTag", movies.toString())
                        movies.add(null)
                        if (page == 1) {
                            instances.mMovies.postValue(movies)
                        } else {
                            val currentMovies = instances.mMovies.value
                            currentMovies?.remove(currentMovies.last())
                            currentMovies?.addAll(movies)
                            instances.mMovies.postValue(currentMovies)
                        }
                        instances.mMovies.value?.forEach {
                            Log.e("test", "name: ${it?.title}")
                        }
                    } else {
                        try {
                            Log.e("jobToTagSearch", "Exception")
                        } catch (e: IOException) {
                            e.stackTrace
                        }
                    }

                }

                override fun onFailure(call: Call<MoviesSearchResponse>, t: Throwable) {
                    Log.e("onFailure", t.stackTraceToString())
                }
            })
        }
    }

    fun searchMoviesApiById(
        key: String,
        language: String,
        tags: ArrayList<String>,
        pages: ArrayList<Int>,
        listIds: ArrayList<String>
    ) {

        for (i in 0 until listIds.size) {

            searchMoviesId(key, language, tags[i], pages[i], listIds[i])

        }

    }

    fun searchMoviesId(
        key: String,
        language: String,
        tag: String,
        page: Int,
        searched_id: String
    ) {
        Log.e("searchMoviesId", "key: $key language: $language tag: $tag page: $page")
        var returnd_movie = arrayListOf<MovieModel?>()

        val movieApi = Servicey.instances.movieApi
        instances.mMovies.postValue(returnd_movie)

            val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
                throwable.printStackTrace()
            }
            var responseCall: Call<MoviesSearchResponse>? = null
            jobToIdSearch = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
//           withContext(Dispatchers.IO + exceptionHandler,{
                Log.e("jobToIdSearch", "key: $key language: $language tag: $tag page: $page")
//                val responseCall: Call<MoviesSearchResponse> = movieApi
                responseCall = movieApi
                    .searchMovisByTag(
                        tag,
                        key,
                        language,
                        page.toString()
                    )
            }
//           )

            jobToIdSearch!!.invokeOnCompletion {
                Log.e("responseId", responseCall!!.request().url().url().toString())
                responseCall?.enqueue(object : Callback<MoviesSearchResponse> {
                    override fun onResponse(
                        call: Call<MoviesSearchResponse>,
                        response: Response<MoviesSearchResponse>
                    ) {
                        Log.e("responseId", "key: $key language: $language tag: $tag page: $page")
                        if (response.code() == 200) {
                            Log.e("jobToIdSearch", response.body()!!.toString())
                            val movies = response.body()!!.getMovies()
                            for (i in 0 until movies.size) {
                                Log.e(
                                    "SearchmyforEach",
                                    "id: ${movies[i]!!.id.toString()},  searched_id: ${searched_id}"
                                )
                                if (movies[i]!!.id.toString() == searched_id) {
                                    Log.e(
                                        "forEach",
                                        "id: ${movies[i]!!.id.toString()},  searched_id: ${searched_id}"
                                    )
                                    returnd_movie = instances.mMovies.value!!
                                    returnd_movie.add(movies[i])
                                    instances.mMovies.postValue(returnd_movie)
                                    break
                                }
                            }
                        } else {
                            try {
                                Log.e("jobToIdSearch", "Exception")
                            } catch (e: IOException) {
                                e.stackTrace
                            }
                        }

                    }

                    override fun onFailure(call: Call<MoviesSearchResponse>, t: Throwable) {
                        Log.e("onFailure", t.stackTraceToString())
                    }
                })

            }


    }

}


