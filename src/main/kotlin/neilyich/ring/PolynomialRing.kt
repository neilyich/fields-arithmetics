package neilyich.ring

import neilyich.field.element.FieldElement
import neilyich.field.polynomial.AFieldPolynomial
import neilyich.field.polynomial.OnePolynomial
import neilyich.field.polynomial.ZeroPolynomial
import neilyich.field.polynomial.constant
import neilyich.util.FieldPolynomialUtils
import neilyich.field.polynomial.iterator.PolynomialsRangeIterator
import neilyich.util.pow

open class PolynomialRing<Coef: FieldElement>(val mod: AFieldPolynomial<Coef>): UnitalRing<AFieldPolynomial<Coef>> {
    val literal = mod.literal
    protected val innerField = mod.field

    override fun zero(): AFieldPolynomial<Coef> = ZeroPolynomial(innerField, literal)

    override fun one(): AFieldPolynomial<Coef> = OnePolynomial(innerField, literal)

    private fun createElements(): Iterable<AFieldPolynomial<Coef>> {
        val elements = mutableListOf<AFieldPolynomial<Coef>>(ZeroPolynomial(innerField, literal))
        FieldPolynomialUtils.forAllPolynomials(innerField, 0 until mod.degree(), literal) {
            elements.add(it)
            for (coef in innerField) {
                if (!coef.isZero() && !coef.isOne()) {
                    elements.add(it.mult(coef))
                }
            }
        }
        return elements
    }

    override fun add(lhs: AFieldPolynomial<Coef>, rhs: AFieldPolynomial<Coef>): AFieldPolynomial<Coef> = lhs + rhs

    override fun mult(lhs: AFieldPolynomial<Coef>, rhs: AFieldPolynomial<Coef>): AFieldPolynomial<Coef> = (lhs * rhs) % mod

    override fun inverseAdd(e: AFieldPolynomial<Coef>): AFieldPolynomial<Coef> = -e

    override fun size(): Int = innerField.size().pow(mod.degree())

    override fun contains(e: AFieldPolynomial<Coef>): Boolean = innerField == e.field && e.degree() < mod.degree()

    override fun toString(): String {
        return "$innerField[$literal]/$mod"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is PolynomialRing<*>) return false

        if (mod != other.mod) return false

        return true
    }

    override fun hashCode(): Int {
        return mod.hashCode()
    }

    override fun iterator(): Iterator<AFieldPolynomial<Coef>> {
        return PolynomialsRangeIterator(innerField, mod.degree() - 1, literal)
    }

    override fun one(times: Int): AFieldPolynomial<Coef> {
        return constant(innerField, innerField.one(times), literal)
    }

    override fun fromString(str: String): AFieldPolynomial<Coef> {
        return AFieldPolynomial.fromString(str.trim(), innerField, literal) % mod
    }

}