package com.recipe.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Snackbar
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.tab.CurrentTab
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabDisposable
import cafe.adriel.voyager.navigator.tab.TabNavigator
import co.touchlab.kermit.Logger
import com.recipe.navigation.HomeTab
import com.recipe.navigation.RecipeTab
import com.recipe.navigation.SettingsTab
import com.recipe.ui.theme.black
import com.recipe.ui.theme.blue5
import com.recipe.ui.theme.grey5
import com.recipe.ui.theme.grey9
import com.recipe.ui.theme.orange
import com.recipe.ui.theme.white
import com.recipe.viewmodels.SharedViewModel
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

class TabsScreen : Screen, KoinComponent {

    @OptIn(ExperimentalResourceApi::class)
    @ExperimentalMaterialApi
    @Composable
    override fun Content() {

        val viewModel = get<SharedViewModel>()

        val scaffoldState = rememberScaffoldState()

        val snackBarHostState = scaffoldState.snackbarHostState

        LaunchedEffect(snackBarHostState) {
            viewModel.snackBarFlow.collect { message ->
                Logger.i { "dope " + message }
                snackBarHostState.showSnackbar(message,duration = SnackbarDuration.Long)
            }
        }

        MaterialTheme {
            TabNavigator(
                HomeTab,
                tabDisposable = {
                    TabDisposable(
                        navigator = it,
                        tabs = listOf(
                            HomeTab,
                            RecipeTab,
                            SettingsTab
                        )
                    )
                }
            ) { tabNavigator ->
                Scaffold(
                    scaffoldState = scaffoldState,
                    snackbarHost = { CustomSnackbarHost(scaffoldState.snackbarHostState) },
                    topBar = {
                        TopAppBar(backgroundColor = white, // Change this to your desired background color
                            contentColor = grey9,
                            title = {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    if (!viewModel.showBackIcon.value) {
                                        Spacer(modifier = Modifier.width(48.dp)) // Dummy spacer for balancing when no icon
                                    }
                                    if (!viewModel.showBackIcon.value && tabNavigator.current.options.title == "Home") {
                                        Image(
                                            painter = painterResource("logo_ing.png"),
                                            contentDescription = "logo",
                                            modifier = Modifier
                                                .width(110.dp)
                                                .height(30.dp)
                                                .fillMaxWidth(),
                                            colorFilter = ColorFilter.tint(black)
                                        )
                                    } else {
                                        Text(
                                            text = if (viewModel.showBackIcon.value) viewModel.topBarTitle.value else tabNavigator.current.options.title,
                                            fontSize = 17.sp,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                            fontWeight = FontWeight.Bold,
                                            textAlign = TextAlign.Center,
                                            style = MaterialTheme.typography.subtitle2
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(48.dp)) // Always add this spacer for balance
                                }
                            },
                            navigationIcon = if (viewModel.showBackIcon.value) {
                                {
                                    IconButton(onClick = {
                                        viewModel.showBackIcon.value = false
                                        viewModel.nav?.pop()
                                    }) {
                                        Icon(
                                            Icons.Default.ArrowBack,
                                            contentDescription = "Back"
                                        )
                                    }
                                }
                            } else null
                        )
                    },
                    content = {
                        CurrentTab()
                    },
                    bottomBar = {
                        Box(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.Bottom
                            ) {
                                BottomNavigation(
                                    backgroundColor = white, // Change this to your desired background color
                                    contentColor = blue5    // Change this to your desired content color
                                ) {
                                    TabNavigationItem(HomeTab, viewModel)
                                    TabNavigationItem(RecipeTab, viewModel)
                                    TabNavigationItem(SettingsTab, viewModel)
                                }
                            }
                        }
                    }
                )
            }
        }
    }

    @Composable
    fun CustomSnackbarHost(snackbarHostState: SnackbarHostState) {
        SnackbarHost(hostState = snackbarHostState) { data ->
            Snackbar(
                snackbarData = data,
                backgroundColor = grey9,
                contentColor = white,
                actionColor = white
            )
        }
    }

    @Composable
    private fun RowScope.TabNavigationItem(tab: Tab, sharedViewModel: SharedViewModel) {
        val tabNavigator = LocalTabNavigator.current

        BottomNavigationItem(
            selected = tabNavigator.current == tab,
            onClick = {
                if (tab != tabNavigator.current) {
                    sharedViewModel.showBackIcon.value = false
                }
                tabNavigator.current = tab
            },
            label = {
                Text(
                    text = tab.options.title,
                    color = if (tab == tabNavigator.current) orange else grey5,
                    style = MaterialTheme.typography.subtitle2,
                    fontSize = 12.sp
                )
            },
            icon = {
                tab.options.icon?.let {
                    Icon(
                        modifier = Modifier.padding(bottom = 2.dp),
                        painter = it,
                        contentDescription = tab.options.title,
                        tint = if (tab == tabNavigator.current) orange else grey5
                    )
                }
            }
        )
    }
}
