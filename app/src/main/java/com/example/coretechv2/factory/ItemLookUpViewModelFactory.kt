package com.example.coretechv2.factory

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.coretechv2.repository.DataStoreManager
import com.example.coretechv2.viewmodel.ItemLookUpViewModel
import com.example.coretechv2.viewmodel.SharedViewModel
import com.example.coretechv2.viewmodel.assemblytests.GelTimeViewModel

/**
 * Factory class responsible for creating instances of [ItemLookUpViewModel].
 *
 * This factory is required because [ItemLookUpViewModel] has dependencies that cannot be
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
 * @throws IllegalArgumentException if the requested ViewModel class is not [ItemLookUpViewModel].
 */

class ItemLookUpViewModelFactory(
    private val context: Context,
    val sharedViewModel: SharedViewModel
) : ViewModelProvider.Factory{

    /**
     * Creates a new instance of the requested ViewModel class.
     *
     * @param modelClass The class of the ViewModel to be created.
     * @return A new instance of [ItemLookUpViewModel] cast to the requested type.
     */
    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        val dataStoreManager =
            DataStoreManager(context.applicationContext)

        return ItemLookUpViewModel(dataStoreManager, sharedViewModel) as T
    }
}