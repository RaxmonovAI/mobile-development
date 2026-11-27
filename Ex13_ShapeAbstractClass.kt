package ex13abstract

// Exercise 13.1 — Shape hierarchy with an abstract class
abstract class Shape {
    abstract fun area(): Double

    fun describe(): String = "This shape has an area of ${area()}"
}

class Circle(val radius: Double) : Shape() {
    override fun area(): Double = Math.PI * radius * radius
}

class Rectangle(val width: Double, val height: Double) : Shape() {
    override fun area(): Double = width * height
}

fun main() {
    println(Circle(2.0).describe())
    println(Rectangle(3.0, 4.0).describe())
}
