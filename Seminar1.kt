
// 1. Variables — val and var

fun exercise1_1() {
    val city: String = "Tashkent"
    var temperature: Double = 18.5

    temperature = 20.0
    println("City: $city, Temperature: $temperature")
}

fun exercise1_2() {
    val itemPrice = 3000
    var stock = 10
    var totalCollected = 0

    // Purchase 1
    stock -= 1
    totalCollected += itemPrice
    println("After purchase 1 -> Stock: $stock, Total collected: $totalCollected")

    // Purchase 2
    stock -= 1
    totalCollected += itemPrice
    println("After purchase 2 -> Stock: $stock, Total collected: $totalCollected")

    // Purchase 3
    stock -= 1
    totalCollected += itemPrice
    println("After purchase 3 -> Stock: $stock, Total collected: $totalCollected")

    println("Grand total collected: $totalCollected")
}

// 2. String Templates & Triple-Quoted Strings

fun exercise2_1() {
    val customer = "Diyor"
    val total = 145000

    println("Order confirmation for $customer: total is $total.")
    println("Free delivery: ${if (total >= 100000) "Yes" else "No"}")
}

fun printReceipt(item: String, price: Int) {
    val receipt = """
        Item: "$item"
        Price: $${price}
        Thank you for visiting!
    """.trimIndent()
    println(receipt)
}

fun exercise2_2() {
    printReceipt("Latte", 24000)
    printReceipt("Cheesecake", 32000)
}

// 3. Basic Types & Type Inference

fun exercise3_1() {
    // a) 9 / 4        -> value: 2,    inferred type: Int (integer division truncates fractional part)
    // b) 9.0 / 4      -> value: 2.25, inferred type: Double (Double operand promotes division to Double)
    // c) 9 % 4        -> value: 1,    inferred type: Int (remainder of 9 divided by 4)
    // d) 9 / 4.0      -> value: 2.25, inferred type: Double (Double operand promotes division to Double)
    // e) 2 + 3 * 5    -> value: 17,   inferred type: Int (`*` has higher precedence than `+`)
    // f) (2 + 3) * 5  -> value: 25,   inferred type: Int (parentheses evaluate `2 + 3` first)

    println("a) 9 / 4 = ${9 / 4}")
    println("b) 9.0 / 4 = ${9.0 / 4}")
    println("c) 9 % 4 = ${9 % 4}")
    println("d) 9 / 4.0 = ${9 / 4.0}")
    println("e) 2 + 3 * 5 = ${2 + 3 * 5}")
    println("f) (2 + 3) * 5 = ${(2 + 3) * 5}")
}

// 4. Collections — List, Set, Map

fun exercise4_1() {
    val defaultPlaylist: List<String> = listOf(
        "Morning Light",
        "City Pulse",
        "Midnight Drive",
        "Acoustic Breeze",
        "Sunset Echo"
    )

    // Trying to add a 6th item:
    // defaultPlaylist.add("Rainy Day")
    // Compiler error: "Unresolved reference: add" (or "Unresolved reference 'add'.")
    // `List` is read-only and does not provide `add()`; `MutableList` does.

    val likedSongs: MutableList<String> = mutableListOf()
    likedSongs.add("City Pulse")
    likedSongs.add("Midnight Drive")
    likedSongs.add("Rainy Day")
    println("Liked Songs: $likedSongs")
}

fun exercise4_2() {
    val menu: Map<String, Int> = mapOf(
        "Espresso" to 18000,
        "Americano" to 20000,
        "Cappuccino" to 26000,
        "Croissant" to 22000
    )

    for ((name, price) in menu) {
        println("$name costs $price")
    }

    val totalValue = menu.values.sum()
    println("Total menu value: $totalValue")

    val cheapest = menu.minByOrNull { it.value }
    val priciest = menu.maxByOrNull { it.value }
    println("Cheapest item: ${cheapest?.key} (${cheapest?.value})")
    println("Most expensive item: ${priciest?.key} (${priciest?.value})")
}

fun exercise4_3() {
    val signInSheet: List<String> = listOf(
        "Aziz", "Malika", "Aziz", "Jasur", "Malika", "Aziz"
    )

    val uniqueStudents: Set<String> = signInSheet.toSet()
    val attendanceMap: Map<String, Boolean> = uniqueStudents.associateWith { true }

    println("Attendance: $attendanceMap")
    println("Unique students present: ${attendanceMap.size}")
}

// 5. Control Flow — if / when / for / while / do-while

fun gradeFromScore(score: Int): String = when {
    score >= 90 -> "A"
    score >= 80 -> "B"
    score >= 70 -> "C"
    score >= 60 -> "D"
    else -> "F"
}

fun exercise5_1() {
    println("Score 92 -> ${gradeFromScore(92)}")
    println("Score 75 -> ${gradeFromScore(75)}")
}

fun exercise5_2() {
    for (count in 25 downTo 0 step 5) {
        println(count)
    }
    println("Liftoff!")
}

fun exercise5_3() {
    val attempts = listOf(1111, 4321, 9999)
    val correctPin = 4321
    var index = 0
    var unlocked = false

    while (index < attempts.size && !unlocked) {
        val currentAttempt = attempts[index]
        if (currentAttempt == correctPin) {
            unlocked = true
        } else {
            println("Incorrect PIN attempt: $currentAttempt")
        }
        index++
    }

    if (unlocked) {
        println("Card unlocked")
    } else {
        println("Card locked")
    }

    // Why a `do-while` loop would be risky if `attempts` is empty:
    // A `do-while` loop always executes its body at least once before checking the loop condition.
    // If `attempts` were empty (`size == 0`), accessing `attempts[0]` on the first iteration
    // would throw an `IndexOutOfBoundsException`.
}

fun exercise5_4() {
    for (n in 1..30) {
        val result = when {
            n % 15 == 0 -> "FizzBuzz"
            n % 3 == 0 -> "Fizz"
            n % 5 == 0 -> "Buzz"
            else -> n.toString()
        }
        println(result)
    }
}

// 6. Functions

fun notify(message: String, channel: String = "Email"): String {
    return "[$channel] $message"
}

fun exercise6_1() {
    println(notify("Build succeeded"))
    println(notify("Build succeeded", "SMS"))
    println(notify(channel = "Push", message = "New alert"))
}

fun absoluteValue(x: Int): Int = if (x < 0) -x else x

fun exercise6_2() {
    println(absoluteValue(-14))
    println(absoluteValue(9))
}

fun isPassing(score: Int): Boolean = score >= 60

fun exercise6_3() {
    val scores = listOf(45, 60, 82, 59, 91, 60, 33)
    val passingScores = scores.filter { isPassing(it) }
    println("Passing scores: $passingScores")
}

fun digitSum(n: Int): Int {
    var current = n
    var sum = 0
    while (current > 0) {
        sum += current % 10
        current /= 10
    }
    return sum
}

fun exercise6_4() {
    println("digitSum(1234) = ${digitSum(1234)}")
    println("digitSum(507) = ${digitSum(507)}")
}

// 7. Classes

class Book(val title: String, val author: String, val pages: Int)

fun exercise7_1() {
    val firstBook = Book("The Pragmatic Programmer", "Andrew Hunt", 352)
    val secondBook = Book("Clean Code", "Robert C. Martin", 464)

    println("${firstBook.title} — ${firstBook.author}")
    println("${secondBook.title} — ${secondBook.author}")
}

data class BookData(val title: String, val author: String, val pages: Int)

fun exercise7_2() {
    val bookA = BookData("1984", "George Orwell", 328)
    val bookB = BookData("1984", "George Orwell", 328)
    val bookC = bookA.copy(pages = 336)

    println(bookA)
    println("bookA == bookB: ${bookA == bookB}")
    println(bookC)
}

class Student(val name: String, val grades: List<Int>) {
    fun average(): Double {
        if (grades.isEmpty()) return 0.0
        return grades.average()
    }
}

fun exercise7_3() {
    val s1 = Student("Madina", listOf(90, 85, 95))
    val s2 = Student("Timur", listOf(70, 80, 75, 85))

    println("${s1.name}: ${s1.average()}")
    println("${s2.name}: ${s2.average()}")
}

// 8. Null Safety

fun exercise8_1() {
    var nickname: String? = null
    println("Length when null: ${nickname?.length ?: 0}")

    nickname = "Alex"
    println("Length when set to '$nickname': ${nickname?.length ?: 0}")
}

fun describeStringExplicit(maybeString: String?): String {
    if (maybeString == null || maybeString.length == 0) {
        return "empty or null"
    }
    return maybeString
}

fun describeStringElvis(maybeString: String?): String {
    return maybeString?.takeIf { it.isNotEmpty() } ?: "empty or null"
}

fun exercise8_2() {
    println(describeStringExplicit(null))
    println(describeStringExplicit(""))
    println(describeStringExplicit("Kotlin"))

    println(describeStringElvis(null))
    println(describeStringElvis(""))
    println(describeStringElvis("Kotlin"))
}

fun exercise8_3() {
    val readings: List<String?> = listOf(null, null, "23.5", "19.1", null)
    val firstReading = readings.firstOrNull { it != null } ?: "No data"
    println("First valid reading: $firstReading")
}

// 9. Naming Conventions

fun exercise9_1() {
    // user_name       -> variable -> userName (camelCase)
    // MAX-RETRIES     -> constant -> MAX_RETRIES (UPPER_SNAKE_CASE)
    // get_user_name   -> function -> getUserName (camelCase)
    // circle          -> class    -> Circle (PascalCase / UpperCamelCase)
    // PI_value        -> constant -> PI or PI_VALUE (UPPER_SNAKE_CASE)
    // is_logged_in    -> variable -> isLoggedIn (camelCase with boolean `is` prefix)
    // DEFAULT-TIMEOUT -> constant -> DEFAULT_TIMEOUT (UPPER_SNAKE_CASE)
    println("Naming conventions documented in comments.")
}

fun main() {
    exercise1_1()
    exercise1_2()
    exercise2_1()
    exercise2_2()
    exercise3_1()
    exercise4_1()
    exercise4_2()
    exercise4_3()
    exercise5_1()
    exercise5_2()
    exercise5_3()
    exercise5_4()
    exercise6_1()
    exercise6_2()
    exercise6_3()
    exercise6_4()
    exercise7_1()
    exercise7_2()
    exercise7_3()
    exercise8_1()
    exercise8_2()
    exercise8_3()
    exercise9_1()
}
