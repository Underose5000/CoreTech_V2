package com.example.coretechv2.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coretechv2.dataclasses.APICallTables
import com.example.coretechv2.repository.APICall
import com.example.coretechv2.repository.DataStoreManager
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class AssemblyOrdersViewModel(private val dataStoreManager: DataStoreManager) : ViewModel() {

    private val apiCall = APICall(dataStoreManager)
    private val _snackbarEvent = MutableSharedFlow<String>()
    val snackbarEvent = _snackbarEvent.asSharedFlow()

    var AssemblyOrdersList = mutableStateListOf<APICallTables.AssemblyHeader>()
        private set

    var AssemblyOrderListSearached = mutableStateListOf<APICallTables.AssemblyHeader>()
        private set

    var selectedOrder by mutableStateOf<APICallTables.AssemblyHeader?>(null)
        private set
    var Searchfield by mutableStateOf("")
        private set


    fun onSearchFieldChange(newValue: String){
        Searchfield = newValue
        AssemblyOrderListSearached.clear()
        if (newValue.isBlank()){
            AssemblyOrderListSearached.addAll(AssemblyOrdersList)
        } else {
            AssemblyOrderListSearached.addAll(
                AssemblyOrdersList.filter {
                    it.ITEMCODE.contains(newValue, ignoreCase = true) ||
                    it.ITEMDESCRIPTION.contains(newValue, ignoreCase = true)  ||
                    it.ORDERNUMBER.contains(newValue, ignoreCase = true)})
        }
    }

    fun getSelectedOrder(order: APICallTables.AssemblyHeader){
        selectedOrder = order
    }

    fun retrieveAssemblyOrders(){
        viewModelScope.launch {
            val assemblyOrdersListCall : List<APICallTables.AssemblyHeader>? = apiCall.query("SELECT * FROM AssemblyHeader where OrderStatus = 'Open' order by OrderNumber")

            AssemblyOrdersList.clear()
            AssemblyOrderListSearached.clear()
            assemblyOrdersListCall?.let {
                AssemblyOrdersList.addAll(it)
                AssemblyOrderListSearached.addAll(it)
            }
        }
    }
}