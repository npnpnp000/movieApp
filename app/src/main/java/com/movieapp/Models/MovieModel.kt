package com.movieapp.Models

import android.os.Parcel
import android.os.Parcelable

open class MovieModel(var title: String, var poster_path: String, var release_date: String, var id :Int, var overview: String, var vote_average :Float) : Parcelable{
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readFloat()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(title)
        parcel.writeString(poster_path)
        parcel.writeString(release_date)
        parcel.writeInt(id)
        parcel.writeString(overview)
        parcel.writeFloat(vote_average)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<MovieModel?> {
        override fun createFromParcel(parcel: Parcel): MovieModel? {
            return MovieModel(parcel)
        }

        override fun newArray(size: Int): Array<MovieModel?> {
            return arrayOfNulls(size)
        }
    }


}