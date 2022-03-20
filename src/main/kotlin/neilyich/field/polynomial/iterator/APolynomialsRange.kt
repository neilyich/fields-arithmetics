package neilyich.field.polynomial.iterator

import neilyich.field.Field
import neilyich.field.element.FieldElement
import neilyich.field.polynomial.AFieldPolynomial

abstract class APolynomialsRange<CoefsFieldElement: FieldElement>(
    private val field: Field<CoefsFieldElement>,
    private val maxDegree: Int,
    private val literal: String,
    private val polynomialsRangeIteratorProvider: (Field<CoefsFieldElement>, Int, String) -> IPolynomialsRangeIterator<CoefsFieldElement>
): Iterable<AFieldPolynomial<CoefsFieldElement>> {
    override fun iterator(): Iterator<AFieldPolynomial<CoefsFieldElement>> {
        return polynomialsRangeIteratorProvider(field, maxDegree, literal)
    }
}
