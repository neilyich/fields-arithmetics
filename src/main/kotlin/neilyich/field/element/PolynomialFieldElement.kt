package neilyich.field.element

import neilyich.field.Field
import neilyich.field.polynomial.AFieldPolynomial

class PolynomialFieldElement<CoefsFieldElement: FieldElement>(field: Field<out PolynomialFieldElement<CoefsFieldElement>>, val polynomial: AFieldPolynomial<CoefsFieldElement>, val discreteLogarithm: Int? = null): FieldElement(field) {
    override fun isZero(): Boolean = polynomial.isZero()

    override fun isOne(): Boolean = polynomial.degree() == 0 && polynomial[0].isOne()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is PolynomialFieldElement<*>) return false

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