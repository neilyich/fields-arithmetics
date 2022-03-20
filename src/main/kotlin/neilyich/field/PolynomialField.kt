package neilyich.field

import neilyich.field.element.FieldElement
import neilyich.field.polynomial.AFieldPolynomial
import neilyich.ring.PolynomialRing
import neilyich.util.FieldPolynomialUtils
import neilyich.util.pow

abstract class PolynomialField<InnerFieldElement : FieldElement>(mod: AFieldPolynomial<InnerFieldElement>):
    PolynomialRing<InnerFieldElement>(mod), Field<AFieldPolynomial<InnerFieldElement>> {

    protected val primitiveElement: AFieldPolynomial<InnerFieldElement> = FieldPolynomialUtils.findPrimitiveElement(mod)

    final override fun primitiveElement(): AFieldPolynomial<InnerFieldElement> = primitiveElement

    final override fun iterator(): Iterator<AFieldPolynomial<InnerFieldElement>> {
        return super<Field>.iterator()
    }

    final override fun characteristics(): Int = innerField.characteristics()
    final override fun extensionDegree(): Int = innerField.extensionDegree() * mod.degree()
    final override fun size(): Int = innerField.size().pow(mod.degree())

    final override fun innerField(): Field<InnerFieldElement> = innerField

    final override fun contains(e: AFieldPolynomial<InnerFieldElement>): Boolean = e.field == innerField && e.degree() < mod.degree() && e.literal == literal

    final override fun toString(): String = "{ GF${characteristics()}^${extensionDegree()} ~ $innerField[$literal]/$mod, a=$primitiveElement }"

    final override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is PolynomialField<*>) return false
        if (!super.equals(other)) return false

        if (primitiveElement != other.primitiveElement) return false

        return true
    }

    final override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + primitiveElement.hashCode()
        return result
    }

}