package neilyich.field.serialization.description.factory

import neilyich.field.element.FieldElement
import neilyich.field.serialization.PolynomialRingDescription
import neilyich.ring.PolynomialRing

interface PolynomialRingDescriptionFactory {
    fun createPolynomialRingDescription(ring: PolynomialRing<out FieldElement>): PolynomialRingDescription
}