package neilyich.field.serialization.deserialization.factory

import neilyich.field.Field
import neilyich.field.element.FieldElement
import neilyich.field.polynomial.AFieldPolynomial
import neilyich.field.polynomial.FieldPolynomial
import neilyich.field.serialization.PolynomialDescription

internal class PolynomialFactoryImpl : PolynomialFactory {

    override fun <Element: FieldElement> createPolynomial(polynomialDescription: PolynomialDescription, field: Field<Element>, literal: String): AFieldPolynomial<Element> {
        val coefs = polynomialDescription.coefs.associate { it.pow to field.element(it.n) }
        return FieldPolynomial(field, coefs, literal)
    }

}