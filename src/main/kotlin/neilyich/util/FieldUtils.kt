package neilyich.util

import neilyich.field.Field
import neilyich.field.PolynomialField
import neilyich.field.PrimeField
import neilyich.field.element.FieldElement
import neilyich.field.element.PolynomialFieldElement
import neilyich.field.element.PrimeFieldElement

class FieldUtils {
    companion object {
        private const val literals = "xyzabcdefghijklmnopqrstuvw"

        fun primeField(p: Int): Field<PrimeFieldElement> = PrimeField(p)

        fun field(p: Int, n: Int): Field<out FieldElement> {
            if (n == 1) {
                return primeField(p)
            }
            return extend(primeField(p), n)
        }

        fun <AFieldElement: FieldElement> extend(field: Field<AFieldElement>, degree: Int): Field<PolynomialFieldElement<AFieldElement>> {
            val literal: String = if (field is PolynomialField<*>) {
                literals[literals.indexOf(field.literal) + 1].toString()
            } else {
                literals[0].toString()
            }
            return PolynomialField(FieldPolynomialUtils.findPrimitivePolynomial(field, degree, literal))
        }
    }
}