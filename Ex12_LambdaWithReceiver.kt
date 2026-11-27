package ex12

// Exercise 12.1 — buildGreeting with StringBuilder receiver
fun buildGreeting(action: StringBuilder.() -> Unit): String {
    val builder = StringBuilder()
    builder.action()
    return builder.toString()
}

fun main() {
    val message = buildGreeting {
        append("Hello, ")
        append("class! ")
        append("Welcome to Kotlin.")
    }
    println(message)
}
