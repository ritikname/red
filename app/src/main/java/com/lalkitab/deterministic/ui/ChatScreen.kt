package com.lalkitab.deterministic.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.lalkitab.deterministic.engine.QueryParser
import com.lalkitab.deterministic.engine.RuleEngine

data class ChatMessage(val sender: String, val content: String)

@Composable
fun ChatScreen(engine: RuleEngine) {
    var query by remember { mutableStateOf("") }
    val messages = remember { mutableStateListOf<ChatMessage>() }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Ask Lal Kitab (Offline)", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(messages) { msg ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (msg.sender == "User") MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Text(
                        text = "${msg.sender}: ${msg.content}",
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        Row(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("e.g. What happens if Mars is in House 8?") }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = {
                    if (query.isNotBlank()) {
                        messages.add(ChatMessage("User", query))
                        val input = QueryParser.parse(query)
                        val results = engine.evaluate(input)
                        val responseText = if (results.finalPredictions.isEmpty()) {
                            "Not established by the available Lal Kitab source."
                        } else {
                            results.finalPredictions.joinToString("\n")
                        }
                        messages.add(ChatMessage("System", responseText))
                        query = ""
                    }
                }
            ) {
                Text("Send")
            }
        }
    }
}
