package neilyich.field.element

import neilyich.field.Field
import neilyich.field.polynomial.AFieldPolynomial

class NoCachePolynomialFieldElement<CoefsFieldElement: FieldElement>(
    field: Field<NoCachePolynomialFieldElement<CoefsFieldElement>>,
    polynomial: AFieldPolynomial<CoefsFieldElement>,
    discreteLogarithm: Int? = -1
) :
    PolynomialFieldElement<NoCachePolynomialFieldElement<CoefsFieldElement>, CoefsFieldElement>(field, polynomial, discreteLogarithm) {

    private var calcedDiscreteLogarithm = discreteLogarithm

    override fun discreteLogarithm(): Int? {
        if (hasDiscreteLogarithm()) {
            return calcedDiscreteLogarithm
        }
        calcedDiscreteLogarithm = calcDiscreteLogarithm()
        return calcedDiscreteLogarithm
    }

    private fun calcDiscreteLogarithm(): Int? {
        if (isZero()) {
            return null
        }
        val x = field.primitiveElement()
        var currentDiscreteLog = 0
        var current = this
        while (!current.isOne()) {
            currentDiscreteLog++
            current = field.div(current, x)
        }
        return currentDiscreteLog
    }

    fun hasDiscreteLogarithm(): Boolean = calcedDiscreteLogarithm != -1
}