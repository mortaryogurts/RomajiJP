package com.example.romajijp.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.romajijp.uistate.LyricsToken

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FuriganaText(
    tokens: List<LyricsToken>,
    modifier: Modifier = Modifier,
    baseColor: Color = Color.White,
    furiganaColor: Color = baseColor.copy(alpha = 0.7f),
    baseFontSize: TextUnit = 20.sp,
    furiganaFontSize: TextUnit = 10.sp
) {
    val lines = remember(tokens) {
        val result = mutableListOf<List<LyricsToken>>()
        var currentLine = mutableListOf<LyricsToken>()

        tokens.forEach { token ->
            if (token.surface.contains("\n")) {
                val parts = token.surface.split("\n")
                parts.forEachIndexed { index, part ->
                    if (part.isNotEmpty()) {
                        currentLine.add(token.copy(surface = part))
                    }
                    if (index < parts.size - 1) {
                        result.add(currentLine.toList())
                        currentLine = mutableListOf()
                    }
                }
            } else {
                currentLine.add(token)
            }
        }
        if (currentLine.isNotEmpty()) result.add(currentLine.toList())
        result
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        lines.forEach { line ->
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalArrangement = Arrangement.Center
            ) {
                line.forEach { token ->
                    FuriganaToken(
                        token = token,
                        baseColor = baseColor,
                        furiganaColor = furiganaColor,
                        baseFontSize = baseFontSize,
                        furiganaFontSize = furiganaFontSize
                    )
                }
            }
        }
    }
}

@Composable
private fun FuriganaToken(
    token: LyricsToken,
    baseColor: Color,
    furiganaColor: Color,
    baseFontSize: TextUnit,
    furiganaFontSize: TextUnit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = token.reading ?: "",
            color = furiganaColor,
            fontSize = furiganaFontSize,
            lineHeight = furiganaFontSize,
            maxLines = 1
        )
        Text(
            text = token.surface,
            color = baseColor,
            fontSize = baseFontSize,
            lineHeight = baseFontSize,
            modifier = Modifier.padding(horizontal = 1.dp)
        )
    }
}
