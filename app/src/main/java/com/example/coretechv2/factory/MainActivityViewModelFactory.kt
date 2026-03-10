package com.example.coretechv2.factory

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.coretechv2.MainActivity
import com.example.coretechv2.repository.DataStoreManager
import com.example.coretechv2.viewmodel.APISettingViewModel
import com.example.coretechv2.viewmodel.MainActivityViewModel

class MainActivityViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory{

    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        val dataStoreManager =
            DataStoreManager(context.applicationContext)

        return MainActivityViewModel(dataStoreManager) as T
    }
}