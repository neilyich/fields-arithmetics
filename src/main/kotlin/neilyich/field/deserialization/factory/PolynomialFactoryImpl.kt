package neilyich.field.deserialization.factory

import neilyich.field.Field
import neilyich.field.element.FieldElement
import neilyich.field.polynomial.AFieldPolynomial
import neilyich.field.polynomial.FieldPolynomial
import neilyich.field.serialization.PolynomialDescription

class PolynomialFactoryImpl : PolynomialFactory {

    override fun <Element: FieldElement> createPolynomial(polynomialDescription: PolynomialDescription, field: Field<Element>): AFieldPolynomial<Element> {
        val coefs = polynomialDescription.coefs.associate { it.pow to field.element(it.n) }
        return FieldPolynomial(field, coefs, polynomialDescription.literal)
    }

}