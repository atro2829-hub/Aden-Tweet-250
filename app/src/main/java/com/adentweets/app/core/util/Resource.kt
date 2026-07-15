package com.adentweets.app.core.util

sealed class Resource<out T> {
    data class Success<T>(val data: T) : Resource<T>()
    data class Error(val message: String, val code: Int? = null) : Resource<Nothing>()
    object Loading : Resource<Nothing>()
    object Idle : Resource<Nothing>()
}

fun <T> Resource<T>.isLoading() = this is Resource.Loading
fun <T> Resource<T>.isSuccess() = this is Resource.Success
fun <T> Resource<T>.isError() = this is Resource.Error
fun <T> Resource<T>.getDataOrNull(): T? = (this as? Resource.Success)?.data
fun <T> Resource<T>.getErrorMessageOrNull(): String? = (this as? Resource.Error)?.message