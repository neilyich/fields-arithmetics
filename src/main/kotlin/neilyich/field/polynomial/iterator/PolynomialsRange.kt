package neilyich.field.polynomial.iterator

import neilyich.field.Field
import neilyich.field.element.FieldElement

fun <CoefsFieldElement: FieldElement> polynomialsRange(
    field: Field<CoefsFieldElement>,
    maxDegree: Int,
    literal: String = "x"
): PolynomialsRange<CoefsFieldElement> {
    return PolynomialsRange(field, maxDegree, literal)
}

fun <CoefsFieldElement: FieldElement> polynomials(
    field: Field<CoefsFieldElement>,
    degree: Int,
    literal: String = "x"
): PolynomialsRange<CoefsFieldElement> {
    return polynomialsRange(field, degree, literal)
}

class PolynomialsRange<CoefsFieldElement: FieldElement>(
    field: Field<CoefsFieldElement>,
    maxDegree: Int,
    literal: String
): APolynomialsRange<CoefsFieldElement>(field, maxDegree, literal, { f, dr, l -> PolynomialsRangeIterator(f, dr, l) })