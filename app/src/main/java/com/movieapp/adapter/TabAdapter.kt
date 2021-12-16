package com.tRadioChaneloappp10.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.movieapp.Models.Category
import com.movieapp.databinding.CardTabBinding


class TabAdapter(val context: Context) : RecyclerView.Adapter<TabAdapter.ViewHolder>()  {

    private val tabList = Category.values()

    inner class ViewHolder( binding: CardTabBinding) : RecyclerView.ViewHolder(binding.root) {

        val car_tab_txt = binding.carTabTxt
        fun bind(category: Category) {

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val binding = CardTabBinding.inflate(LayoutInflater.from(context), parent, false)

        return ViewHolder(binding)
    }
    override fun getItemCount(): Int {
       return tabList.size
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bind(tabList[position])
    }
    override fun getItemViewType(position: Int): Int {
        return itemCount
    }
}




