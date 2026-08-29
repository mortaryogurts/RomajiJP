package com.example.romajijp.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.romajijp.activity.ui.theme.BrandDark
import com.example.romajijp.activity.ui.theme.BrandPeach
import com.example.romajijp.activity.ui.theme.RomajiJPTheme

@Composable
fun RecentlySearchItemsSheet(
    queries: List<String>,
    onQueryClick: (String) -> Unit,
    onDeleteClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp),
        colors = CardDefaults.cardColors(containerColor = BrandPeach),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 300.dp)
                .padding(horizontal = 8.dp),
            contentPadding = PaddingValues(top = 28.dp, bottom = 8.dp)
        ) {
            item {
                Text(
                    text = "Recent searches",
                    color = BrandDark.copy(alpha = 0.7f),
                    fontSize = 12.sp,
                    modifier = Modifier.padding(start = 12.dp, bottom = 4.dp)
                )
            }

            items(queries) { query ->
                RecentlySearchedItems(
                    query = query,
                    onDeleteClick = { onDeleteClick(query) },
                    modifier = Modifier.clickable { onQueryClick(query) }
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF2D0B1C)
@Composable
fun RecentlySearchItemsSheetPreview() {
    RomajiJPTheme {
        RecentlySearchItemsSheet(
            queries = listOf("Yoasobi", "LiSA", "Kenshi Yonezu", "Aimer"),
            onQueryClick = {},
            onDeleteClick = {}
        )
    }
}
