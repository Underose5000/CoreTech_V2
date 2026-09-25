package com.example.coretechv2.factory

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.coretechv2.repository.DataStoreManager
import com.example.coretechv2.viewmodel.AssemblyOrdersRecordDetailsViewModel
import com.example.coretechv2.viewmodel.SharedViewModel

/**
 * Factory responsible for creating instances of [AssemblyOrdersRecordDetailsViewModel].
 *
 * This factory provides the dependencies required by [AssemblyOrdersRecordDetailsViewModel]:
 * - [DataStoreManager] for API and persistent data access.
 * - [SharedViewModel] for sharing application state between screens.
 *
 * A custom factory is required because [AssemblyOrdersRecordDetailsViewModel] uses constructor
 * dependencies that cannot be provided automatically by the default [ViewModelProvider].
 *
 * @property context The application context used to initialise [DataStoreManager].
 * @property sharedViewModel The shared ViewModel instance used to provide application state.
 */
class AssemblyOrdersRecordDetailsViewModelFactory(
    private val context: Context,
    val sharedViewModel: SharedViewModel
) : ViewModelProvider.Factory {

    /**
     * Creates an instance of the requested ViewModel.
     *
     * Initialises the required [DataStoreManager] and uses it to create
     * an [AssemblyOrdersRecordDetailsViewModel] with the provided [SharedViewModel].
     *
     * @param modelClass The class of the ViewModel to be created.
     * @return A new [AssemblyOrdersRecordDetailsViewModel] instance cast to the requested type.
     */
    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        val dataStoreManager =
            DataStoreManager(context.applicationContext)

        return AssemblyOrdersRecordDetailsViewModel(dataStoreManager, sharedViewModel) as T
    }
}