package com.kel5.ekanbeta.ViewModel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.kel5.ekanbeta.Data.ProductData
import com.kel5.ekanbeta.Repository.CartRepo
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import androidx.compose.runtime.State
import androidx.lifecycle.viewModelScope
import com.kel5.ekanbeta.Common.hitungHargaPromo
import com.kel5.ekanbeta.Repository.ProductRepo
import com.kel5.ekanbeta.Repository.StockReqRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CartViewModel(
    private val cartRepo : CartRepo = CartRepo(),
    private val productRepo : ProductRepo = ProductRepo(),
    private val stockReqRepo: StockReqRepo = StockReqRepo()
) : ViewModel(){
    private val _toastMessage = MutableSharedFlow<String>()
    val toastMessage = _toastMessage.asSharedFlow()

    private val _products = mutableStateOf<Map<String, ProductData>>(emptyMap())
    val products: State<Map<String, ProductData>> = _products

    private val _stockReqStatus = MutableStateFlow<String?>(null)
    val stockReqStatus: StateFlow<String?> = _stockReqStatus

    fun loadProduct(productId: String) {
        viewModelScope.launch {
            val product = productRepo.getProductById(productId)

            product?.let {
                val updatedMap = _products.value.toMutableMap()
                updatedMap[productId] = it
                _products.value = updatedMap
            }
        }
    }

    fun addToCart(productId: String){
        viewModelScope.launch {
            val success = cartRepo.addToCart(productId)
            if(success){
                _toastMessage.emit("Berhasil menambahkan produk ke keranjang")
            } else {
                _toastMessage.emit("Gagal menambahkan produk ke keranjang")
            }
        }
    }

    fun removeFromCart(productId: String, removeAll: Boolean = false){
        viewModelScope.launch {
            val success = cartRepo.removeFromCart(productId, removeAll)
            if(success) {
                if(removeAll)
                    _toastMessage.emit("Berhasil Menghapus Semua Produk dari Keranjang")
                else
                    _toastMessage.emit("Berhasil Mengurangi Produk dari Keranjang")
            } else {
                _toastMessage.emit("Gagal Mengurangi Produk dari Keranjang")
            }
        }
    }

    fun loadStockReqStatus(userId: String){
        viewModelScope.launch {
            val status = stockReqRepo.getStockRequestStatus(userId)
            _stockReqStatus.value = status
        }
    }

    fun sendStockRequest(userId: String){
        viewModelScope.launch {
            stockReqRepo.sendStockRequest(userId)
            loadStockReqStatus(userId)
        }
    }

    fun cancelStockRequest(userId: String){
        viewModelScope.launch {
            stockReqRepo.cancelStockRequest(userId)
            loadStockReqStatus(userId)
        }
    }
}