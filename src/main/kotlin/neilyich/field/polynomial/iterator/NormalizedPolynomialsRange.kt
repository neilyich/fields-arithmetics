package neilyich.field.polynomial.iterator

import neilyich.field.Field
import neilyich.field.element.FieldElement

fun <CoefsFieldElement: FieldElement> normalizedPolynomialsRange(
    field: Field<CoefsFieldElement>,
    maxDegree: Int,
    literal: String = "x"
): NormalizedPolynomialsRange<CoefsFieldElement> {
    return NormalizedPolynomialsRange(field, maxDegree, literal)
}

fun <CoefsFieldElement: FieldElement> normalizedPolynomials(
    field: Field<CoefsFieldElement>,
    degree: Int,
    literal: String = "x"
): NormalizedPolynomialsRange<CoefsFieldElement> {
    return normalizedPolynomialsRange(field, degree, literal)
}

class NormalizedPolynomialsRange<CoefsFieldElement: FieldElement>(
    field: Field<CoefsFieldElement>,
    maxDegree: Int,
    literal: String
): APolynomialsRange<CoefsFieldElement>(field, maxDegree, literal, { f, dr, l -> NormalizedPolynomialsRangeIterator(f, dr, l) })