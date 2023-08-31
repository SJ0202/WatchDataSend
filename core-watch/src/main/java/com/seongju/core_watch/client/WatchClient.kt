package com.seongju.core_watch.client

import com.seongju.core_watch.common.result.ClientResult
import kotlinx.coroutines.flow.Flow

interface WatchClient {

    fun findNode(): Flow<ClientResult>

}