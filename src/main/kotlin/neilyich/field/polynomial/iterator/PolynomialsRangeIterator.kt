package neilyich.field.polynomial.iterator

import neilyich.field.Field
import neilyich.field.element.FieldElement

class PolynomialsRangeIterator<CoefsFieldElement: FieldElement>(
    field: Field<CoefsFieldElement>,
    private val maxDegree: Int,
    literal: String
): APolynomialsIterator<CoefsFieldElement>(field, literal), IPolynomialsRangeIterator<CoefsFieldElement> {
    override val currentCoefs = (0..maxDegree).map { field.zero() }.toMutableList()
    private var hasNext = true

    override fun hasNext(): Boolean = hasNext

    override fun overflow(pow: Int) {
        if (pow == maxDegree) {
            hasNext = false
        }
    }
}