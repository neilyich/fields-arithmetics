package neilyich.field.deserialization.factory

import neilyich.field.Field
import neilyich.field.element.FieldElement
import neilyich.field.polynomial.AFieldPolynomial
import neilyich.field.serialization.PolynomialDescription

interface PolynomialFactory {
    fun <Element: FieldElement> createPolynomial(polynomialDescription: PolynomialDescription, field: Field<Element>): AFieldPolynomial<Element>
}
