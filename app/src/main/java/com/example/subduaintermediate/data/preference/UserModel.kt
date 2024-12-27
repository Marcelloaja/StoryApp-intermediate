package com.example.subduaintermediate.data.preference

data class UserModel(
    val password: String,
    val email: String,
    val token: String,
    val isLogin: Boolean = false
)
