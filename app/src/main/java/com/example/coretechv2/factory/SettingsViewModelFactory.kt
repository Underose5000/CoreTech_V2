package com.example.coretechv2.factory

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.coretechv2.repository.DataStoreManager
import com.example.coretechv2.viewmodel.SettingsViewModel
import com.example.coretechv2.viewmodel.SharedViewModel

/**
 * Factory responsible for creating instances of [SettingsViewModel].
 *
 * This factory provides the dependencies required by [SettingsViewModel]:
 * - [DataStoreManager] for API and persistent data access.
 * - [SharedViewModel] for the current user and snackbar messages.
 *
 * A custom factory is required because [SettingsViewModel] uses constructor
 * dependencies that cannot be provided automatically by the default [ViewModelProvider].
 *
 * @property context The application context used to initialise [DataStoreManager].
 * @property sharedViewModel The shared ViewModel instance used to provide application state.
 */
class SettingsViewModelFactory(
    private val context: Context,
    val sharedViewModel: SharedViewModel
) : ViewModelProvider.Factory {

    /**
     * Creates an instance of the requested ViewModel.
     *
     * @param modelClass The class of the ViewModel to be created.
     * @return A new [SettingsViewModel] instance cast to the requested type.
     */
    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        val dataStoreManager =
            DataStoreManager(context.applicationContext)

        return SettingsViewModel(dataStoreManager, sharedViewModel) as T
    }
}
