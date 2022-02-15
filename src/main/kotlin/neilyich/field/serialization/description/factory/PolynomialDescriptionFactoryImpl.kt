package neilyich.field.serialization.description.factory

import neilyich.field.element.FieldElement
import neilyich.field.polynomial.AFieldPolynomial
import neilyich.field.serialization.PolynomialCoefDescription
import neilyich.field.serialization.PolynomialDescription

internal class PolynomialDescriptionFactoryImpl : PolynomialDescriptionFactory {
    override fun createPolynomialDescription(polynomial: AFieldPolynomial<out FieldElement>): PolynomialDescription {
        val coefs = mutableListOf<PolynomialCoefDescription>()
        for ((pow, coef) in polynomial.coefs()) {
            if (!coef.isZero()) coefs.add(PolynomialCoefDescription(pow, coef.number()))
        }
        return PolynomialDescription(coefs)
    }
}