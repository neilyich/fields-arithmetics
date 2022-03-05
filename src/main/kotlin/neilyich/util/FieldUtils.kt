package neilyich.util

import neilyich.field.Field
import neilyich.field.CachingPolynomialField
import neilyich.field.NoCachePolynomialField
import neilyich.field.PrimeField
import neilyich.field.element.FieldElement
import neilyich.field.element.PolynomialFieldElement
import neilyich.field.element.PrimeFieldElement
import kotlin.math.pow

class FieldUtils {
    companion object {
        private const val literals = "xyzabcdefghijklmnopqrstuvw"
        private const val maxCachingFieldSize = 2 shl 12

        fun primeField(p: Int): Field<PrimeFieldElement> = PrimeField(p)

        fun field(p: Int, n: Int): Field<out FieldElement> {
            if (n == 1) {
                return primeField(p)
            }
            return extend(primeField(p), n)
        }

        fun <AFieldElement: FieldElement> extend(field: Field<AFieldElement>, degree: Int): Field<out PolynomialFieldElement<*, AFieldElement>> {
            val literal: String = if (field is CachingPolynomialField<*>) {
                literals[literals.indexOf(field.literal) + 1].toString()
            } else {
                literals[0].toString()
            }
            val size = field.size().toDouble().pow(degree).toInt()
            if (size > maxCachingFieldSize) {
                return NoCachePolynomialField(FieldPolynomialUtils.findPrimitivePolynomial(field, degree, literal))
            }
            return CachingPolynomialField(FieldPolynomialUtils.findPrimitivePolynomial(field, degree, literal))
        }
    }
}