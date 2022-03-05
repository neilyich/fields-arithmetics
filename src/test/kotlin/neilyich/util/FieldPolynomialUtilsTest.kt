package neilyich.util

import neilyich.field.PrimeField
import neilyich.field.polynomial.FieldPolynomial
import org.junit.jupiter.api.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class FieldPolynomialUtilsTest {
    @Test
    fun test() {
        val f = PrimeField(5)
        listOf(
            FieldPolynomial(f, f(1), f(0), f(2)),
            FieldPolynomial(f, f(1), f(4), f(2)),
            FieldPolynomial(f, f(1), f(0), f(0), f(0), f(2)),
        ).forEach {
            assertTrue(FieldPolynomialUtils.isPrime(it), it.toString())
        }

        listOf(
            FieldPolynomial(f, f(1), f(0), f(4)),
            FieldPolynomial(f, f(1), f(2), f(2)),
            FieldPolynomial(f, f(1), f(4), f(3)),
            FieldPolynomial(f, f(1), f(0), f(0), f(0), f(1)),
        ).forEach {
            assertFalse(FieldPolynomialUtils.isPrime(it), it.toString())
        }
    }
}