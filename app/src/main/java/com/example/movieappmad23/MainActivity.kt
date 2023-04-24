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

class MainActivity : ComponentActivity() { // Hauptaktivität erben von ComponentActivity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { // UI-Inhalte festlegen
            MovieAppMAD23Theme {
                Scaffold(topBar = {
                    SimpleTopAppBar()
                }) { padding ->
                    val movies = getMovies() // Filme aus der Funktion holen
                    MovieList(
                        modifier = Modifier.padding(padding),
                        movies = movies// Filme an die Liste übergeben
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
        actions = {   // Aktionen innerhalb der AppBar
            IconButton(onClick = { showMenu = !showMenu }) {
                Icon(imageVector = Icons.Default.MoreVert, contentDescription = "More")
            }
            DropdownMenu(
                expanded = showMenu,
                onDismissRequest = { showMenu = false } // Menü schließen, wenn außerhalb geklickt wird
            ) {
                DropdownMenuItem(onClick = { println("Favs clicked") }) {
                    Row {// Zeile für Icon und Text
                        Icon(imageVector = Icons.Default.Favorite, contentDescription = "Favorites", modifier = Modifier.padding(4.dp)) // Icon für Favoriten
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
        items(movies) { movie -> // Elemente der Liste
            MovieRow(movie) // Filmzeile erstellen
        }
    }
}

@Composable
fun MovieImage(imageUrl: String) { // Funktion für Vorschaubild
    SubcomposeAsyncImage( // Verwendung von SubcomposeAsyncImage zum asynchronen Laden von Bildern
        model = ImageRequest.Builder(LocalContext.current) // ImageRequest erstellen
            .data(imageUrl)
            .crossfade(true)
            .build(),
        contentScale = ContentScale.Crop,
        contentDescription = stringResource(id = R.string.movie_poster), // Lädt Text aus der Ressourcendatei um Inhalte vom Bild auch Menschen mit Behinderung zu vermitteln
        loading = {
            CircularProgressIndicator()// Ladeanzeige anzeigen, während das Bild geladen wird
        }
    )
}

@Composable
fun FavoriteIcon() { // Funktion zur Erstellung des Favoriten-Icons
    Box(modifier = Modifier
        .fillMaxSize() // Füllen des verfügbaren Platzes
        .padding(10.dp),
        contentAlignment = Alignment.TopEnd // Ausrichtung oben rechts
    ){
        Icon(
            tint = MaterialTheme.colors.secondary, // Farbe des Icons
            imageVector = Icons.Default.FavoriteBorder, // Verwenden des Herz-Icons
            contentDescription = "Add to favorites") // Beschreibung des Inhalts für Barrierefreiheit
    }
}
@Preview
@Composable
fun MovieRow(movie: Movie = getMovies()[0]) { // Funktion zur Erstellung einer Filmzeile
    Card(modifier = Modifier
        .fillMaxWidth()
        .padding(5.dp),
        shape = Shapes.large, // Runde Ecken für die Card
        elevation = 10.dp // Schattierung für die Karte
    ) {
        Column {// Spalte für die Inhalte der Karte
            Box(modifier = Modifier
                .height(150.dp)
                .fillMaxWidth(),
                contentAlignment = Alignment.Center // Inhalt zentrieren
            ) {
                MovieImage(imageUrl = movie.images[0])// Filmbild anzeigen
                FavoriteIcon() //Favoriten Herzal anzeigen
            }

            MovieDetails(modifier = Modifier.padding(12.dp), movie = movie)
        }
    }
}

@Composable
fun MovieDetails(modifier: Modifier = Modifier, movie: Movie) { // Funktion zur Anzeige von Filmdetails

    var expanded by remember {
        mutableStateOf(false) // Zustand für Anzeigen/Verbergen details
    }

    Row( // Zeile für den Titel und den Aufklapp-Pfeil
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            movie.title,
            modifier = Modifier.weight(6f),
            style = MaterialTheme.typography.h6
        )

        IconButton( // Schaltfläche für den Pfeil
            modifier = Modifier.weight(1f),
            onClick = { expanded = !expanded }) {
            Icon(imageVector =
            if (expanded) Icons.Filled.KeyboardArrowDown
            else Icons.Filled.KeyboardArrowUp,
                contentDescription = "expand",
                modifier = Modifier
                    .size(25.dp), // Größe des Icons auf 25.dp setzen
                tint = Color.DarkGray
            )
        }

    }

    AnimatedVisibility( // Eine animierte Sichtbarkeit, um die Details sanft ein- und auszublenden
        visible = expanded,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Column (modifier = modifier) { // Eine Spalte für die Filmdetails
            Text(text = "Regisseur: ${movie.director}", style = MaterialTheme.typography.caption)
            Text(text = "Erscheinungsjahr: ${movie.year}", style = MaterialTheme.typography.caption)
            Text(text = "Genre: ${movie.genre}", style = MaterialTheme.typography.caption)
            Text(text = "Schauspieler: ${movie.actors}", style = MaterialTheme.typography.caption)
            Text(text = "Bewertung: ${movie.rating}", style = MaterialTheme.typography.caption)

            Divider(modifier = Modifier.padding(3.dp)) // Trennlinie

            Text(buildAnnotatedString { // AnnotatedString zum Kombinieren von unterschiedlichen Stilen im Text
                withStyle(style = SpanStyle(color = Color.DarkGray, fontSize = 13.sp)) {
                    append("Handlung: ") // Füge den Text "Handlung: " mit einem dunkelgrauen Stil hinzu
                }
                withStyle(style = SpanStyle(color = Color.DarkGray, fontSize = 13.sp, fontWeight = FontWeight.Light)){
                    append(movie.plot) // Füge die Handlung des Films hinhz
                }
            })
        }
    }
}
