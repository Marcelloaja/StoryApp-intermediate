package com.example.subduaintermediate.view.upload

import androidx.lifecycle.ViewModel
import com.example.subduaintermediate.repository.UserRepository
import java.io.File

class UploadViewModel(private val repository: UserRepository) : ViewModel() {
    fun uploadImage(file: File, description: String) = repository.uploadImage(file, description)
}