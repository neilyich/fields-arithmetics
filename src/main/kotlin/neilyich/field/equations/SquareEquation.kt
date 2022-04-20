package neilyich.field.equations

import neilyich.field.Field
import neilyich.field.element.FieldElement
import neilyich.field.polynomial.AFieldPolynomial
import neilyich.field.polynomial.FieldPolynomial

data class SquareEquation<Coef: FieldElement>(
    val field: Field<Coef>,
    val squareCoef: Coef,
    val linearCoef: Coef,
    val freeCoef: Coef
) {
    constructor(polynomial: AFieldPolynomial<Coef>): this(polynomial.field, polynomial[2], polynomial[1], polynomial[0]) {
        if (polynomial.degree() != 2) {
            throw IllegalArgumentException("polynomial of square equation must have degree = 2")
        }
    }

    val polynomial: AFieldPolynomial<Coef> = FieldPolynomial(field, 2 to squareCoef, 1 to linearCoef, 0 to freeCoef)

    override fun toString(): String {
        return "$polynomial = 0, x in $field"
    }
}
