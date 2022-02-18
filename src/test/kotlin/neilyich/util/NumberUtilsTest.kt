package neilyich.util

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class NumberUtilsTest {
    @Test
    fun isPrimeTest() {
        listOf(2, 3, 5, 7, 11, 13, 17, 23, 31, 7919).forEach {
            assertTrue(NumberUtils.isPrime(it), it.toString())
        }
        listOf(4, 6, 8, 12, 15, 33, 7917).forEach {
            assertFalse(NumberUtils.isPrime(it), it.toString())
        }
    }

    @Test
    fun extendedEuclidAlgorithmTest() {
        var a = 10
        var b = 13
        var (na, nb, r) = NumberUtils.extendedEuclidAlgorithm(a, b)
        assertEquals(1, r)
        assertEquals(1, mod(a * na, b))
        assertEquals(1, mod(b * nb, a))
        a = 20
        b = 12
        var triple = NumberUtils.extendedEuclidAlgorithm(a, b)
        na = triple.first
        nb = triple.second
        r = triple.third
        assertEquals(4, r)
        assertEquals(r, mod(a * na, b))
        assertEquals(r, mod(b * nb, a))

        val n = 46
        triple = NumberUtils.extendedEuclidAlgorithm(n, n)
        assertEquals(n, triple.third)
        assertEquals(n, n * triple.first + n * triple.second)
    }

    private fun mod(a: Int, mod: Int): Int {
        val r = a % mod
        return if (r >= 0) r else r + mod
    }
}