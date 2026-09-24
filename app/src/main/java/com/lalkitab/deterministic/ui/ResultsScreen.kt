package com.lalkitab.deterministic.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.lalkitab.deterministic.engine.EngineResult

@Composable
fun ResultsScreen(result: EngineResult?, onNavigateToAudit: () -> Unit) {
    if (result == null) {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Text("No results available. Please submit a Kundali first.")
        }
        return
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Astrological Results", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        if (result.finalPredictions.isEmpty()) {
            Text("Not established by the available Lal Kitab source.")
        } else {
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(result.finalPredictions) { prediction ->
                    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                        Text(
                            text = prediction,
                            modifier = Modifier.padding(16.dp),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onNavigateToAudit, modifier = Modifier.fillMaxWidth()) {
            Text("View Rule Audit")
        }
    }
}
