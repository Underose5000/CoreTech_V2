package com.example.coretechv2.factory

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.coretechv2.repository.DataStoreManager
import com.example.coretechv2.viewmodel.AssemblyOrdersViewModel
import com.example.coretechv2.viewmodel.SharedViewModel

/**
 * Factory responsible for creating instances of [AssemblyOrdersViewModel].
 *
 * This factory provides the dependencies required by [AssemblyOrdersViewModel]:
 * - [DataStoreManager] for API and persistent data access.
 * - [SharedViewModel] for sharing application state between screens.
 *
 * A custom factory is required because [AssemblyOrdersViewModel] uses constructor
 * dependencies that cannot be provided automatically by the default [ViewModelProvider].
 *
 * @property context The application context used to initialise [DataStoreManager].
 * @property sharedViewModel The shared ViewModel instance used to provide application state.
 */
class AssemblyOrdersViewModelFactory(
    private val context: Context,
    val sharedViewModel: SharedViewModel
) : ViewModelProvider.Factory {

    /**
     * Creates an instance of the requested ViewModel.
     *
     * Initialises the required [DataStoreManager] and uses it to create
     * an [AssemblyOrdersViewModel] with the provided [SharedViewModel].
     *
     * @param modelClass The class of the ViewModel to be created.
     * @return A new [AssemblyOrdersViewModel] instance cast to the requested type.
     */
    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        val dataStoreManager =
            DataStoreManager(context.applicationContext)

        return AssemblyOrdersViewModel(dataStoreManager, sharedViewModel) as T
    }
}