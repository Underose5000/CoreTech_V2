package com.example.coretechv2.factory

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.coretechv2.MainActivity
import com.example.coretechv2.repository.DataStoreManager
import com.example.coretechv2.viewmodel.APISettingViewModel
import com.example.coretechv2.viewmodel.MainActivityViewModel

/**
 * Factory class used to create instances of [MainActivityViewModel]
 * with required constructor dependencies.
 *
 * This factory provides:
 * - Application [Context]
 * - [DataStoreManager] for persistent settings and API configuration access
 *
 * Required because [MainActivityViewModel] uses constructor
 * injection and cannot be automatically instantiated by the
 * default [ViewModelProvider].
 *
 * Commonly used by [MainActivity] during application startup
 * and navigation initialization.
 *
 * @property context Application context used to initialize DataStoreManager.
 */
class MainActivityViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory{

    /**
     * Creates a new instance of the requested ViewModel class.
     *
     * Initializes:
     * - [DataStoreManager]
     * - [MainActivityViewModel]
     *
     * @param modelClass The ViewModel class being requested.
     * @return Instance of [MainActivityViewModel].
     */
    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        val dataStoreManager =
            DataStoreManager(context.applicationContext)

        return MainActivityViewModel(dataStoreManager) as T
    }
}