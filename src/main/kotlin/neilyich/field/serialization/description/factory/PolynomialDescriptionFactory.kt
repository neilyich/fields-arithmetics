package neilyich.field.serialization.description.factory

import neilyich.field.element.FieldElement
import neilyich.field.polynomial.AFieldPolynomial
import neilyich.field.serialization.PolynomialDescription

internal interface PolynomialDescriptionFactory {
    fun createPolynomialDescription(polynomial: AFieldPolynomial<out FieldElement>): PolynomialDescription
}