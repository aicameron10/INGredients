package com.recipe.screens

import KottieAnimation
import KottieCompositionSpec
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import com.recipe.isConnected
import com.recipe.ui.theme.MEDIUM_PADDING
import com.recipe.ui.theme.blue5
import com.recipe.ui.theme.grey2
import com.recipe.ui.theme.grey5
import com.recipe.ui.theme.grey9
import com.recipe.ui.theme.white
import com.recipe.viewmodels.FavouriteViewModel
import com.recipe.viewmodels.SharedViewModel
import com.seiko.imageloader.rememberImagePainter
import compose.icons.FeatherIcons
import compose.icons.feathericons.MoreHorizontal
import comrecipe.RecipeInfo
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import rememberKottieComposition

class FavouriteScreen : Screen, KoinComponent {

    @ExperimentalMaterialApi
    @Composable
    override fun Content() {
        val viewModel = get<FavouriteViewModel>()
        val sharedViewModel = get<SharedViewModel>()

        viewModel.loadFavourite()

        val favList = viewModel.favouriteList.value

        FavouriteList(favList = favList, sharedViewModel = sharedViewModel, viewModel = viewModel)
    }

    @Composable
    fun FavouriteList(
        modifier: Modifier = Modifier,
        favList: List<RecipeInfo>,
        sharedViewModel: SharedViewModel,
        viewModel: FavouriteViewModel
    ) {
        val stateLister = rememberLazyListState()
        Column(
            modifier = modifier.fillMaxWidth().fillMaxHeight()
        ) {
            if (!isConnected() && favList.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        val composition = rememberKottieComposition(
                            spec = KottieCompositionSpec.File("internet.json")
                        )

                        val animationState by animateKottieCompositionAsState(
                            composition = composition,
                            speed = 1f,
                            iterations = 5
                        )

                        KottieAnimation(
                            composition = composition,
                            progress = { animationState.progress },
                            modifier = Modifier.width(300.dp).height(300.dp),
                        )
                    }
                }
            } else {
                if (favList.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            val composition = rememberKottieComposition(
                                spec = KottieCompositionSpec.File("fav.json")
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
                                text = "Search for you favourite recipes and add them here!",
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
            }

            LazyColumn(
                state = stateLister,
                modifier = Modifier.padding(start = MEDIUM_PADDING, end = MEDIUM_PADDING),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(top = 24.dp, bottom = 68.dp)
            ) {
                itemsIndexed(favList, key = { _, item -> item.id.toString() }) { _, item ->
                    FavouriteItem(
                        item,
                        sharedViewModel,
                        viewModel
                    )
                }
            }
        }
    }

    @Composable
    fun FavouriteItem(
        item: RecipeInfo,
        sharedViewModel: SharedViewModel,
        viewModel: FavouriteViewModel
    ) {
        val navigator = LocalNavigator.currentOrThrow
        Card(
            modifier = Modifier
                .height(150.dp)
                .fillMaxWidth()
                .clickable {
                    sharedViewModel.showBackIcon.value = true
                    sharedViewModel.topBarTitle.value = item.title.toString()
                    sharedViewModel.nav = navigator
                    navigator.push(
                        RecipeDetailScreen(
                            recipeId = item.id.toInt(),
                            recipeTitle = item.title,
                            recipeImage = item.image
                        )
                    )
                },
            shape = RoundedCornerShape(8.dp), elevation = 2.dp
        ) {
            Row(
                modifier = Modifier
                    .background(grey2, RoundedCornerShape(8.dp)),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Card(
                    modifier = Modifier
                        .width(130.dp)
                        .padding(start = 16.dp)
                        .height(130.dp).padding(end = 8.dp),
                    shape = RoundedCornerShape(8.dp), elevation = 2.dp
                ) {
                    Image(
                        rememberImagePainter(item.image.toString()),
                        contentDescription = null,
                        contentScale = ContentScale.FillBounds,
                        modifier = Modifier
                            .height(312.dp)
                            .width(231.dp)
                    )
                }
                Column(
                    modifier = Modifier.weight(1f).padding(top = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(end = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            item.title.toString(),
                            fontSize = 18.sp,
                            color = grey9,
                            maxLines = 2,
                            fontWeight = FontWeight.Bold,
                            overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.weight(1f)
                        )
                        Spacer(Modifier.width(8.dp))
                        Icon(
                            modifier = Modifier
                                .wrapContentWidth(align = Alignment.End)
                                .width(35.dp).height(35.dp).clickable {
                                    viewModel.updateFavourite(item.id)
                                },
                            imageVector = Icons.Filled.Favorite,
                            contentDescription = "favourite",
                            tint = grey9
                        )
                    }
                    Divider(
                        modifier = Modifier
                            .padding(top = 18.dp, end = 8.dp),
                        thickness = 0.75.dp,
                        color = grey5
                    )
                    Row(
                        modifier = Modifier,
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier.clickable(
                                onClick = {}
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    modifier = Modifier
                                        .weight(weight = 1f, fill = false)
                                        .padding(end = 8.dp),
                                    imageVector = FeatherIcons.MoreHorizontal,
                                    contentDescription = "",
                                    tint = Color.Black.copy(alpha = 0.70f)
                                )
                                Text(
                                    modifier = Modifier.padding(end = 8.dp),
                                    text = "More options",
                                    fontSize = 13.sp,
                                    color = blue5,
                                    fontWeight = FontWeight.Medium,
                                    textAlign = TextAlign.Center,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}


