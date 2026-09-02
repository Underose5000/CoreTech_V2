package com.example.coretechv2.factory

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.coretechv2.repository.DataStoreManager
import com.example.coretechv2.viewmodel.ItemLookUpViewModel
import com.example.coretechv2.viewmodel.SharedViewModel

/**
 * Factory responsible for creating instances of [ItemLookUpViewModel].
 *
 * This factory is required because [ItemLookUpViewModel] has dependencies that cannot be
 * provided by the default [ViewModelProvider], specifically:
 * - [DataStoreManager] for API and persistent data access.
 * - [SharedViewModel] for sharing data across multiple ViewModels.
 *
 * The [Context] is used to initialise [DataStoreManager] with the application context
 * to help prevent memory leaks.
 *
 * @property context The context used to initialise [DataStoreManager]. The application
 * context is used internally.
 * @property sharedViewModel The shared ViewModel instance used to pass data between screens.
 */

class ItemLookUpViewModelFactory(
    private val context: Context,
    val sharedViewModel: SharedViewModel
) : ViewModelProvider.Factory {

    /**
     * Creates an instance of the requested ViewModel.
     *
     * Initialises the required [DataStoreManager] and uses it to create
     * an [ItemLookUpViewModel] with the provided [SharedViewModel].
     *
     * @param modelClass The class of the ViewModel to be created.
     * @return A new [ItemLookUpViewModel] instance cast to the requested type.
     */
    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        val dataStoreManager =
            DataStoreManager(context.applicationContext)

        return ItemLookUpViewModel(dataStoreManager, sharedViewModel) as T
    }
}