package com.example.coretechv2.factory

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.coretechv2.repository.DataStoreManager
import com.example.coretechv2.viewmodel.LoginViewModel

/**
 * Factory class used to create instances of [LoginViewModel]
 * with required constructor dependencies.
 *
 * This factory provides:
 * - Application [Context]
 * - [DataStoreManager] for persistent storage and API access
 *
 * Required because [LoginViewModel] has constructor dependencies
 * and cannot be automatically instantiated by the default
 * [ViewModelProvider].
 *
 * @property context Application context used to initialize DataStoreManager.
 */
class LoginViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {

    /**
     * Creates a new instance of the requested ViewModel class.
     *
     * Initializes:
     * - [DataStoreManager]
     * - [LoginViewModel]
     *
     * @param modelClass The ViewModel class being requested.
     * @return Instance of [LoginViewModel].
     */
    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        val dataStoreManager =
            DataStoreManager(context.applicationContext)

        return LoginViewModel(dataStoreManager) as T
    }
}