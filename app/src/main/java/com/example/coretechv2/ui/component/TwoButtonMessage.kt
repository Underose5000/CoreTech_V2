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
import com.example.coretechv2.dataclasses.PopupMessageItem

@Composable
fun TwoButtonMessage(pouUpMessage : PopupMessageItem){
    PopupWindow(300,400,) {
        Column(modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center) {
            Text(pouUpMessage.message)
            Row(){
                Button(onClick = {pouUpMessage.onClickAction1()}) {
                    Text(pouUpMessage.messageButton1Text)
                }
                Button(onClick = {pouUpMessage.onClickAction2()}) {
                    Text(pouUpMessage.messageButton2Text)
            }
        }
        }
    }
}




