package com.example.coretechv2.factory

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.coretechv2.repository.DataStoreManager
import com.example.coretechv2.viewmodel.AssemblyOrderDetailsViewModel
import com.example.coretechv2.viewmodel.AssemblyOrdersViewModel
import com.example.coretechv2.viewmodel.LoginViewModel
import com.example.coretechv2.viewmodel.SharedViewModel

/**
 * Factory class used to create instances of [AssemblyOrderDetailsViewModel]
 * with required constructor dependencies.
 *
 * This factory supplies:
 * - Application [Context]
 * - [DataStoreManager] for API/data access
 * - Shared application state via [SharedViewModel]
 *
 * Required because [AssemblyOrderDetailsViewModel] uses constructor
 * injection and cannot be created automatically by the default
 * [ViewModelProvider].
 *
 * @property context Application context used to initialize DataStoreManager.
 * @property sharedViewModel Shared ViewModel containing global app state.
 */
class AssemblyOrderDetailsViewModelFactory(
    private val context: Context,
    val sharedViewModel: SharedViewModel
) : ViewModelProvider.Factory{

    /**
     * Creates a new instance of the requested ViewModel class.
     *
     * Initializes:
     * - [DataStoreManager]
     * - [AssemblyOrderDetailsViewModel]
     *
     * @param modelClass The ViewModel class being requested.
     * @return Instance of [AssemblyOrderDetailsViewModel].
     */
    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        val dataStoreManager =
            DataStoreManager(context.applicationContext)

        return AssemblyOrderDetailsViewModel(dataStoreManager,sharedViewModel) as T
    }
}