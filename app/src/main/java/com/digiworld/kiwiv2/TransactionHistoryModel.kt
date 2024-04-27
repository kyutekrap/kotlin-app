package com.digiworld.kiwiv2

data class TransactionHistoryModel(
    val activityType: String,
    val datetime: String,
    val amount: String,
    val token: String,
    val actionType: String)