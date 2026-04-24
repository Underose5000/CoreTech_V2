package com.example.coretechv2.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun TwoButtonMessage(message : String, buttonText1 : String, buttonText2 : String, onClickAction1 : () -> Unit, onClickAction2 : () -> Unit){
    PopupWindow(300,400,) {
        Column(modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center) {
            Text(message)
            Row(){
                Button(onClick = {onClickAction1()}) {
                    Text(buttonText1)
                }
                Button(onClick = {onClickAction2()}) {
                    Text(buttonText2)
            }
        }
        }
    }
}




