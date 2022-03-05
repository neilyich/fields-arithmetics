package neilyich.field

import neilyich.field.element.CachingPolynomialFieldElement
import neilyich.field.element.FieldElement
import neilyich.field.polynomial.AFieldPolynomial
import neilyich.field.polynomial.OnePolynomial

class CachingPolynomialField<InnerFieldElement: FieldElement>(
    mod: AFieldPolynomial<InnerFieldElement>
): PolynomialField<InnerFieldElement, CachingPolynomialFieldElement<InnerFieldElement>>(mod) {

    private val discreteLogarithmsMapping: Map<Int, CachingPolynomialFieldElement<InnerFieldElement>>
    private val polynomialsMapping: Map<AFieldPolynomial<InnerFieldElement>, CachingPolynomialFieldElement<InnerFieldElement>>

    init {
        discreteLogarithmsMapping = mutableMapOf()
        polynomialsMapping = mutableMapOf()
        var currentElement: AFieldPolynomial<InnerFieldElement> = OnePolynomial(innerField, literal)
        for (pow in 0 until size() - 1) {
            val polynomialFieldElement = createElement(currentElement, pow)
            discreteLogarithmsMapping[pow] = polynomialFieldElement
            polynomialsMapping[currentElement] = polynomialFieldElement
            currentElement = (currentElement * primitiveElement.polynomial) % mod
        }
        val zero = zero()
        polynomialsMapping[zero.polynomial] = zero
    }

    override fun element(discreteLogarithm: Int?): CachingPolynomialFieldElement<InnerFieldElement> {
        discreteLogarithm ?: return zero()
        val mod = size() - 1
        val aN = if (discreteLogarithm < 0) {
            discreteLogarithm % mod + mod
        } else {
            discreteLogarithm % mod
        }
        return discreteLogarithmsMapping.getOrElse(aN) { throw IllegalArgumentException("no element with discrete logarithm $discreteLogarithm ~ $aN") }
    }

    override fun getElementByPolynomial(_polynomial: AFieldPolynomial<InnerFieldElement>): CachingPolynomialFieldElement<InnerFieldElement> {
        val polynomial = _polynomial % mod
        return polynomialsMapping.getOrElse(polynomial) { throw IllegalArgumentException("no element with such polynomial representation $polynomial, field: ${toString()}") }
    }

    override fun createElement(_polynomial: AFieldPolynomial<InnerFieldElement>, discreteLogarithm: Int?): CachingPolynomialFieldElement<InnerFieldElement> {
        return CachingPolynomialFieldElement(this, _polynomial, discreteLogarithm)
    }

    override fun mult(lhs: CachingPolynomialFieldElement<InnerFieldElement>, rhs: CachingPolynomialFieldElement<InnerFieldElement>): CachingPolynomialFieldElement<InnerFieldElement> {
        checkSameField(lhs, rhs)
        if (lhs.discreteLogarithm == null || rhs.discreteLogarithm == null) {
            return zero()
        }
        return element(lhs.discreteLogarithm + rhs.discreteLogarithm)
    }

    override fun inverseMult(e: CachingPolynomialFieldElement<InnerFieldElement>): CachingPolynomialFieldElement<InnerFieldElement> {
        checkSameField(e)
        if (e.discreteLogarithm == null) {
            throw IllegalArgumentException("cannot find inverse multiplicative element for 0")
        }
        return element(-e.discreteLogarithm)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is CachingPolynomialField<*>) return false

        if (mod != other.mod) return false

        return true
    }

    override fun hashCode(): Int {
        var result = mod.hashCode()
        result = 31 * result + discreteLogarithmsMapping.hashCode()
        result = 31 * result + polynomialsMapping.hashCode()
        return result
    }

}