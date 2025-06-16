package com.kel5.ekanbeta.ViewModel

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.kel5.ekanbeta.Common.hitungHargaPromo
import com.kel5.ekanbeta.Data.AddressData
import com.kel5.ekanbeta.Data.OrderData
import com.kel5.ekanbeta.Data.ProductData
import com.kel5.ekanbeta.Data.UserData
import com.kel5.ekanbeta.Repository.AuthRepo
import com.kel5.ekanbeta.Repository.OrderHistoryRepo
import com.kel5.ekanbeta.Repository.ProductRepo
import com.kel5.ekanbeta.Repository.StockReqRepo
import kotlinx.coroutines.launch
import kotlin.text.toDouble
import kotlin.text.toFloatOrNull

class OrderViewModel(

    private val userRepo: AuthRepo = AuthRepo(),
    private val productRepo: ProductRepo = ProductRepo(),
    private val historyRepo: OrderHistoryRepo = OrderHistoryRepo(),
    ) : ViewModel() {

        private val stockReqRepo: StockReqRepo = StockReqRepo()
    val user = mutableStateOf(UserData())
    val productList = mutableStateListOf<ProductData>()
    val total = mutableFloatStateOf(0f)
    val ongkir = mutableFloatStateOf(0f)
    val totalFinal = mutableFloatStateOf(0f)

    val diskonMember = mutableFloatStateOf(0f)
    val diskonOngkir = mutableFloatStateOf(0f)
    val totalSetelahDiskon = mutableFloatStateOf(0f)

    private val _orders = MutableLiveData<List<OrderData>>()
    val orders: LiveData<List<OrderData>> = _orders

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    val currentUid: String?
        get() = FirebaseAuth.getInstance().currentUser?.uid

    fun setUser(uid: String) {
        historyRepo.setUserId(uid)
        loadOrders(uid)
    }

    fun loadOrders(uid: String) {
        historyRepo.getAllOrders(
            onResult = { list ->
                _orders.postValue(list)
            },
            onError = { e ->
                _error.postValue(e.message ?: "Unknown error")
            }
        )
    }

    private val _selectedOrder = MutableLiveData<OrderData?>()
    val selectedOrder: LiveData<OrderData?> = _selectedOrder

    fun loadOrderById(orderId: String) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return

        val userOrderRef = FirebaseFirestore.getInstance()
            .collection("users")
            .document(uid)
            .collection("orders")
            .document(orderId)

        userOrderRef.get()
            .addOnSuccessListener { doc ->
                val order = doc.toObject(OrderData::class.java)
                _selectedOrder.postValue(order)
            }
            .addOnFailureListener {
                _error.postValue("Gagal memuat pesanan: ${it.message}")
            }
    }

    fun updateOrderStatus(
        orderId: String,
        newStatus: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val uid = currentUid
        Log.d("ViewModel", "updateOrderStatus called with orderId=$orderId, uid=$uid")
        if (uid == null) {
            Log.e("ViewModel", "UID is null")
            onFailure(IllegalStateException("UID is null"))
            return
        }

        historyRepo.updateOrderStatus(uid, orderId, newStatus, {
            Log.d("ViewModel", "updateOrderStatus success callback")
            loadOrders(uid)
            onSuccess()
        }, { error ->
            Log.e("ViewModel", "updateOrderStatus failure callback", error)
            onFailure(error)
        })
    }

    init {
        fetchCheckoutData()
    }

    private fun calculate(){
        total.value = productList.sumOf {
            val qty = user.value.cartItems[it.id] ?: 0
            val hargaFinal = if (it.diskon > 0) hitungHargaPromo(it.harga, it.diskon) else it.harga
            hargaFinal * qty
        }.toFloat()

        ongkir.value = total.value * 0.1f
        totalFinal.value = total.value + ongkir.value
    }

    fun fetchCheckoutData() {
        viewModelScope.launch {
            val currentUser = userRepo.getCurrentUser()
            if (currentUser != null) {
                user.value = currentUser
                val products = productRepo.getProductByIds(currentUser.cartItems.keys.toList())
                productList.clear()
                productList.addAll(products)
                calculate()
            }
        }
    }

    fun placeOrder(context: Context, selectedAddress: AddressData?) {
        val userData = user.value
        val cartItems = userData.cartItems

        if (cartItems.isEmpty()) {
            Toast.makeText(context, "Keranjang kosong!", Toast.LENGTH_SHORT).show()
            return
        }

        if (selectedAddress == null) {
            Toast.makeText(context, "Pilih alamat pengiriman terlebih dahulu!", Toast.LENGTH_SHORT).show()
            return
        }

        val orderId = Firebase.firestore.collection("orders").document().id

        val orderModel = OrderData(
            id = orderId,
            address = selectedAddress,
            status = "belum bayar",
            items = cartItems,
            total = totalSetelahDiskon.value
        )

        val firestore = Firebase.firestore
        val globalOrderRef = firestore.collection("orders").document(orderId)
        val userOrderRef = firestore.collection("users")
            .document(userData.uid)
            .collection("orders")
            .document(orderId)

        firestore.runBatch { batch ->
            batch.set(globalOrderRef, orderModel)
            batch.set(userOrderRef, orderModel)
            batch.update(firestore.collection("users").document(userData.uid), "cartItems", emptyMap<String, Int>())
        }.addOnSuccessListener {
            viewModelScope.launch {
                try {
                    stockReqRepo.cancelStockRequest(userData.uid)
                    Log.d("OrderViewModel", "Request stok dihapus setelah order.")
                } catch (e: Exception) {
                    Log.e("OrderViewModel", "Gagal hapus request stok: ${e.message}")
                }
            }

            user.value = user.value.copy(cartItems = emptyMap())
            productList.clear()
            calculate()
            Toast.makeText(context, "Pesanan berhasil dibuat!", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Toast.makeText(context, "Gagal membuat pesanan!", Toast.LENGTH_SHORT).show()
        }
    }
}