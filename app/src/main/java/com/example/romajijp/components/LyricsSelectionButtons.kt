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

@Composable
fun LyricsSelectionButtons(
    isRomajiSelected: Boolean,
    onOriginalClick: () -> Unit,
    onRomajiClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (!isRomajiSelected) {
            Button(
                onClick = onOriginalClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = BrandPeach,
                    contentColor = BrandDarker
                ),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.padding(horizontal = 8.dp)
            ) {
                Text("Original")
            }
        } else {
            OutlinedButton(
                onClick = onOriginalClick,
                border = BorderStroke(1.dp, BrandPeach),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.padding(horizontal = 8.dp)
            ) {
                Text("Original", color = BrandPeach)
            }
        }

        if (isRomajiSelected) {
            Button(
                onClick = onRomajiClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = BrandPeach,
                    contentColor = BrandDarker
                ),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.padding(horizontal = 8.dp)
            ) {
                Text("Romaji")
            }
        } else {
            OutlinedButton(
                onClick = onRomajiClick,
                border = BorderStroke(1.dp, BrandPeach),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.padding(horizontal = 8.dp)
            ) {
                Text("Romaji", color = BrandPeach)
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF2D0B1C)
@Composable
fun LyricsSelectionButtonsPreview() {
    RomajiJPTheme {
        LyricsSelectionButtons(
            isRomajiSelected = true,
            onOriginalClick = {},
            onRomajiClick = {}
        )
    }
}