package neilyich.util

import neilyich.field.*
import neilyich.field.element.FieldElement
import neilyich.field.element.PrimeFieldElement
import neilyich.field.polynomial.AFieldPolynomial
import kotlin.math.ceil
import kotlin.math.sqrt

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

        fun <AFieldElement: FieldElement> extend(field: Field<AFieldElement>, degree: Int): PolynomialField<AFieldElement> {
            val literal = findLiteral(field)
            return field(FieldPolynomialUtils.findPrimitivePolynomial(field, degree, literal))
        }

        fun <AFieldElement: FieldElement> field(mod: AFieldPolynomial<AFieldElement>, maxCachingSize: Int? = null): PolynomialField<AFieldElement> {
            val size = mod.field.size().pow(mod.degree())
            if (size > (maxCachingSize ?: maxCachingFieldSize)) {
                return NoCachePolynomialField(mod)
            }
            return CachingPolynomialField(mod)
        }

        private fun findLiteral(field: Field<*>): String {
            return if (field is PolynomialField<*>) {
                literals[literals.indexOf(field.literal) + 1].toString()
            } else {
                literals[0].toString()
            }
        }

        fun <Element: FieldElement> calcDiscreteLogarithm(element: Element, field: Field<Element>): Int? {
            if (element.isZero()) {
                return null
            }
            if (element.isOne()) {
                return 0
            }
            if (element == field.primitiveElement()) {
                return 1
            }
            val n = field.size() - 1
            val m = ceil(sqrt(n.toDouble())).toInt()
            val am = field.element(m)
            var currentElement = am
            val iMapping = mutableMapOf<Element, Int>()
            for (i in 1..m) {
                if (iMapping.contains(currentElement)) {
                    continue
                }
                val tmp = currentElement
                currentElement = field.mult(currentElement, am)
                iMapping[tmp] = i
            }
            var bj = element
            for (j in 0 until m) {
                iMapping[bj]?.let { return (it * m - j) % n }
                bj = field.mult(bj, field.primitiveElement())
            }
            throw RuntimeException("unable to calculate discrete logarithm for $element in $field")
        }
    }
}