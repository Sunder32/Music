package com.example.musicai.ui.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.musicai.player.GlobalPlayerManager
import com.example.musicai.ui.components.*
import com.example.musicai.ui.theme.*
import com.example.musicai.ui.viewmodel.MusicUiState
import com.example.musicai.ui.viewmodel.MusicViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MusicAppScreen(
    viewModel: MusicViewModel = viewModel()
) {
    val context = LocalContext.current
    val currentTrack by viewModel.currentTrack.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    
    var currentScreen by remember { mutableStateOf("search") }
    var searchQuery by remember { mutableStateOf("") }
    var selectedGenre by remember { mutableStateOf("Поп") }
    var selectedMood by remember { mutableStateOf("Энергичное") }
    
    // Инициализация плеера
    LaunchedEffect(Unit) {
        GlobalPlayerManager.initialize(context)
    }
    
    // Cleanup при выходе
    DisposableEffect(Unit) {
        onDispose {
            if (currentTrack == null) {
                GlobalPlayerManager.release()
            }
        }
    }

    Scaffold(
        containerColor = BackgroundDark,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = when (currentScreen) {
                            "search" -> "🎵 MusicAI"
                            "favorites" -> "💖 Избранное"
                            "player" -> "Плеер"
                            else -> "MusicAI"
                        },
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    if (currentScreen != "search") {
                        IconButton(onClick = { currentScreen = "search" }) {
                            Icon(Icons.Filled.ArrowBack, "Назад", tint = Color.White)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BackgroundDark
                )
            )
        },
        bottomBar = {
            // Скрываем MiniPlayer когда открыт полноэкранный плеер
            if (currentScreen != "player") {
                currentTrack?.let { track ->
                    MiniPlayer(
                        track = track,
                        isPlaying = GlobalPlayerManager.isPlayerPlaying(),
                        onPlayPause = { GlobalPlayerManager.togglePlayPause() },
                        onClick = { currentScreen = "player" },
                        onStop = {
                            GlobalPlayerManager.stop()
                            viewModel.clearCurrentTrack()
                        }
                    )
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Brush.verticalGradient(listOf(BackgroundDark, BackgroundMid)))
        ) {
            when (currentScreen) {
                "search" -> SearchScreen(
                    viewModel = viewModel,
                    uiState = uiState,
                    searchQuery = searchQuery,
                    onSearchChange = { searchQuery = it },
                    selectedGenre = selectedGenre,
                    onGenreChange = { selectedGenre = it },
                    selectedMood = selectedMood,
                    onMoodChange = { selectedMood = it },
                    onFavoritesClick = {
                        currentScreen = "favorites"
                        viewModel.loadFavorites()
                    },
                    onTrackClick = { track ->
                        viewModel.playTrack(track) {
                            currentScreen = "player"
                        }
                    }
                )
                
                "favorites" -> FavoritesScreen(
                    viewModel = viewModel,
                    onTrackClick = { track ->
                        viewModel.playTrack(track) {
                            currentScreen = "player"
                        }
                    }
                )
                
                "player" -> currentTrack?.let { track ->
                    PlayerScreen(
                        track = track,
                        viewModel = viewModel,
                        onBack = {
                            currentScreen = "search"
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun SearchScreen(
    viewModel: MusicViewModel,
    uiState: MusicUiState,
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    selectedGenre: String,
    onGenreChange: (String) -> Unit,
    selectedMood: String,
    onMoodChange: (String) -> Unit,
    onFavoritesClick: () -> Unit,
    onTrackClick: (com.example.musicai.data.model.MusicTrack) -> Unit
) {
    val favoriteTracks by viewModel.favoriteTracks.collectAsState()
    
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Кнопка избранного
        item {
            ElevatedCard(
                onClick = onFavoritesClick,
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = CardBackground
                ),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Filled.Favorite,
                        contentDescription = null,
                        tint = AccentPink,
                        modifier = Modifier.size(36.dp)
                    )
                    Spacer(Modifier.width(16.dp))
                    Column {
                        Text(
                            "Избранные треки",
                            style = MaterialTheme.typography.titleLarge,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "Ваша коллекция любимой музыки",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary
                        )
                    }
                }
            }
        }

        // Поиск по тексту
        item {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchChange,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Название, исполнитель или текст песни...") },
                    leadingIcon = {
                        Icon(Icons.Filled.Search, null, tint = AccentPurple)
                    },
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AccentPurple,
                        unfocusedBorderColor = Color.White.copy(0.3f),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        cursorColor = AccentPurple,
                        focusedContainerColor = CardBackground,
                        unfocusedContainerColor = CardBackground
                    ),
                    singleLine = true
                )
                
                Text(
                    "💡 Лучше всего искать по названию и исполнителю (например: \"Марабу ATL\")",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
            }
        }

        // Жанры
        item {
            FilterSection(
                title = "🎵 Жанр",
                items = listOf("Поп", "Рок", "Электроника", "Хип-хоп", "Джаз", "Классика").map { it to it },
                selectedValue = selectedGenre,
                onSelect = onGenreChange
            )
        }

        // Настроение
        item {
            FilterSection(
                title = "😊 Настроение",
                items = listOf("Энергичное", "Спокойное", "Счастливое", "Грустное", "Романтичное").map { it to it },
                selectedValue = selectedMood,
                onSelect = onMoodChange
            )
        }

        // Кнопка поиска
        item {
            Button(
                onClick = { viewModel.searchMusic(searchQuery, selectedGenre, selectedMood) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                contentPadding = PaddingValues(0.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.horizontalGradient(listOf(AccentPurple, AccentPink)),
                            RoundedCornerShape(16.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Filled.Search, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "Найти музыку",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Результаты
        when (val state = uiState) {
            is MusicUiState.Loading -> {
                item {
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .padding(60.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            CircularProgressIndicator(
                                color = AccentPurple,
                                strokeWidth = 4.dp,
                                modifier = Modifier.size(48.dp)
                            )
                            Text(
                                "Поиск музыки...",
                                style = MaterialTheme.typography.titleMedium,
                                color = AccentPurple,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
            is MusicUiState.Success -> {
                if (state.tracks.isNotEmpty()) {
                    item {
                        Text(
                            "✨ Найдено: ${state.tracks.size} треков",
                            style = MaterialTheme.typography.titleLarge,
                            color = AccentPurple,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    items(state.tracks) { track ->
                        TrackCard(
                            track = track,
                            isFavorite = favoriteTracks.contains(track.id),
                            onFavoriteClick = { viewModel.toggleFavorite(track.id) },
                            onClick = { onTrackClick(track) }
                        )
                    }
                } else {
                    item {
                        EmptyState(
                            icon = Icons.Filled.SearchOff,
                            title = "Ничего не найдено",
                            message = "Попробуйте изменить запрос или выбрать другой жанр"
                        )
                    }
                }
            }
            is MusicUiState.Error -> {
                item {
                    ErrorCard(message = state.message)
                }
            }
            else -> {}
        }

        item { Spacer(Modifier.height(100.dp)) }
    }
}

@Composable
fun FavoritesScreen(
    viewModel: MusicViewModel,
    onTrackClick: (com.example.musicai.data.model.MusicTrack) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadFavorites()
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        when (val state = uiState) {
            is MusicUiState.Success -> {
                if (state.tracks.isEmpty()) {
                    item {
                        EmptyState(
                            icon = Icons.Filled.Favorite,
                            title = "Нет избранных треков",
                            message = "Добавьте треки в избранное, чтобы они появились здесь"
                        )
                    }
                } else {
                    item {
                        Text(
                            "💖 Избранное (${state.tracks.size})",
                            style = MaterialTheme.typography.headlineMedium,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    items(state.tracks) { track ->
                        TrackCard(
                            track = track,
                            isFavorite = true,
                            onFavoriteClick = { viewModel.toggleFavorite(track.id) },
                            onClick = { onTrackClick(track) }
                        )
                    }
                }
            }
            else -> {}
        }
        item { Spacer(Modifier.height(100.dp)) }
    }
}
