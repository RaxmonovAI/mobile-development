package ex11

class Book(var title: String = "", var author: String = "", var pages: Int = 0)

fun main() {
    // Exercise 11.1 — Safe print with let
    println("--- 11.1 ---")
    var maybeName: String? = "Amin"
    maybeName?.let { println(it.uppercase()) }

    maybeName = null
    maybeName?.let { println(it.uppercase()) } // nothing is printed

    // Exercise 11.2 — Configure with apply
    println("--- 11.2 ---")
    val book = Book().apply {
        title = "Kotlin in Action"
        author = "Dmitry Jemerov"
        pages = 360
    }
    println("${book.title} by ${book.author}, ${book.pages} pages")

    // Exercise 11.3 — Log creation with also
    println("--- 11.3 ---")
    val book2 = Book("Atomic Habits", "James Clear", 320).also {
        println("Created book: ${it.title}")
    }

    // Exercise 11.4 — let vs. apply vs. also
    println("--- 11.4 ---")

    // let: the lambda result is returned, so we must return `it` ourselves
    val b1 = Book("Sample", "Someone", 0).let {
        it.pages = 400
        println(it.title)
        it
    }

    // apply: configures the object via `this` and returns it automatically
    val b2 = Book("Sample", "Someone", 0).apply {
        pages = 400
        println(title)
    }

    // also: meant for side effects (like printing); returns the object unchanged
    val b3 = Book("Sample", "Someone", 0).also {
        it.pages = 400
        println(it.title)
    }

    // Best fit: apply — no `it.` prefix and no manual `return it`,
    // it is made exactly for configuring an object right after creation.
    println("${b1.pages} ${b2.pages} ${b3.pages}")
}
