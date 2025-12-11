package com.netown.semuabisa.features.messages

data class MessageModel(
    val name: String,
    val lastMessage: String,
    val time: String,
    val image: Int,
    var isSelected: Boolean = false // harus var agar bisa berubah
)
