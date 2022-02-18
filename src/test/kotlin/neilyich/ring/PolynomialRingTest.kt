package neilyich.ring

import neilyich.field.PrimeField
import neilyich.field.polynomial.FieldPolynomial
import org.junit.jupiter.api.Test

class PolynomialRingTest: UnitalRingTest() {
    @Test
    fun test() {
        var field = PrimeField(3)
        var polynomial = FieldPolynomial(field, mapOf(
            3 to field(1),
            2 to field(2),
            1 to field(1),
            0 to field(1),
        ))
        var ring = PolynomialRing(polynomial)
        checkUnitalRingAxioms(ring)

        field = PrimeField(5)
        polynomial = FieldPolynomial(field, mapOf(
            2 to field(1),
            1 to field(3),
            0 to field(4),
        ))
        ring = PolynomialRing(polynomial)
        checkUnitalRingAxioms(ring)
    }
}