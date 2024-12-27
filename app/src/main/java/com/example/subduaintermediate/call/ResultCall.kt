package com.example.subduaintermediate.call

sealed class ResultCall<out R> private constructor() {
    data class Success<out T>(val data: T) : ResultCall<T>()
    data class Error(val error: String) : ResultCall<Nothing>()
    object Loading : ResultCall<Nothing>()
}
