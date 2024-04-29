package com.dldev.tcom.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import com.dldev.tcom.network.UserApiService
import android.content.Context

class LoginViewModel(private val userApi: UserApiService, private val context: Context) : ViewModel() {
    private val _loginStatus = MutableLiveData<Boolean>()
    val loginStatus: LiveData<Boolean> = _loginStatus

    fun login(email: String) {
        viewModelScope.launch {
            try {
                val response = userApi.login(mapOf("email" to email))
                val token = response.token
                if (token != null && token.isNotEmpty()) {
                    SharedPreferencesHelper.saveLoginToken(context, token)
                    _loginStatus.postValue(true)
                } else {
                    _loginStatus.postValue(false)
                }
            } catch (e: Exception) {
                _loginStatus.postValue(false)
            }
        }
    }
}


object SharedPreferencesHelper {
    fun saveLoginToken(context: Context, token: String) {
        val sharedPreferences = context.getSharedPreferences("USER_CREDENTIALS", Context.MODE_PRIVATE)
        sharedPreferences.edit().putString("token", token).apply()
    }

    fun getLoginToken(context: Context): String? =
        context.getSharedPreferences("USER_CREDENTIALS", Context.MODE_PRIVATE).getString("token", null)
}