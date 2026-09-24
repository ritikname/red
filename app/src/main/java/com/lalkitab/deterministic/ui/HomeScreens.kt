package com.lalkitab.deterministic.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun HomeScreen(
    onNavigateToKundali: () -> Unit,
    onNavigateToChat: () -> Unit,
    onNavigateToBrowser: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Lal Kitab Engine", style = MaterialTheme.typography.headlineLarge)
        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = onNavigateToKundali, modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
            Text("Create / Edit Kundali")
        }

        Button(onClick = onNavigateToChat, modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
            Text("Ask Lal Kitab (Offline Chat)")
        }

        Button(onClick = onNavigateToBrowser, modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
            Text("Source Browser (Rule Corpus)")
        }
    }
}
