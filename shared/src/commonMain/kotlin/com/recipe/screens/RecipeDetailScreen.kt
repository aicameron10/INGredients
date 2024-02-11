package com.recipe.screens

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
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import com.recipe.database.DatabaseRepository
import com.recipe.multiplatformsettings.SessionManager
import com.recipe.network.model.request.RecipeRequest
import com.recipe.network.model.response.ExtendedIngredients
import com.recipe.network.model.response.Steps
import com.recipe.openBrowser
import com.recipe.renderHtml
import com.recipe.screens.components.ShimmerBox
import com.recipe.ui.theme.HORIZONTAL_PADDING
import com.recipe.ui.theme.LARGE_PADDING
import com.recipe.ui.theme.MEDIUM_PADDING
import com.recipe.ui.theme.SMALL_PADDING
import com.recipe.ui.theme.X_SMALL_PADDING
import com.recipe.ui.theme.grey1
import com.recipe.ui.theme.grey2
import com.recipe.ui.theme.grey3
import com.recipe.ui.theme.grey9
import com.recipe.ui.theme.white
import com.recipe.ui.theme.yellow
import com.recipe.utils.RatingCalculator
import com.recipe.utils.StarType
import com.recipe.viewmodels.RecipeDetailViewModel
import com.seiko.imageloader.rememberImagePainter
import compose.icons.FeatherIcons
import compose.icons.feathericons.Clock
import compose.icons.feathericons.Heart
import compose.icons.feathericons.ThumbsUp
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

class RecipeDetailScreen(
    private val recipeId: Int?
) : Screen, KoinComponent {

    @OptIn(ExperimentalResourceApi::class)
    @ExperimentalMaterialApi
    @Composable
    override fun Content() {

        val viewModel = get<RecipeDetailViewModel>()
        val sessionManager = get<SessionManager>()
        val databaseRepository = get<DatabaseRepository>()
        val scope = rememberCoroutineScope()
        val isLoading = viewModel.isLoading.collectAsState().value

        viewModel.isFavourite.value =
            databaseRepository.database.recipesQueries.selectFavouriteInfo(
                recipeId?.toLong() ?: 0L
            ).executeAsOneOrNull()?.favourite?.toInt() ?: 0

        LaunchedEffect(key1 = sessionManager.getAuthorization()) {
            scope.launch {
                viewModel.getRecipeInformation(
                    RecipeRequest(
                        authorization = sessionManager.getAuthorization(),
                        id = recipeId.toString()
                    )
                )
            }
        }

        LaunchedEffect(key1 = sessionManager.getAuthorization()) {
            scope.launch {
                viewModel.recipeInfoObserver.filterNotNull().collect { response ->
                    if (response.data != null) {
                        viewModel.recipeInfo = response.data

                        val existingId =
                            databaseRepository.database.recipesQueries.selectAllRecipes(
                                recipeId?.toLong() ?: 0L
                            ).executeAsOneOrNull()

                        if (existingId == null) {
                            response.data.let {
                                databaseRepository.database.recipesQueries.insertRecipeItems(
                                    id = recipeId?.toLong(),
                                    title = it?.title,
                                    image = it?.image,
                                    servings = it?.servings?.toLong(),
                                    readyInMinutes = it?.readyInMinutes?.toLong(),
                                    sourceName = it?.sourceName,
                                    summary = it?.summary,
                                    sourceUrl = it?.sourceUrl,
                                    spoonacularScore = it?.spoonacularScore?.toLong(),
                                    favourite = 0,
                                )
                            }

                            response.data?.analyzedInstructions?.getOrNull(0)?.steps?.forEach { step ->
                                databaseRepository.database.recipesQueries.insertInstructionsItems(
                                    number = step.number?.toLong(),
                                    step = step.step,
                                    ingredients = step.ingredients.joinToString { it.name.toString() },
                                    equipment = step.equipment.joinToString { it.name.toString() },
                                    recipeId = recipeId?.toLong()
                                )
                            }

                            response.data?.extendedIngredients?.forEach {
                                databaseRepository.database.recipesQueries.insertIngredientsItems(
                                    amount = it.amount?.toLong(),
                                    originalName = it.original,
                                    unit = it.unit,
                                    recipeId = recipeId?.toLong()
                                )
                            }
                        }
                    }
                }
            }
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(white)
        ) {
            LazyColumn(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = CenterHorizontally,
                modifier = Modifier
                    .background(white)
            ) {
                item {
                    Column(
                        modifier = Modifier
                            .background(grey2)
                    ) {
                        if (isLoading) {
                            BookDetailSkeleton()
                        } else {

                            Spacer(modifier = Modifier.padding(SMALL_PADDING))

                            Row(
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                                    .padding(horizontal = HORIZONTAL_PADDING)
                            ) {
                                Card(
                                    modifier = Modifier
                                        .width(100.dp)
                                        .height(100.dp),
                                    shape = RoundedCornerShape(8.dp), elevation = 2.dp
                                ) {
                                    Image(
                                        painter = rememberImagePainter(viewModel.recipeInfo?.image.toString()),
                                        contentScale = ContentScale.Crop,
                                        contentDescription = "",
                                        modifier = Modifier
                                            .height(100.dp)
                                            .width(100.dp)
                                    )
                                }
                                Column(verticalArrangement = Arrangement.SpaceEvenly) {
                                    val numberRatings =
                                        (viewModel.recipeInfo?.spoonacularScore?.toInt() ?: 0) / 2
                                    val stars = RatingCalculator.calculateStars(
                                        numberRatings.toDouble(), 5
                                    )
                                    Row(
                                        horizontalArrangement = Arrangement.Center,
                                        verticalAlignment = Alignment.CenterVertically,
                                    ) {
                                        Icon(
                                            modifier = Modifier
                                                .width(24.dp).height(24.dp),
                                            imageVector = FeatherIcons.ThumbsUp,
                                            contentDescription = "approved",
                                            tint = grey9
                                        )
                                        Text(
                                            modifier = Modifier.padding(
                                                start = SMALL_PADDING,
                                                end = SMALL_PADDING
                                            ),
                                            text = "rating",
                                            textAlign = TextAlign.Center,
                                            color = grey9,
                                            style = MaterialTheme.typography.subtitle2,
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Normal
                                        )
                                        for (star in stars) {
                                            Icon(
                                                modifier = Modifier
                                                    .size(16.dp)
                                                    .padding(end = 2.dp),
                                                painter = when (star) {
                                                    StarType.FULL -> painterResource("star.png")
                                                    StarType.HALF -> painterResource("half_star.png")
                                                    StarType.EMPTY -> painterResource("empty_star.png")
                                                },
                                                contentDescription = "review stars",
                                                tint = when (star) {
                                                    StarType.FULL -> yellow
                                                    StarType.HALF -> Color.Unspecified
                                                    StarType.EMPTY -> grey3
                                                }
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.padding(X_SMALL_PADDING))

                                    Row {
                                        Icon(
                                            modifier = Modifier
                                                .width(24.dp).height(24.dp),
                                            imageVector = FeatherIcons.Clock,
                                            contentDescription = "clock",
                                            tint = grey9
                                        )
                                        Text(
                                            modifier = Modifier.padding(start = SMALL_PADDING),
                                            text = viewModel.recipeInfo?.readyInMinutes.toString() + " minutes",
                                            textAlign = TextAlign.Left,
                                            lineHeight = 24.sp,
                                            style = MaterialTheme.typography.subtitle2,
                                            fontSize = 16.sp,
                                            color = grey9,
                                            fontWeight = FontWeight.Normal
                                        )
                                    }
                                }

                                Icon(
                                    modifier = Modifier
                                        .width(35.dp).height(35.dp).clickable {
                                            viewModel.isFavourite.value = if (viewModel.isFavourite.value == 0) 1 else 0
                                            databaseRepository.database.recipesQueries.updateFavourite(
                                                favourite = viewModel.isFavourite.value.toLong(),
                                                id = recipeId?.toLong() ?: 0L,
                                            )
                                        },
                                    imageVector = if (viewModel.isFavourite.value == 0) FeatherIcons.Heart else Icons.Filled.Favorite,
                                    contentDescription = "favourite",
                                    tint = grey9
                                )
                            }

                            Spacer(modifier = Modifier.padding(SMALL_PADDING))

                            Text(
                                text = renderHtml(viewModel.recipeInfo?.summary.toString()),
                                modifier = Modifier
                                    .fillMaxWidth(1f).padding(horizontal = HORIZONTAL_PADDING),
                                textAlign = TextAlign.Left,
                                style = MaterialTheme.typography.subtitle2,
                                color = grey9,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Normal
                            )

                            Spacer(modifier = Modifier.padding(SMALL_PADDING))

                            Row(modifier = Modifier.padding(horizontal = HORIZONTAL_PADDING)) {
                                Text(
                                    text = viewModel.recipeInfo?.sourceName.toString(),
                                    modifier = Modifier.padding(end = 8.dp),
                                    textAlign = TextAlign.Left,
                                    style = MaterialTheme.typography.subtitle2,
                                    color = grey9,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Normal
                                )

                                val annotatedText = buildAnnotatedString {
                                    withStyle(style = SpanStyle(textDecoration = TextDecoration.Underline)) {
                                        append("View original recipe")
                                    }
                                }
                                Text(
                                    modifier = Modifier.padding(horizontal = 8.dp)
                                        .clickable { openBrowser(viewModel.recipeInfo?.sourceUrl.toString()) },
                                    text = annotatedText,
                                    textAlign = TextAlign.Center,
                                    color = grey9,
                                    style = MaterialTheme.typography.subtitle2,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Normal
                                )
                            }

                            Spacer(modifier = Modifier.padding(MEDIUM_PADDING))
                        }
                    }
                }

                item {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(white),
                    ) {
                        Spacer(modifier = Modifier.padding(SMALL_PADDING))
                        Text(
                            text = "Ingredients for " + viewModel.recipeInfo?.servings + " servings",
                            modifier = Modifier
                                .fillMaxWidth(1f)
                                .padding(horizontal = HORIZONTAL_PADDING),
                            textAlign = TextAlign.Left,
                            lineHeight = 24.sp,
                            color = grey9,
                            fontSize = 21.sp,
                            style = MaterialTheme.typography.subtitle2,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.padding(MEDIUM_PADDING))
                    }
                }

                itemsIndexed(viewModel.recipeInfo?.extendedIngredients ?: emptyList()) { _, item ->
                    IngredientsView(item)
                }

                item {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(grey2),
                    ) {
                        Spacer(modifier = Modifier.padding(SMALL_PADDING))
                        Text(
                            text = "Preparation",
                            modifier = Modifier
                                .padding(horizontal = HORIZONTAL_PADDING)
                                .fillMaxWidth(1f),
                            textAlign = TextAlign.Left,
                            lineHeight = 24.sp,
                            color = grey9,
                            style = MaterialTheme.typography.subtitle2,
                            fontSize = 21.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.padding(X_SMALL_PADDING))

                        Text(
                            modifier = Modifier.padding(horizontal = HORIZONTAL_PADDING),
                            text = "Total Time " + viewModel.recipeInfo?.readyInMinutes + " min",
                            textAlign = TextAlign.Center,
                            color = grey9,
                            style = MaterialTheme.typography.subtitle2,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Normal
                        )

                        Spacer(modifier = Modifier.padding(SMALL_PADDING))

                    }
                }

                itemsIndexed(
                    viewModel.recipeInfo?.analyzedInstructions?.getOrNull(0)?.steps ?: emptyList()
                ) { _, item ->
                    InstructionsView(item)
                }

                item {
                    Spacer(modifier = Modifier.padding(LARGE_PADDING))
                }
            }
        }
    }
}


@Composable
fun IngredientsView(item: ExtendedIngredients) {

    Column(modifier = Modifier.background(white)) {

        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = HORIZONTAL_PADDING),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = item.originalName.toString(),
                textAlign = TextAlign.Left,
                color = grey9,
                lineHeight = 24.sp,
                style = MaterialTheme.typography.subtitle2,
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal
            )
            Text(
                text = item.amount.toString() + " " + item.unit,
                textAlign = TextAlign.End,
                lineHeight = 24.sp,
                color = grey9,
                style = MaterialTheme.typography.subtitle2,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.padding(SMALL_PADDING))

        Divider(
            modifier = Modifier
                .padding(end = 30.dp),
            startIndent = 30.dp,
            thickness = 1.dp,
            color = grey1
        )

        Spacer(modifier = Modifier.padding(SMALL_PADDING))
    }
}

@Composable
fun InstructionsView(item: Steps) {

    Column(modifier = Modifier.background(grey2).fillMaxWidth()) {

        Card(
            modifier = Modifier.padding(top = 8.dp, bottom = 8.dp, start = 20.dp, end = 20.dp)
                .fillMaxWidth().background(white),
            shape = RoundedCornerShape(8.dp), elevation = 2.dp
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(all = 8.dp),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    modifier = Modifier.padding(end = HORIZONTAL_PADDING),
                    text = item.number.toString(),
                    textAlign = TextAlign.Left,
                    color = grey9,
                    lineHeight = 24.sp,
                    style = MaterialTheme.typography.subtitle2,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Column {
                    Text(
                        text = item.step.toString(),
                        textAlign = TextAlign.Left,
                        lineHeight = 24.sp,
                        color = grey9,
                        style = MaterialTheme.typography.subtitle2,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Normal
                    )
                    Spacer(modifier = Modifier.padding(X_SMALL_PADDING))
                    if (item.ingredients.isNotEmpty()) {
                        Row {
                            Text(
                                text = "Ingredients Needed: ",
                                textAlign = TextAlign.Left,
                                lineHeight = 24.sp,
                                color = grey9,
                                style = MaterialTheme.typography.subtitle2,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.padding(X_SMALL_PADDING))
                            Text(
                                text = item.ingredients.joinToString { it.name.toString() },
                                textAlign = TextAlign.Left,
                                lineHeight = 24.sp,
                                color = grey9,
                                style = MaterialTheme.typography.subtitle2,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Normal
                            )
                        }
                        Spacer(modifier = Modifier.padding(X_SMALL_PADDING))
                    }
                    if (item.equipment.isNotEmpty()) {
                        Row {
                            Text(
                                text = "Equipment Needed: ",
                                textAlign = TextAlign.Left,
                                lineHeight = 24.sp,
                                color = grey9,
                                style = MaterialTheme.typography.subtitle2,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.padding(X_SMALL_PADDING))
                            Text(
                                text = item.equipment.joinToString { it.name.toString() },
                                textAlign = TextAlign.Left,
                                lineHeight = 24.sp,
                                color = grey9,
                                style = MaterialTheme.typography.subtitle2,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Normal
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.padding(SMALL_PADDING))
        }
    }
}

@Composable
fun BookDetailSkeleton() {

    Column(modifier = Modifier.padding(30.dp)) {

        ShimmerBox(
            modifier = Modifier.size(200.dp, 300.dp).clip(RoundedCornerShape(size = 10.dp))
                .align(CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(16.dp))

        ShimmerBox(
            modifier = Modifier.height(16.dp).fillMaxWidth(0.6f)
                .align(CenterHorizontally).clip(RoundedCornerShape(size = 10.dp))
        )

        Spacer(modifier = Modifier.height(16.dp))

        ShimmerBox(
            modifier = Modifier.height(16.dp).fillMaxWidth(0.45f)
                .align(CenterHorizontally).clip(RoundedCornerShape(size = 10.dp))
        )
        Spacer(modifier = Modifier.height(16.dp))

        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                ShimmerBox(
                    modifier = Modifier.height(45.dp).weight(1f)
                        .clip(RoundedCornerShape(size = 10.dp))
                )
                Spacer(modifier = Modifier.width(16.dp))
                ShimmerBox(
                    modifier = Modifier.height(45.dp).weight(1f)
                        .clip(RoundedCornerShape(size = 10.dp))
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        val options =
            listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "10")

        options.forEach { _ ->
            ShimmerBox(
                modifier = Modifier.height(16.dp).fillMaxWidth(0.98f)
                    .align(CenterHorizontally).clip(RoundedCornerShape(size = 10.dp))
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}