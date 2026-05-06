package com.example.coretechv2.factory.assemblytests

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.coretechv2.repository.DataStoreManager
import com.example.coretechv2.viewmodel.SharedViewModel
import com.example.coretechv2.viewmodel.assemblytests.ViscosityViewModel

/**
 * Factory class responsible for creating instances of [ViscosityViewModel].
 *
 * This factory is necessary because [ViscosityViewModel] requires external
 * dependencies that cannot be supplied by the default [ViewModelProvider],
 * specifically:
 * - [DataStoreManager] for handling persistent data storage
 * - [SharedViewModel] for sharing state and data across different screens
 *
 * The provided [Context] is used to initialise [DataStoreManager] using the
 * application context to prevent memory leaks.
 *
 * @property context The context used to initialise [DataStoreManager]. The
 * application context is used internally for safety.
 * @property sharedViewModel The shared ViewModel instance used for passing
 * data between different parts of the application.
 *
 * @throws IllegalArgumentException if the requested ViewModel class is not
 * [ViscosityViewModel].
 */
class ViscosityViewModelFactory(
    private val context: Context,
    val sharedViewModel: SharedViewModel
) : ViewModelProvider.Factory{

    /**
     * Creates a new instance of the requested ViewModel class.
     *
     * @param modelClass The class of the ViewModel to be created.
     * @return A new instance of [ViscosityViewModel], cast to the requested type.
     */
    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        val dataStoreManager =
            DataStoreManager(context.applicationContext)

        return ViscosityViewModel(dataStoreManager, sharedViewModel) as T
    }
}