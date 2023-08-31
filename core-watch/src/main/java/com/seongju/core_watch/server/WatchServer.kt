package com.seongju.core_watch.server

import kotlinx.coroutines.flow.Flow

interface WatchServer {

    fun getData(): Flow<Int>

}