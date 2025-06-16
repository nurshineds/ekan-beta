package com.kel5.ekanbeta.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kel5.ekanbeta.Repository.StockReqRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class StockRequestViewModel(
    private val stockReqRepo: StockReqRepo = StockReqRepo()
): ViewModel() {
    private val _stockRequests = MutableStateFlow<List<Map<String, Any>>>(emptyList())
    val stockRequests : StateFlow<List<Map<String, Any>>> = _stockRequests

    fun loadStockRequests(){
        viewModelScope.launch {
            val data = stockReqRepo.getAllStockRequest()
            _stockRequests.value = data
        }
    }
    fun confirmStock(userId: String){
        viewModelScope.launch {
            stockReqRepo.confirmStockRequest(userId)
            loadStockRequests()
        }
    }
}