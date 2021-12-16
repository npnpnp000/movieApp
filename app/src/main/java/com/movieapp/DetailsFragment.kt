package com.movieapp

import android.graphics.drawable.Drawable
import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.movieapp.Models.Category
import com.movieapp.Models.FavoriteModel
import com.movieapp.Models.MovieModel
import com.movieapp.Util.FragmentCommunicator
import com.movieapp.Util.IntegerVersionSignature
import com.movieapp.ViewModel.MoviesListViewModel
import com.movieapp.databinding.FragmentDetailsBinding
import org.json.JSONArray
import org.json.JSONObject

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
private const val ARG_PARAM3 = "param3"
private const val ARG_PARAM4 = "param4"
private const val ARG_PARAM5 = "param5"
private const val ARG_PARAM6 = "param6"
private const val ARG_PARAM7 = "param7"
private const val ARG_PARAM8 = "param8"

class DetailsFragment : Fragment() {

    private var binding: FragmentDetailsBinding? = null
    private var movieId: Int? = null
    private var posterPath: String? = null
    private var title: String? = null
    private var movieOverview: String? = null
    private var voteAverage: Float? = null
    private var releasedDate: String? = null
    private var category: String? = null
    private var page: Int? = null
    private var moviesListVM: MoviesListViewModel? = null
    private var listCategoriesFavorites = arrayListOf<String>()
    private var listIdsFavorites = arrayListOf<String>()
    private var listPagesFavorites = arrayListOf<Int>()
    private var isSelected = false
    private lateinit var communicator : FragmentCommunicator


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            movieId = it.getInt(ARG_PARAM1)
            posterPath = it.getString(ARG_PARAM2)
            title = it.getString(ARG_PARAM3)
            movieOverview = it.getString(ARG_PARAM4)
            voteAverage = it.getFloat(ARG_PARAM5)
            releasedDate = it.getString(ARG_PARAM6)
            category = it.getString(ARG_PARAM7)
            page = it.getInt(ARG_PARAM8)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentDetailsBinding.inflate(inflater, container, false)
        val view = binding!!.root

        // get ViewModel
        moviesListVM = ViewModelProvider(this)[MoviesListViewModel::class.java]

        setVisibelViews()

        requestFavorites()

        ObserverOfChange()

        return view
    }

    private fun requestFavorites() {
        moviesListVM!!.requestFavorites()
    }

    private fun setVisibelViews() {
        binding!!.dateTxt.text = releasedDate               // set released date of the movie
        binding!!.descriptionTxt.text = movieOverview      // set overview of the movie
        binding!!.detailsTitleTxt.text = title              //set movie's namme
        binding!!.ratingRtb.rating = voteAverage!! / 2      // set rating (the original is till 10)

        val url =
            "https://image.tmdb.org/t/p/w500/${posterPath}" //basic path to get images + specific end path from the movie
        val calendar = Calendar.getInstance()       // get today date
        val versionNumber = calendar.get(Calendar.DAY_OF_WEEK) + // get daily version number
                calendar.get(Calendar.WEEK_OF_YEAR) +
                calendar.get(Calendar.YEAR) * 100
        Glide.with(requireContext())            // set glide to loading movie's image
            .load(url)
            .signature(IntegerVersionSignature(versionNumber))  // set daily cline catch
            .placeholder(circularProgressDrawable())
            .error(
                ContextCompat.getDrawable(
                    requireContext().applicationContext,
                    R.drawable.ic_baseline_image_not_supported_40
                )
            )
            .into(binding!!.detailsImg)
        binding!!.favoriteImg.setOnClickListener {
            if (!isSelected) {
                isSelected = true
                moviesListVM!!.addToFavorites(category!!, page!!, movieId.toString())
                binding!!.favoriteImg.setImageResource(R.drawable.ic_baseline_star_50)

            }else{
                moviesListVM!!.removeFavorites( movieId.toString())
                binding!!.favoriteImg.setImageResource(R.drawable.ic_baseline_star_border_50)
                isSelected = false
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        requestFavorites()
        binding = null
    }

    override fun onDetach() {
        super.onDetach()
        communicator.fragmentDetached()
    }

    companion object {
        @JvmStatic
        fun newInstance(
            movieId: Int,
            posterPath: String,
            title: String,
            movieOverview: String,
            voteAverage: Float,
            releasedDate: String,
            category: String,
            page: Int
        ) =
            DetailsFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_PARAM1, movieId)
                    putString(ARG_PARAM2, posterPath)
                    putString(ARG_PARAM3, title)
                    putString(ARG_PARAM4, movieOverview)
                    putFloat(ARG_PARAM5, voteAverage)
                    putString(ARG_PARAM6, releasedDate)
                    putString(ARG_PARAM7, category)
                    putInt(ARG_PARAM8, page)
                }
            }
    }

    //    Observing data change
    fun ObserverOfChange() {
        moviesListVM!!.getFavorites()
            .observe(viewLifecycleOwner, object : Observer<HashMap<String, FavoriteModel>> {
                override fun onChanged(map: HashMap<String, FavoriteModel>?) {

                    Log.e("observegetFavorites", map.toString())
                    if (map != null && map.containsKey(movieId.toString())){
                        isSelected = true
                        binding!!.favoriteImg.setImageResource(R.drawable.ic_baseline_star_50)
                    }
                    Log.e("bifire", map.toString())
                    val parserJsonFavorites = parserJsonFavorites(map)
                    Log.e("after", parserJsonFavorites)
                    val appSharedPrefs =
                        requireContext().getSharedPreferences("app", AppCompatActivity.MODE_PRIVATE)

                    val personalSharedPrefsEditor =
                        requireContext().getSharedPreferences("app", AppCompatActivity.MODE_PRIVATE)?.edit()
                    personalSharedPrefsEditor?.clear()
                    personalSharedPrefsEditor?.putString(
                        "file",
                        parserJsonFavorites
                    )
                    personalSharedPrefsEditor?.apply()
                    fromJsonFavorites(appSharedPrefs.getString("file", "")!!)
                    Log.e("-----SPfrag-------", appSharedPrefs.getString("file", "")!!)


                }
            }
            )
    }

    private fun fromJsonFavorites(jsonAsString: String) {

        Log.e("fromJsonFavorites", jsonAsString)

        val new_listPagesFavorites = arrayListOf<Int>()
        val new_listCategoriesFavorites = arrayListOf<String>()
        val new_listIdsFavorites = arrayListOf<String>()

        if (jsonAsString.isNotEmpty()) {
            val jsonObject = JSONObject(jsonAsString)
            val jArray = jsonObject.getJSONArray("favorites")

            for (i in 0 until jArray.length()) {
                val jsonIndex = jArray.getJSONObject(i)
                new_listPagesFavorites.add(jsonIndex.getInt("page"))
                new_listCategoriesFavorites.add(jsonIndex.getString("category"))
                new_listIdsFavorites.add(jsonIndex.getString("id"))
            }
        }
        listCategoriesFavorites = new_listCategoriesFavorites
        listIdsFavorites = new_listIdsFavorites
        listPagesFavorites = new_listPagesFavorites
    }

    private fun parserJsonFavorites(parser_map: HashMap<String, FavoriteModel>?): String {
        var returnJsonString = ""

        Log.e("parser_map",  parser_map.toString())
            val jsonArray = JSONArray()
        Log.e("map_size",  parser_map?.keys?.size.toString())
            parser_map?.keys?.forEach {
                val json = JSONObject()
                json.put("category", parser_map.get(it)!!.category_name)
                    .put("id", parser_map.get(it)!!.id)
                    .put("page", parser_map.get(it)!!.page)
                jsonArray.put(json)
            }
            val mainJson = JSONObject()
            mainJson.put("favorites", jsonArray)
            returnJsonString = mainJson.toString()

        Log.e("parserJsonFavorites", returnJsonString)
        return returnJsonString

    }

    private fun circularProgressDrawable(): Drawable {
        val circularProgressDrawable = CircularProgressDrawable(requireContext())
        circularProgressDrawable.strokeWidth = 5f
        circularProgressDrawable.centerRadius = 30f
        circularProgressDrawable.start()
        return circularProgressDrawable

    }
    fun setCommunicator(new_communicator :FragmentCommunicator){
        communicator = new_communicator
    }
}
