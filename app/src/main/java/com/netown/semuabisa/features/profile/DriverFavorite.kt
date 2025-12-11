package com.netown.semuabisa.features.profile

data class DriverFavorite(
    val name: String,
    val rating: Double,
    val reviews: Int,
    val image: Int,
    val isFavorite: Boolean
)
