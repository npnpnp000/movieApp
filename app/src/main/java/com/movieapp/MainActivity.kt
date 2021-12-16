package com.movieapp

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentOnAttachListener
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayout
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.movieapp.Models.Category
import com.movieapp.Models.FavoriteModel
import com.movieapp.Models.MovieModel
import com.movieapp.Util.FragmentCommunicator
import com.movieapp.ViewModel.MoviesListViewModel
import com.movieapp.adapter.MovieMainAdapter
import com.movieapp.adapter.OnMovieListener
import com.movieapp.databinding.ActivityMainBinding
import org.json.JSONArray
import org.json.JSONObject


class MainActivity : AppCompatActivity(),FragmentCommunicator {

    private var moviesListVM: MoviesListViewModel? = null
    private lateinit var binding: ActivityMainBinding
    private lateinit var movies_recyclerView_adapter: MovieMainAdapter
    private var current_page = 1
    private var selected_category = Category.top_rated.name
    private var isLoading = false
    private val categoryList = Category.values()
    val START_ID = 1000
    private var listCategoriesFavorites = arrayListOf<String>()
    private var listIdsFavorites = arrayListOf<String>()
    private var listPagesFavorites = arrayListOf<Int>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // get ViewModel
        moviesListVM = ViewModelProvider(this)[MoviesListViewModel::class.java]

        setTitle(Category.top_rated.title) // set first title

        setTabs()                         // all tab setting

        setMoviesRecyclerView()          // all sets of the movies list

//        listCategoriesFavorites.add(Category.top_rated.name)
//        listPagesFavorites.add(1)
//        listIdsFavorites.add("482321")
        getSaveFavorites()

        ObserverOfChange()

    }

    private fun getSaveFavorites() {
        Log.e("-----getSaveFavorites-------","getSaveFavorites")
        val appSharedPrefs =
            getSharedPreferences("app", AppCompatActivity.MODE_PRIVATE)
        if (appSharedPrefs!!.contains("file")) {
            fromJsonFavorites(appSharedPrefs.getString("file", "")!!)
            Log.e("-----SPStrt-------", appSharedPrefs.getString("file", "")!!)

            for(i in 0 until listCategoriesFavorites.size){
                moviesListVM!!.addToFavorites(listCategoriesFavorites[i],listPagesFavorites[i],listIdsFavorites[i])
            }
        }
    }

    //function to set the main title
    private fun setTitle(text: String) {
        binding.mainTitleTxt.text = text
    }

    // all tab setting
    private fun setTabs() {

        dynamicSetTabs()        //dynamic set tabs for adding categories easily

        setTabsListener()       // set tabs behavior
    }

    private fun setTabsListener() {
        binding.tabTbl.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab!!.id - START_ID) {             // if selected tab  (id - 1000) equal to category
                    Category.now_playing.ordinal -> {
                        setBehavior(Category.now_playing)
                    }  // set the behavior
                    Category.top_rated.ordinal -> {
                        setBehavior(Category.top_rated)
                    }
                    Category.upcoming.ordinal -> {
                        setBehavior(Category.upcoming)
                    }
                    Category.favorites.ordinal -> {
                        setFavoritesBehavior(Category.favorites)
                    } // favorites have special behavior

                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        }
        )
    }

    private fun setFavoritesBehavior(selectedCategory: Category) {
        selected_category = selectedCategory.name         // set the category
        current_page = 1                                 // set page to the first

        setTitle(selectedCategory.title)                // set maine title

        if (listCategoriesFavorites.isNotEmpty()) {
            Log.e("is", listCategoriesFavorites.toString())
            searchMovieById(                              // make favorites request
                getString(R.string.key),
                getString(R.string.def_language),
                listCategoriesFavorites,
                listPagesFavorites,
                listIdsFavorites
            )
        } else { // fake sear
          val  fake_listCategoriesFavorites = arrayListOf<String>()
           val fake_listPagesFavorites = arrayListOf<Int>()
              val  fake_listIdsFavorites = arrayListOf<String>()
            Log.e("isEmpty", "listCategoriesFavorites emty ")
            fake_listCategoriesFavorites.add(Category.top_rated.name)
            fake_listPagesFavorites.add(1)
            fake_listIdsFavorites.add("000000")

            searchMovieById(                              // make favorites request
                getString(R.string.key),
                getString(R.string.def_language),
                fake_listCategoriesFavorites,
                fake_listPagesFavorites,
                fake_listIdsFavorites
            )
        }
        requestFavorites()

    }

    private fun setBehavior(selectedCategory: Category) {
        selected_category = selectedCategory.name         // set the category
        current_page = 1                                 // set page to the first

        setTitle(selectedCategory.title)                // set maine title

        searchMovieByTag(                              // make new data request
            getString(R.string.key),
            getString(R.string.def_language),
            selected_category,
            current_page
        )
    }

    //dynamic set tabs
    private fun dynamicSetTabs() {
        for (i in 0 until categoryList.size) {
            val tab: TabLayout.Tab =
                binding.tabTbl.newTab()  // create tabs as the number of categories
            tab.id = START_ID + categoryList[i].ordinal       // id: 1000+ category ordinal
            tab.text = categoryList[i].tab_nmae              // tab Text: category text
            binding.tabTbl.addTab(tab)                       // add to the view
        }
    }

    // all sets of the movies Movies RecyclerView
    private fun setMoviesRecyclerView() {
        //set adapter and click listener
        movies_recyclerView_adapter = MovieMainAdapter(this, object : OnMovieListener {
            override fun onMovieClick(position: Int) {
                Log.e("onMovieClick", "Go to movie $position ")
                startFragment(position)
            }
        })
        // set the layoutManager to horizontal list
        binding.moviesRcv.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        // loading data for the first time
        searchMovieByTag(
            getString(R.string.key),
            getString(R.string.def_language),
            selected_category,
            current_page
        )
        initScrollListener()  // set the lisiner to the end page
    }

    private fun startFragment(position: Int) {
        val fragmentManager = supportFragmentManager

        val transaction = fragmentManager.beginTransaction()
        transaction.setCustomAnimations(
            R.anim.enter,
            R.anim.exit,
            R.anim.pop_enter,
            R.anim.pop_exit
        )

        val selectedMovie = moviesListVM!!.getMovies().value?.get(position)!!
        Log.e(
            "${selectedMovie.id}",
            "${selectedMovie.poster_path}, ${selectedMovie.title} ${selectedMovie.overview}${selectedMovie.vote_average}${selectedMovie.release_date}"
        )
        val newDetailsFragment: DetailsFragment = DetailsFragment.newInstance(
            selectedMovie.id,
            selectedMovie.poster_path,
            selectedMovie.title,
            selectedMovie.overview,
            selectedMovie.vote_average,
            selectedMovie.release_date,
            selected_category,
            current_page
        )
        newDetailsFragment.setCommunicator(this)
        transaction.replace(binding.mainRll.id, newDetailsFragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    //    Observing data change
    fun ObserverOfChange() {
        moviesListVM!!.getMovies().observe(this, object : Observer<ArrayList<MovieModel?>> {
            override fun onChanged(list: ArrayList<MovieModel?>?) {
                if (list != null) {
                    list.forEach {
                        Log.e("test", "name: ${it?.title} id: ${it?.id}")
                    }
                } else {
                    Log.e("test", "empty  ")
                }
                if (current_page != 1) {                     //if current_page = 1 this is new category and need refresh the list else is new page and need to save the position
                    val recyclerViewState =
                        binding.moviesRcv.getLayoutManager() // save position of the list
                            ?.onSaveInstanceState()
                    setRecyclerViewAdapter(list!!)
                    binding.moviesRcv.getLayoutManager()
                        ?.onRestoreInstanceState(recyclerViewState)   // fix the list position after refresh to the data to the saved position
                } else {
                    setRecyclerViewAdapter(list!!)   // set new data to the Recycler View (no position saved)
                }

                isLoading = false
            }
        })

        moviesListVM!!.getFavorites()
            .observe(this, object : Observer<HashMap<String, FavoriteModel>> {
                override fun onChanged(map: HashMap<String, FavoriteModel>?) {

                    Log.e("observegetFavorites", map.toString())

                    val parserJsonFavorites = parserJsonFavorites(map)
                    val appSharedPrefs =
                        getSharedPreferences("app", AppCompatActivity.MODE_PRIVATE)

                    val personalSharedPrefsEditor =
                        getSharedPreferences("app", AppCompatActivity.MODE_PRIVATE)?.edit()
                    personalSharedPrefsEditor?.clear()
                    personalSharedPrefsEditor?.putString(
                        "file",
                        parserJsonFavorites
                    )
                    personalSharedPrefsEditor?.apply()
                    fromJsonFavorites(appSharedPrefs.getString("file", "")!!)
                    Log.e("-----SPmain-------", appSharedPrefs.getString("file", "")!!)

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



        Log.e("EEEEEEEEEEEEEEEEEEEE",listCategoriesFavorites.toString()+"---------"+listIdsFavorites.toString()+"--------------"+listPagesFavorites.toString())
    }

    private fun parserJsonFavorites(parser_map: HashMap<String, FavoriteModel>?): String {
        var returnJsonString = ""


            val jsonArray = JSONArray()
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

    // function to set the data of the RecyclerView
    private fun setRecyclerViewAdapter(list: ArrayList<MovieModel?>) {
        binding.moviesRcv.adapter = movies_recyclerView_adapter // set new Adapter
        movies_recyclerView_adapter.setData(list)               // add the new data
    }

    // function to viewModel data request
    fun searchMovieByTag(key: String, language: String, tag: String, page: Int) {
        isLoading = true  // flag start loading data
        moviesListVM?.searchMovieByTag(key, language, tag, page) // viewModel data request

    }

    // function to viewModel data request
    fun searchMovieById(
        key: String,
        language: String,
        tags: ArrayList<String>,
        pages: ArrayList<Int>,
        Ids: ArrayList<String>
    ) {
        isLoading = true  // flag start loading data
        moviesListVM?.searchMovieByid(key, language, tags, pages, Ids) // viewModel data request

    }

    fun requestFavorites() {
        moviesListVM!!.requestFavorites()
    }

    // set the lisiner to the end page
    private fun initScrollListener() {
        binding.moviesRcv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager?

                if (!isLoading) {               // do only if not loading data
                    if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == moviesListVM!!.getMovies().value!!.size - 1) { //bottom of list!
                        current_page += 1       // new page
                        searchMovieByTag(       //request the data of the page
                            getString(R.string.key),
                            getString(R.string.def_language),
                            selected_category,
                            current_page
                        )

                    }
                }
            }
        })
    }

    override fun fragmentDetached() {

        if (selected_category == "favorites") {
            if (listCategoriesFavorites.isNotEmpty()) {
                Log.e("is", listCategoriesFavorites.toString())
                searchMovieById(                              // make favorites request
                    getString(R.string.key),
                    getString(R.string.def_language),
                    listCategoriesFavorites,
                    listPagesFavorites,
                    listIdsFavorites
                )
            } else { // fake sear
                val fake_listCategoriesFavorites = arrayListOf<String>()
                val fake_listPagesFavorites = arrayListOf<Int>()
                val fake_listIdsFavorites = arrayListOf<String>()
                Log.e("isEmpty", "listCategoriesFavorites emty ")
                fake_listCategoriesFavorites.add(Category.top_rated.name)
                fake_listPagesFavorites.add(1)
                fake_listIdsFavorites.add("000000")

                searchMovieById(                              // make favorites request
                    getString(R.string.key),
                    getString(R.string.def_language),
                    fake_listCategoriesFavorites,
                    fake_listPagesFavorites,
                    fake_listIdsFavorites
                )
            }
            requestFavorites()
        }
    }
}