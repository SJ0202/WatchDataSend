package com.seongju.watchdatasend

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.seongju.core_watch.client.WatchClientImpl
import com.seongju.core_watch.common.result.ClientResult
import com.seongju.watchdatasend.ui.theme.WatchDataSendTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val watchClient = WatchClientImpl(context = application)

        val numberData = mutableStateOf(0)

        watchClient.findNode()
            .onEach {
                when(it) {
                    is ClientResult.Error -> {
                        Log.d("Activity", "Not find")
                    }
                    ClientResult.Success -> {
                        Log.d("Activity", "find")
                    }
                }
            }.launchIn(scope = serviceScope)

        setContent {
            WatchDataSendTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        TextField(
                            value = numberData.value.toString(),
                            onValueChange = {
                                numberData.value = it.toInt()
                            }
                        )
                        Button(
                            onClick = {
                                watchClient.sendData(numberData.value)
                                    .onEach { clientResult ->
                                        when(clientResult) {
                                            is ClientResult.Error -> Unit
                                            ClientResult.Success -> Unit
                                        }
                                    }.launchIn(serviceScope)
                            }
                        ) {
                            Text(text = "Send Data")
                        }
                    }
                }
            }
        }


    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }
}
