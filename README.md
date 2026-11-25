
Kotlin/JVM console project using `kotlinx-coroutines-core:1.10.2` with theory answers (Q1–Q10) and five coroutine tasks.

- **Task 1 (`Thread.sleep()` vs `delay()`):** `Thread.sleep()` blocks the current thread instead of suspending it, changing the execution order because the thread cannot switch to other work while sleeping.
- **Task 2 (Calling `fetchName()` from a normal function):** Calling a `suspend` function outside a coroutine or another `suspend` function produces a compiler error: `Suspend function 'fetchName' should be called only from a coroutine or another suspend function`[cite: 6].
- **Task 4 (`await()` right after each `async`):** Awaiting immediately after `async { loadName() }` suspends and finishes the first task before `async { loadAge() }` starts, making execution sequential (~200 ms) instead of concurrent (~100 ms)[cite: 6].
