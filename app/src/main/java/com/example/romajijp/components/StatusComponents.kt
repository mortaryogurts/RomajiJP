package com.example.romajijp.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.romajijp.R
import com.example.romajijp.activity.ui.theme.BrandPeach
import com.example.romajijp.activity.ui.theme.RomajiJPTheme

@Composable
fun WelcomeState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = painterResource(id = android.R.drawable.ic_menu_search),
            contentDescription = null,
            modifier = Modifier
                .size(120.dp)
                .alpha(0.3f)
                .padding(bottom = 16.dp),
            tint = BrandPeach
        )

        Text(
            text = stringResource(id = R.string.welcome_to_nromaji),
            color = BrandPeach,
            fontSize = 40.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            lineHeight = 48.sp
        )
    }
}

@Composable
fun EmptyState(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "No results found.",
            color = BrandPeach,
            fontSize = 18.sp
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF2D0B1C)
@Composable
fun WelcomeStatePreview() {
    RomajiJPTheme {
        WelcomeState()
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF2D0B1C)
@Composable
fun EmptyStatePreview() {
    RomajiJPTheme {
        EmptyState()
    }
}