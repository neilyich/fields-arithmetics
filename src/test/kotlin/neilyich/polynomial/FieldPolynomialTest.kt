package neilyich.polynomial

import neilyich.field.PrimeField
import neilyich.field.polynomial.FieldPolynomial
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class FieldPolynomialTest {
    @Test
    fun testMult() {
        var f = PrimeField(5)
        val a1 = FieldPolynomial(f, 3 to f(1), 1 to f(4))
        val a2 = FieldPolynomial(f, 6 to f(1), 4 to f(3), 2 to f(1))
        assertEquals(a2, a1 * a1)

        f = PrimeField(7)
        val a = FieldPolynomial(f, f(1), f(3), f(0), f(5))
        val b = FieldPolynomial(f, f(2), f(6), f(4))
        val ab = FieldPolynomial(f, f(2), f(5), f(1), f(1), f(2), f(6))
        assertEquals(ab, a * b, "$a * $b")
    }

    @Test
    fun testDiv() {
        val f = PrimeField(7)
        val a = FieldPolynomial(f, f(1), f(3), f(0), f(5))
        val b = FieldPolynomial(f, f(2), f(6), f(4))
        val ab = FieldPolynomial(f, f(2), f(5), f(1), f(1), f(2), f(6))
        assertEquals(a, ab / b, "$ab / $b")
        assertEquals(b, ab / a, "$ab / $a")
    }
}