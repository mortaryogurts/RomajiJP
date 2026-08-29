package com.example.romajijp.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.romajijp.activity.ui.theme.BrandDarker
import com.example.romajijp.activity.ui.theme.BrandPeach
import com.example.romajijp.activity.ui.theme.RomajiJPTheme
import com.example.romajijp.uistate.LyricsDisplayMode

@Composable
fun LyricsSelectionButtons(
    currentMode: LyricsDisplayMode,
    onModeChange: (LyricsDisplayMode) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        LyricsModeButton(
            text = "Kanji",
            isSelected = currentMode == LyricsDisplayMode.ORIGINAL,
            onClick = { onModeChange(LyricsDisplayMode.ORIGINAL) }
        )

        LyricsModeButton(
            text = "Romaji",
            isSelected = currentMode == LyricsDisplayMode.ROMAJI,
            onClick = { onModeChange(LyricsDisplayMode.ROMAJI) }
        )

        LyricsModeButton(
            text = "Furigana",
            isSelected = currentMode == LyricsDisplayMode.FURIGANA,
            onClick = { onModeChange(LyricsDisplayMode.FURIGANA) }
        )
    }
}

@Composable
private fun LyricsModeButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    if (isSelected) {
        Button(
            onClick = onClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = BrandPeach,
                contentColor = BrandDarker
            ),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.padding(horizontal = 4.dp)
        ) {
            Text(text)
        }
    } else {
        OutlinedButton(
            onClick = onClick,
            border = BorderStroke(1.dp, BrandPeach),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.padding(horizontal = 4.dp)
        ) {
            Text(text, color = BrandPeach)
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF2D0B1C)
@Composable
fun LyricsSelectionButtonsPreview() {
    RomajiJPTheme {
        LyricsSelectionButtons(
            currentMode = LyricsDisplayMode.ROMAJI,
            onModeChange = {}
        )
    }
}