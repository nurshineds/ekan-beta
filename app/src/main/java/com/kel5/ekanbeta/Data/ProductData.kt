package com.kel5.ekanbeta.Data

data class ProductData(
    val id: String = "",
    val kategori: String = "",
    val nama: String = "",
    val harga: Int = 0,
    val diskon: Int = 0,
    val rating: Float = 0f,
    val reviewCount: Int = 0,
    val deskripsi: String = "",
    val origin: String = "",
    val penyajian: String = "",
    val kondisi: String = "",
    val potongan: String = "",
    val imageUrl: String = ""
){
    val hargaDiskon: Int
        get() = if (diskon in 1..100) harga - (harga * diskon / 100) else harga
}