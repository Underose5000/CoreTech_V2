package com.example.coretechv2.factory

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.coretechv2.repository.DataStoreManager
import com.example.coretechv2.viewmodel.SharedViewModel


/**
 * Factory responsible for creating instances of [SharedViewModel].
 *
 * This factory is required because [SharedViewModel] has a constructor dependency
 * that cannot be provided automatically by the default [ViewModelProvider].
 *
 * [DataStoreManager] is provided to [SharedViewModel] to handle API and persistent
 * data access.
 *
 * @property context The application context used to initialise [DataStoreManager].
 */
class SharedViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {

    /**
     * Creates an instance of the requested ViewModel.
     *
     * Initialises the required [DataStoreManager] and uses it to create
     * a [SharedViewModel].
     *
     * @param modelClass The class of the ViewModel to be created.
     * @return A new [SharedViewModel] instance cast to the requested type.
     */
    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        val dataStoreManager =
            DataStoreManager(context.applicationContext)

        return SharedViewModel(dataStoreManager) as T
    }
}