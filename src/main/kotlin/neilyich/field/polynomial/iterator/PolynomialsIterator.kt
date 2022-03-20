package neilyich.field.polynomial.iterator

import neilyich.field.Field
import neilyich.field.element.FieldElement

class PolynomialsIterator<CoefsFieldElement: FieldElement>(
    field: Field<CoefsFieldElement>,
    private val degree: Int,
    literal: String
): APolynomialsIterator<CoefsFieldElement>(field, literal) {
    override val currentCoefs = (0..degree).map { field.zero() }.toMutableList()
    private var hasNext = true

    init {
        currentCoefs[degree] = field.one()
    }

    override fun hasNext(): Boolean = hasNext

    override fun overflow(pow: Int) {
        if (pow == degree) {
            hasNext = false
        }
    }
}