package com.example.coretechv2.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.coretechv2.R
import com.example.coretechv2.factory.APISettingViewModelFactory
import com.example.coretechv2.viewmodel.APISettingViewModel

@Composable
fun APISettingsScreen(
    onConnectedSuccess: () -> Unit
) {
    val context = LocalContext.current
    val viewModel: APISettingViewModel = viewModel(
        factory = APISettingViewModelFactory(context)
    )

    if (viewModel.ConnectedSuccess) {
        onConnectedSuccess()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Row {
            Spacer(modifier = Modifier.weight(1f))
            Image(
                painter = painterResource(id = R.drawable.company_logo),
                contentDescription = null,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(30.dp))

        OutlinedTextField(
            value = viewModel.newurl,
            onValueChange = viewModel::onUrlChange,
            label = { Text("Server URL") },
            modifier = Modifier.padding(16.dp)
        )

        OutlinedTextField(
            value = viewModel.newport,
            onValueChange = viewModel::onPortChange,
            label = { Text("Port") },
            modifier = Modifier.padding(16.dp)
        )

        OutlinedTextField(
                value = viewModel.newkey,
        onValueChange = viewModel::onKeyChange,
        label = { Text("API Key") },
        modifier = Modifier.padding(16.dp),
        )

        if (viewModel.showErrorMessage.value) {
            Text(
                viewModel.errorMessage.value,
                color = Color.Red,
                fontSize = 15.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(10.dp))
        } else {
            Spacer(modifier = Modifier.height(20.dp))
        }
        OutlinedButton(
            onClick = { viewModel.connect() }
        ) {
            Text("connect")
        }
    }

}