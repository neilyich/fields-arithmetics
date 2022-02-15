package neilyich.field.serialization.deserialization.factory

import neilyich.field.Field
import neilyich.field.element.FieldElement
import neilyich.field.polynomial.AFieldPolynomial
import neilyich.field.serialization.PolynomialDescription

internal interface PolynomialFactory {
    fun <Element: FieldElement> createPolynomial(polynomialDescription: PolynomialDescription, field: Field<Element>, literal: String = "x"): AFieldPolynomial<Element>
}