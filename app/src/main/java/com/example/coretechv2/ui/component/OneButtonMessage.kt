package com.example.coretechv2.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun OneButtonMessage(message : String, buttonText : String, onClickaction : () -> Unit){
    PopupWindow(300,400,) {
        Column(modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center) {
            Text(message)
            Button(onClick = {onClickaction()}) {
                Text(buttonText)
            }
        }
    }
}



