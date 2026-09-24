package com.lalkitab.deterministic.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.lalkitab.deterministic.data.LalKitabRule

@Composable
fun SourceBrowserScreen(rules: List<LalKitabRule>) {
    var searchQuery by remember { mutableStateOf("") }

    val filteredRules = if (searchQuery.isEmpty()) {
        rules
    } else {
        rules.filter {
            it.source_text.contains(searchQuery, ignoreCase = true) ||
            it.planets?.contains(searchQuery, ignoreCase = true) == true ||
            it.houses?.contains(searchQuery, ignoreCase = true) == true ||
            it.rule_id.contains(searchQuery, ignoreCase = true)
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Source Browser", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Search Rules (Planet, House, Keyword)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(filteredRules) { rule ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Rule ID: ${rule.rule_id}", fontWeight = FontWeight.Bold)
                        Text("Edition: ${rule.edition} | Page: ${rule.page} | Type: ${rule.rule_type ?: "N/A"}")
                        Text("Planets: ${rule.planets ?: "N/A"} | Houses: ${rule.houses ?: "N/A"}")
                        Divider(modifier = Modifier.padding(vertical = 8.dp))
                        Text(rule.source_text, style = MaterialTheme.typography.bodyMedium)

                        if (rule.exception_to != null) {
                            Text("Exception To: ${rule.exception_to}", color = MaterialTheme.colorScheme.error)
                        }
                        if (rule.contradiction != null) {
                            Text("Contradiction: ${rule.contradiction}", color = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }
        }
    }
}
