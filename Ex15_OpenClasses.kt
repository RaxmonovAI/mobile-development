package ex15

// Exercise 15.1 — Vehicle -> Car
open class Vehicle(val make: String, val model: String) {
    open fun honk(): String = "Beep!"
}

class Car(make: String, model: String, val numberOfDoors: Int) : Vehicle(make, model) {
    override fun honk(): String = "Honk honk!"
}

fun main() {
    val vehicle = Vehicle("Generic", "Model X")
    println("${vehicle.make} ${vehicle.model}: ${vehicle.honk()}")

    val car = Car("Chevrolet", "Cobalt", 4)
    println("${car.make} ${car.model} (${car.numberOfDoors} doors): ${car.honk()}")
}
