package com.lalkitab.deterministic.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.lalkitab.deterministic.engine.EngineResult

@Composable
fun AuditScreen(result: EngineResult?) {
    if (result == null) {
        Text("No audit data available.", modifier = Modifier.padding(16.dp))
        return
    }

    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        item {
            Text("Rule Audit Log", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(16.dp))
            Text("Matched Rules", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
        }

        items(result.matchedRules) { matched ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Rule ID: ${matched.rule.rule_id}", fontWeight = FontWeight.Bold)
                    Text("Edition: ${matched.rule.edition} | Page: ${matched.rule.page}")
                    Text("Evidence Level: ${matched.evidenceLevel}", color = Color.Blue)
                    Text("Explanation: ${matched.explanation}")
                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                    Text("Source Text:", fontWeight = FontWeight.Bold)
                    Text(matched.rule.source_text)
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
            Text("Rejected Rules", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
        }

        items(result.rejectedRules) { rejected ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Rule ID: ${rejected.rule.rule_id}", fontWeight = FontWeight.Bold)
                    Text("Reason Rejected: ${rejected.reason}", color = Color.Red)
                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                    Text("Source Text:", fontWeight = FontWeight.Bold)
                    Text(rejected.rule.source_text)
                }
            }
        }
    }
}
