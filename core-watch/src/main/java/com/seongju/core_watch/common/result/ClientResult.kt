package com.seongju.core_watch.common.result

sealed interface ClientResult{
    object Success: ClientResult
    data class Error(val message: String): ClientResult
}