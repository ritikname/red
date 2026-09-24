package com.lalkitab.deterministic.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.lalkitab.deterministic.engine.KundaliInput

@Composable
fun KundaliScreen(onSubmit: (KundaliInput) -> Unit) {
    val planets = listOf("Sun", "Moon", "Mars", "Mercury", "Jupiter", "Venus", "Saturn", "Rahu", "Ketu")
    val planetsInHouses = remember { mutableStateMapOf<String, String>() }
    var contextType by remember { mutableStateOf("Janam Kundali") }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Create Kundali", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        Row {
            RadioButton(selected = contextType == "Janam Kundali", onClick = { contextType = "Janam Kundali" })
            Text("Janam Kundali", modifier = Modifier.padding(top = 12.dp, end = 16.dp))
            RadioButton(selected = contextType == "Varshfal", onClick = { contextType = "Varshfal" })
            Text("Varshfal", modifier = Modifier.padding(top = 12.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))

        planets.forEach { planet ->
            var house by remember { mutableStateOf("") }
            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                Text(planet, modifier = Modifier.weight(1f).padding(top = 12.dp))
                OutlinedTextField(
                    value = house,
                    onValueChange = {
                        house = it
                        if (it.isNotBlank()) planetsInHouses[planet] = it
                        else planetsInHouses.remove(planet)
                    },
                    modifier = Modifier.weight(2f),
                    label = { Text("House (1-12)") }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                onSubmit(KundaliInput(planetsInHouses.toMap(), type = contextType))
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Calculate Results")
        }
    }
}
