package com.kel5.ekanbeta.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kel5.ekanbeta.Repository.BannerRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class BannerViewModel(
    private val bannerRepo : BannerRepo = BannerRepo()
) : ViewModel() {

    private val _banners = MutableStateFlow<List<String>>(emptyList())
    val banners: StateFlow<List<String>> = _banners

    init{
        fetchBanners()
    }

    private fun fetchBanners(){
        viewModelScope.launch {
            _banners.value = bannerRepo.getBanners()
        }
    }
}