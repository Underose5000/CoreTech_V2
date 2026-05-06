package com.example.coretechv2.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coretechv2.dataclasses.APICallTables
import com.example.coretechv2.repository.APICall
import com.example.coretechv2.repository.DataStoreManager
import com.example.coretechv2.repository.HashPassword
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlin.properties.ReadWriteProperty

class AssemblyOrdersViewModel(private val dataStoreManager: DataStoreManager) : ViewModel() {

    private val apiCall = APICall(dataStoreManager)
    private val _snackbarEvent = MutableSharedFlow<String>()
    val snackbarEvent = _snackbarEvent.asSharedFlow()

    var AssemblyOrdersList by mutableStateOf<List<APICallTables.AssemblyHeader>>(emptyList())
        private set

    var AssemblyOrderListSearached by mutableStateOf<List<APICallTables.AssemblyHeader>>(emptyList())
        private set

    var selectedOrder by mutableStateOf<APICallTables.AssemblyHeader?>(null)
        private set
    var Searchfield by mutableStateOf("")
        private set


    fun onSerachfieldChange(newValue: String){
        Searchfield = newValue
        AssemblyOrderListSearached = AssemblyOrdersList.filter{ order ->
            order.ITEMCODE.contains(Searchfield, ignoreCase = true) ||
            order.ITEMDESCRIPTION.contains(Searchfield, ignoreCase = true)  ||
            order.ORDERNUMBER.contains(Searchfield, ignoreCase = true)
        }
    }

    fun getSelectedOrder(order: APICallTables.AssemblyHeader){
        selectedOrder = order
    }

    fun retrieveAssemblyOrders(){
        viewModelScope.launch {
            val AssemblyOrdersListCall : List<APICallTables.AssemblyHeader>? = apiCall.query("SELECT * FROM AssemblyHeader where OrderStatus = 'Open' order by OrderNumber")

            AssemblyOrdersList = AssemblyOrdersListCall ?: emptyList()
            AssemblyOrderListSearached = AssemblyOrdersList
        }
    }
}