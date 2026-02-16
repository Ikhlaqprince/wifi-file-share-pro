package com.wifisharepro

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.wifisharepro.server.ServerService
import com.wifisharepro.utils.NetworkUtils

class MainActivity : ComponentActivity() {

    private var isRunning = false
    private val port = 8080

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    Column(modifier = Modifier.padding(16.dp)) {

                        val ip = NetworkUtils.getLocalIpAddress()

                        Text(text = "WiFi File Share Pro", style = MaterialTheme.typography.headlineMedium)

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(text = "IP: ${ip ?: "Not Connected"}")
                        Text(text = "Port: $port")

                        Spacer(modifier = Modifier.height(20.dp))

                        Button(
                            onClick = {
                                if (isRunning) stopServer() else startServer()
                                isRunning = !isRunning
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(if (isRunning) "Stop Server" else "Start Server")
                        }

                        if (isRunning && ip != null) {
                            Spacer(modifier = Modifier.height(20.dp))
                            Text("Open in browser:")
                            Text("http://$ip:$port")
                        }
                    }
                }
            }
        }
    }

    private fun startServer() {
        val intent = Intent(this, ServerService::class.java)
        intent.putExtra("port", port)
        ContextCompat.startForegroundService(this, intent)
    }

    private fun stopServer() {
        stopService(Intent(this, ServerService::class.java))
    }
}
