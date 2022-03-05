package neilyich.field.element

import neilyich.field.Field
import neilyich.field.polynomial.AFieldPolynomial

abstract class PolynomialFieldElement<Element: FieldElement, InnerElement: FieldElement>(
    override val field: Field<Element>,
    val polynomial: AFieldPolynomial<InnerElement>,
    val discreteLogarithm: Int?):
    FieldElement(field) {

    final override fun isZero(): Boolean = polynomial.isZero()

    final override fun isOne(): Boolean = polynomial.isOne()

    override fun discreteLogarithm(): Int? = discreteLogarithm

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is PolynomialFieldElement<*, *>) return false

        if (polynomial != other.polynomial) return false
        if (discreteLogarithm != other.discreteLogarithm) return false

        return true
    }

    override fun hashCode(): Int {
        var result = polynomial.hashCode()
        result = 31 * result + (discreteLogarithm ?: 0)
        return result
    }

    override fun toString(): String {
        return "($polynomial)"
    }
}