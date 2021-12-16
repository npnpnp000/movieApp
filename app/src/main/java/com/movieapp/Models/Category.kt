package com.movieapp.Models

enum class Category(val tab_nmae: String, val title: String) {
    top_rated(
        "Top-Rated",
        "Best Movies Out There!"
    ),
    upcoming(
        "Upcoming",
        "Comming Soon! "
    ),
    now_playing(
        "Now-Plaing",
        "On The Screen!"
    )
    ,
    favorites(
        "Favorites",
        "MY Favorites Movies!"
    );
}
