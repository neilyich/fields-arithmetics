package neilyich.field

import neilyich.field.element.FieldElement
import neilyich.field.polynomial.AFieldPolynomial
import neilyich.field.polynomial.x
import neilyich.util.FieldPolynomialUtils
import neilyich.util.FieldUtils

class NoCachePolynomialField<InnerFieldElement: FieldElement>(mod: AFieldPolynomial<InnerFieldElement>): PolynomialField<InnerFieldElement>(mod) {

    override fun element(discreteLogarithm: Int?): AFieldPolynomial<InnerFieldElement> {
        if (discreteLogarithm == null) {
            return zero()
        }
        if (discreteLogarithm == 0) {
            return one()
        }
        val mod = size() - 1
        val pow = if (discreteLogarithm < 0) {
            discreteLogarithm % mod + mod
        } else {
            discreteLogarithm % mod
        }
        val x = x(innerField, literal)
        return x.pow(pow, this.mod)
    }

    override fun discreteLogarithm(e: AFieldPolynomial<InnerFieldElement>): Int? {
        return FieldUtils.calcDiscreteLogarithm(e, this)
    }

    override fun inverseMult(e: AFieldPolynomial<InnerFieldElement>): AFieldPolynomial<InnerFieldElement> {
        val (ne, _, d) = FieldPolynomialUtils.extendedEuclidAlgorithm(e, mod)
        if (d.degree() > 0 || d.isZero()) {
            throw IllegalArgumentException("unable to find inverse multiplicative polynomial: ($e)^-1 mod ($mod)")
        }
        if (d[0].isOne()) {
            return ne
        }
        return ne.mult(innerField.inverseMult(d[0]))
    }
}