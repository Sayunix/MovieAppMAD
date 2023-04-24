package com.example.movieappmad23

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.example.movieappmad23.models.Movie
import com.example.movieappmad23.models.getMovies
import com.example.movieappmad23.ui.theme.MovieAppMAD23Theme
import com.example.movieappmad23.ui.theme.Shapes

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MovieAppMAD23Theme {
                Scaffold(topBar = {
                    SimpleTopAppBar()
                }) { padding ->
                    val movies = getMovies()
                    MovieList(
                        modifier = Modifier.padding(padding),
                        movies = movies
                    )
                }
            }
        }
    }
}

@Composable
fun SimpleTopAppBar(){
    var showMenu by remember { mutableStateOf(false) }

    TopAppBar(
        title = { Text("Movies") },
        actions = {
            IconButton(onClick = { showMenu = !showMenu }) {
                Icon(imageVector = Icons.Default.MoreVert, contentDescription = "More")
            }
            DropdownMenu(
                expanded = showMenu,
                onDismissRequest = { showMenu = false }
            ) {
                DropdownMenuItem(onClick = { println("Favs clicked") }) {
                    Row {
                        Icon(imageVector = Icons.Default.Favorite, contentDescription = "Favorites", modifier = Modifier.padding(4.dp))
                        Text(text = "Favorites", modifier = Modifier
                            .width(100.dp)
                            .padding(4.dp))
                    }
                }
            }
        }
    )
}

@Preview
@Composable
fun MovieList(modifier: Modifier = Modifier, movies: List<Movie> = getMovies()) {
    LazyColumn (
        modifier = modifier,
        contentPadding = PaddingValues(all = 12.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        items(movies) { movie ->
            MovieRow(movie)
        }
    }
}

@Composable
fun MovieImage(imageUrl: String) {
    SubcomposeAsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(imageUrl)
            .crossfade(true)
            .build(),
        contentScale = ContentScale.Crop,
        contentDescription = stringResource(id = R.string.movie_poster),
        loading = {
            CircularProgressIndicator()
        }
    )
}

@Composable
fun FavoriteIcon() {
    Box(modifier = Modifier
        .fillMaxSize()
        .padding(10.dp),
        contentAlignment = Alignment.TopEnd
    ){
        Icon(
            tint = MaterialTheme.colors.secondary,
            imageVector = Icons.Default.FavoriteBorder,
            contentDescription = "Add to favorites")
    }
}
@Preview
@Composable
fun MovieRow(movie: Movie = getMovies()[0]) {
    Card(modifier = Modifier
        .fillMaxWidth()
        .padding(5.dp),
        shape = Shapes.large,
        elevation = 10.dp
    ) {
        Column {
            Box(modifier = Modifier
                .height(150.dp)
                .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                MovieImage(imageUrl = movie.images[0])
                FavoriteIcon()
            }

            MovieDetails(modifier = Modifier.padding(12.dp), movie = movie)
        }
    }
}

@Composable
fun MovieDetails(modifier: Modifier = Modifier, movie: Movie) {

    var expanded by remember {
        mutableStateOf(false)
    }

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            movie.title,
            modifier = Modifier.weight(6f),
            style = MaterialTheme.typography.h6
        )

        IconButton(
            modifier = Modifier.weight(1f),
            onClick = { expanded = !expanded }) {
            Icon(imageVector =
                if (expanded) Icons.Filled.KeyboardArrowDown
                else Icons.Filled.KeyboardArrowUp,
                    contentDescription = "expand",
                    modifier = Modifier
                        .size(25.dp),
                    tint = Color.DarkGray
            )
        }

    }

    AnimatedVisibility(
        visible = expanded,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Column (modifier = modifier) {
            Text(text = "Director: ${movie.director}", style = MaterialTheme.typography.caption)
            Text(text = "Released: ${movie.year}", style = MaterialTheme.typography.caption)
            Text(text = "Genre: ${movie.genre}", style = MaterialTheme.typography.caption)
            Text(text = "Actors: ${movie.actors}", style = MaterialTheme.typography.caption)
            Text(text = "Rating: ${movie.rating}", style = MaterialTheme.typography.caption)

            Divider(modifier = Modifier.padding(3.dp))

            Text(buildAnnotatedString {
                withStyle(style = SpanStyle(color = Color.DarkGray, fontSize = 13.sp)) {
                    append("Plot: ")
                }
                withStyle(style = SpanStyle(color = Color.DarkGray, fontSize = 13.sp, fontWeight = FontWeight.Light)){
                    append(movie.plot)
                }
            })
        }
    }
}
