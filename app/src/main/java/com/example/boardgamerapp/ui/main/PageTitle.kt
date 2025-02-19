package com.example.boardgamerapp.ui.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun PageTitle(subTitle: String, thirdSubTitle: String = "") {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Board-Gamer",
            style = MaterialTheme.typography.headlineLarge
        )
        Text(
            text = subTitle,
            style = MaterialTheme.typography.headlineMedium
        )
        Text(
            text = thirdSubTitle,
            style = MaterialTheme.typography.headlineSmall
        )
    }
}