package com.example.subduaintermediate.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.example.subduaintermediate.call.ResultCall
import com.example.subduaintermediate.data.api.ApiService
import com.example.subduaintermediate.data.preference.UserModel
import com.example.subduaintermediate.data.preference.UserPreference
import com.example.subduaintermediate.data.response.ErrorResponse
import com.example.subduaintermediate.data.response.ListStoryItem
import com.example.subduaintermediate.data.response.LoginResponse
import com.example.subduaintermediate.data.response.RegisterResponse
import com.example.subduaintermediate.data.response.UploadResponse
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.io.File

class UserRepository private constructor(
    private val userPreference: UserPreference,
    private val apiService: ApiService
) {

    suspend fun saveSession(user: UserModel) {
        userPreference.saveSession(user)
    }

    fun signup(
        name: String,
        email: String,
        password: String
    ): LiveData<ResultCall<RegisterResponse>> = liveData {
        emit(ResultCall.Loading)
        try {
            val response = apiService.register(name, email, password)
            emit(ResultCall.Success(response))
        } catch (e: HttpException) {
            val error = e.response()?.errorBody()?.string()
            val body = Gson().fromJson(error, ErrorResponse::class.java)
            emit(ResultCall.Error(body.message))
        }
    }

    fun login(email: String, password: String): LiveData<ResultCall<LoginResponse>> = liveData {
        emit(ResultCall.Loading)
        try {
            val response = apiService.login(email, password)
            emit(ResultCall.Success(response))
        } catch (e: HttpException) {
            val error = e.response()?.errorBody()?.string()
            val body = Gson().fromJson(error, ErrorResponse::class.java)
            emit(ResultCall.Error(body.message))
        }
    }

    fun getSession(): Flow<UserModel> = userPreference.getSession()

    fun uploadImage(imageFile: File, description: String): LiveData<ResultCall<UploadResponse>> =
        liveData {
            emit(ResultCall.Loading)
            val requestBody = description.toRequestBody("text/plain".toMediaType())
            val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
            val multipartBody = MultipartBody.Part.createFormData(
                "photo",
                imageFile.name,
                requestImageFile
            )
            try {
                val successResponse = apiService.uploadImage(multipartBody, requestBody)
                emit(ResultCall.Success(successResponse))
            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                val errorResponse = Gson().fromJson(errorBody, UploadResponse::class.java)
                emit(ResultCall.Error(errorResponse.message))
            }
        }

    suspend fun logout() {
        userPreference.logout()
    }

    companion object {
        @Volatile
        private var instance: UserRepository? = null

        fun clearInstance() {
            instance = null
        }

        fun getInstance(
            apiService: ApiService,
            userPreference: UserPreference
        ): UserRepository =
            instance ?: synchronized(this) {
                instance ?: UserRepository(userPreference, apiService)
            }.also { instance = it }
    }
}