package neilyich.util

import kotlin.math.sqrt

class NumberUtils {
    companion object {
        fun isPrime(_n: Int): Boolean {
            var n = _n
            if (n < 0) {
                n = -n
            }
            if (n == 1 || n == 2) {
                return true
            }
            if (n % 2 == 0) {
                return false
            }
            val sqrN = sqrt(n.toFloat()).toInt() + 1
            for (i in 3..sqrN step 2) {
                if (n % i == 0) {
                    return false
                }
            }
            return true
        }

        fun extendedEuclidAlgorithm(_a: Int, _b: Int): Triple<Int, Int, Int> {
            if (_a < _b) {
                val result = extendedEuclidAlgorithm(_b, _a)
                return Triple(result.second, result.first, result.third)
            }
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
                if (r == 0) {
                    return Triple(xa1, xb1, b)
                }
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

        fun mod(value: Int, mod: Int): Int {
            if (value < 0) {
                return value % mod + mod
            }
            return value % mod
        }

        fun findPrimitiveElement(p: Int): Int {
            if (p == 2) {
                return 1
            }
            val dividers = findSubExtensionDegrees(p - 1)
            for (i in 2 until p) {
                var primitive = true
                for (d in dividers) {
                    val iPowD = i.modPow(d, p)
                    if (iPowD == 1 || iPowD == 0) {
                        primitive = false
                        break
                    }
                }
                if (primitive) {
                    return i
                }
            }
            throw IllegalArgumentException("unable to find primitive element mod $p")
        }

        fun findSubExtensionDegrees(_n: Int): Set<Int> {
            val dividers = mutableSetOf<Int>()
            var n = _n
            var divider = 2
            val sqrtN = (sqrt(n.toDouble()) + 1).toInt()
            while (divider < sqrtN) {
                while (n % divider == 0) {
                    dividers.add(_n / divider)
                    n /= divider
                }
                if (divider == 2) divider++
                else divider += 2
            }
            if (dividers.isEmpty()) {
                dividers.add(1)
            }
            return dividers
        }

    }
}