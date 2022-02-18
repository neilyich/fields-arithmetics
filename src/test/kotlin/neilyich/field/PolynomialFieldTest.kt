package neilyich.field

import neilyich.field.polynomial.FieldPolynomial
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class PolynomialFieldTest: FieldTest() {
    @Test
    fun test() {
        val f = PrimeField(5)
        listOf(
            FieldPolynomial(f, f(1), f(0), f(2)),
            FieldPolynomial(f, f(1), f(4), f(2)),
            FieldPolynomial(f, f(1), f(1), f(0), f(2)),
        ).forEach {
            checkFieldAxioms(PolynomialField(it))
        }

        listOf(
            FieldPolynomial(f, f(1), f(0), f(4)),
            FieldPolynomial(f, f(1), f(2), f(2)),
            FieldPolynomial(f, f(1), f(4), f(3)),
        ).forEach {
            assertThrows<IllegalArgumentException> {
                PolynomialField(it)
            }
        }
    }
}