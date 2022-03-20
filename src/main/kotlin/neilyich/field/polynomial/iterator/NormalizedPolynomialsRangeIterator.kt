package neilyich.field.polynomial.iterator

import neilyich.field.Field
import neilyich.field.element.FieldElement
import neilyich.field.polynomial.AFieldPolynomial

class NormalizedPolynomialsRangeIterator<CoefsFieldElement: FieldElement>(
    private val field: Field<CoefsFieldElement>,
    private val maxDegree: Int,
    private val literal: String,
): Iterator<AFieldPolynomial<CoefsFieldElement>>, IPolynomialsRangeIterator<CoefsFieldElement> {
    private var currentDegree = 0
    private var currentIterator = NormalizedPolynomialsIterator(field, currentDegree, literal)

    override fun hasNext(): Boolean {
        return currentDegree < maxDegree || currentIterator.hasNext()
    }

    override fun next(): AFieldPolynomial<CoefsFieldElement> {
        if (currentIterator.hasNext()) {
            return currentIterator.next()
        }
        currentDegree++
        currentIterator = NormalizedPolynomialsIterator(field, currentDegree, literal)
        return currentIterator.next()
    }
}