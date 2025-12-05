package com.netown.semuabisa.features.train

data class Seat(
    val id: Int,
    var status: SeatStatus
)

enum class SeatStatus {
    AVAILABLE,
    SELECTED,
    TAKEN
}