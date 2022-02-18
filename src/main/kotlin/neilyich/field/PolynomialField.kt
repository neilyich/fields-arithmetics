package neilyich.field

import neilyich.field.element.FieldElement
import neilyich.field.element.PolynomialFieldElement
import neilyich.field.polynomial.AFieldPolynomial
import neilyich.field.polynomial.FieldPolynomial
import neilyich.field.polynomial.OnePolynomial
import neilyich.field.polynomial.ZeroPolynomial
import neilyich.util.FieldPolynomialUtils
import kotlin.math.pow

class PolynomialField<InnerFieldElement: FieldElement>(val mod: AFieldPolynomial<InnerFieldElement>): Field<PolynomialFieldElement<InnerFieldElement>>() {
    val literal = mod.literal
    private val innerField = mod.field
    private val primitiveElement: PolynomialFieldElement<InnerFieldElement>
    private val discreteLogarithmsMapping: Map<Int, PolynomialFieldElement<InnerFieldElement>>
    private val polynomialsMapping: Map<AFieldPolynomial<InnerFieldElement>, PolynomialFieldElement<InnerFieldElement>>

    init {
        discreteLogarithmsMapping = mutableMapOf()
        polynomialsMapping = mutableMapOf()
        val primitiveElement = FieldPolynomialUtils.findPrimitiveElement(mod)
        this.primitiveElement = PolynomialFieldElement(this, primitiveElement, 1)
        var currentElement: AFieldPolynomial<InnerFieldElement> = OnePolynomial(innerField, literal)
        for (pow in 0 until size() - 1) {
            val polynomialFieldElement = PolynomialFieldElement(this, currentElement, pow)
            discreteLogarithmsMapping[pow] = polynomialFieldElement
            polynomialsMapping[currentElement] = polynomialFieldElement
            currentElement = (currentElement * primitiveElement) % mod
        }
        val zero = zero()
        polynomialsMapping[zero.polynomial] = zero
    }

    override fun zero(): PolynomialFieldElement<InnerFieldElement> = PolynomialFieldElement(this, ZeroPolynomial(innerField, literal), null)

    override fun one(): PolynomialFieldElement<InnerFieldElement> = PolynomialFieldElement(this, FieldPolynomial(innerField, mapOf(0 to innerField.one()), literal), 0)

    override fun element(discreteLogarithm: Int?): PolynomialFieldElement<InnerFieldElement> {
        discreteLogarithm ?: return zero()
        val aN: Int
        val mod = size() - 1
        aN = if (discreteLogarithm < 0) {
            discreteLogarithm % mod + mod
        } else {
            discreteLogarithm % mod
        }
        return discreteLogarithmsMapping.getOrElse(aN) { throw IllegalArgumentException("no element with discrete logarithm $discreteLogarithm ~ $aN") }
    }

    private fun getElementByPolynomial(_polynomial: AFieldPolynomial<InnerFieldElement>): PolynomialFieldElement<InnerFieldElement> {
        val polynomial = _polynomial % mod
        return polynomialsMapping.getOrElse(polynomial) { throw IllegalArgumentException("no element with such polynomial representation $polynomial") }
    }

    override fun add(lhs: PolynomialFieldElement<InnerFieldElement>, rhs: PolynomialFieldElement<InnerFieldElement>): PolynomialFieldElement<InnerFieldElement> {
        checkSameField(lhs, rhs)
        return getElementByPolynomial((lhs.polynomial + rhs.polynomial) % mod)
    }

    override fun mult(lhs: PolynomialFieldElement<InnerFieldElement>, rhs: PolynomialFieldElement<InnerFieldElement>): PolynomialFieldElement<InnerFieldElement> {
        checkSameField(lhs, rhs)
        if (lhs.discreteLogarithm == null || rhs.discreteLogarithm == null) {
            return zero()
        }
        return element(lhs.discreteLogarithm + rhs.discreteLogarithm)
    }

    fun mult(lhs: PolynomialFieldElement<InnerFieldElement>, rhs: InnerFieldElement): PolynomialFieldElement<InnerFieldElement> {
        return getElementByPolynomial(lhs.polynomial.mult(rhs))
    }

    override fun inverseAdd(e: PolynomialFieldElement<InnerFieldElement>): PolynomialFieldElement<InnerFieldElement> {
        checkSameField(e)
        return getElementByPolynomial(-e.polynomial)
    }

    override fun inverseMult(e: PolynomialFieldElement<InnerFieldElement>): PolynomialFieldElement<InnerFieldElement> {
        checkSameField(e)
        if (e.discreteLogarithm == null) {
            throw IllegalArgumentException("cannot find inverse multiplicative element for 0")
        }
        return element(-e.discreteLogarithm)
    }

    override fun characteristics(): Int = innerField.characteristics()
    override fun extensionDegree(): Int = innerField.extensionDegree() * mod.degree()
    override fun size(): Int = innerField.size().toDouble().pow(mod.degree()).toInt()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is PolynomialField<*>) return false

        if (mod != other.mod) return false

        return true
    }

    override fun hashCode(): Int {
        var result = mod.hashCode()
        result = 31 * result + discreteLogarithmsMapping.hashCode()
        result = 31 * result + polynomialsMapping.hashCode()
        return result
    }

    override fun toString(): String {
        return "{ GF${characteristics()}^${extensionDegree()} ~ $innerField[$literal]/($mod) }"
    }

    override fun innerField(): Field<out FieldElement> = innerField

}