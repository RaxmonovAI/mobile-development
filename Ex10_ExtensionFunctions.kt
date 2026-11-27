package ex10

// Exercise 10.1 — String.shout()
fun String.shout(): String = this.uppercase() + "!!!"

// Exercise 10.2 — Int.isPrime()
fun Int.isPrime(): Boolean {
    if (this < 2) return false
    for (divisor in 2 until this) {
        if (this % divisor == 0) return false
    }
    return true
}

fun main() {
    println("--- 10.1 ---")
    println("hello".shout())
    println("kotlin is fun".shout())

    println("--- 10.2 ---")
    for (n in 2..20) {
        if (n.isPrime()) println(n)
    }
}
