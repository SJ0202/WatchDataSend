package com.seongju.core_watch.server

import android.content.Context
import android.util.Log
import com.google.android.gms.wearable.MessageClient
import com.google.android.gms.wearable.Wearable
import com.seongju.core_watch.util.byteArrayToInt
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch

class WatchServerImpl(
    private val context: Context
): WatchServer {

    private val messageClient by lazy {
        Wearable.getMessageClient(context)
    }
    override fun getData(): Flow<Int> {
        return callbackFlow {
            val messageReceiver = MessageClient.OnMessageReceivedListener { messageEvent ->
                if (messageEvent.path == DATA_PATH) {

                    val getData = byteArrayToInt(messageEvent.data)

                    Log.d(TAG, "getData: $messageEvent, value: $getData")

                    launch { send(getData) }
                }
            }

            messageClient.addListener(messageReceiver)

            awaitClose{
                messageClient.removeListener(messageReceiver)
            }
        }
    }

    companion object {
        private const val TAG = "WatchServerImpl"

        private const val WEAR_CAPABILITY = "sj0202_wear"
        private const val DATA_PATH = "/data"
    }
}