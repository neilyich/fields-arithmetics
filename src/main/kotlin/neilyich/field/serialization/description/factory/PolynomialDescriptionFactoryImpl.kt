package neilyich.field.serialization.description.factory

import neilyich.field.element.FieldElement
import neilyich.field.polynomial.AFieldPolynomial
import neilyich.field.serialization.FieldDescription
import neilyich.field.serialization.PolynomialCoefDescription
import neilyich.field.serialization.PolynomialDescription

class PolynomialDescriptionFactoryImpl : PolynomialDescriptionFactory {
    override fun <CoefsFieldElement:FieldElement> createPolynomialDescription(polynomial: AFieldPolynomial<CoefsFieldElement>, fieldDescription: FieldDescription): PolynomialDescription {
        val coefs = mutableListOf<PolynomialCoefDescription>()
        for ((pow, coef) in polynomial.coefs().entries.sortedBy{ (k, _) -> k }) {
            if (!coef.isZero()) coefs.add(PolynomialCoefDescription(pow, polynomial.field.discreteLogarithm(coef)))
        }
        return PolynomialDescription(fieldDescription, coefs, polynomial.literal)
    }
}