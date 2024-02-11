package com.recipe.screens.components

import KottieAnimation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import animateKottieCompositionAsState
import com.recipe.ui.theme.grey9
import rememberKottieComposition

@Composable
fun ErrorView(name: String, message: String) {

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            val composition = rememberKottieComposition(
                spec = KottieCompositionSpec.File(name)
            )

            val animationState by animateKottieCompositionAsState(
                composition = composition,
                speed = 1f,
                iterations = 10
            )

            KottieAnimation(
                composition = composition,
                progress = { animationState.progress },
                modifier = Modifier.width(300.dp).height(300.dp),
            )

            Text(
                text = message,
                modifier = Modifier
                    .fillMaxWidth(1f)
                    .padding(start = 48.dp, end = 48.dp),
                textAlign = TextAlign.Center,
                color = grey9,
                style = MaterialTheme.typography.subtitle2,
                fontWeight = FontWeight.Bold

            )
        }
    }
}