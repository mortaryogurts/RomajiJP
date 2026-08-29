package com.example.romajijp.activity

import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.text.selection.rememberSelectionState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.DownloadDone
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalTextToolbar
import androidx.compose.ui.platform.TextToolbar
import androidx.compose.ui.platform.TextToolbarStatus
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.romajijp.activity.ui.theme.BrandDarker
import com.example.romajijp.activity.ui.theme.BrandPeach
import com.example.romajijp.activity.ui.theme.RomajiJPTheme
import com.example.romajijp.components.FuriganaText
import com.example.romajijp.components.LyricsSelectionButtons
import com.example.romajijp.components.LyricsSkeleton
import com.example.romajijp.components.SongDetailsHeader
import com.example.romajijp.model.Song
import com.example.romajijp.uistate.LyricsDisplayMode
import com.example.romajijp.uistate.LyricsUiState
import com.example.romajijp.viewmodel.LyricsViewModel

class LyricsScreen : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val song = Song(
            id = 0,
            title = intent.getStringExtra("song_title") ?: "",
            artist = intent.getStringExtra("song_artist") ?: "",
            album = intent.getStringExtra("song_album"),
            lyrics = intent.getStringExtra("song_lyrics"),
            artworkUrl = intent.getStringExtra("song_artwork"),
            durationMillis = intent.getLongExtra("song_duration", 0)
        )

        setContent {
            RomajiJPTheme {
                LyricsScreen(
                    song = song,
                    onBackClick = { finish() }
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun LyricsScreen(
    song: Song,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LyricsViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(song) {
        viewModel.loadLyrics(song)
    }

    LyricsScreenContent(
        song = song,
        uiState = uiState,
        onBackClick = onBackClick,
        onDownloadClick = { viewModel.downloadSong(song) },
        onModeChange = { viewModel.updateDisplayMode(it) },
        onTranslateRequest = { viewModel.translateSelection(it) },
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun LyricsScreenContent(
    song: Song,
    uiState: LyricsUiState,
    onBackClick: () -> Unit,
    onDownloadClick: () -> Unit,
    onModeChange: (LyricsDisplayMode) -> Unit,
    onTranslateRequest: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val selectionState = rememberSelectionState()
    var selectedText by remember { mutableStateOf("") }
    var isTranslationSheetVisible by remember { mutableStateOf(false) }

    val selectedSubstring = remember(selectionState.selectedTexts) {
        selectionState.selectedTexts.joinToString("") { it.text }
    }

    val platformtoolbar = LocalTextToolbar.current
    val clipboardManager = LocalClipboardManager.current
    val currentSelectedText by rememberUpdatedState(selectedSubstring)

    val customToolbar = remember {
        object : TextToolbar {
            override val status: TextToolbarStatus
                get() = platformtoolbar.status

            override fun hide() {
                platformtoolbar.hide()
            }

            override fun showMenu(
                rect: Rect,
                onCopyRequested: (() -> Unit)?,
                onPasteRequested: (() -> Unit)?,
                onCutRequested: (() -> Unit)?,
                onSelectAllRequested: (() -> Unit)?
            ) {
                if (currentSelectedText.isNotBlank()) {
                    selectedText = currentSelectedText
                    isTranslationSheetVisible = true
                    onTranslateRequest(currentSelectedText)
                }
            }
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = song.title,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    },
                    actions = {
                        if (!uiState.isSaved) {
                            IconButton(onClick = {
                                onDownloadClick()
                                Toast.makeText(context, "Saved to library", Toast.LENGTH_SHORT).show()
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Download,
                                    contentDescription = "Download"
                                )
                            }
                        } else {
                            Icon(
                                imageVector = Icons.Default.DownloadDone,
                                contentDescription = "Saved",
                                modifier = Modifier.padding(end = 12.dp),
                                tint = BrandPeach
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = BrandDarker,
                        titleContentColor = BrandPeach,
                        navigationIconContentColor = BrandPeach,
                        actionIconContentColor = BrandPeach
                    )
                )
            },
            containerColor = BrandDarker
        ) { innerPadding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                item {
                    SongDetailsHeader(song = song)
                }

                if (uiState.isLoading) {
                    item {
                        LyricsSkeleton()
                    }
                } else if (uiState.error != null) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = uiState.error, color = BrandPeach)
                        }
                    }
                } else {
                    if (uiState.isJapanese) {
                        item {
                            LyricsSelectionButtons(
                                currentMode = uiState.displayMode,
                                onModeChange = onModeChange,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )
                        }
                    }

                    item {
                        CompositionLocalProvider(LocalTextToolbar provides customToolbar) {
                            SelectionContainer(
                                state = selectionState
                            ) {
                                Column {
                                    when (uiState.displayMode) {
                                        LyricsDisplayMode.ORIGINAL -> {
                                            Text(
                                                text = uiState.originalLyrics ?: "",
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(horizontal = 24.dp, vertical = 16.dp),
                                                color = BrandPeach,
                                                style = MaterialTheme.typography.bodyLarge,
                                                lineHeight = 28.sp,
                                                textAlign = TextAlign.Center
                                            )
                                        }

                                        LyricsDisplayMode.ROMAJI -> {
                                            Text(
                                                text = uiState.romanizedLyrics ?: uiState.originalLyrics ?: "",
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(horizontal = 24.dp, vertical = 16.dp),
                                                color = BrandPeach,
                                                style = MaterialTheme.typography.bodyLarge,
                                                lineHeight = 28.sp,
                                                textAlign = TextAlign.Center
                                            )
                                        }

                                        LyricsDisplayMode.FURIGANA -> {
                                            if (uiState.furiganaLyrics != null) {
                                                FuriganaText(
                                                    tokens = uiState.furiganaLyrics,
                                                    baseColor = BrandPeach,
                                                    modifier = Modifier.padding(
                                                        horizontal = 24.dp,
                                                        vertical = 16.dp
                                                    )
                                                )
                                            } else {
                                                Text(
                                                    text = uiState.originalLyrics ?: "",
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(horizontal = 24.dp, vertical = 16.dp),
                                                    color = BrandPeach,
                                                    style = MaterialTheme.typography.bodyLarge,
                                                    lineHeight = 28.sp,
                                                    textAlign = TextAlign.Center
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(48.dp))
                }
            }
        }

        // Floating Translate Button
        if (selectedSubstring.isNotBlank()) {
            ExtendedFloatingActionButton(
                onClick = {
                    selectedText = selectedSubstring
                    onTranslateRequest(selectedSubstring)
                    isTranslationSheetVisible = true
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 32.dp),
                containerColor = BrandPeach,
                contentColor = BrandDarker,
                icon = { Icon(Icons.Default.Translate, contentDescription = null) },
                text = { Text("Translate Selection") }
            )
        }

        if (isTranslationSheetVisible) {
            ModalBottomSheet(
                onDismissRequest = { isTranslationSheetVisible = false },
                containerColor = BrandDarker,
                contentColor = BrandPeach
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = "Selection",
                        style = MaterialTheme.typography.labelLarge,
                        color = BrandPeach.copy(alpha = 0.6f)
                    )
                    Text(
                        text = selectedText,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(top = 4.dp)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "Translation",
                        style = MaterialTheme.typography.labelLarge,
                        color = BrandPeach.copy(alpha = 0.6f)
                    )
                    Text(
                        text = uiState.translationResult ?: "Translating...",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(top = 4.dp)
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = {
                                val textToCopy = uiState.translationResult
                                if (!textToCopy.isNullOrBlank()) {
                                    clipboardManager.setText(AnnotatedString(textToCopy))
                                    Toast.makeText(context, "Translation copied", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = BrandPeach, contentColor = BrandDarker),
                            enabled = uiState.translationResult != null
                        ) {
                            Text("Copy Translation")
                        }

                        OutlinedButton(
                            onClick = {
                                if (selectedText.isNotBlank()) {
                                    clipboardManager.setText(AnnotatedString(selectedText))
                                    Toast.makeText(context, "Selection copied", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier.weight(1f),
                            border = BorderStroke(1.dp, BrandPeach)
                        ) {
                            Text("Copy Selection", color = BrandPeach)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedButton(
                        onClick = {
                            val allLyrics = when (uiState.displayMode) {
                                LyricsDisplayMode.ORIGINAL -> uiState.originalLyrics
                                LyricsDisplayMode.ROMAJI -> uiState.romanizedLyrics
                                LyricsDisplayMode.FURIGANA -> uiState.originalLyrics
                            }
                            clipboardManager.setText(AnnotatedString(allLyrics ?: ""))
                            Toast.makeText(context, "All lyrics copied", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        border = BorderStroke(1.dp, BrandPeach)
                    ) {
                        Text("Copy All Lyrics", color = BrandPeach)
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.Q)
@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    RomajiJPTheme {
        LyricsScreenContent(
            song = Song(0, "Title", "Artist", "Album", null, null, 0),
            uiState = LyricsUiState(
                isLoading = false,
                originalLyrics = "こんにちは\n世界",
                romanizedLyrics = "konnichiwa\nsekai",
                isJapanese = true
            ),
            onBackClick = {},
            onDownloadClick = {},
            onModeChange = {},
            onTranslateRequest = {}
        )
    }
}
