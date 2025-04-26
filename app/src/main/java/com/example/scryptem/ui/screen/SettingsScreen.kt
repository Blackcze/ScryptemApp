package com.example.scryptem.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.scryptem.presentation.settings.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val theme by viewModel.theme.collectAsState()
    val currency by viewModel.currency.collectAsState()
    val defaultScreen by viewModel.defaultScreen.collectAsState()
    val refreshInterval by viewModel.refreshInterval.collectAsState()
    val notificationsEnabled by viewModel.notificationsEnabled.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nastavení") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigate("coin_list") }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Zpět")
                    }
                }
            )
        }
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) { Text("Motiv aplikace")
                DropdownSelector(
                    options = listOf("system", "light", "dark"),
                    selected = theme,
                    onSelectedChange = { viewModel.setTheme(it) }
                )

                Text("Výchozí měna")
                DropdownSelector(
                    options = listOf(
                        "USD", "EUR", "CZK", "GBP", "AUD", "CAD", "CHF", "JPY", "CNY", "SEK",
                        "NZD", "NOK", "SGD", "HKD", "PLN", "MXN", "INR", "BRL", "ZAR", "TRY", "DKK"
                    ),
                    selected = currency,
                    onSelectedChange = { viewModel.setCurrency(it) }
                )

                Text("Výchozí obrazovka")
                DropdownSelector(
                    options = listOf("List of all", "favorites"),
                    selected = defaultScreen,
                    onSelectedChange = { viewModel.setDefaultScreen(it) }
                )

                Text("Automatické obnovení dat")
                DropdownSelector(
                    options = listOf("5 min", "10 min", "15 min", "30 min", "1 h", "1 day", "1 week"),
                    selected = refreshInterval,
                    onSelectedChange = { viewModel.setRefreshInterval(it) }
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Notifikace")
                    Switch(
                        checked = notificationsEnabled,
                        onCheckedChange = { viewModel.setNotifications(it) }
                    )
                }
            }
        }
    }
}

@Composable
fun DropdownSelector(
    options: List<String>,
    selected: String,
    onSelectedChange: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        OutlinedButton(onClick = { expanded = true }) {
            Text(selected)
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onSelectedChange(option)
                        expanded = false
                    }
                )
            }
        }
    }
}
