package com.pjasoft.reactiontime.screens


import android.util.Log
import android.widget.Space
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.modifier.modifierLocalOf
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pjasoft.reactiontime.components.SensorTag
import com.pjasoft.reactiontime.ui.theme.Naranja
import com.pjasoft.reactiontime.ui.theme.Pink40
import com.pjasoft.reactiontime.ui.theme.Pink80
import com.pjasoft.reactiontime.ui.theme.Purple40
import com.pjasoft.reactiontime.ui.theme.PurpleGrey40
import com.pjasoft.reactiontime.ui.theme.ReactionTimeTheme
import com.pjasoft.reactiontime.ui.theme.RojoNEON
import com.pjasoft.reactiontime.ui.theme.SkyBlue

@Composable
fun ReactionScreen(
    innerPadding : PaddingValues,
    onSpinClick : (String) -> Unit = { },
    onDisconnect : () -> Unit = {  },
    onConnect: () -> Boolean = { false },
    tiempo1: Int = 0,
    tiempo2: Int = 0,
    tiempo3: Int = 0,
    tiempofinal: Int = 0
) {
    val colors = MaterialTheme.colorScheme
    var isOn by remember {
        mutableStateOf(false)
    }
    var isConnected by remember {
        mutableStateOf(false)
    }
    Column(
        modifier = Modifier
            .padding(innerPadding)
            .fillMaxSize()
            .background(colors.background),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            modifier = Modifier.padding(top = 30.dp),
            text = "ReactionTime",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = Purple40
        )
        Spacer( modifier = Modifier.height(40.dp))
        Text(
            text = "Medidor de Tiempo de Reacción",
            color = Naranja
        )
        Spacer(modifier = Modifier.height(20.dp))
        Column  (
            modifier = Modifier
                .size(width = 300.dp, height = 500.dp) ,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            SensorTag(
                modifier = Modifier.weight(1f),
                title = "Tiempo 1",
                value = tiempo1,
                unit = "ms",
                colorbox = colors.surface,
                colortext = colors.primary
            )
            Spacer(modifier = Modifier.height(20.dp))
            SensorTag(
                modifier = Modifier.weight(1f),
                title = "Tiempo 2",
                value = tiempo2,
                unit = "ms",
                colorbox = colors.surface,
                colortext = colors.primary

            )
            Spacer(modifier = Modifier.height(20.dp))
            SensorTag(
                modifier = Modifier.weight(1f),
                title = "Tiempo 3",
                value = tiempo3,
                unit = "ms",
                colorbox = colors.surface,
                colortext = colors.primary

            )
            Spacer(modifier = Modifier.height(20.dp))
            SensorTag(
                modifier = Modifier.weight(1f),
                title = "Tiempo Promedio",
                value = tiempofinal,
                unit = "ms",
                colorbox = Pink80,
                colortext = colors.surface
            )

        }
        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                isOn = !isOn
                val comando = if(isOn) "1" else "0"
                Log.d("BOTON_REINICIO", "Presionado. Comando generado: $comando")
                onSpinClick(comando)
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = Naranja
            )
        ) {
            Text(
                text = if(isOn) "Inicio" else "Reinicio",
                color = Color.Black
            )
        }
        Spacer(modifier = Modifier.height(5.dp))
        Button(
            onClick = {
                if(isConnected){
                    onDisconnect()
                }
                else{
                    onConnect()
                }
                isConnected = !isConnected
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = if(!isConnected) PurpleGrey40 else RojoNEON
            )
        ) {
            Text(
                text = if(isConnected) "Desconectarse" else "Conectarse",
                color = if(!isConnected) Color.Black else Color.White


            )
        }

    }
}
@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
fun ReactionPreview(){
    ReactionTimeTheme() {
        ReactionScreen(
            innerPadding = PaddingValues(0.0.dp)
        )
    }
}
