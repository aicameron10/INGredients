package com.recipe.screens

import KottieAnimation
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Switch
import androidx.compose.material.SwitchDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import animateKottieCompositionAsState
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.recipe.database.DatabaseRepository
import com.recipe.getAppVersion
import com.recipe.multiplatformsettings.SessionManager
import com.recipe.switchToDarkMode
import com.recipe.switchToLightMode
import com.recipe.ui.theme.LARGE_PADDING
import com.recipe.ui.theme.MEDIUM_PADDING
import com.recipe.ui.theme.SMALL_PADDING
import com.recipe.ui.theme.X_LARGE_PADDING
import com.recipe.ui.theme.black
import com.recipe.ui.theme.blue5
import com.recipe.ui.theme.grey1
import com.recipe.ui.theme.grey3
import com.recipe.ui.theme.grey5
import com.recipe.ui.theme.grey9
import com.recipe.ui.theme.orange
import com.recipe.ui.theme.white
import com.recipe.viewmodels.SharedViewModel
import com.seiko.imageloader.rememberImagePainter
import compose.icons.FeatherIcons
import compose.icons.feathericons.ChevronRight
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import rememberKottieComposition


class SettingsScreen : Screen, KoinComponent {

    @ExperimentalMaterialApi
    @Composable
    override fun Content() {
        val sessionManager = get<SessionManager>()
        val viewModel = get<SharedViewModel>()
        val databaseRepository = get<DatabaseRepository>()
        SettingsScreenContent(sessionManager, viewModel, databaseRepository)
    }
}

@Composable
fun SettingsScreenContent(
    sessionManager: SessionManager,
    viewModel: SharedViewModel,
    databaseRepository: DatabaseRepository
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Options(sessionManager, viewModel, databaseRepository)
    }
}

@Composable
fun Options(
    sessionManager: SessionManager,
    viewModel: SharedViewModel,
    databaseRepository: DatabaseRepository
) {
    val switchState = remember { mutableStateOf(false) }
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 40.dp)
    ) {

        item {
            TopDetails()
        }

        item {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth().padding(start = 28.dp, end = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Go to Dark Mode",
                        style = MaterialTheme.typography.subtitle2,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = grey9
                    )

                    Switch(
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = orange,
                            uncheckedThumbColor = grey3,
                            checkedTrackColor = orange.copy(alpha = 0.5f),
                            uncheckedTrackColor = grey3.copy(alpha = 0.5f)
                        ),
                        checked = switchState.value,
                        onCheckedChange = { switchState.value = it }
                    )

                    if (switchState.value) {
                        switchToDarkMode()
                    } else {
                        switchToLightMode()
                    }
                }

                Divider(
                    modifier = Modifier
                        .padding(top = 8.dp, bottom = 2.dp,start = 18.dp, end = 16.dp),
                    startIndent = 8.dp,
                    thickness = 1.dp,
                    color = grey1
                )
            }
        }

        item {
            OptionsItemStyle(
                OptionsData(
                    title = "Delete all Favourites",
                    subTitle = "Make space for all new recipes"
                ), sessionManager, databaseRepository
            )
        }


        item {
            Text(
                text = "App Version " + getAppVersion(), modifier = Modifier
                    .fillMaxWidth(1f)
                    .padding(top = 20.dp),
                textAlign = TextAlign.Center,
                lineHeight = 40.sp,
                color = grey5,
                fontSize = 14.sp,
                style = MaterialTheme.typography.subtitle2,
                fontWeight = FontWeight.Normal

            )
        }
    }
}

// This composable displays user's image, name, email and edit button
@OptIn(ExperimentalResourceApi::class)
@Composable
private fun TopDetails() {

    val composition = rememberKottieComposition(
        spec = KottieCompositionSpec.File("ghost.json")
    )

    val animationState by animateKottieCompositionAsState(
        composition = composition,
        speed = 1f,
        iterations = 10
    )
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(MEDIUM_PADDING),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            KottieAnimation(
                composition = composition,
                progress = { animationState.progress },
                modifier = Modifier.width(200.dp).height(200.dp),
            )
            Box(
                contentAlignment = Alignment.Center, modifier = Modifier.padding(top = 12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier
                            .weight(weight = 3f, fill = false)
                            .padding(start = 16.dp, end = 32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally, // Aligning contents horizontally in the center
                        verticalArrangement = Arrangement.Center // Aligning contents vertically in the center
                    ) {
                        // User's name
                        Image(
                            painter = painterResource("logo_ing.png"),
                            contentDescription = "ING Logo",
                            modifier = Modifier
                                .height(80.dp)
                                .fillMaxWidth(),
                            colorFilter = ColorFilter.tint(black)
                        )
                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "The best recipes around!",
                            style = MaterialTheme.typography.subtitle2,
                            maxLines = 1,
                            fontSize = 16.sp,
                            color = grey5,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun OptionsItemStyle(
    item: OptionsData,
    sessionManager: SessionManager,
    databaseRepository: DatabaseRepository
) {
    var showDialog by remember { mutableStateOf(false) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
            }
            .padding(start = 16.dp, end = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth().height(56.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier
                        .weight(weight = 3f, fill = false)
                        .padding(start = 12.dp)
                ) {
                    // Title
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.subtitle2,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = grey9
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // Sub title
                    Text(
                        text = item.subTitle,
                        style = MaterialTheme.typography.subtitle2,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal,
                        color = grey5
                    )
                }
                // Right arrow icon
                Icon(
                    modifier = Modifier
                        .weight(weight = 1f, fill = false).size(18.dp),
                    imageVector = FeatherIcons.ChevronRight,
                    contentDescription = item.title,
                    tint = Color.Black.copy(alpha = 0.70f)
                )
            }
            Divider(
                modifier = Modifier
                    .padding(top = 8.dp, bottom = 2.dp),
                startIndent = 8.dp,
                thickness = 1.dp,
                color = grey1
            )
        }
    }
}

data class OptionsData(val title: String, val subTitle: String)
