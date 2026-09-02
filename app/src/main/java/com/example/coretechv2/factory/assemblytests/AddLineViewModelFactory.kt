package com.example.coretechv2.factory.assemblytests

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.coretechv2.repository.DataStoreManager
import com.example.coretechv2.viewmodel.SharedViewModel
import com.example.coretechv2.viewmodel.assemblytests.AddLineViewModel


/**
 * Factory responsible for creating instances of [AddLineViewModel].
 *
 * This factory is required because [AddLineViewModel] has dependencies that cannot be
 * provided by the default [ViewModelProvider], specifically:
 * - [DataStoreManager] for persistent data storage.
 * - [SharedViewModel] for sharing data across multiple ViewModels.
 *
 * The [Context] is used to initialise [DataStoreManager] with the application context
 * to help prevent memory leaks.
 *
 * @property context The context used to initialise [DataStoreManager]. The application
 * context is used internally.
 * @property sharedViewModel The shared ViewModel instance used to pass data between screens.
 */

class AddLineViewModelFactory(
    private val context: Context,
    val sharedViewModel: SharedViewModel
) : ViewModelProvider.Factory {

    /**
     * Creates an instance of the requested ViewModel.
     *
     * @param modelClass The class of the ViewModel to be created.
     * @return A new [AddLineViewModel] instance cast to the requested type.
     */
    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        val dataStoreManager =
            DataStoreManager(context.applicationContext)

        return AddLineViewModel(dataStoreManager, sharedViewModel) as T
    }
}