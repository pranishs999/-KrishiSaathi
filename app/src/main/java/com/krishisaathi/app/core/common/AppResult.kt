package com.krishisaathi.app.core.common

/**
 * Lightweight result wrapper for asynchronous data and domain operations.
 */
sealed interface AppResult<out T> {
    data object Loading : AppResult<Nothing>
    data class Success<out T>(val data: T) : AppResult<T>
    data class Error(
        val message: String,
        val cause: Throwable? = null
    ) : AppResult<Nothing>
}
