

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun GitHubUsersScreen(
    viewModel: GitHubUsersViewModel = viewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val selectedUser by viewModel.selectedUser.collectAsState()
    val retryCount by viewModel.retryCount.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.load(perPage = 5)
    }

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(onClick = { viewModel.load(perPage = 5) }) {
                    Text("Reload (5)")
                }
                OutlinedButton(onClick = { viewModel.testNotFoundError() }) {
                    Text("Test 404")
                }
            }

            when (val state = uiState) {
                is UiState.Loading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                is UiState.Success -> {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(items = state.users, key = { it.id }) { user ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { viewModel.selectUser(user.login) }
                            ) {
                                Text(
                                    text = user.login,
                                    modifier = Modifier.padding(16.dp),
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }
                        }
                    }

                    selectedUser?.let { details ->
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "Selected: ${details.login}",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("id: ${details.id}")
                                Text("avatarUrl: ${details.avatarUrl}")
                            }
                        }
                    }
                }

                is UiState.Error -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = state.message, style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(12.dp))
                        if (retryCount < 3) {
                            Button(onClick = { viewModel.retry(perPage = 5) }) {
                                Text("Retry ($retryCount/3)")
                            }
                        } else {
                            Button(onClick = { openWifiSettings(context) }) {
                                Text("Open Wi-Fi settings")
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun openWifiSettings(context: Context) {
    val intent = Intent(Settings.ACTION_WIFI_SETTINGS).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    context.startActivity(intent)
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                GitHubUsersScreen()
            }
        }
    }
}
