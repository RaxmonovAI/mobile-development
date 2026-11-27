package ex14

// Exercise 14.1 — AppConfig singleton
object AppConfig {
    val appName = "GradeBook"
    var isDebug = false

    fun toggleDebug() {
        isDebug = !isDebug
    }
}

// Exercise 14.2 — Companion object factory
class User private constructor(val name: String) {
    companion object {
        fun create(name: String): User = User(name)
    }
}

fun main() {
    println("--- 14.1 ---")
    println(AppConfig.appName)
    println("Debug: ${AppConfig.isDebug}")
    AppConfig.toggleDebug()
    println("Debug: ${AppConfig.isDebug}")

    println("--- 14.2 ---")
    val user = User.create("Amin")
    println(user.name)
}
