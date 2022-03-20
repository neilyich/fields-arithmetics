package neilyich.util

fun Int.modPow(exponent: Int, mod: Int): Int = toBigInteger().modPow(exponent.toBigInteger(), mod.toBigInteger()).toInt()

fun Int.pow(exponent: Int): Int = toBigInteger().pow(exponent).toInt()

fun Int.modInverse(mod: Int): Int = toBigInteger().modInverse(mod.toBigInteger()).toInt()

fun <T> List<T>.startPad(desiredLength: Int, elementsProvider: (Int) -> T): List<T> {
    return (0 until desiredLength - size).map(elementsProvider) + this
}

fun <T> List<T>.startPad(desiredLength: Int, element: T): List<T> {
    return startPad(desiredLength) { element }
}
