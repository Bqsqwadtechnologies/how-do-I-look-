package com.bqsqwad.howdoilook.feature.history

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.bqsqwad.howdoilook.core.domain.model.LookRating

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RatingDetailScreen(
    onBack: () -> Unit,
    viewModel: RatingDetailViewModel
) {
    val rating by viewModel.rating.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Rating details") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        val item = rating
        if (item == null) {
            Column(
                modifier = Modifier.fillMaxSize().padding(padding).padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("This saved result is unavailable.", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))
                Text("It may have been deleted from your history.", textAlign = TextAlign.Center)
            }
        } else {
            RatingDetailContent(item, padding)
        }
    }
}

@Composable
private fun RatingDetailContent(rating: LookRating, padding: PaddingValues) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(padding),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            rating.photoUri?.let { uri ->
                AsyncImage(
                    model = uri,
                    contentDescription = "Saved photo",
                    modifier = Modifier.fillMaxWidth().height(280.dp)
                )
            }
        }
        item {
            Text(rating.mode.displayName, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text(
                "Photo quality score: ${rating.overallRounded}/10",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
        item { Text("Photo checks", style = MaterialTheme.typography.titleMedium) }
        items(rating.subScores.size) { index ->
            val entry = rating.subScores.entries.elementAt(index)
            Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                Text(entry.key, style = MaterialTheme.typography.bodyMedium)
                LinearProgressIndicator(
                    progress = { (entry.value / 10f).coerceIn(0f, 1f) },
                    modifier = Modifier.fillMaxWidth()
                )
                Text("${"%.1f".format(entry.value)}/10", style = MaterialTheme.typography.bodySmall)
            }
        }
        item { Text("Helpful photo tips", style = MaterialTheme.typography.titleMedium) }
        items(rating.tips.size) { index ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Text(rating.tips[index], modifier = Modifier.padding(14.dp))
            }
        }
        item {
            Text(
                "These checks describe the photo only. They do not measure your appearance or personal worth.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
