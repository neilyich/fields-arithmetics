package neilyich.field.serialization.description.factory

import neilyich.field.element.FieldElement
import neilyich.field.polynomial.AFieldPolynomial
import neilyich.field.serialization.FieldDescription
import neilyich.field.serialization.PolynomialDescription

interface PolynomialDescriptionFactory {
    fun <CoefsFieldElement:FieldElement> createPolynomialDescription(polynomial: AFieldPolynomial<CoefsFieldElement>, fieldDescription: FieldDescription): PolynomialDescription
}