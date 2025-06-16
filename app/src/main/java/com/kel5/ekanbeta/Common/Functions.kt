package com.kel5.ekanbeta.Common

import java.text.NumberFormat
import java.util.Locale

fun formatRupiah(number: Int?): String {
    val formatter = NumberFormat.getCurrencyInstance(Locale("in", "ID"))
    return formatter.format(number)
}

fun hitungHargaPromo(harga: Int, diskon: Int): Int{
    return harga - (harga * diskon / 100)
}