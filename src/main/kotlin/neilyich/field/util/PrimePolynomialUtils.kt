package neilyich.field.util

import neilyich.field.Field
import neilyich.field.element.FieldElement
import neilyich.field.polynomial.AFieldPolynomial
import neilyich.field.polynomial.OnePolynomial
import kotlin.math.pow
import kotlin.math.sqrt

class PrimePolynomialUtils {
    companion object {
        private fun <CoefsFieldElement: FieldElement> isPrime(polynomial: AFieldPolynomial<CoefsFieldElement>): Boolean {
            var prime = true
            forAllPolynomials(polynomial.field, polynomial.degree() / 2 + polynomial.degree() % 2) {
                if ((polynomial % it).isZero()) {
                    prime = false
                    return@forAllPolynomials true
                }
                return@forAllPolynomials false
            }
            return prime
        }

        fun <CoefsFieldElement: FieldElement> findPrimitiveElement(polynomial: AFieldPolynomial<CoefsFieldElement>): AFieldPolynomial<CoefsFieldElement> {
            if (!isPrime(polynomial)) {
                throw IllegalArgumentException("polynomial must be prime: $polynomial")
            }
            if (isPrimitive(polynomial)) {
                return OnePolynomial(polynomial.field, polynomial.literal).shift(1)
            }
            var primitiveElement: AFieldPolynomial<CoefsFieldElement>? = null
            val dividers = findMaxDividers(polynomial.field.size().toDouble().pow(polynomial.degree()).toInt() - 1)
            forAllPolynomials(polynomial.field, polynomial.degree() - 1, polynomial.literal) {
                var primitive = true
                for (divider in dividers) {
                    if (it.pow(divider, polynomial).isOne()) {
                        primitive = false
                        break
                    }
                }
                if (primitive) {
                    primitiveElement = it
                    return@forAllPolynomials true
                }
                return@forAllPolynomials false
            }
            return primitiveElement ?: throw IllegalArgumentException("polynomial is prime but has no primitive elements (fatal): $polynomial")
        }

        fun <CoefsFieldElement: FieldElement> findPrimitivePolynomial(field: Field<CoefsFieldElement>, degree: Int, literal: String = "x"): AFieldPolynomial<CoefsFieldElement> {
            var primitivePolynomial: AFieldPolynomial<CoefsFieldElement>? = null
            iterateAllPolynomials(field, degree, literal) {
                if (isPrimitive(it)) {
                    primitivePolynomial = it
                    return@iterateAllPolynomials true
                }
                return@iterateAllPolynomials false
            }
            return primitivePolynomial ?: throw IllegalArgumentException("no primitive polynomials of degree=$degree, field=$field")
        }

        private fun <CoefsFieldElement: FieldElement> isPrimitive(polynomial: AFieldPolynomial<CoefsFieldElement>): Boolean {
            if (!isPrime(polynomial)) {
                return false
            }
            val dividers = findMaxDividers(polynomial.field.size().toDouble().pow(polynomial.degree()).toInt() - 1)
            val x = OnePolynomial(polynomial.field).shift(1)
            for (divider in dividers) {
                val xPow = x.pow(divider, polynomial)
                if (xPow.isOne() || xPow.isZero()) {
                    return false
                }
            }
            return true
        }

        fun <CoefsFieldElement: FieldElement> forAllPolynomials(field: Field<CoefsFieldElement>, maxDegree: Int, literal: String = "x", canStop: (AFieldPolynomial<CoefsFieldElement>) -> Boolean) {
            for (degree in 1..maxDegree) {
                if(iterateAllPolynomials(field, degree, literal, canStop)) return
            }
        }

        private fun <CoefsFieldElement: FieldElement> iterateAllPolynomials(field: Field<CoefsFieldElement>, degree: Int, literal: String = "x", canStop: (AFieldPolynomial<CoefsFieldElement>) -> Boolean): Boolean {
            val basePolynomial = OnePolynomial(field, literal).shift(degree)
            if (canStop(basePolynomial)) return true
            return iterateAllPolynomials(basePolynomial, degree - 1, field.multiplicativeGroup(), canStop)
        }

        private fun <CoefsFieldElement: FieldElement> iterateAllPolynomials(basePolynomial: AFieldPolynomial<CoefsFieldElement>,
                                                                            currentDegree: Int, toAdd: Set<CoefsFieldElement>, canStop: (AFieldPolynomial<CoefsFieldElement>) -> Boolean): Boolean {
            if (currentDegree > 0) {
                if (iterateAllPolynomials(basePolynomial, currentDegree - 1, toAdd, canStop)) return true
            }
            for (e in toAdd) {
                val newBasePolynomial = basePolynomial.with(currentDegree, e)
                if (canStop(newBasePolynomial)) return true
                if (currentDegree > 0) {
                    if (iterateAllPolynomials(newBasePolynomial, currentDegree - 1, toAdd, canStop)) return true
                }
            }
            return false
        }

        private fun findMaxDividers(_n: Int): Set<Int> {
            val dividers = mutableSetOf<Int>()
            var n = _n
            var divider = 2
            val sqrtN = (sqrt(n.toDouble()) + 1).toInt()
            while (divider < sqrtN) {
                while (n % divider == 0) {
                    dividers.add(_n / divider)
                    n /= divider
                }
                if (divider == 2) divider++
                else divider += 2
            }
            return dividers
        }
    }
}