package neilyich.field

import neilyich.field.element.FieldElement
import neilyich.field.element.NoCachePolynomialFieldElement
import neilyich.field.polynomial.AFieldPolynomial
import neilyich.field.polynomial.x
import neilyich.util.FieldPolynomialUtils

class NoCachePolynomialField<InnerFieldElement: FieldElement>(

    mod: AFieldPolynomial<InnerFieldElement>
): PolynomialField<InnerFieldElement, NoCachePolynomialFieldElement<InnerFieldElement>>(mod) {

    override fun mult(lhs: NoCachePolynomialFieldElement<InnerFieldElement>, rhs: NoCachePolynomialFieldElement<InnerFieldElement>): NoCachePolynomialFieldElement<InnerFieldElement> {
        return createElement((lhs.polynomial * rhs.polynomial) % mod)
    }

    override fun element(discreteLogarithm: Int?): NoCachePolynomialFieldElement<InnerFieldElement> {
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
        return createElement(x.pow(pow, this.mod))
    }

    override fun inverseMult(e: NoCachePolynomialFieldElement<InnerFieldElement>): NoCachePolynomialFieldElement<InnerFieldElement> {
        val (ne, _, d) = FieldPolynomialUtils.extendedEuclidAlgorithm(e.polynomial, mod)
        if (d.degree() > 0 || d.isZero()) {
            throw IllegalArgumentException("unable to find inverse multiplicative polynomial: (${e.polynomial})^-1 mod ($mod)")
        }
        if (d[0].isOne()) {
            return createElement(ne)
        }
        return createElement(ne.mult(innerField.inverseMult(d[0])))
    }

    override fun getElementByPolynomial(_polynomial: AFieldPolynomial<InnerFieldElement>): NoCachePolynomialFieldElement<InnerFieldElement> {
        return createElement(_polynomial % mod)
    }

    override fun createElement(_polynomial: AFieldPolynomial<InnerFieldElement>, discreteLogarithm: Int?): NoCachePolynomialFieldElement<InnerFieldElement> {
        return NoCachePolynomialFieldElement(this, _polynomial, discreteLogarithm)
    }

    private fun createElement(polynomial: AFieldPolynomial<InnerFieldElement>): NoCachePolynomialFieldElement<InnerFieldElement> = createElement(polynomial, -1)

    override fun iterator(): Iterator<NoCachePolynomialFieldElement<InnerFieldElement>> = NoCacheFieldIterator()

    override fun createMultiplicativeGroup(): Iterable<NoCachePolynomialFieldElement<InnerFieldElement>> {
        return Iterable { NoCacheFieldIterator().apply { next() } }
    }

    private inner class NoCacheFieldIterator: Iterator<NoCachePolynomialFieldElement<InnerFieldElement>> {
        private var currentPow = -1
        private val maxPow = size() - 1
        private var xPow = zero().polynomial
        private val x = primitiveElement.polynomial

        override fun hasNext(): Boolean = currentPow < maxPow

        override fun next(): NoCachePolynomialFieldElement<InnerFieldElement> {
            if (currentPow == 0) {
                xPow = one().polynomial
            }
            else if (currentPow != -1) {
                xPow = (xPow * x) % mod
            }
            currentPow++
            return createElement(xPow)
        }
    }
}