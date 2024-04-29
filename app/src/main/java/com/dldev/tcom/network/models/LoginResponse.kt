package com.dldev.tcom.network.models

data class LoginResponse(
    val user: User,
    val token: String
)
