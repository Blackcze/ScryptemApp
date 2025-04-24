package com.example.scryptem.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.scryptem.presentation.settings.SettingsViewModel

@Composable
fun SettingsScreen(viewModel: SettingsViewModel = hiltViewModel()) {
    val theme by viewModel.theme.collectAsState()
    val currency by viewModel.currency.collectAsState()
    val defaultScreen by viewModel.defaultScreen.collectAsState()
    val refreshInterval by viewModel.refreshInterval.collectAsState()
    val notificationsEnabled by viewModel.notificationsEnabled.collectAsState()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text("Nastavení", style = MaterialTheme.typography.titleLarge)

            // Motiv
            Text("Motiv aplikace")
            DropdownSelector(
                options = listOf("system", "light", "dark"),
                selected = theme,
                onSelectedChange = { viewModel.setTheme(it) }
            )

            // Měna
            Text("Výchozí měna")
            DropdownSelector(
                options = listOf("USD", "EUR", "CZK"),
                selected = currency,
                onSelectedChange = { viewModel.setCurrency(it) }
            )

            // Výchozí obrazovka
            Text("Výchozí obrazovka")
            DropdownSelector(
                options = listOf("List of all", "favorites"),
                selected = defaultScreen,
                onSelectedChange = { viewModel.setDefaultScreen(it) }
            )

            // Interval obnovy dat
            Text("Automatické obnovení dat")
            DropdownSelector(
                options = listOf("5 min", "10 min", "15 min", "30 min", "1 h", "1 day", "1 week"),
                selected = refreshInterval,
                onSelectedChange = { viewModel.setRefreshInterval(it) }
            )

            // Notifikace
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Notifikace")
                Switch(checked = notificationsEnabled, onCheckedChange = {
                    viewModel.setNotifications(it)
                })
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
