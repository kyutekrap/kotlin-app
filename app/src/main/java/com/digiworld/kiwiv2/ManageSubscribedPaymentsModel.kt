package com.digiworld.kiwiv2

data class ManageSubscribedPaymentsModel(
    val paymentName: String,
    val amount: String,
    val payment_date: String,
    val token: String,
    val recipient: String,
    val recipient_address: String,
    val paymentId: String
    )
