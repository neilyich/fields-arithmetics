package neilyich.field.polynomial

import neilyich.field.Field
import neilyich.field.element.FieldElement

class OnePolynomial<CoefsFieldElement: FieldElement>(field: Field<CoefsFieldElement>, literal: String = "x"): AFieldPolynomial<CoefsFieldElement>(field, literal) {
    override fun coefs(): Map<Int, CoefsFieldElement> = mapOf(0 to field.one())

    override fun degree(): Int = 0

    override fun get(pow: Int): CoefsFieldElement = if (pow == 0) field.one() else field.zero()

    override fun with(pow: Int, e: CoefsFieldElement): AFieldPolynomial<CoefsFieldElement> = FieldPolynomial(field, mapOf(0 to field.one(), pow to e), literal)

    override fun valueAt(e: CoefsFieldElement): CoefsFieldElement = field.one()

    override fun plus(other: AFieldPolynomial<CoefsFieldElement>): AFieldPolynomial<CoefsFieldElement> = other.with(0, field.add(field.one(), other[0]))

    override fun times(other: AFieldPolynomial<CoefsFieldElement>): AFieldPolynomial<CoefsFieldElement> = other

    override fun divide(other: AFieldPolynomial<CoefsFieldElement>): Pair<AFieldPolynomial<CoefsFieldElement>, AFieldPolynomial<CoefsFieldElement>> {
        if (other.isZero()) {
            throw IllegalArgumentException("divider must not be 0")
        }
        if (other.degree() == 0) {
            return Pair(FieldPolynomial(field, mapOf(0 to field.inverseMult(other[0])), literal), ZeroPolynomial(field, literal))
        }
        return Pair(ZeroPolynomial(field, literal), this)
    }

    override fun shift(n: Int): AFieldPolynomial<CoefsFieldElement> = with(0, field.zero()).with(n, field.one())

    override fun mult(e: CoefsFieldElement): AFieldPolynomial<CoefsFieldElement> = with(0, e)

    override fun normalized(): AFieldPolynomial<CoefsFieldElement> = this

    override fun reverse(): AFieldPolynomial<CoefsFieldElement> = this
}