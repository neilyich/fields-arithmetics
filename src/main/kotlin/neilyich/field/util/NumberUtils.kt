package neilyich.field.util

import kotlin.math.sqrt

class NumberUtils {
    companion object {
        fun isPrime(_n: Int): Boolean {
            var n = _n
            if (n < 0) {
                n = -n
            }
            val sqrN = sqrt(n.toFloat()).toInt() + 1
            for (i in 2..sqrN) {
                if (n % i == 0) {
                    return false
                }
            }
            return true
        }

        fun extendedEuclidAlgorithm(_a: Int, _b: Int): Triple<Int, Int, Int> {
            var a = _a
            var b = _b
            var xa0 = 1
            var xb0 = 0
            var xa1 = 0
            var xb1 = 1
            var r = 2
            var q: Int
            while (r > 1) {
                r = a % b
                q = a / b
                val newXa1 = xa0 - xa1 * q
                val newXb1 = xb0 - xb1 * q
                xa0 = xa1
                xb0 = xb1
                xa1 = newXa1
                xb1 = newXb1
                a = b
                b = r
            }
            return Triple(xa1, xb1, r)
        }
    }
}