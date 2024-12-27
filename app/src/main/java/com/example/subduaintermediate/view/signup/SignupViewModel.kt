package com.example.subduaintermediate.view.signup

import androidx.lifecycle.ViewModel
import com.example.subduaintermediate.repository.UserRepository

class SignupViewModel(private val repository: UserRepository) : ViewModel() {
    fun register(name: String, email: String, password: String) =
        repository.signup(name, email, password)
}