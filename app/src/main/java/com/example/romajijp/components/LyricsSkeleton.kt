package com.example.romajijp.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.romajijp.activity.ui.theme.BrandDark
import com.example.romajijp.activity.ui.theme.RomajiJPTheme

@Composable
fun LyricsSkeleton(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Skeleton Art
        Box(
            modifier = Modifier
                .size(200.dp)
                .background(BrandDark)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Skeleton Title
        Box(
            modifier = Modifier
                .width(150.dp)
                .height(24.dp)
                .background(BrandDark)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Skeleton Artist
        Box(
            modifier = Modifier
                .width(100.dp)
                .height(18.dp)
                .background(BrandDark)
        )

        Spacer(modifier = Modifier.height(40.dp))

        // Skeleton Lyrics Lines
        repeat(5) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(16.dp)
                    .padding(bottom = 12.dp)
                    .background(BrandDark)
            )
        }
        Box(
            modifier = Modifier
                .width(200.dp)
                .height(16.dp)
                .background(BrandDark)
                .align(Alignment.Start)
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF2D0B1C)
@Composable
fun LyricsSkeletonPreview() {
    RomajiJPTheme {
        LyricsSkeleton()
    }
}