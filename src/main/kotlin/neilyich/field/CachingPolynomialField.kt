package neilyich.field

import neilyich.field.element.FieldElement
import neilyich.field.polynomial.AFieldPolynomial
import neilyich.field.polynomial.OnePolynomial
import neilyich.field.polynomial.ZeroPolynomial
import neilyich.util.NumberUtils

class CachingPolynomialField<InnerFieldElement: FieldElement>(
    mod: AFieldPolynomial<InnerFieldElement>
): PolynomialField<InnerFieldElement>(mod) {

    private val discreteLogarithmsMapping: Map<Int, AFieldPolynomial<InnerFieldElement>>
    private val polynomialsMapping: Map<AFieldPolynomial<InnerFieldElement>, Int>

    init {
        discreteLogarithmsMapping = mutableMapOf()
        polynomialsMapping = mutableMapOf()
        var currentElement: AFieldPolynomial<InnerFieldElement> = OnePolynomial(innerField, literal)
        for (pow in 0 until size() - 1) {
            discreteLogarithmsMapping[pow] = currentElement
            polynomialsMapping[currentElement] = pow
            currentElement = (currentElement * primitiveElement) % mod
        }
    }

    override fun element(discreteLogarithm: Int?): AFieldPolynomial<InnerFieldElement> {
        discreteLogarithm ?: return zero()
        return discreteLogarithmsMapping.getOrElse(NumberUtils.mod(discreteLogarithm, size() - 1)) {
            throw IllegalArgumentException("no element with discrete logarithm $discreteLogarithm")
        }
    }

    override fun discreteLogarithm(e: AFieldPolynomial<InnerFieldElement>): Int? {
        if (e.isZero()) return null
        return polynomialsMapping.getOrElse(e) {
            throw IllegalArgumentException("unable to find discrete logarithm of polynomial $e in field $this")
        }
    }

    override fun inverseMult(e: AFieldPolynomial<InnerFieldElement>): AFieldPolynomial<InnerFieldElement> {
        val dl = discreteLogarithm(e)
            ?: throw IllegalArgumentException("unable to find inverse multiplicative element for zero in field $this")
        return element(-dl)
    }

    override fun mult(
        lhs: AFieldPolynomial<InnerFieldElement>,
        rhs: AFieldPolynomial<InnerFieldElement>
    ): AFieldPolynomial<InnerFieldElement> {
        val dll = discreteLogarithm(lhs)
        val dlr = discreteLogarithm(rhs)
        if (dll == null || dlr == null) {
            return ZeroPolynomial(innerField, literal)
        }
        return element(NumberUtils.mod(dll + dlr, size() - 1))
    }

}