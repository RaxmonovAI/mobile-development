package ex13interface

// Exercise 13.2 — Same hierarchy with an interface
interface Shape {
    fun area(): Double

    fun describe(): String = "This shape has an area of ${area()}"
}

class Circle(val radius: Double) : Shape {
    override fun area(): Double = Math.PI * radius * radius
}

class Rectangle(val width: Double, val height: Double) : Shape {
    override fun area(): Double = width * height
}

// Lost: an interface cannot have a constructor or store state (no backing fields),
//       so shared data/initialization can't live in Shape itself.
// Gained: a class can implement several interfaces at once, while it can
//         extend only one abstract class. Interfaces are more flexible.

fun main() {
    println(Circle(2.0).describe())
    println(Rectangle(3.0, 4.0).describe())
}
