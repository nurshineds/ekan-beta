package com.kel5.ekanbeta.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
            _productList.value = productRepo.getProducts()
        }
    }

    fun getProductById(productId: String) {
        viewModelScope.launch {
            val product = productRepo.getProductById(productId)
            _selectedProduct.value = product
        }
    }

    private fun getCategories(){
        viewModelScope.launch {
            _categoryList.value = productRepo.getCategories()
        }
    }
}