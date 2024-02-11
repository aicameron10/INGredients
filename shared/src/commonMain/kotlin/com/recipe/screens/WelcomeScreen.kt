package com.recipe.screens

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.recipe.multiplatformsettings.SessionManager
import com.recipe.screens.components.OnBoarding
import com.recipe.screens.components.PagerScreen
import com.recipe.ui.theme.BOTTOM_PADDING
import com.recipe.ui.theme.LARGE_PADDING
import com.recipe.ui.theme.X_LARGE_PADDING
import com.recipe.ui.theme.black
import com.recipe.ui.theme.grey3
import com.recipe.ui.theme.orange
import com.recipe.ui.theme.white
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

class WelcomeScreen : Screen, KoinComponent {

    @OptIn(ExperimentalFoundationApi::class, ExperimentalResourceApi::class)
    @ExperimentalMaterialApi
    @Composable
    override fun Content() {
        val sessionManager = get<SessionManager>()
        val navigator = LocalNavigator.currentOrThrow
        val pages = listOf(
            OnBoarding.First,
            OnBoarding.Second,
            OnBoarding.Third
        )
        Column(
            modifier = Modifier.fillMaxSize()
                .background(white).clipToBounds(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val pageCount = 3
            val pagerState = rememberPagerState(
                pageCount = { pageCount },
            )
            val indicatorScrollState = rememberLazyListState()

            Image(
                painter = painterResource("logo_ing.png"),
                contentDescription = "ING Logo",
                modifier = Modifier
                    .height(120.dp)
                    .padding(top = X_LARGE_PADDING, start = LARGE_PADDING, end = LARGE_PADDING)
                    .fillMaxWidth(),
                colorFilter = ColorFilter.tint(black)
            )

            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) { position ->
                PagerScreen(
                    onBoarding = pages[position],
                    onFinishClick = {
                        sessionManager.setOnBoarding(true)
                        navigator.replaceAll(TabsScreen())
                    },
                    pagerState = pagerState
                )
            }
            Row(modifier = Modifier.padding(bottom = BOTTOM_PADDING)) {
                val scope = rememberCoroutineScope()
                Image(
                    painter = painterResource("arrow-left-circle.png"),
                    contentDescription = "arrow left",
                    modifier = Modifier
                        .size(40.dp)
                        .fillMaxWidth()
                        .clickable {
                            val previousPage =
                                (pagerState.currentPage - 1).coerceIn(0, pageCount - 1)

                            // Animate scroll to the previous page
                            scope.launch {
                                pagerState.animateScrollToPage(previousPage)
                            }
                        }, colorFilter = ColorFilter.tint(color = black)
                )
                LazyRow(
                    state = indicatorScrollState,
                    modifier = Modifier
                        .height(50.dp).padding(start = 24.dp, end = 24.dp)
                        .width(((6 + 16) * 2 + 3 * (10 + 16)).dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(pageCount) { iteration ->
                        val color =
                            if (pagerState.currentPage == iteration) orange else grey3
                        item(key = "item$iteration") {
                            val currentPage = pagerState.currentPage
                            val firstVisibleIndex by remember { derivedStateOf { indicatorScrollState.firstVisibleItemIndex } }
                            val lastVisibleIndex =
                                indicatorScrollState.layoutInfo.visibleItemsInfo.lastOrNull()?.index
                                    ?: 0
                            val size by animateDpAsState(
                                targetValue = when (iteration) {
                                    currentPage -> {
                                        10.dp
                                    }

                                    in firstVisibleIndex + 1 until lastVisibleIndex -> {
                                        10.dp
                                    }

                                    else -> {
                                        6.dp
                                    }
                                }
                            )
                            Box(
                                modifier = Modifier
                                    .padding(8.dp)
                                    .background(color, CircleShape)
                                    .size(size)
                            )
                        }
                    }
                }
                Image(
                    painter = painterResource("arrow-right-circle.png"),
                    contentDescription = "arrow right",
                    modifier = Modifier
                        .size(40.dp)
                        .fillMaxWidth()
                        .clickable {
                            val nextPage = (pagerState.currentPage + 1).coerceIn(0, pageCount - 1)
                            // Animate scroll to the next page
                            scope.launch {
                                pagerState.animateScrollToPage(nextPage)
                            }
                        },
                    colorFilter = ColorFilter.tint(color = black)
                )
            }
        }
    }
}