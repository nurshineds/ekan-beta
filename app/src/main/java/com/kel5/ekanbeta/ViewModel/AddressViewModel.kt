package com.kel5.ekanbeta.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kel5.ekanbeta.Data.AddressData
import com.kel5.ekanbeta.Repository.AddressRepo
import com.kel5.ekanbeta.Repository.AuthRepo

class AddressViewModel : ViewModel(){
    private val addressRepo = AddressRepo()
    private val authRepo = AuthRepo()

    private val _error = MutableLiveData<String>()
    val error : LiveData<String> get() = _error

    private val _addresses = MutableLiveData<List<AddressData>>()
    val addresses : LiveData<List<AddressData>> = _addresses

    private val _selectedAddress = MutableLiveData<AddressData?>()
    val selectedAddress : LiveData<AddressData?> = _selectedAddress

    val uid = authRepo.generateUserId()

    fun fetchAddresses() {
        if (uid != null) {
            addressRepo.getUserAddresses(
                uid,
                onComplete = { result ->
                    _addresses.value = result

                    if (_selectedAddress.value == null && result.isNotEmpty()) {
                        _selectedAddress.value = result.first()
                    }
                },
                onError = { e ->
                    _error.value = e.message
                }
            )
        } else {
            _error.value = "User belum login"
        }
    }


    fun addAddress(
        address: AddressData,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ){
        if(uid != null){
            addressRepo.addUserAddress(
                uid,
                address,
                onSuccess ={
                    fetchAddresses()
                    onSuccess()
                },
                onError = { e->
                    onError(e.message ?: "Gagal menambahkan alamat")
                }
            )
        } else {
            onError("User belum login")
        }
    }

    fun selectAddress(address: AddressData){
        _selectedAddress.value = address
    }
}