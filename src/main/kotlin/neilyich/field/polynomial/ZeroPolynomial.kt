package neilyich.field.polynomial

import neilyich.field.Field
import neilyich.field.element.FieldElement

class ZeroPolynomial<CoefsFieldElement: FieldElement>(field: Field<CoefsFieldElement>, literal: String = "x"): AFieldPolynomial<CoefsFieldElement>(field, literal) {
    override fun coefs(): Map<Int, CoefsFieldElement> = mapOf(0 to field.zero())

    override fun degree(): Int = 0

    override fun get(pow: Int): CoefsFieldElement = field.zero()

    override fun with(pow: Int, e: CoefsFieldElement): AFieldPolynomial<CoefsFieldElement> = FieldPolynomial(field, mapOf(pow to e), literal)

    override fun valueAt(e: CoefsFieldElement): CoefsFieldElement = field.zero()

    override fun plus(other: AFieldPolynomial<CoefsFieldElement>): AFieldPolynomial<CoefsFieldElement> = other

    override fun times(other: AFieldPolynomial<CoefsFieldElement>): AFieldPolynomial<CoefsFieldElement> = this

    override fun divide(other: AFieldPolynomial<CoefsFieldElement>): Pair<AFieldPolynomial<CoefsFieldElement>, AFieldPolynomial<CoefsFieldElement>> {
        if (other.isZero()) {
            throw IllegalArgumentException("divider must not be 0")
        }
        return Pair(this, this)
    }

    override fun shift(n: Int): AFieldPolynomial<CoefsFieldElement> = this

    override fun mult(e: CoefsFieldElement): AFieldPolynomial<CoefsFieldElement> = this

    override fun normalized(): AFieldPolynomial<CoefsFieldElement> = this

    override fun reverse(): AFieldPolynomial<CoefsFieldElement> = this
}