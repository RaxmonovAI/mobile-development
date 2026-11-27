

## Permission Handling Code & Explanation

```kotlin
when {
    isGranted -> currentRoute = ScreenRoute.CAMERA
    cameraTriesLeft <= 0 -> openAppSettings(context)
    else -> cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
}
