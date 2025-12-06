package com.netown.semuabisa.features.notification

data class NotificationModel(
    val title: String,
    val message: String,
    val status: NotificationStatus
)

enum class NotificationStatus {
    SUCCESS, WAITING, CANCELED
}
