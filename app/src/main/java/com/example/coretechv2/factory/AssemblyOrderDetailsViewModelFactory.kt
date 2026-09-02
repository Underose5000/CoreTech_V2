package com.example.coretechv2.factory

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.coretechv2.repository.DataStoreManager
import com.example.coretechv2.viewmodel.AssemblyOrderDetailsViewModel
import com.example.coretechv2.viewmodel.SharedViewModel

/**
 * Factory responsible for creating instances of [AssemblyOrderDetailsViewModel].
 *
 * This factory provides the dependencies required by [AssemblyOrderDetailsViewModel]:
 * - [DataStoreManager] for API and data access.
 * - [SharedViewModel] for sharing application state between screens.
 *
 * A custom factory is required because [AssemblyOrderDetailsViewModel] uses constructor
 * dependencies that cannot be provided automatically by the default [ViewModelProvider].
 *
 * @property context The application context used to initialise [DataStoreManager].
 * @property sharedViewModel The shared ViewModel instance used to provide application state.
 */
class AssemblyOrderDetailsViewModelFactory(
    private val context: Context,
    val sharedViewModel: SharedViewModel
) : ViewModelProvider.Factory {

    /**
     * Creates an instance of the requested ViewModel.
     *
     * Initialises the required [DataStoreManager] and uses it to create
     * an [AssemblyOrderDetailsViewModel] with the provided [SharedViewModel].
     *
     * @param modelClass The class of the ViewModel to be created.
     * @return A new [AssemblyOrderDetailsViewModel] instance cast to the requested type.
     */
    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        val dataStoreManager =
            DataStoreManager(context.applicationContext)

        return AssemblyOrderDetailsViewModel(dataStoreManager, sharedViewModel) as T
    }
}