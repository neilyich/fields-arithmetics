package neilyich.field

import neilyich.field.element.FieldElement
import neilyich.field.element.PolynomialFieldElement
import neilyich.field.polynomial.AFieldPolynomial
import neilyich.field.polynomial.FieldPolynomial
import neilyich.field.polynomial.ZeroPolynomial
import neilyich.util.FieldPolynomialUtils
import kotlin.math.pow

abstract class PolynomialField<InnerFieldElement : FieldElement, PolynomialElement : PolynomialFieldElement<*, InnerFieldElement>>(

    val mod: AFieldPolynomial<InnerFieldElement>
): Field<PolynomialElement>() {

    val literal = mod.literal
    protected val innerField = mod.field
    protected val primitiveElement: PolynomialElement by lazy {
        createElement(FieldPolynomialUtils.findPrimitiveElement(mod), 1)
    }

    override fun primitiveElement(): PolynomialElement = primitiveElement

    override fun zero(): PolynomialElement = createElement(ZeroPolynomial(innerField, literal), null)

    override fun one(): PolynomialElement = createElement(FieldPolynomial(innerField, mapOf(0 to innerField.one()), literal), 0)

    protected abstract fun getElementByPolynomial(_polynomial: AFieldPolynomial<InnerFieldElement>): PolynomialElement

    protected abstract fun createElement(_polynomial: AFieldPolynomial<InnerFieldElement>, discreteLogarithm: Int?): PolynomialElement

    override fun add(lhs: PolynomialElement, rhs: PolynomialElement): PolynomialElement {
        checkSameField(lhs, rhs)
        return getElementByPolynomial((lhs.polynomial + rhs.polynomial) % mod)
    }

    fun mult(lhs: PolynomialElement, rhs: InnerFieldElement): PolynomialElement {
        return getElementByPolynomial(lhs.polynomial.mult(rhs))
    }

    override fun inverseAdd(e: PolynomialElement): PolynomialElement {
        checkSameField(e)
        return getElementByPolynomial(-e.polynomial)
    }

    override fun characteristics(): Int = innerField.characteristics()
    override fun extensionDegree(): Int = innerField.extensionDegree() * mod.degree()
    override fun size(): Int = innerField.size().toDouble().pow(mod.degree()).toInt()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is PolynomialField<*, *>) return false

        if (mod != other.mod) return false

        return true
    }

    override fun hashCode(): Int = mod.hashCode()

    override fun toString(): String = "{ GF${characteristics()}^${extensionDegree()} ~ $innerField[$literal]/($mod) }"

    override fun innerField(): Field<InnerFieldElement> = innerField

    override fun contains(e: PolynomialElement): Boolean = e.polynomial.field == innerField && e.polynomial.degree() < mod.degree() && e.polynomial.literal == literal

}