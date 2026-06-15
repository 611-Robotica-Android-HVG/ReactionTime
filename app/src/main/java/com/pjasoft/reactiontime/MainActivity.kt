package com.pjasoft.reactiontime
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothSocket
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.pjasoft.reactiontime.screens.ReactionScreen
import com.pjasoft.reactiontime.ui.theme.ReactionTimeTheme

import java.io.IOException
import java.util.UUID

class MainActivity : ComponentActivity() {
    private val myUUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
    private var btSocket : BluetoothSocket? = null

    private var listeningThread: Thread? = null


    var tiempo1 by mutableIntStateOf(0)
    var tiempo2 by mutableIntStateOf(0)
    var tiempo3 by mutableIntStateOf(0)
    var tiempofinal  by mutableIntStateOf(0)
    
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ReactionTimeTheme() {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ReactionScreen(
                        innerPadding = innerPadding,
                        onSpinClick = { valor ->
                            sendData(valor)
                        },
                        onDisconnect = {
                            sendData("0")
                            disconnectBT()
                        },
                        onConnect = {
                            connectBT("RECEPTOR")
                        },
                        tiempo1 = tiempo1,
                        tiempo2 = tiempo2,
                        tiempo3 = tiempo3,
                        tiempofinal = tiempofinal
                    )
                }
            }
        }
    }

    private fun parseSensorData(line: String) {
        try {
            val parts = line.split(",")
            for (part in parts) {
                val kv = part.split(":")
                if (kv.size == 2) {
                    when (kv[0].trim()) {
                        "T1" -> tiempo1 = kv[1].trim().toInt()
                        "T2" -> tiempo2 = kv[1].trim().toInt()
                        "T3" -> tiempo3 = kv[1].trim().toInt()
                        "TF" -> tiempofinal = kv[1].trim().toInt()
                    }
                }
            }
            Log.d("ClimaSyncBT", "T1: $tiempo1, T2: $tiempo2, T3: $tiempo3, TF: $tiempofinal")
        } catch (e: Exception) {
            Log.w("ClimaSyncBT", "Error parseando: $line", e)
        }
    }
    private fun startListening() {
        listeningThread = Thread {
            val buffer = StringBuilder()
            val inputStream = btSocket?.inputStream ?: return@Thread

            try {
                while (!Thread.currentThread().isInterrupted) {
                    val byte = inputStream.read()
                    if (byte == -1) break

                    val char = byte.toChar()
                    if (char == '\n') {
                        val line = buffer.toString().trim()
                        parseSensorData(line)
                        buffer.clear()
                    } else {
                        buffer.append(char)
                    }
                }
            } catch (_: IOException) {
            }
        }
        listeningThread?.start()
    }

    @SuppressLint("MissingPermission")
    private fun connectBT(deviceName: String): Boolean {
        return try {
            val btManager = getSystemService(BluetoothManager::class.java)
            val btAdapter: BluetoothAdapter? = btManager?.adapter

            if (btAdapter == null) {
                Toast.makeText(this, "Bluetooth no disponible", Toast.LENGTH_SHORT).show()
                return false
            }

            val device = btAdapter.bondedDevices.firstOrNull { it.name == deviceName }
            if (device == null) {
                Toast.makeText(this, "Dispositivo $deviceName no encontrado. Emparéjalo primero.", Toast.LENGTH_LONG).show()
                return false
            }

            btSocket = device.createRfcommSocketToServiceRecord(myUUID)
            btSocket?.connect()
            startListening()
            Toast.makeText(this, "Conectado a $deviceName", Toast.LENGTH_SHORT).show()
            true
        } catch (e: IOException) {
            Toast.makeText(this, "Error al conectar: ${e.message}", Toast.LENGTH_LONG).show()
            btSocket = null
            false
        }
    }
    private fun stopListening() {
        listeningThread?.interrupt()
        listeningThread = null
    }

    private fun disconnectBT() {
        stopListening()
        try {
            btSocket?.close()
        } catch (_: IOException) { }
        btSocket = null
    }

    private fun sendData(value: String) {
        try {
            btSocket?.outputStream?.write(value.toByteArray())
        } catch (e: Exception) {
            Toast.makeText(this, "Error al enviar: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ReactionTimeTheme() {
        Greeting("Android")
    }
}
