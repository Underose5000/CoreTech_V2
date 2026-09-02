package com.example.coretechv2.factory

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.coretechv2.repository.DataStoreManager
import com.example.coretechv2.viewmodel.APISettingViewModel


/**
 * Factory class used to create instances of [APISettingViewModel]
 * with required constructor dependencies.
 *
 * This factory provides:
 * - Application-level [Context]
 * - [DataStoreManager] dependency injection
 *
 * Required because [APISettingViewModel] has a non-empty constructor
 * and cannot be automatically instantiated by the default
 * [ViewModelProvider].
 *
 * @property context Application context used to initialize DataStoreManager.
 */
class APISettingViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {

    /**
     * Creates a new instance of the requested ViewModel class.
     *
     * Initializes:
     * - [DataStoreManager]
     * - [APISettingViewModel]
     *
     * @param modelClass The ViewModel class being requested.
     * @return Instance of [APISettingViewModel].
     */
    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        val dataStoreManager =
            DataStoreManager(context.applicationContext)

        return APISettingViewModel(dataStoreManager) as T
    }
}