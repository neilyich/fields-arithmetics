package neilyich.field.polynomial.iterator

import neilyich.field.Field
import neilyich.field.element.FieldElement
import neilyich.field.polynomial.AFieldPolynomial
import neilyich.field.polynomial.FieldPolynomial

abstract class APolynomialsIterator<CoefsFieldElement: FieldElement>(
    protected val field: Field<CoefsFieldElement>,
    private val literal: String
): Iterator<AFieldPolynomial<CoefsFieldElement>> {
    protected abstract val currentCoefs: MutableList<CoefsFieldElement> // = (0..degree).map { field.zero() }.toMutableList()
    private val one = field.one()


    abstract override fun hasNext(): Boolean

    override fun next(): AFieldPolynomial<CoefsFieldElement> {
        val result = FieldPolynomial(field, currentCoefs, literal)
        incCoefs()
        return result
    }

    protected abstract fun overflow(pow: Int)

    private fun incCoefs() {
        currentCoefs[0] = field.add(currentCoefs[0], one)
        var i = 0
        while (currentCoefs[i].isZero()) {
            overflow(i++)
            if (i == currentCoefs.size) {
                return
            }
            currentCoefs[i] = field.add(currentCoefs[i], one)
        }
    }
}