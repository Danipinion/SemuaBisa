package com.netown.semuabisa.features.history

data class HistoryModel(
    val fromAddress: String,
    val toAddress: String,
    val dateTime: String,
    val type: String,
    val driver: String,
    val seats: String,
    val paymentStatus: String
)
