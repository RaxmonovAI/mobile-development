

<details>
<summary>Click to expand demo video (Camera, Geolocation, Photos/Videos)</summary>


https://github.com/TTPU-ORG/YOUR-REPO/assets/VIDEO_ID

</details>

## Permission Handling Code & Explanation

```kotlin
when {
    isGranted -> currentRoute = ScreenRoute.CAMERA
    cameraTriesLeft <= 0 -> openAppSettings(context)
    else -> cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
}
