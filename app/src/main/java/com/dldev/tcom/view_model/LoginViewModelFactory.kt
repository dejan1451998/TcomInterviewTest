package com.dldev.tcom.view_model

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dldev.tcom.network.UserApiService

class LoginViewModelFactory(
    private val userApiService: UserApiService,
    private val context: Context
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LoginViewModel(userApiService, context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}