---

### 2. `NOTES.md`

```markdown
# Seminar 15 — NOTES.md

## Q01 — HardCar vs Car with Engine Interface
- `HardCar` instantiates `PetrolEngine()` internally, so tests cannot swap in a deterministic fake or easily isolate failures without changing production code[cite: 4].
- `Car(engine: Engine)` receives its dependency via constructor injection, allowing `FakeEngine` to be passed in unit tests to verify behavior cleanly[cite: 4].

## Q02 — Construct / Grab / Supplied Classification
- `(a)` `val db = Room.databaseBuilder(...).build()` inside a class -> **construct**: creates the concrete object directly inside the class[cite: 4].
- `(b)` `val api = ApiInstance.service` inside a class -> **grab**: pulls a dependency from a global/static singleton registry[cite: 4].
- `(c)` `class Repo(private val api: Api)` -> **supplied**: receives its dependency through a constructor parameter[cite: 4].
- `(d)` `val ctx = LocalContext.current` in a composable -> **grab**: reads ambient context from the current CompositionLocal[cite: 4].
- `(e)` `val vm: NoteViewModel by viewModels()` with a custom factory -> **supplied**: delegated/created through a factory rather than constructed inline by the caller[cite: 4].
- `(f)` `@Inject lateinit var logger: Logger` -> **supplied**: injected externally by the DI framework[cite: 4].

## Q03 — Manual AppContainer & Creation Counts
- `AppContainer` holds shared dependencies (`OkHttpClient`, `Retrofit`, DAO, repository) exposed from `Application`[cite: 4].
- Heavy/shared objects like `OkHttpClient`, `Retrofit`, and database/repository instances should be created once rather than recreated on every screen visit[cite: 4].

## Q04 — `manual-di` Branch Refactoring
- Rewired Notes app dependencies through constructors and `AppContainer` without `ApiInstance.service` or `DatabaseProvider.get(context)` calls inside consumers[cite: 4].
- Record exact `git diff --stat` touched file count and added line count after committing on `manual-di`[cite: 4].

## Q05 — Dependency Graph & Manual DI Pains
- Graph flow: `Screen` -> `ViewModel` -> `Repository` -> `DAO` / `API` -> `OkHttpClient` / `Database` -> `Context`[cite: 4].
- Manual DI pains: factory boilerplate, manual scope/lifecycle wiring, and passing dependencies through intermediate layers[cite: 4].
- Hilt removes repetitive factory/container wiring boilerplate while moving scope and lifecycle decisions into component/scope annotations[cite: 4].

## Q06 — Hilt Gradle Setup (`2.57.1`)
- Plugin applied without `hilt-compiler` or compiler present without the Hilt Gradle plugin fails compilation at build time[cite: 4].

## Q07 — `@HiltAndroidApp` & `Hilt_App`
- Missing `android:name=".App"` in `AndroidManifest.xml` causes a runtime `ClassCastException` (`Application` cannot be cast to `App_GeneratedInjector`)[cite: 4].
- Missing `@HiltAndroidApp` fails compilation because Hilt's generated application base (`Hilt_App`) and component root are not generated[cite: 4].

## Q08 — `@AndroidEntryPoint` & Field Injection
- Making a `@Inject lateinit var` field `private` fails compilation because generated members cannot access private fields directly[cite: 4].
- Omitting `@AndroidEntryPoint` leaves `lateinit var logger` uninitialized and throws `UninitializedPropertyAccessException` on use[cite: 4].
- Android framework classes (`Activity`, `Fragment`, `Service`, `BroadcastReceiver`) are instantiated by the OS, so field injection is required, whereas constructor injection is preferred elsewhere so dependencies are explicit and testable[cite: 4].

## Q09 — Constructor Injection Order (`C, B, A`)
- Because `A` depends on `B` and `B` depends on `C`, Hilt constructs dependencies bottom-up (`C`, then `B`, then `A`)[cite: 4].
- Without a scope annotation, two `@Inject` fields of type `A` produce two distinct instances[cite: 4].

## Q10 — Reading Dagger Build Errors
- Dagger errors report the missing/unscoped/cyclic binding type, the entry point/request site where injection started, and the dependency chain (`[... -> ...]`) leading there[cite: 4].
- Missing binding for an interface/third-party type is fixed with `@Binds` or `@Provides`; a dependency cycle is fixed by breaking the cycle (e.g., `Provider<T>` or refactoring responsibilities)[cite: 4].

## Q11 — `@Binds` Interface Switching
- Changing `bindLogger(impl: LogcatLogger): Logger` to `bindLogger(impl: FileLogger): Logger` updates all `Logger` consumers without editing call sites[cite: 4].

## Q12 — `@Provides @Singleton` NetworkModule
- Providing `OkHttpClient`, `Retrofit`, and `GitHubService` as `@Singleton` in `NetworkModule` builds them once and reuses them across screens[cite: 4].

## Q13 — `@ApplicationContext`
- Removing `@ApplicationContext` from `fun database(@ApplicationContext context: Context)` causes a Dagger duplicate/ambiguous `Context` binding error[cite: 4].
- Plain `Context` is ambiguous because Android has application context and activity contexts with different lifecycles[cite: 4].

## Q14 — `@Named` vs Custom `@Qualifier`
- Dropping `@ApiClient` or `@DownloadClient` on an injection site causes a missing/ambiguous binding error for `OkHttpClient`[cite: 4].
- Custom qualifiers beat string `@Named` keys because they give compile-time type/typo safety and refactorability instead of error-prone string literals[cite: 4].

## Q15 — `@BaseUrl` and `@IoDispatcher`
- Injecting `@BaseUrl String` and `@IoDispatcher CoroutineDispatcher` allows tests to supply a test URL/dispatcher (such as `StandardTestDispatcher`) without Android framework dependencies[cite: 4].

## Q16 — Scope Matrix (`4x4` Prediction vs Actual)
| Binding Scope | Activity 1 | Activity 2 | ViewModel | After Rotation |
| :--- | :--- | :--- | :--- | :--- |
| Unscoped (no annotation) | New instance | New instance | New instance | New instance |
| `@Singleton` | Same singleton | Same singleton | Same singleton | Same singleton |
| `@ActivityRetainedScoped` | Activity 1 instance | Activity 2 instance | Retained across config change | Same retained instance |
| `@ViewModelScoped` | Not directly in Activity | Not directly in Activity | Scoped to ViewModel | Same ViewModel instance |
- Mark any cell that differed from your initial prediction after running the app[cite: 4].

## Q17 — Component Hierarchy & Scope Rules
- Hierarchy: `SingletonComponent` -> `ActivityRetainedComponent` -> `ViewModelComponent` and `ActivityComponent` -> `FragmentComponent`[cite: 4].
- `@Singleton` needing `@ActivityScoped`: **illegal** (parent component cannot depend on shorter-lived child component)[cite: 4].
- `@ActivityScoped` needing `@Singleton`: **legal** (child component can access parent-scoped bindings)[cite: 4].
- `@ViewModelScoped` injected into an `Activity`: **illegal** (`ViewModelComponent` is not a parent of `ActivityComponent`)[cite: 4].
- Across configuration change: `SingletonComponent` and `ActivityRetainedComponent` (and `ViewModelComponent`) survive; `ActivityComponent` and `FragmentComponent` are recreated[cite: 4].

## Q18 — Illegal Hierarchy Errors & Leaked Activity Prevention
- `@Singleton` depending on `@ActivityScoped` or `@ActivityContext` fails at compile time[cite: 4].
- This prevents retaining a short-lived `Activity` (and its entire view/context tree) inside an app-wide singleton, avoiding memory leaks and crashes after `Activity` destruction[cite: 4].

## Q19 — Multi-Module Hilt (`:app`, `:feature-notes`, `:data`)
- `:app` holds `@HiltAndroidApp`, `:feature-notes` holds UI/ViewModels, and `:data` holds Room/modules[cite: 4].
- Removing `:data` from `:app` causes compilation failure because `:app`'s generated Hilt component cannot see `:data`'s `@InstallIn` modules/bindings[cite: 4].

## Q20 — Choosing Scopes Thoughtfully
- `UserSession` (one per app launch): `@Singleton` (thread-safe state across app lifetime)[cite: 4].
- `NoteListState` (one per screen visit): `@ViewModelScoped` or screen/ViewModel state (tied to screen/ViewModel lifetime)[cite: 4].
- `HttpClient` (expensive to build): `@Singleton` (thread-safe client reused across app)[cite: 4].
- `DateFormatter` (stateless, cheap): unscoped or `@Singleton` if thread-safe (e.g. `DateTimeFormatter`), avoiding mutable non-thread-safe formatters[cite: 4].
- `AuthToken` (changes at login): `@Singleton` with thread-safe state holder (`StateFlow`/mutex) or session scope rather than frozen immutable value[cite: 4].
- Making everything `@Singleton` is not a design decision because it ignores lifecycle boundaries, memory retention, and mutable shared state risks[cite: 4].

## Q21 — `hilt` vs `manual-di` Line Counts
- Compare `git diff --stat` line counts between `manual-di` and `hilt` after removing `AppContainer`, manual factories, `ApiInstance`, and `DatabaseProvider`[cite: 4].

## Q22 — `SavedStateHandle` & Process Recreation
- `NoteDetailViewModel` reads `noteId` from `SavedStateHandle`, preserving route arguments across *Don't keep activities* process recreation and unit tests[cite: 4].

## Q23 — Real vs Fake Repository Switch
- `@Binds` binds `RoomNoteRepository` for `NoteRepository`, while a debug/variant switch can supply `FakeNoteRepository` with sample notes[cite: 4].

## Q24 — `BroadcastReceiver` & `super.onReceive(...)`
- Calling `super.onReceive(context, intent)` first triggers Hilt field injection before `onReceive` body runs[cite: 4].
- Omitting `super.onReceive(context, intent)` leaves injected fields uninitialized (`UninitializedPropertyAccessException`)[cite: 4].

## Q25 — Shared `DraftViewModel` on Nested Navigation Graph
- `hiltViewModel(navController.getBackStackEntry("edit"))` shares one `DraftViewModel` between `EditTitle` and `EditBody` and clears it (`onCleared()`) when leaving the `"edit"` graph[cite: 4].

## Q26 — Testing with Hilt vs Plain Unit Tests
- `@TestInstallIn(components = [SingletonComponent::class], replaces = [NetworkModule::class])` swaps a production module with a test module[cite: 4].
- `@HiltAndroidTest`, `HiltAndroidRule`, and a custom `AndroidJUnitRunner` returning `HiltTestApplication` set up Hilt components and field injection in instrumented tests[cite: 4].
- Plain constructor-injected classes tested on the JVM do not need Hilt test rules or an Android runner[cite: 4].

## Q27 — Fast JVM Unit Tests (`./gradlew test`)
- Repositories and ViewModels with constructor-injected fakes run on the JVM with `runTest` and `StandardTestDispatcher` without an emulator[cite: 4].

## Q28 — Instrumented Test Setup Failures
- Missing custom runner (`HiltTestRunner`), missing `@get:Rule val hiltRule = HiltAndroidRule(this)`, or missing `hiltRule.inject()` each cause distinct test setup/injection failures[cite: 4].

## Q29 — `TestDatabaseModule` & Compose UI Test
- Replacing `DatabaseModule` with an in-memory database ensures each instrumented Compose test starts with a clean empty state[cite: 4].

## Q30 — Capstone App Summary
- Integrates GitHub users offline-first cache (`MockWebServer` + Room), notes feature, Hilt modules/qualifiers, `SavedStateHandle`, JVM & instrumented tests, and `README.md` diagram[cite: 4].
```[cite: 4]

---

### 3. `app/build.gradle.kts` (`2.57.1` + KSP)

```kotlin
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "seminar15.hilt"
    compileSdk = 35

    defaultConfig {
        applicationId = "seminar15.hilt"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "seminar15.hilt.q28.HiltTestRunner"
    }

    buildFeatures {
        compose = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    val hiltVersion = "2.57.1"
    implementation("com.google.dagger:hilt-android:$hiltVersion")
    ksp("com.google.dagger:hilt-compiler:$hiltVersion")
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")

    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")
    implementation("androidx.activity:activity-compose:1.9.3")
    implementation(platform("androidx.compose:compose-bom:2024.12.01"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.material3:material3")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.2")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")

    testImplementation("junit:junit:4.13.2")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.10.2")
    testImplementation("com.squareup.okhttp3:mockwebserver:4.12.0")

    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    androidTestImplementation("com.google.dagger:hilt-android-testing:$hiltVersion")
    kspAndroidTest("com.google.dagger:hilt-compiler:$hiltVersion")
}
```[cite: 4]

---

### 4. Har bir savol bo‘yicha alohida fayllar (`q01` – `q30`)

#### `q01_CarVsHardCar.kt`
```kotlin
package seminar15.hilt.q01

interface Engine {
    fun start(): String
}

class PetrolEngine : Engine {
    override fun start(): String = "Petrol engine running"
}

class FakeEngine(private val shouldFail: Boolean = false) : Engine {
    override fun start(): String = if (shouldFail) "Engine fault" else "Fake engine started"
}

class HardCar {
    private val engine = PetrolEngine()
    fun drive(): String = engine.start()
}

class Car(private val engine: Engine) {
    fun drive(): String = engine.start()
}
```[cite: 4]

#### `q02_DiClassification.kt`
```kotlin
package seminar15.hilt.q02

// (a) Room.databaseBuilder(...).build() inside class -> construct
// (b) ApiInstance.service inside class -> grab
// (c) class Repo(private val api: Api) -> supplied
// (d) LocalContext.current in composable -> grab
// (e) by viewModels() with custom factory -> supplied
// (f) @Inject lateinit var logger: Logger -> supplied

enum class AcquisitionMode {
    CONSTRUCT,
    GRAB,
    SUPPLIED
}
```[cite: 4]

#### `q03_ManualAppContainer.kt`
```kotlin
package seminar15.hilt.q03

import android.app.Application

class TrackedClient {
    init {
        count++
        println("created TrackedClient #$count")
    }

    companion object {
        var count = 0
    }
}

class TrackedRetrofit(private val client: TrackedClient) {
    init {
        count++
        println("created TrackedRetrofit #$count")
    }

    companion object {
        var count = 0
    }
}

class TrackedDao {
    init {
        count++
        println("created TrackedDao #$count")
    }

    companion object {
        var count = 0
    }
}

class TrackedRepository(
    private val retrofit: TrackedRetrofit,
    private val dao: TrackedDao
) {
    init {
        count++
        println("created TrackedRepository #$count")
    }

    companion object {
        var count = 0
    }
}

class AppContainer {
    val client = TrackedClient()
    val retrofit = TrackedRetrofit(client)
    val dao = TrackedDao()
    val repository = TrackedRepository(retrofit, dao)
}

class ManualDiApplication : Application() {
    val container = AppContainer()
}
```[cite: 4]

#### `q04_ManualDiWiring.kt`
```kotlin
package seminar15.hilt.q04

import android.content.Context

interface NotesApiClient {
    suspend fun fetchTitles(): List<String>
}

interface NotesLocalDao {
    suspend fun insertTitle(title: String)
    suspend fun allTitles(): List<String>
}

class ManualNotesRepository(
    private val api: NotesApiClient,
    private val dao: NotesLocalDao
) {
    suspend fun syncAndRead(): List<String> {
        api.fetchTitles().forEach { dao.insertTitle(it) }
        return dao.allTitles()
    }
}

class ManualFeatureContainer(appContext: Context) {
    private val unusedAppContextRef: Context = appContext.applicationContext

    fun createRepository(api: NotesApiClient, dao: NotesLocalDao): ManualNotesRepository =
        ManualNotesRepository(api, dao)
}
```[cite: 4]

#### `q05_DependencyGraph.kt`
```kotlin
package seminar15.hilt.q05

// Dependency graph ("A -> B" means A needs B):
// Screen -> ViewModel -> Repository -> DAO / RetrofitApi
// RetrofitApi -> OkHttpClient -> Context
// DAO -> RoomDatabase -> Context

data class DependencyEdge(
    val consumer: String,
    val dependency: String
)

val notesDependencyGraph = listOf(
    DependencyEdge("NoteScreen", "NoteViewModel"),
    DependencyEdge("NoteViewModel", "NoteRepository"),
    DependencyEdge("NoteRepository", "NoteDao"),
    DependencyEdge("NoteRepository", "GitHubService"),
    DependencyEdge("GitHubService", "Retrofit"),
    DependencyEdge("Retrofit", "OkHttpClient"),
    DependencyEdge("NoteDao", "AppDatabase"),
    DependencyEdge("AppDatabase", "Context")
)
```[cite: 4]

#### `q06_HiltGradleSetup.kt`
```kotlin
package seminar15.hilt.q06

// Root build.gradle.kts:
// alias(libs.plugins.hilt.android) apply false
// Module :app build.gradle.kts:
// alias(libs.plugins.hilt.android)
// ksp(libs.hilt.compiler)
// implementation(libs.hilt.android)

object HiltSetupSpec {
    const val HILT_VERSION = "2.57.1"
}
```[cite: 4]

#### `q07_HiltApplication.kt`
```kotlin
package seminar15.hilt.q07

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

// Manifest requires: android:name=".q07.HiltDemoApp"
// Generated Hilt application class: Hilt_HiltDemoApp
@HiltAndroidApp
class HiltDemoApp : Application()
```[cite: 4]

#### `q08_FieldInjectionActivity.kt`
```kotlin
package seminar15.hilt.q08

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

class ActivityLogger @Inject constructor() {
    fun log(msg: String) {
        Log.d("Q08", msg)
    }
}

@AndroidEntryPoint
class FieldInjectionActivity : ComponentActivity() {
    @Inject
    lateinit var logger: ActivityLogger

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        logger.log("Injected in FieldInjectionActivity")
    }
}
```[cite: 4]

#### `q09_ConstructorChain.kt`
```kotlin
package seminar15.hilt.q09

import javax.inject.Inject

class ChainC @Inject constructor() {
    init {
        println("Created C")
    }
}

class ChainB @Inject constructor(private val c: ChainC) {
    init {
        println("Created B")
    }
}

class ChainA @Inject constructor(private val b: ChainB) {
    init {
        println("Created A")
    }

    fun describe(): String = "A(${b.javaClass.simpleName})"
}
```[cite: 4]

#### `q10_DaggerErrorGuide.kt`
```kotlin
package seminar15.hilt.q10

// Reading Dagger build errors:
// 1. Missing/cyclic/scope binding type is identified in the error message.
// 2. Entry point where injection was requested is shown.
// 3. Dependency chain path shows how Hilt tried to resolve the graph.
// Common fixes:
// - Missing binding for interface/external type -> add @Binds or @Provides.
// - Dependency cycle -> break cycle (e.g. Provider<T> or decouple classes).

object DaggerErrorNotes
```[cite: 4]

#### `q11_BindsLoggerModule.kt`
```kotlin
package seminar15.hilt.q11

import android.util.Log
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Inject
import javax.inject.Singleton

interface AppLogger {
    fun d(tag: String, message: String)
}

class LogcatLogger @Inject constructor() : AppLogger {
    override fun d(tag: String, message: String) {
        Log.d(tag, message)
    }
}

class FileLogger @Inject constructor() : AppLogger {
    override fun d(tag: String, message: String) {
        println("[$tag] $message")
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class LoggingModule {
    @Binds
    @Singleton
    abstract fun bindAppLogger(impl: LogcatLogger): AppLogger
}
```[cite: 4]

#### `q12_ProvidesNetworkModule.kt`
```kotlin
package seminar15.hilt.q12

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import javax.inject.Singleton

data class GitHubUserItem(
    val id: Long,
    val login: String
)

interface GitHubUsersService {
    @GET("users")
    suspend fun getUsers(@Query("per_page") perPage: Int = 5): List<GitHubUserItem>
}

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        println("Providing OkHttpClient")
        return OkHttpClient.Builder().build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(client: OkHttpClient): Retrofit {
        println("Providing Retrofit")
        return Retrofit.Builder()
            .baseUrl("https://api.github.com/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideGitHubService(retrofit: Retrofit): GitHubUsersService {
        println("Providing GitHubUsersService")
        return retrofit.create(GitHubUsersService::class.java)
    }
}
```[cite: 4]

#### `q13_DatabaseModule.kt`
```kotlin
package seminar15.hilt.q13

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Entity(tableName = "q13_notes")
data class Q13Note(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String
)

@Dao
interface Q13NoteDao {
    @Query("SELECT * FROM q13_notes")
    suspend fun getAll(): List<Q13Note>
}

@Database(entities = [Q13Note::class], version = 1, exportSchema = false)
abstract class Q13Database : RoomDatabase() {
    abstract fun noteDao(): Q13NoteDao
}

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): Q13Database =
        Room.databaseBuilder(context, Q13Database::class.java, "q13_notes.db").build()

    @Provides
    fun provideNoteDao(db: Q13Database): Q13NoteDao = db.noteDao()
}
```[cite: 4]

#### `q14_Qualifiers.kt`
```kotlin
package seminar15.hilt.q14

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ApiClient

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class DownloadClient

class ApiFetcher(@ApiClient val client: OkHttpClient)
class FileDownloader(@DownloadClient val client: OkHttpClient)

@Module
@InstallIn(SingletonComponent::class)
object ClientQualifiersModule {
    @Provides
    @Singleton
    @ApiClient
    fun provideApiClient(): OkHttpClient =
        OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .build()

    @Provides
    @Singleton
    @DownloadClient
    fun provideDownloadClient(): OkHttpClient =
        OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .build()
}
```[cite: 4]

#### `q15_BaseUrlAndDispatcher.kt`
```kotlin
package seminar15.hilt.q15

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class BaseUrl

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class IoDispatcher

class ConfigRepository(
    @BaseUrl private val baseUrl: String,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {
    suspend fun resolvedEndpoint(): String = withContext(ioDispatcher) {
        "${baseUrl.trimEnd('/')}/users"
    }
}

@Module
@InstallIn(SingletonComponent::class)
object ConfigModule {
    @Provides
    @Singleton
    @BaseUrl
    fun provideBaseUrl(): String = "https://api.github.com/"

    @Provides
    @Singleton
    @IoDispatcher
    fun provideIoDispatcher(): CoroutineDispatcher = Dispatchers.IO
}
```[cite: 4]

#### `q16_ScopeExperiment.kt`
```kotlin
package seminar15.hilt.q16

import dagger.hilt.android.scopes.ActivityRetainedScoped
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject
import javax.inject.Singleton

class UnscopedCounter @Inject constructor() {
    val id = nextId()
    companion object {
        private var n = 0
        private fun nextId() = ++n
    }
}

@Singleton
class SingletonCounter @Inject constructor() {
    val id = nextId()
    companion object {
        private var n = 0
        private fun nextId() = ++n
    }
}

@ActivityRetainedScoped
class RetainedCounter @Inject constructor() {
    val id = nextId()
    companion object {
        private var n = 0
        private fun nextId() = ++n
    }
}

@ViewModelScoped
class ViewModelCounter @Inject constructor() {
    val id = nextId()
    companion object {
        private var n = 0
        private fun nextId() = ++n
    }
}
```[cite: 4]

#### `q17_ComponentHierarchyTheory.kt`
```kotlin
package seminar15.hilt.q17

// Hierarchy:
// SingletonComponent
//  ├── ActivityRetainedComponent
//  │    └── ViewModelComponent
//  └── ActivityComponent
//       └── FragmentComponent
//
// - @Singleton needing @ActivityScoped: illegal (child scope cannot feed parent scope).
// - @ActivityScoped needing @Singleton: legal (child can consume parent bindings).
// - @ViewModelScoped in Activity: illegal (ViewModelComponent is not a parent of ActivityComponent).
// - Configuration change: SingletonComponent & ActivityRetainedComponent survive;
//   ActivityComponent & FragmentComponent are recreated.

object Q17HierarchyRules
```[cite: 4]

#### `q18_IllegalHierarchyExamples.kt`
```kotlin
package seminar15.hilt.q18

// Illegal examples (kept commented so build succeeds; uncomment to inspect Dagger error):
// 1. @Singleton class BadSingleton(private val activityScoped: AnyActivityScoped)
// 2. @Singleton class ContextLeakSingleton(@ActivityContext private val context: Context)
// Both prevent leaking a short-lived Activity and its view hierarchy in a long-lived singleton.

object Q18LeakPrevention
```[cite: 4]

#### `q19_MultiModuleStructure.kt`
```kotlin
package seminar15.hilt.q19

// Multi-module layout:
// :app -> @HiltAndroidApp application
// :feature-notes -> Compose screen & @HiltViewModel
// :data -> Room database, DAO, repository & @InstallIn modules
// Removing :data from :app causes missing binding / unresolved module errors at compile time.

interface FeatureNotesEntryPoint
```[cite: 4]

#### `q20_ScopeReview.kt`
```kotlin
package seminar15.hilt.q20

// Scope review:
// - UserSession (one per app launch): @Singleton (thread-safe shared session)
// - NoteListState (one per screen visit): @ViewModelScoped / screen state
// - HttpClient (expensive to build): @Singleton (reused, thread-safe)
// - DateFormatter (stateless, cheap): unscoped or thread-safe singleton
// - AuthToken (changes at login): @Singleton holding dynamic/thread-safe token state
// "Just make it a singleton" ignores lifecycle boundaries, memory retention, and mutable state.

object Q20ScopeDesign
```[cite: 4]

#### `q21_HiltNotesMigration.kt`
```kotlin
package seminar15.hilt.q21

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

interface NotesRepoQ21 {
    val titles: StateFlow<List<String>>
    suspend fun addTitle(title: String)
}

class RoomNotesRepoQ21 @Inject constructor() : NotesRepoQ21 {
    private val _titles = MutableStateFlow<List<String>>(emptyList())
    override val titles: StateFlow<List<String>> = _titles

    override suspend fun addTitle(title: String) {
        _titles.value = _titles.value + title
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class NotesRepoModuleQ21 {
    @Binds
    @Singleton
    abstract fun bindNotesRepo(impl: RoomNotesRepoQ21): NotesRepoQ21
}

@HiltViewModel
class NotesViewModelQ21 @Inject constructor(
    private val repo: NotesRepoQ21
) : ViewModel() {
    val titles: StateFlow<List<String>> = repo.titles
}
```[cite: 4]

#### `q22_SavedStateHandleViewModel.kt`
```kotlin
package seminar15.hilt.q22

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

interface DetailNoteRepository {
    fun findTitleById(id: Long): String?
}

@HiltViewModel
class NoteDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: DetailNoteRepository
) : ViewModel() {
    val noteId: Long = savedStateHandle["noteId"] ?: -1L
    val title: String? = repository.findTitleById(noteId)
}
```[cite: 4]

#### `q23_RealAndFakeRepository.kt`
```kotlin
package seminar15.hilt.q23

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Inject
import javax.inject.Singleton

interface SwitchableNoteRepository {
    fun items(): List<String>
}

class RealNoteRepository @Inject constructor() : SwitchableNoteRepository {
    override fun items(): List<String> = listOf("Room Note 1", "Room Note 2")
}

class FakeNoteRepository @Inject constructor() : SwitchableNoteRepository {
    override fun items(): List<String> = listOf("Sample Fake Note A", "Sample Fake Note B")
}

@Module
@InstallIn(SingletonComponent::class)
abstract class RealRepositoryBindingModule {
    @Binds
    @Singleton
    abstract fun bindRepository(impl: RealNoteRepository): SwitchableNoteRepository
}
```[cite: 4]

#### `q24_ConnectivityReceiver.kt`
```kotlin
package seminar15.hilt.q24

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

class ReceiverLogger @Inject constructor() {
    fun log(text: String) {
        Log.d("Q24_RECEIVER", text)
    }
}

@AndroidEntryPoint
class ConnectivityReceiver : BroadcastReceiver() {
    @Inject
    lateinit var logger: ReceiverLogger

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        logger.log("Received action=${intent.action}")
    }
}
```[cite: 4]

#### `q25_SharedDraftViewModel.kt`
```kotlin
package seminar15.hilt.q25

import android.util.Log
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class DraftViewModel @Inject constructor() : ViewModel() {
    private val _draftTitle = MutableStateFlow("")
    val draftTitle: StateFlow<String> = _draftTitle

    private val _draftBody = MutableStateFlow("")
    val draftBody: StateFlow<String> = _draftBody

    fun updateTitle(value: String) {
        _draftTitle.value = value
    }

    fun updateBody(value: String) {
        _draftBody.value = value
    }

    override fun onCleared() {
        super.onCleared()
        Log.d("Q25_DRAFT", "DraftViewModel cleared")
    }
}
```[cite: 4]

#### `q26_TestingTheory.kt`
```kotlin
package seminar15.hilt.q26

// @TestInstallIn(components = [SingletonComponent::class], replaces = [NetworkModule::class])
// swaps a real Hilt module with a test module during instrumented Hilt tests.
// @HiltAndroidTest, HiltAndroidRule, and a custom AndroidJUnitRunner returning HiltTestApplication
// initialize Hilt's test component and perform field injection in instrumented tests.
// A unit test for a class with @Inject constructor needs none of them because dependencies
// can be passed directly to the constructor on the JVM.

object Q26TestingNotes
```[cite: 4]

#### `q27_JvmUnitTests.kt`
```kotlin
package seminar15.hilt.q27

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

interface JvmNotesRepo {
    val notes: StateFlow<List<String>>
    fun add(note: String)
}

class FakeJvmNotesRepo : JvmNotesRepo {
    private val _notes = MutableStateFlow<List<String>>(emptyList())
    override val notes: StateFlow<List<String>> = _notes

    override fun add(note: String) {
        _notes.value = _notes.value + note
    }
}

class FirstListViewModel(private val repo: JvmNotesRepo) {
    val notes: StateFlow<List<String>> = repo.notes
    fun createNote(title: String) {
        if (title.isNotBlank()) repo.add(title.trim())
    }
}

class SecondSummaryViewModel(private val repo: JvmNotesRepo) {
    fun count(): Int = repo.notes.value.size
}
```[cite: 4]

#### `q28_HiltTestRunner.kt`
```kotlin
package seminar15.hilt.q28

import android.app.Application
import android.content.Context
import androidx.test.runner.AndroidJUnitRunner
import dagger.hilt.android.testing.HiltTestApplication

class HiltTestRunner : AndroidJUnitRunner() {
    override fun newApplication(
        cl: ClassLoader?,
        className: String?,
        context: Context?
    ): Application = super.newApplication(cl, HiltTestApplication::class.java.name, context)
}
```[cite: 4]

#### `q29_TestDatabaseModule.kt`
```kotlin
package seminar15.hilt.q29

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import seminar15.hilt.q13.DatabaseModule
import seminar15.hilt.q13.Q13Database
import seminar15.hilt.q13.Q13NoteDao
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [DatabaseModule::class]
)
object TestDatabaseModule {
    @Provides
    @Singleton
    fun provideInMemoryDatabase(@ApplicationContext context: Context): Q13Database =
        Room.inMemoryDatabaseBuilder(context, Q13Database::class.java)
            .allowMainThreadQueries()
            .build()

    @Provides
    fun provideDao(db: Q13Database): Q13NoteDao = db.noteDao()
}
```[cite: 4]

#### `q30_CapstoneHiltApp.kt`
```kotlin
package seminar15.hilt.q30

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class CapstoneIoDispatcher

data class CapstoneUser(val id: Long, val login: String)

interface CapstoneRepository {
    val users: StateFlow<List<CapstoneUser>>
    suspend fun refreshUsers()
}

class DefaultCapstoneRepository @Inject constructor() : CapstoneRepository {
    private val _users = MutableStateFlow<List<CapstoneUser>>(emptyList())
    override val users: StateFlow<List<CapstoneUser>> = _users

    override suspend fun refreshUsers() {
        _users.value = listOf(CapstoneUser(1L, "octocat"))
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class CapstoneBindingsModule {
    @Binds
    @Singleton
    abstract fun bindCapstoneRepository(impl: DefaultCapstoneRepository): CapstoneRepository
}

@HiltViewModel
class CapstoneDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: CapstoneRepository
) : ViewModel() {
    val selectedUserId: Long = savedStateHandle["userId"] ?: -1L
    val users: StateFlow<List<CapstoneUser>> = repository.users
}
```[cite: 4]

---

### 5. Git’ga yuklash buyruqlari

```bash
git checkout -b seminar-15-hilt
git add README.md NOTES.md app/build.gradle.kts
git commit -m "docs: add Seminar 15 README, NOTES.md, and Hilt build config"

git add q01_CarVsHardCar.kt && git commit -m "q01: hardcar vs constructor-injected car"
git add q02_DiClassification.kt && git commit -m "q02: construct grab supplied classification"
git add q03_ManualAppContainer.kt && git commit -m "q03: manual appcontainer creation counts"
git add q04_ManualDiWiring.kt && git commit -m "q04: manual-di wiring refactor"
git add q05_DependencyGraph.kt && git commit -m "q05: dependency graph and manual di trade-offs"
git add q06_HiltGradleSetup.kt && git commit -m "q06: hilt 2.57.1 gradle setup"
git add q07_HiltApplication.kt && git commit -m "q07: hilt application and generated class"
git add q08_FieldInjectionActivity.kt && git commit -m "q08: android entry point field injection"
git add q09_ConstructorChain.kt && git commit -m "q09: constructor injection order"
git add q10_DaggerErrorGuide.kt && git commit -m "q10: dagger build error guide"
git add q11_BindsLoggerModule.kt && git commit -m "q11: binds logger module"
git add q12_ProvidesNetworkModule.kt && git commit -m "q12: provides singleton network module"
git add q13_DatabaseModule.kt && git commit -m "q13: application context database module"
git add q14_Qualifiers.kt && git commit -m "q14: custom qualifiers for okhttpclient"
git add q15_BaseUrlAndDispatcher.kt && git commit -m "q15: baseurl and iodispatcher qualifiers"
git add q16_ScopeExperiment.kt && git commit -m "q16: scope experiment"
git add q17_ComponentHierarchyTheory.kt && git commit -m "q17: component hierarchy rules"
git add q18_IllegalHierarchyExamples.kt && git commit -m "q18: illegal hierarchy and leak prevention"
git add q19_MultiModuleStructure.kt && git commit -m "q19: multi-module hilt setup"
git add q20_ScopeReview.kt && git commit -m "q20: scope selection review"
git add q21_HiltNotesMigration.kt && git commit -m "q21: hilt migration for notes app"
git add q22_SavedStateHandleViewModel.kt && git commit -m "q22: savedstatehandle in hiltviewmodel"
git add q23_RealAndFakeRepository.kt && git commit -m "q23: real and fake repository binding"
git add q24_ConnectivityReceiver.kt && git commit -m "q24: broadcastreceiver injection"
git add q25_SharedDraftViewModel.kt && git commit -m "q25: shared draftviewmodel in nested graph"
git add q26_TestingTheory.kt && git commit -m "q26: hilt testing theory"
git add q27_JvmUnitTests.kt && git commit -m "q27: jvm unit tests without hilt"
git add q28_HiltTestRunner.kt && git commit -m "q28: hilt test runner setup"
git add q29_TestDatabaseModule.kt && git commit -m "q29: test database module with in-memory db"
git add q30_CapstoneHiltApp.kt && git commit -m "q30: capstone hilt app"

git push -u origin seminar-15-hilt
```[cite: 4]
