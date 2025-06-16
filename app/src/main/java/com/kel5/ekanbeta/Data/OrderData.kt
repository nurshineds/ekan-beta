package com.kel5.ekanbeta.Data

data class OrderData(
    val id: String = "",
    val address: AddressData = AddressData(),
    val status: String = "",
    val items: Map<String, Long> = emptyMap(),
    val total: Float = 0f
)
