package com.lalkitab.deterministic

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.lalkitab.deterministic.data.AppDatabase
import com.lalkitab.deterministic.data.DataImporter
import com.lalkitab.deterministic.data.LalKitabRule
import com.lalkitab.deterministic.engine.EngineResult
import com.lalkitab.deterministic.engine.RuleEngine
import com.lalkitab.deterministic.ui.*
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var isLoaded by remember { mutableStateOf(false) }
                    var errorMsg by remember { mutableStateOf<String?>(null) }
                    var rules by remember { mutableStateOf<List<LalKitabRule>>(emptyList()) }
                    var engine by remember { mutableStateOf<RuleEngine?>(null) }

                    LaunchedEffect(Unit) {
                        try {
                            val db = AppDatabase.getDatabase(this@MainActivity)
                            DataImporter.importDataIfEmpty(applicationContext, db)
                            rules = db.ruleDao().getAllRules()
                            engine = RuleEngine(rules)
                            isLoaded = true
                        } catch (e: Exception) {
                            errorMsg = e.message ?: "Unknown error during initialization."
                        }
                    }

                    if (errorMsg != null) {
                        Column(
                            modifier = Modifier.fillMaxSize().padding(16.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("Initialization Error", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.error)
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(errorMsg!!, textAlign = TextAlign.Center)
                        }
                    } else if (!isLoaded || engine == null) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator()
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Loading Lal Kitab Database...")
                        }
                    } else {
                        AppNavigation(engine!!, rules)
                    }
                }
            }
        }
    }
}

@Composable
fun AppNavigation(engine: RuleEngine, allRules: List<LalKitabRule>) {
    val navController = rememberNavController()
    var currentResult by remember { mutableStateOf<EngineResult?>(null) }

    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            HomeScreen(
                onNavigateToKundali = { navController.navigate("kundali") },
                onNavigateToChat = { navController.navigate("chat") },
                onNavigateToBrowser = { navController.navigate("browser") }
            )
        }
        composable("kundali") {
            KundaliScreen(onSubmit = { input ->
                currentResult = engine.evaluate(input)
                navController.navigate("results")
            })
        }
        composable("results") {
            ResultsScreen(
                result = currentResult,
                onNavigateToAudit = { navController.navigate("audit") }
            )
        }
        composable("audit") {
            AuditScreen(result = currentResult)
        }
        composable("chat") {
            ChatScreen(engine = engine)
        }
        composable("browser") {
            SourceBrowserScreen(rules = allRules)
        }
    }
}
