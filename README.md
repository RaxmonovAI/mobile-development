

Simple single-screen Jetpack Compose app that fetches GitHub users (`per_page = 5`), shows user details on tap, logs network requests in debug builds, and handles network/HTTP errors with retry limits and Wi-Fi settings fallback.

<details>
<summary>Click to expand demo video (Happy path, retry exhaustion -> Wi-Fi settings, recovery)</summary>

https://github.com/user-attachments/assets/

</details>

## Retrofit -> Converter -> OkHttp Call Flow

```kotlin
val okHttpClient = OkHttpClient.Builder()
    .addInterceptor(AuthInterceptor("sample-token"))
    .addInterceptor(loggingInterceptor)
    .build()

val retrofit = Retrofit.Builder()
    .baseUrl("[https://api.github.com/](https://api.github.com/)")
    .client(okHttpClient)
    .addConverterFactory(GsonConverterFactory.create())
    .build()
