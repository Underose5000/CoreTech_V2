package com.example.coretechv2.factory.assemblytests

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.coretechv2.repository.DataStoreManager
import com.example.coretechv2.viewmodel.SharedViewModel
import com.example.coretechv2.viewmodel.assemblytests.GelTimeViewModel

/**
 * Factory class responsible for creating instances of [GelTimeViewModel].
 *
 * This factory is required because [GelTimeViewModel] has dependencies that cannot be
 * provided by the default [ViewModelProvider], specifically:
 * - [DataStoreManager] for persistent data storage
 * - [SharedViewModel] for sharing data across multiple ViewModels
 *
 * The [Context] is used to initialise the [DataStoreManager] using the application context
 * to avoid memory leaks.
 *
 * @property context The context used to initialise [DataStoreManager]. The application
 * context is used internally for safety.
 * @property sharedViewModel The shared ViewModel instance used to pass data between screens.
 *
 * @throws IllegalArgumentException if the requested ViewModel class is not [GelTimeViewModel].
 */

class GelTimeViewModelFactory(
    private val context: Context,
    val sharedViewModel: SharedViewModel
) : ViewModelProvider.Factory {

    /**
     * Creates a new instance of the requested ViewModel class.
     *
     * @param modelClass The class of the ViewModel to be created.
     * @return A new instance of [GelTimeViewModel] cast to the requested type.
     */
    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        val dataStoreManager =
            DataStoreManager(context.applicationContext)

        return GelTimeViewModel(dataStoreManager, sharedViewModel) as T
    }
}