package com.example.subduaintermediate.view.login

import androidx.lifecycle.ViewModel
import com.example.subduaintermediate.data.preference.UserModel
import com.example.subduaintermediate.repository.UserRepository

class LoginViewModel(private val repository: UserRepository) : ViewModel() {

    fun login(email: String, password: String) = repository.login(email, password)

    suspend fun saveSession(userModel: UserModel) {
        repository.saveSession(userModel)
    }
}