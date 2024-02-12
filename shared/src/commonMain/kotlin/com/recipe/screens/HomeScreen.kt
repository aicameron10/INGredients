package com.recipe.screens

import KottieAnimation
import KottieCompositionSpec
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import animateKottieCompositionAsState
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.recipe.isConnected
import com.recipe.multiplatformsettings.SessionManager
import com.recipe.network.model.request.RecipeRequest
import com.recipe.network.model.response.ResultData
import com.recipe.ui.theme.MEDIUM_PADDING
import com.recipe.ui.theme.SMALL_PADDING
import com.recipe.ui.theme.black
import com.recipe.ui.theme.blue5
import com.recipe.ui.theme.grey1
import com.recipe.ui.theme.grey2
import com.recipe.ui.theme.grey5
import com.recipe.ui.theme.grey9
import com.recipe.ui.theme.white
import com.recipe.utils.Constants.API_KEY
import com.recipe.viewmodels.SharedViewModel
import com.seiko.imageloader.rememberImagePainter
import compose.icons.FeatherIcons
import compose.icons.feathericons.Filter
import compose.icons.feathericons.MoreHorizontal
import compose.icons.feathericons.Search
import comrecipe.RecipeInfo
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import rememberKottieComposition

class HomeScreen : Screen, KoinComponent {

    @ExperimentalMaterialApi
    @Composable
    override fun Content() {
        val viewModel = get<SharedViewModel>()
        val sessionManager = get<SessionManager>()
        viewModel.loadRecentViewed()
        HomeScreenContent(viewModel, sessionManager)
    }
}

@Composable
fun HomeScreenContent(
    viewModel: SharedViewModel,
    sessionManager: SessionManager
) {
    sessionManager.setAuthorization(API_KEY)

    LaunchedEffect(key1 = sessionManager.getAuthorization()) {
        viewModel.recipeObserver.filterNotNull().collect { response ->
            when {
                response.data != null -> {
                    if (response.data?.results?.isNotEmpty() == true) {
                        viewModel.recipeList = response.data?.results
                    } else {
                        viewModel.showSnackBar("Empty results, please try again!")
                    }
                }
                response.apiError != null -> {
                    viewModel.showSnackBar(response.apiError.message.toString())
                }
                else -> {
                    viewModel.showSnackBar("Something went wrong, please try again!")
                }
            }
        }
    }

    Box(modifier = Modifier.pointerInput(Unit) {
        detectTapGestures(onTap = {
        })
    }) {
        Column {
            RecipeList(
                recipeList = viewModel.recipeList ?: emptyList(),
                sharedViewModel = viewModel
            )
        }
        // Positioned at the top, SearchWithAutocomplete will float over the RecipeList
        SearchWithAutocomplete(viewModel, sessionManager).apply {
            Modifier.align(Alignment.TopStart)
        }
    }
}

@Composable
fun SearchWithAutocomplete(viewModel: SharedViewModel, sessionManager: SessionManager) {
    var searchText by remember { mutableStateOf("") }
    var sortOption by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()
    val apiResponse by viewModel.autoCompleteObserver.collectAsState()

    LaunchedEffect(searchText) {
        coroutineScope.launch {
            if (searchText.length > 2) {
                val request = RecipeRequest(
                    authorization = sessionManager.getAuthorization(),
                    query = searchText
                )
                viewModel.getAutoComplete(request)
            }
        }
    }

    val trailingIcon = @Composable {

        var showMore by remember { mutableStateOf(false) }
        DropdownMenu(modifier = Modifier.background(white).padding(horizontal = SMALL_PADDING),
            expanded = showMore,
            onDismissRequest = { showMore = false }
        ) {
            val options =
                listOf("calories", "time", "popularity", "healthiness", "price", "alcohol")

            options.forEach { option ->
                DropdownMenuItem(onClick = {
                    sortOption = option
                    viewModel.getRecipes(
                        RecipeRequest(
                            authorization = sessionManager.getAuthorization(),
                            query = viewModel.lastSearchedText,
                            sort = sortOption
                        )
                    )
                    showMore = false
                }) {
                    Text(option, style = MaterialTheme.typography.subtitle2)
                }
            }
        }
        IconButton(onClick = {
            if (viewModel.recipeList?.isNotEmpty() == true) {
                showMore = true
            }
        }) {
            Icon(
                FeatherIcons.Filter,
                contentDescription = "",
                tint = grey5
            )
        }
    }

    Column {
        val focusManager = LocalFocusManager.current
        val focusRequester = remember { FocusRequester() }

        TextField(
            modifier = Modifier.fillMaxWidth().focusRequester(focusRequester),
            value = searchText,
            trailingIcon = trailingIcon,
            onValueChange = { searchText = it },
            singleLine = true,
            colors = TextFieldDefaults.textFieldColors(
                focusedIndicatorColor = Color.Transparent, // Hide the underline when focused
                unfocusedIndicatorColor = Color.Transparent
            ),
            placeholder = { Text("Search") },
            leadingIcon = { Icon(FeatherIcons.Search, contentDescription = "Search Icon") },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Done,
                keyboardType = KeyboardType.Text
            ),
            keyboardActions = KeyboardActions(onDone = {
                viewModel.lastSearchedText = searchText
                viewModel.getRecipes(
                    RecipeRequest(
                        authorization = sessionManager.getAuthorization(),
                        query = searchText
                    )
                )
                apiResponse?.data = emptyList()
                searchText = ""
                focusManager.clearFocus()
            })
        )

        LazyColumn {
            itemsIndexed(
                apiResponse?.data ?: emptyList(),
                key = { _, item -> item.id.toString() }) { _, item ->
                Column(modifier = Modifier.background(white).clickable {
                    searchText = item.title.toString()
                    viewModel.lastSearchedText = searchText
                    viewModel.getRecipes(
                        RecipeRequest(
                            authorization = sessionManager.getAuthorization(),
                            query = searchText
                        )
                    )
                    apiResponse?.data = emptyList()
                    searchText = ""
                    focusManager.clearFocus()
                }) {
                    Text(
                        text = item.title.toString(),
                        fontSize = 16.sp,
                        color = grey9,
                        maxLines = 1,
                        style = MaterialTheme.typography.subtitle2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(16.dp)
                    )

                    Divider(
                        modifier = Modifier
                            .padding(end = 16.dp),
                        startIndent = 16.dp,
                        thickness = 1.dp,
                        color = grey1
                    )
                }
            }
        }
    }
}

@Composable
fun RecentlyViewedItem(
    item: RecipeInfo,
    sharedViewModel: SharedViewModel
) {
    val navigator = LocalNavigator.currentOrThrow
    Column(
        modifier = Modifier
            .width(130.dp)
            .height(200.dp)
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
            }
    ) {
        Card(
            modifier = Modifier
                .padding(4.dp, 4.dp)
                .width(130.dp)
                .height(100.dp),
            shape = RoundedCornerShape(8.dp), elevation = 4.dp
        ) {
            Image(
                painter = rememberImagePainter(item.image.toString()),
                contentScale = ContentScale.FillBounds,
                contentDescription = "image",
                modifier = Modifier
                    .height(312.dp)
                    .width(231.dp)
            )
        }

        Text(
            text = item.title.toString(),
            style = MaterialTheme.typography.subtitle2,
            fontSize = 14.sp,
            maxLines = 3,
            color = black,
            overflow = TextOverflow.Ellipsis,
            fontWeight = FontWeight.Medium,
            modifier = Modifier
                .padding(start = 4.dp, end = 4.dp, top = 4.dp, bottom = 2.dp)
        )
    }
}

@Composable
fun RecipeList(
    modifier: Modifier = Modifier,
    recipeList: List<ResultData>,
    sharedViewModel: SharedViewModel
) {
    val stateLister = rememberLazyListState()
    Column(
        modifier = modifier.fillMaxWidth().background(white).fillMaxHeight().padding(top = 54.dp)
    ) {
        when {
            !isConnected() -> {
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
            }
            else -> {
                if (recipeList.isEmpty()) {
                    val recentList = sharedViewModel.recentList.value
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.TopCenter
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Spacer(modifier = Modifier.padding(MEDIUM_PADDING))
                            if (sharedViewModel.recentList.value.isNotEmpty()) {
                                Text(
                                    text = "Recently viewed recipes.", modifier = Modifier
                                        .fillMaxWidth(1f)
                                        .padding(start = 48.dp, end = 48.dp),
                                    textAlign = TextAlign.Center,
                                    color = grey9,
                                    fontSize = 19.sp,
                                    style = MaterialTheme.typography.subtitle2,
                                    fontWeight = FontWeight.Bold
                                )

                                Spacer(modifier = Modifier.padding(SMALL_PADDING))
                                LazyRow(
                                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp)
                                ) {
                                    itemsIndexed(
                                        items = recentList,
                                        key = { _, item -> item.id }) { _, item ->
                                        RecentlyViewedItem(item = item, sharedViewModel)
                                    }
                                }
                            }

                            val composition = rememberKottieComposition(
                                spec = KottieCompositionSpec.File("ingredients.json")
                            )

                            val animationState by animateKottieCompositionAsState(
                                composition = composition,
                                speed = 1f,
                                iterations = 1
                            )

                            KottieAnimation(
                                composition = composition,
                                progress = { animationState.progress },
                                modifier = Modifier.width(280.dp).height(250.dp),
                            )
                            Text(
                                text = "Start your search for amazing recipes!", modifier = Modifier
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
        }

        LazyColumn(
            state = stateLister,
            modifier = Modifier.padding(start = MEDIUM_PADDING, end = MEDIUM_PADDING),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(top = 24.dp, bottom = 68.dp)
        ) {
            itemsIndexed(recipeList, key = { _, item -> item.id.toString() }) { _, item ->
                RecipeItem(
                    item,
                    sharedViewModel
                )
            }
        }
    }
}

@Composable
fun RecipeItem(
    item: ResultData,
    sharedViewModel: SharedViewModel
) {
    val navigator = LocalNavigator.currentOrThrow
    Card(
        modifier = Modifier
            .height(180.dp)
            .fillMaxWidth()
            .clickable {
                sharedViewModel.showBackIcon.value = true
                sharedViewModel.topBarTitle.value = item.title.toString()
                sharedViewModel.nav = navigator
                navigator.push(
                    RecipeDetailScreen(
                        recipeId = item.id,
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
                Text(
                    item.title.toString(),
                    fontSize = 18.sp,
                    color = grey9,
                    maxLines = 2,
                    fontWeight = FontWeight.Bold,
                    overflow = TextOverflow.Ellipsis
                )
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