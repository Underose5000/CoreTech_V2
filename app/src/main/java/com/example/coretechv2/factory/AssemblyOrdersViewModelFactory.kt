package com.example.coretechv2.factory

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.coretechv2.repository.DataStoreManager
import com.example.coretechv2.viewmodel.AssemblyOrdersViewModel
import com.example.coretechv2.viewmodel.LoginViewModel

/**
 * Factory class used to create instances of [AssemblyOrdersViewModel]
 * with required constructor dependencies.
 *
 * This factory provides:
 * - Application [Context]
 * - [DataStoreManager] for API and persistent data access
 *
 * Required because [AssemblyOrdersViewModel] uses constructor
 * injection and cannot be instantiated automatically by the
 * default [ViewModelProvider].
 *
 * @property context Application context used to initialize DataStoreManager.
 */
class AssemblyOrdersViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory{

    /**
     * Creates a new instance of the requested ViewModel class.
     *
     * Initializes:
     * - [DataStoreManager]
     * - [AssemblyOrdersViewModel]
     *
     * @param modelClass The ViewModel class being requested.
     * @return Instance of [AssemblyOrdersViewModel].
     */
    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        val dataStoreManager =
            DataStoreManager(context.applicationContext)

        return AssemblyOrdersViewModel(dataStoreManager) as T
    }
}