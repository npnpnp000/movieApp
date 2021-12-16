package com.movieapp.adapter

import android.content.Context
import android.graphics.drawable.Drawable
import android.icu.util.Calendar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.movieapp.Models.MovieModel
import com.movieapp.R
import com.movieapp.Util.IntegerVersionSignature
import com.movieapp.databinding.CardMovieBinding
import com.movieapp.databinding.CardNullBinding


class MovieMainAdapter(val context: Context,val onMovieListener: OnMovieListener) : RecyclerView.Adapter<MovieMainAdapter.ViewHolder>()  {

    private var list = arrayListOf<MovieModel?>()
    private val VIEW_TYPE_ITEM = 0
    private val VIEW_TYPE_LOADING = 1

    fun setData(newItems: ArrayList<MovieModel?>){
       list = newItems
        notifyDataSetChanged()
    }
    inner class ViewHolder(val binding: CardMovieBinding, itemView: View) : RecyclerView.ViewHolder(itemView) ,
        View.OnClickListener{

        fun bind(movieModel: MovieModel) {

            val car_movie_img = binding.carMovieImg
            val root = binding.root

            root.setOnClickListener(this)

            // Using Glide to ImageView
            val url = "https://image.tmdb.org/t/p/w500/${movieModel.poster_path}" //basic path to get images + specific end path from the movie
            val calendar = Calendar.getInstance()
            val versionNumber =calendar.get(Calendar.DAY_OF_WEEK) + calendar.get(Calendar.WEEK_OF_YEAR) +
                    calendar.get(Calendar.YEAR) *100
            Glide.with(context)
                .load(url)
                .signature(IntegerVersionSignature(versionNumber))
                .placeholder(circularProgressDrawable())
                .error(ContextCompat.getDrawable(context.applicationContext, R.drawable.ic_baseline_image_not_supported_40 ))
                .into(car_movie_img)



        }

        override fun onClick(v: View?) {
            onMovieListener.onMovieClick(bindingAdapterPosition)
        }
    }

    private fun circularProgressDrawable(): Drawable {
        val circularProgressDrawable = CircularProgressDrawable(context)
        circularProgressDrawable.strokeWidth = 5f
        circularProgressDrawable.centerRadius = 30f
        circularProgressDrawable.start()
        return circularProgressDrawable

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view : View?
        val binding = CardMovieBinding.inflate(LayoutInflater.from(context), parent, false)
        val binding_null = CardNullBinding.inflate(LayoutInflater.from(context), parent, false)
        if(viewType == 0){
            view = binding.root
        }else{
            view = binding_null.root
        }

        return ViewHolder(binding, view)
    }
    override fun getItemCount(): Int {
       return list.size
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if(list[position]!= null){
            holder.bind(list[position]!!)
        }

    }
    override fun getItemViewType(position: Int): Int {
        return if(list.get(position)==null)  VIEW_TYPE_LOADING else VIEW_TYPE_ITEM
    }


}




