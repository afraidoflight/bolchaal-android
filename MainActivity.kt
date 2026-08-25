package com.bolchaal.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {

    private val viewModel: TranslatorViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    BolChaalScreen(viewModel)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BolChaalScreen(vm: TranslatorViewModel) {
    var input by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().padding(12.dp)) {
        Text("BolChaal", style = MaterialTheme.typography.headlineSmall)
        Text("casual roman urdu, on tap", style = MaterialTheme.typography.labelSmall)

        Spacer(Modifier.height(8.dp))

        // Engine picker
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            EngineType.values().forEach { type ->
                FilterChip(
                    selected = vm.engineType.value == type,
                    onClick = { vm.engineType.value = type },
                    label = { Text(type.label) }
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(vm.messages, key = { it.id }) { msg ->
                Column(horizontalAlignment = Alignment.End, modifier = Modifier.fillMaxWidth()) {
                    Surface(color = MaterialTheme.colorScheme.primaryContainer, shape = MaterialTheme.shapes.medium) {
                        Text(msg.english, modifier = Modifier.padding(10.dp))
                    }
                    Spacer(Modifier.height(4.dp))
                }
                Row(modifier = Modifier.fillMaxWidth()) {
                    Surface(color = MaterialTheme.colorScheme.surfaceVariant, shape = MaterialTheme.shapes.medium) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(msg.urdu)
                            if (msg.approx) {
                                Text("approx. spelling", style = MaterialTheme.typography.labelSmall)
                            }
                            Row {
                                TextButton(onClick = { vm.toggleFavorite(msg.id) }) {
                                    Text(if (msg.favorite) "★" else "☆")
                                }
                                TextButton(onClick = { vm.remove(msg.id) }) { Text("✕") }
                            }
                        }
                    }
                }
            }
        }

        vm.error.value?.let {
            Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        }

        Spacer(Modifier.height(4.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = input,
                onValueChange = { input = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("type something casual...") }
            )
            Spacer(Modifier.width(8.dp))
            Button(
                enabled = !vm.loading.value,
                onClick = {
                    vm.send(input)
                    input = ""
                }
            ) { Text("Send") }
        }
    }
}
