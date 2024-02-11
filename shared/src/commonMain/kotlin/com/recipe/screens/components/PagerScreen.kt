package com.recipe.screens.components

import KottieAnimation
import KottieCompositionSpec
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import animateKottieCompositionAsState
import com.recipe.ui.theme.BUTTON_SIZE
import com.recipe.ui.theme.MEDIUM_PADDING
import com.recipe.ui.theme.ROUND_CORNERS
import com.recipe.ui.theme.SMALL_PADDING
import com.recipe.ui.theme.black
import com.recipe.ui.theme.orange
import com.recipe.ui.theme.white
import com.recipe.utils.Constants.CURRENT_PAGE
import rememberKottieComposition

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PagerScreen(
    onBoarding: OnBoarding,
    onFinishClick: () -> Unit,
    pagerState: PagerState
) {

    val composition = rememberKottieComposition(
        spec = KottieCompositionSpec.File(onBoarding.json)
    )

    val animationState by animateKottieCompositionAsState(
        composition = composition,
        speed = 1f,
        iterations = 10
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(white)
    )
    {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = MEDIUM_PADDING, end = MEDIUM_PADDING)
                .align(Alignment.Center),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            KottieAnimation(
                composition = composition,
                progress = { animationState.progress },
                modifier = Modifier.width(200.dp).height(200.dp),
            )
            Spacer(modifier = Modifier.padding(SMALL_PADDING))
            Text(
                text = onBoarding.title,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.subtitle2,
                textAlign = TextAlign.Center,
                fontSize = 36.sp,
                color = black
            )
            Spacer(modifier = Modifier.padding(SMALL_PADDING))
            Text(
                text = onBoarding.text,
                fontWeight = FontWeight.Normal,
                style = MaterialTheme.typography.subtitle2,
                textAlign = TextAlign.Center,
                fontSize = 18.sp,
                color = black
            )
            Spacer(modifier = Modifier.padding(SMALL_PADDING))
            FinishButton(onFinishClick = onFinishClick, pagerState = pagerState)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FinishButton(
    pagerState: PagerState,
    onFinishClick: () -> Unit
) {
    Box(
        modifier = Modifier.padding(MEDIUM_PADDING).clipToBounds(),
        contentAlignment = Alignment.BottomEnd
    ) {
        AnimatedVisibility(
            modifier = Modifier.fillMaxWidth(),
            visible = pagerState.currentPage == CURRENT_PAGE
        ) {
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(BUTTON_SIZE),
                shape = RoundedCornerShape(ROUND_CORNERS),
                onClick = onFinishClick,
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = orange,
                    contentColor = white,
                )
            )
            {
                Text(text = "Lets get Cooking...")
            }
        }
    }
}

