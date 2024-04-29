package com.dldev.tcom

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.dldev.tcom.network.ApiClient
import com.dldev.tcom.view_model.LoginViewModel
import com.dldev.tcom.view_model.LoginViewModelFactory
import com.dldev.tcom.view_model.SharedPreferencesHelper

class LoginActivity : AppCompatActivity() {
    private lateinit var viewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val factory = LoginViewModelFactory(ApiClient.userApiService, this)
        viewModel = ViewModelProvider(this, factory).get(LoginViewModel::class.java)

        checkLogin()
        setupLoginObserver()

        val loginButton: Button = findViewById(R.id.buttonLogin)
        loginButton.setOnClickListener {
            performLogin()
        }
    }

    private fun performLogin() {
        val emailEditText: EditText = findViewById(R.id.editTextEmail)
        val email = emailEditText.text.toString().trim()
        if (email.isNotEmpty()) {
            viewModel.login(email)
        } else {
            Toast.makeText(this, "Unesite svoj email!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkLogin() {
        val token = SharedPreferencesHelper.getLoginToken(this)
        if (token != null && token.isNotEmpty()) {
            navigateToMainActivity()
        }
    }

    private fun setupLoginObserver() {
        viewModel.loginStatus.observe(this) { isLoggedIn ->
            if (isLoggedIn) {
                navigateToMainActivity()
            } else {
                Toast.makeText(this, "Pogrešan email!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}
