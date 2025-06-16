package com.kel5.ekanbeta.ViewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kel5.ekanbeta.Data.OrderData
import com.kel5.ekanbeta.Data.ProductData
import com.kel5.ekanbeta.Data.ReviewData
import com.kel5.ekanbeta.Repository.ReviewRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ReviewViewModel : ViewModel() {
    private val reviewRepo: ReviewRepo = ReviewRepo()

    private val _reviews = MutableLiveData<List<ReviewData>>()
    val reviews: LiveData<List<ReviewData>> = _reviews

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _products = MutableStateFlow<List<ProductData>>(emptyList())
    val products: StateFlow<List<ProductData>> = _products

    fun loadReviews(productId: String) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val result = reviewRepo.getReviews(productId)
                _reviews.value = result
            } catch (e: Exception) {
                Log.e("ReviewViewModel", "Error getting reviews", e)
                _reviews.value = emptyList()
            } finally {
                _loading.value = false
            }
        }
    }

    suspend fun loadProductsForOrder(uid: String, orderId: String): OrderData {
        val order = reviewRepo.getOrderById(uid, orderId)
        val productIds = order.items.keys.toList()
        val productList = reviewRepo.getProductsByIds(productIds)
        _products.value = productList
        return order
    }

    fun submitAllReview(
        ratings: Map<String, Float>,
        comments: Map<String, String>,
        products: List<ProductData>,
        orderId: String,
        onSuccess: () -> Unit,
        onError: () -> Unit
    ){
        viewModelScope.launch {
            try{
                val uid = reviewRepo.auth.currentUser?.uid ?: return@launch
                for(product in products){
                    val rating = ratings[product.id] ?: 0f
                    val comment = comments[product.id] ?: ""
                    reviewRepo.submitReview(product.id, rating, comment, orderId)

                }
                onSuccess()
            }catch (e: Exception){
                onError()
            }
        }
    }
}