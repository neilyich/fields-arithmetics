package neilyich.ring

import neilyich.field.element.FieldElement
import neilyich.field.polynomial.AFieldPolynomial
import neilyich.field.polynomial.OnePolynomial
import neilyich.field.polynomial.ZeroPolynomial
import neilyich.util.FieldPolynomialUtils
import kotlin.math.pow

class PolynomialRing<Coef: FieldElement>(val mod: AFieldPolynomial<Coef>): UnitalRing<AFieldPolynomial<Coef>>() {
    val literal = mod.literal
    private val coefsField = mod.field

    override fun zero(): AFieldPolynomial<Coef> = ZeroPolynomial(coefsField, literal)

    override fun one(): AFieldPolynomial<Coef> = OnePolynomial(coefsField, literal)

    override fun createElements(): Iterable<AFieldPolynomial<Coef>> {
        val elements = mutableListOf<AFieldPolynomial<Coef>>(ZeroPolynomial(coefsField, literal))
        FieldPolynomialUtils.forAllPolynomials(coefsField, 0 until mod.degree(), literal) {
            elements.add(it)
            for (coef in coefsField) {
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

    override fun size(): Int = coefsField.size().toDouble().pow(mod.degree()).toInt()

    override fun contains(e: AFieldPolynomial<Coef>): Boolean = coefsField == e.field && e.degree() < mod.degree()

    override fun toString(): String {
        return "$coefsField[$literal]/($mod)"
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

}