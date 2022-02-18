package neilyich.field.deserialization.factory

import neilyich.field.element.FieldElement
import neilyich.field.serialization.PolynomialRingDescription
import neilyich.ring.PolynomialRing

interface PolynomialRingFactory {
    fun createPolynomialRing(polynomialRingDescription: PolynomialRingDescription): PolynomialRing<out FieldElement>
}