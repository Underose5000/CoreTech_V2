package com.example.coretechv2.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.coretechv2.dataclasses.MessageItems
import com.example.coretechv2.dataclasses.PopupItems
import com.example.coretechv2.ui.theme.lightBlue

@Composable
fun ButtonMessage(popUpMessage: MessageItems) {
    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .width(popUpMessage.width.dp)
                .height(popUpMessage.height.dp)
                .background(lightBlue)
                .border(1.dp, Color.Black)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        popUpMessage.message,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }
                Spacer(modifier = Modifier.height(20.dp))
                if (popUpMessage.onClickAction2 == {} && popUpMessage.messageButton2Text == "") {
                    Row() {
                        Button(onClick = { popUpMessage.onClickAction1() }) {
                            Text(popUpMessage.messageButton1Text)
                        }
                    }
                } else {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Spacer(modifier = Modifier.weight(1f))
                        Column(
                            modifier = Modifier.weight(3f),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Button(onClick = { popUpMessage.onClickAction1() }) {
                                Text(popUpMessage.messageButton1Text)
                            }
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        Column(
                            modifier = Modifier.weight(3f),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Button(onClick = { popUpMessage.onClickAction2() }) {
                                Text(popUpMessage.messageButton2Text)
                            }
                        }
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}





