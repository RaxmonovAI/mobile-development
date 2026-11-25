
import kotlinx.coroutines.*
import kotlin.system.measureTimeMillis

// PART A — THEORY (Q1–Q10)

// Q1: `newSingleThreadContext` vs `Dispatchers.Unconfined`
// - `newSingleThreadContext("MyThread")` creates a dedicated OS thread. A coroutine launched on it
//   runs on `"MyThread"` before and after `delay()`, and you MUST call `.close()` when finished to avoid
//   leaking the thread/resources.
// - `Dispatchers.Unconfined` starts on the caller thread (e.g., `main`), and after `delay()` resumes
//   on whichever thread the continuation runs on (e.g., `DefaultExecutor`). No `.close()` is needed.
@OptIn(DelicateCoroutinesApi::class)
suspend fun q1DispatcherComparison() = coroutineScope {
    val singleThread = newSingleThreadContext("MyThread")
    try {
        launch(singleThread) {
            println("Q1 Single before delay: ${Thread.currentThread().name}")
            delay(50)
            println("Q1 Single after delay: ${Thread.currentThread().name}")
        }
    } finally {
        singleThread.close()
    }

    launch(Dispatchers.Unconfined) {
        println("Q1 Unconfined before delay: ${Thread.currentThread().name}")
        delay(50)
        println("Q1 Unconfined after delay: ${Thread.currentThread().name}")
    }
}

// Q2: `async` vs `launch`
// - `launch` returns `Job` (fire-and-forget); `async` returns `Deferred<T>` (a `Job` that returns `T`).
// - Get result from `async` via `deferred.await()`.
// - `join()` waits for completion without a return value; `await()` waits and returns the result `T`.

// Q3: Coroutine, `suspend` vs `block`, `delay()` vs `Thread.sleep()`
// - A coroutine is a lightweight suspendable computation managed by Kotlin coroutines.
// - Suspending pauses the coroutine without blocking the OS thread; blocking freezes the thread itself.
// - With two coroutines on one thread: `delay()` lets the other coroutine run while waiting;
//   `Thread.sleep()` blocks the thread so the second coroutine must wait until the first finishes.

// Q4: `suspend` keyword
// - Does not create a new thread on its own.
// - Can only be called from another `suspend` function or a coroutine builder.
// - Compiler adds a hidden `Continuation<T>` parameter.

// Q5: Concurrency vs Parallelism
// - Concurrency is interleaving/progressing on overlapping tasks (coroutines achieve this on 1 thread).
// - Parallelism is simultaneous execution across multiple CPU cores.

// Q6: Structured concurrency
// - Coroutines form a parent-child job hierarchy inside a scope.
// - Parent waits for children to finish; cancelling parent cancels all children; an uncaught child
//   failure cancels siblings and propagates up to fail the parent (unless using `SupervisorJob`).

// Q7: Builders comparison
// - `launch` -> `Job` (non-blocking)
// - `async` -> `Deferred<T>` (non-blocking)
// - `runBlocking` -> `T` (BLOCKS current thread; never use on Android UI thread because it freezes UI/ANR)
// - `withContext` -> `T` (suspends, switches context/dispatcher)
// - `coroutineScope` -> `T` (suspends, awaits all children)

// Q8: `CoroutineScope` vs `CoroutineContext`
// - Scope controls lifecycle ("when" coroutines live/are cancelled); Context holds elements like `Job`
//   and `CoroutineDispatcher` ("where/how" they run).
// - If unspecified, dispatcher is inherited from parent or defaults to `Dispatchers.Default`
//   (`runBlocking` uses its own single-thread event loop).

// Q9: Dispatchers.Default, IO, Main
// - `Dispatchers.Default`: CPU-bound work (e.g., sorting/JSON parsing); avoid blocking I/O.
// - `Dispatchers.IO`: blocking I/O work (e.g., network/database/file); avoid heavy CPU loops.
// - `Dispatchers.Main`: UI thread work (e.g., updating UI); avoid blocking or heavy work.

// Q10: `viewModelScope.launch { withContext(Dispatchers.IO) { ... } }`
// - Outer `launch` starts on `Dispatchers.Main.immediate` (Main thread).
// - `withContext(Dispatchers.IO)` switches inner block to an IO worker thread, then returns to Main.
// - When `ViewModel` is cleared, `viewModelScope` cancels the coroutine automatically.

// PART B — 5 COROUTINE TASKS 

// Task 1: First coroutine with runBlocking, launch and delay
fun task1() = runBlocking {
    println("--- Task 1 ---")
    launch {
        delay(200)
        println("A done")
    }
    launch {
        delay(100)
        println("B done")
    }
    println("Start")
}

// Task 2: suspend function with n tries
private var task2CallCount = 0

suspend fun fetchName(): String? {
    delay(100)
    task2CallCount++
    return if (task2CallCount <= 2) null else "Ada"
}

suspend fun showUser(n: Int) {
    for (k in 1..n) {
        val name = fetchName()
        if (name != null) {
            println("User: $name")
            return
        } else {
            println("try $k failed")
        }
    }
    println("Server is down, show cached user")
}

fun task2() = runBlocking {
    println("--- Task 2 (n = 3) ---")
    task2CallCount = 0
    showUser(n = 3)

    println("--- Task 2 (n = 2) ---")
    task2CallCount = 0
    showUser(n = 2)
}

// Task 3: Control coroutine with Job
fun task3() = runBlocking {
    println("--- Task 3 ---")
    val job = launch {
        try {
            for (i in 0..4) {
                println("tick $i")
                delay(100)
            }
        } finally {
            println("cleanup")
        }
    }

    delay(250)
    job.cancelAndJoin()
    println("isCancelled=${job.isCancelled}")
}

// Task 4: Load name and age with async
suspend fun loadName(): String {
    delay(100)
    return "Ada"
}

suspend fun loadAge(): Int {
    delay(100)
    return 36
}

fun task4() = runBlocking {
    println("--- Task 4 ---")

    val sequentialMs = measureTimeMillis {
        val name = loadName()
        val age = loadAge()
        println("Sequential: $name ($age)")
    }
    println("Sequential time: ${sequentialMs}ms")

    val concurrentMs = measureTimeMillis {
        val nameDeferred = async { loadName() }
        val ageDeferred = async { loadAge() }
        println("Concurrent: ${nameDeferred.await()} (${ageDeferred.await()})")
    }
    println("Concurrent time: ${concurrentMs}ms")

    val immediateAwaitMs = measureTimeMillis {
        val name = async { loadName() }.await()
        val age = async { loadAge() }.await()
        println("Immediate await: $name ($age)")
    }
    println("Immediate await time: ${immediateAwaitMs}ms")
}

// Task 5: Find thread of each dispatcher
@OptIn(DelicateCoroutinesApi::class)
fun task5() = runBlocking {
    println("--- Task 5 ---")

    val defaultJob = launch(Dispatchers.Default) {
        println("Default thread: ${Thread.currentThread().name}")
    }
    val ioJob = launch(Dispatchers.IO) {
        println("IO thread: ${Thread.currentThread().name}")
    }
    val unconfinedJob = launch(Dispatchers.Unconfined) {
        println("Unconfined thread: ${Thread.currentThread().name}")
    }

    val singleThreadContext = newSingleThreadContext("MyThread")
    val singleThreadJob = launch(singleThreadContext) {
        println("newSingleThreadContext before delay: ${Thread.currentThread().name}")
        delay(50)
        println("newSingleThreadContext after delay: ${Thread.currentThread().name}")
    }

    val inheritedJob = launch {
        println("Inherited dispatcher thread: ${Thread.currentThread().name}")
    }

    joinAll(defaultJob, ioJob, unconfinedJob, singleThreadJob, inheritedJob)
    singleThreadContext.close()
}

fun main() {
    task1()
    task2()
    task3()
    task4()
    task5()
}
