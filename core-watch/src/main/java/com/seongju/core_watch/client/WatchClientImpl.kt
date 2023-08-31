package com.seongju.core_watch.client

import android.content.Context
import android.net.Uri
import android.util.Log
import com.google.android.gms.tasks.Tasks
import com.google.android.gms.wearable.CapabilityClient
import com.google.android.gms.wearable.CapabilityInfo
import com.google.android.gms.wearable.Node
import com.google.android.gms.wearable.Wearable
import com.seongju.core_watch.common.result.ClientResult
import com.seongju.core_watch.util.intToByteArray
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

class WatchClientImpl(
    private val context: Context
): WatchClient {

    // Data Client - Data size >= 10KB, Network
    // Message Client - Data size < 10KB, Bluetooth
    // Channel Client - Data size < 10KB, Bluetooth
    private val messageClient by lazy {
        Wearable.getMessageClient(context)
    }
    private val capabilityClient by lazy {
        Wearable.getCapabilityClient(context)
    }

    private var transcriptionNodeId: String? = null
    override fun findNode(): Flow<ClientResult> {
        return callbackFlow {

            val capabilityCallback = CapabilityClient.OnCapabilityChangedListener { capabilityInfo ->
                Log.d(TAG, "Capability changed: $capabilityInfo")
            }

            capabilityClient.addListener(
                capabilityCallback,
                Uri.parse("wear://"),
                CapabilityClient.FILTER_REACHABLE
            )

            val capabilityInfo: CapabilityInfo = Tasks.await(
                capabilityClient.getCapability(WEAR_CAPABILITY, CapabilityClient.FILTER_REACHABLE)
            )

            // capabilityInfo has the reachable nodes with the transcription capability
            val nodeCheck = updateTranscriptionCapability(capabilityInfo)

            if (nodeCheck) {
                launch { send(ClientResult.Success) }
            } else {
                launch { send(ClientResult.Error("Fail node find")) }
            }

            awaitClose {
                capabilityClient.removeListener(capabilityCallback)
            }
        }
    }

    override fun sendData(data: Int): Flow<ClientResult> {
        return flow {
            val byteArray = intToByteArray(data)

            if (transcriptionNodeId != null) {
                Log.d(TAG, "send data")

                messageClient.sendMessage(
                    transcriptionNodeId!!,
                    DATA_PATH,
                    byteArray
                ).apply {
                    addOnSuccessListener {
                        Log.d(TAG, "Send Success")
                    }
                    addOnFailureListener {
                        Log.d(TAG, "Send Fail")
                    }
                }
            } else {
                Log.d(TAG, "Send Fail. transcriptionNodeId null")
            }
        }
    }

    private fun updateTranscriptionCapability(capabilityInfo: CapabilityInfo): Boolean {
        Log.d(TAG, "find nodes: ${capabilityInfo.nodes}")
        transcriptionNodeId = pickBestNodeId(capabilityInfo.nodes)
        return !transcriptionNodeId.isNullOrBlank()
    }

    private fun pickBestNodeId(nodes: Set<Node>): String? {
        // Find a nearby node or pick one arbitrarily
        return nodes.firstOrNull { it.isNearby }?.id ?: nodes.firstOrNull()?.id
    }
    companion object {
        private const val TAG = "WatchClientImpl"

        private const val WEAR_CAPABILITY = "sj0202_wear"
        private const val DATA_PATH = "/data"
    }
}