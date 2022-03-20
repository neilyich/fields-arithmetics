package neilyich.field.polynomial.iterator

import neilyich.field.element.FieldElement
import neilyich.field.polynomial.AFieldPolynomial

interface IPolynomialsRangeIterator<CoefsFieldElement: FieldElement>: Iterator<AFieldPolynomial<CoefsFieldElement>> {
    override fun hasNext(): Boolean
    override fun next(): AFieldPolynomial<CoefsFieldElement>
}