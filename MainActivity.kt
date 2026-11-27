package seminar11.permissions

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import kotlinx.coroutines.launch

private enum class ScreenRoute {
    SETTINGS,
    CAMERA,
    GEOLOCATION,
    MEDIA
}

fun openAppSettings(context: Context) {
    val intent = Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.fromParts("package", context.packageName, null)
    ).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    context.startActivity(intent)
}

@Composable
fun PermissionsApp(maxRetries: Int = 3) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var currentRoute by remember { mutableStateOf(ScreenRoute.SETTINGS) }

    var cameraTriesLeft by remember { mutableIntStateOf(maxRetries) }
    var locationTriesLeft by remember { mutableIntStateOf(maxRetries) }
    var mediaTriesLeft by remember { mutableIntStateOf(maxRetries) }

    fun handleDenied(
        label: String,
        currentTries: Int,
        updateTries: (Int) -> Unit
    ) {
        val nextTries = (currentTries - 1).coerceAtLeast(0)
        updateTries(nextTries)
        scope.launch {
            snackbarHostState.showSnackbar(
                "$label permission denied, you've $nextTries amount of tries left."
            )
        }
    }

    // 1. Camera Launcher
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            cameraTriesLeft = maxRetries
            currentRoute = ScreenRoute.CAMERA
        } else {
            handleDenied("Camera", cameraTriesLeft) { cameraTriesLeft = it }
        }
    }

    // 2. Geolocation Launcher 
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        val fineGranted = result[Manifest.permission.ACCESS_FINE_LOCATION] == true
        val coarseGranted = result[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (fineGranted || coarseGranted) {
            locationTriesLeft = maxRetries
            currentRoute = ScreenRoute.GEOLOCATION
        } else {
            handleDenied("Geolocation", locationTriesLeft) { locationTriesLeft = it }
        }
    }

    // 3. Photos / Videos Launcher
    val mediaPermissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
        arrayOf(
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.READ_MEDIA_VIDEO,
            Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
        )
    } else {
        arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
    }

    val mediaPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        val anyGranted = result.values.any { it }
        if (anyGranted) {
            mediaTriesLeft = maxRetries
            currentRoute = ScreenRoute.MEDIA
        } else {
            handleDenied("Photos/Videos", mediaTriesLeft) { mediaTriesLeft = it }
        }
    }

    Scaffold(snackbarHost = { SnackbarHost(hostState = snackbarHostState) }) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            when (currentRoute) {
                ScreenRoute.SETTINGS -> {
                    SettingsPermissionsScreen(
                        cameraTriesLeft = cameraTriesLeft,
                        locationTriesLeft = locationTriesLeft,
                        mediaTriesLeft = mediaTriesLeft,
                        onCameraClick = {
                            val isGranted = ContextCompat.checkSelfPermission(
                                context,
                                Manifest.permission.CAMERA
                            ) == PackageManager.PERMISSION_GRANTED
                            when {
                                isGranted -> currentRoute = ScreenRoute.CAMERA
                                cameraTriesLeft <= 0 -> openAppSettings(context)
                                else -> cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                            }
                        },
                        onLocationClick = {
                            val fineGranted = ContextCompat.checkSelfPermission(
                                context,
                                Manifest.permission.ACCESS_FINE_LOCATION
                            ) == PackageManager.PERMISSION_GRANTED
                            val coarseGranted = ContextCompat.checkSelfPermission(
                                context,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            ) == PackageManager.PERMISSION_GRANTED
                            when {
                                fineGranted || coarseGranted -> currentRoute = ScreenRoute.GEOLOCATION
                                locationTriesLeft <= 0 -> openAppSettings(context)
                                else -> locationPermissionLauncher.launch(
                                    arrayOf(
                                        Manifest.permission.ACCESS_FINE_LOCATION,
                                        Manifest.permission.ACCESS_COARSE_LOCATION
                                    )
                                )
                            }
                        },
                        onMediaClick = {
                            val hasMediaAccess = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                ContextCompat.checkSelfPermission(
                                    context,
                                    Manifest.permission.READ_MEDIA_IMAGES
                                ) == PackageManager.PERMISSION_GRANTED ||
                                    ContextCompat.checkSelfPermission(
                                        context,
                                        Manifest.permission.READ_MEDIA_VIDEO
                                    ) == PackageManager.PERMISSION_GRANTED
                            } else {
                                ContextCompat.checkSelfPermission(
                                    context,
                                    Manifest.permission.READ_EXTERNAL_STORAGE
                                ) == PackageManager.PERMISSION_GRANTED
                            }
                            when {
                                hasMediaAccess -> currentRoute = ScreenRoute.MEDIA
                                mediaTriesLeft <= 0 -> openAppSettings(context)
                                else -> mediaPermissionLauncher.launch(mediaPermissions)
                            }
                        }
                    )
                }

                ScreenRoute.CAMERA -> {
                    OptionalCameraScreen(onBack = { currentRoute = ScreenRoute.SETTINGS })
                }

                ScreenRoute.GEOLOCATION -> {
                    OptionalGeolocationScreen(onBack = { currentRoute = ScreenRoute.SETTINGS })
                }

                ScreenRoute.MEDIA -> {
                    OptionalMediaScreen(onBack = { currentRoute = ScreenRoute.SETTINGS })
                }
            }
        }
    }
}

@Composable
private fun SettingsPermissionsScreen(
    cameraTriesLeft: Int,
    locationTriesLeft: Int,
    mediaTriesLeft: Int,
    onCameraClick: () -> Unit,
    onLocationClick: () -> Unit,
    onMediaClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Permissions Settings",
            style = MaterialTheme.typography.headlineSmall
        )

        PermissionItemCard(
            title = "Camera",
            subtitle = "Tries left before Settings: $cameraTriesLeft",
            onClick = onCameraClick
        )

        PermissionItemCard(
            title = "Geolocation",
            subtitle = "Tries left before Settings: $locationTriesLeft",
            onClick = onLocationClick
        )

        PermissionItemCard(
            title = "Photos/Videos (API 34+)",
            subtitle = "Tries left before Settings: $mediaTriesLeft",
            onClick = onMediaClick
        )
    }
}

@Composable
private fun PermissionItemCard(
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, style = MaterialTheme.typography.titleMedium)
                Text(text = subtitle, style = MaterialTheme.typography.bodySmall)
            }
            Text(text = "Manage")
        }
    }
}

@Composable
private fun OptionalCameraScreen(onBack: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Camera Screen (Granted)", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onBack) { Text("Back to Settings") }
    }
}

@Composable
private fun OptionalGeolocationScreen(onBack: () -> Unit) {
    // Sample coordinates placeholder; wire FusedLocationProviderClient for live GPS updates
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Geolocation Screen", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(8.dp))
        Text("Latitude: 41.2995 | Longitude: 69.2401")
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onBack) { Text("Back to Settings") }
    }
}

@Composable
private fun OptionalMediaScreen(onBack: () -> Unit) {
    var selectedUri by remember { mutableStateOf<Uri?>(null) }

    val photoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        selectedUri = uri
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Photos / Videos Picker", style = MaterialTheme.typography.titleLarge)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = {
                    photoPicker.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                }
            ) {
                Text("Pick Photo")
            }
            Button(
                onClick = {
                    photoPicker.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly)
                    )
                }
            ) {
                Text("Pick Video")
            }
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = selectedUri?.toString() ?: "No photo or video selected yet"
                )
            }
        }

        Button(onClick = onBack) { Text("Back to Settings") }
    }
}
