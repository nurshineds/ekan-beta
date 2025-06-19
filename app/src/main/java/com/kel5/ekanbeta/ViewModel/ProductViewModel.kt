package com.kel5.ekanbeta.ViewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kel5.ekanbeta.Common.hitungHargaPromo
import com.kel5.ekanbeta.Data.CategoryData
import com.kel5.ekanbeta.Data.ProductData
import com.kel5.ekanbeta.Repository.ProductRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class ProductViewModel(
    private val productRepo: ProductRepo = ProductRepo()
) : ViewModel() {

    private val _productList = MutableStateFlow<List<ProductData>>(emptyList())
    val productList : StateFlow<List<ProductData>> = _productList

    private val _selectedProduct = MutableStateFlow<ProductData?>(null)
    val selectedProduct : StateFlow<ProductData?> = _selectedProduct

    private val _categoryList = MutableStateFlow<List<CategoryData>>(emptyList())
    val categoryList: StateFlow<List<CategoryData>> = _categoryList

    init{
        getProducts()
        getCategories()
    }

    private fun getProducts(){
        viewModelScope.launch {
            val allProducts = productRepo.getProducts()
            _productList.value = allProducts
        }
    }

    fun getProductById(productId: String) {
        viewModelScope.launch {
            val product = productRepo.getProductById(productId)
            _selectedProduct.value = product
        }
    }

    fun getCategories(){
        viewModelScope.launch {
            productRepo.getCategories()
                .catch { e -> e.printStackTrace() }
                .collect { categories ->
                    _categoryList.value = categories
                }
        }
    }
}