package neilyich.util

import neilyich.field.Field
import neilyich.field.element.FieldElement
import neilyich.field.polynomial.AFieldPolynomial
import neilyich.field.polynomial.FieldPolynomial
import neilyich.field.polynomial.OnePolynomial
import neilyich.field.polynomial.ZeroPolynomial
import kotlin.math.pow
import kotlin.math.sqrt

class FieldPolynomialUtils {
    companion object {
        fun <CoefsFieldElement: FieldElement> isPrime(polynomial: AFieldPolynomial<CoefsFieldElement>): Boolean {
            if (polynomial.degree() == 1) {
                return true
            }
            val q = polynomial.field.size()
            val x = OnePolynomial(polynomial.field, polynomial.literal).shift(1)
            if (polynomial.normalized() != GCD(x.pow(q.toDouble().pow(polynomial.degree()).toInt(), polynomial) - x, polynomial).normalized()) {
                return false
            }
            val dividers = findSubExtensionDegrees(polynomial.degree())
            for (d in dividers) {
                if (GCD(x.pow(q.toDouble().pow(d).toInt(), polynomial) - x, polynomial).degree() != 0) {
                    return false
                }
            }
            return true
        }

        fun <CoefsFieldElement: FieldElement> findPrimitiveElement(polynomial: AFieldPolynomial<CoefsFieldElement>): AFieldPolynomial<CoefsFieldElement> {
            if (!isPrime(polynomial)) {
                throw IllegalArgumentException("polynomial must be prime: $polynomial")
            }
            if (isPrimitive(polynomial)) {
                if (polynomial.degree() > 1) {
                    return OnePolynomial(polynomial.field, polynomial.literal).shift(1)
                }
                val dividers = findSubExtensionDegrees(polynomial.field.size().toDouble().pow(polynomial.degree()).toInt() - 1)
                for (e in polynomial.field.multiplicativeGroup()) {
                    val pol = FieldPolynomial(polynomial.field, polynomial.literal, 0 to e)
                    var primitive = true
                    for (divider in dividers) {
                        if (pol.pow(divider, polynomial).isOne()) {
                            primitive = false
                            break
                        }
                    }
                    if (primitive) {
                        return pol
                    }
                }
                throw IllegalArgumentException("could not find primitive element for polynomial with degree <= 1: $polynomial")
            }
            var primitiveElement: AFieldPolynomial<CoefsFieldElement>? = null
            val dividers = findSubExtensionDegrees(polynomial.field.size().toDouble().pow(polynomial.degree()).toInt() - 1)
            forAllPolynomialsUntil(polynomial.field, 0 until polynomial.degree(), polynomial.literal) {
                var primitive = true
                for (divider in dividers) {
                    if (it.pow(divider, polynomial).isOne()) {
                        primitive = false
                        break
                    }
                }
                if (primitive) {
                    primitiveElement = it
                    return@forAllPolynomialsUntil true
                }
                return@forAllPolynomialsUntil false
            }
            return primitiveElement ?: throw IllegalArgumentException("polynomial is prime but has no primitive elements (fatal): $polynomial")
        }

        fun <CoefsFieldElement: FieldElement> findPrimitivePolynomial(field: Field<CoefsFieldElement>, degree: Int, literal: String = "x"): AFieldPolynomial<CoefsFieldElement> {
            var primitivePolynomial: AFieldPolynomial<CoefsFieldElement>? = null
            iterateAllPolynomialsUntil(field, degree, literal) {
                if (isPrimitive(it)) {
                    primitivePolynomial = it
                    return@iterateAllPolynomialsUntil true
                }
                return@iterateAllPolynomialsUntil false
            }
            return primitivePolynomial ?: throw IllegalArgumentException("no primitive polynomials of degree=$degree, field=$field")
        }

        private fun <CoefsFieldElement: FieldElement> isPrimitive(polynomial: AFieldPolynomial<CoefsFieldElement>): Boolean {
            if (!isPrime(polynomial)) {
                return false
            }
            val dividers = findSubExtensionDegrees(polynomial.field.size().toDouble().pow(polynomial.degree()).toInt() - 1)
            val x = OnePolynomial(polynomial.field).shift(1)
            for (divider in dividers) {
                val xPow = x.pow(divider, polynomial)
                if (xPow.isOne() || xPow.isZero()) {
                    return false
                }
            }
            return true
        }

        fun <CoefsFieldElement: FieldElement> forAllPolynomials(field: Field<CoefsFieldElement>, degreeRange: IntRange, literal: String = "x", action: (AFieldPolynomial<CoefsFieldElement>) -> Unit) {
            forAllPolynomialsUntil(field, degreeRange, literal) {
                action(it)
                return@forAllPolynomialsUntil false
            }
        }

        fun <CoefsFieldElement: FieldElement> forAllPolynomials(field: Field<CoefsFieldElement>, degree: Int, literal: String = "x", action: (AFieldPolynomial<CoefsFieldElement>) -> Unit) {
            forAllPolynomialsUntil(field, degree..degree, literal) {
                action(it)
                return@forAllPolynomialsUntil false
            }
        }

        fun <CoefsFieldElement: FieldElement> forAllPolynomialsUntil(field: Field<CoefsFieldElement>, degreeRange: IntRange, literal: String = "x", canStop: (AFieldPolynomial<CoefsFieldElement>) -> Boolean) {
            for (degree in degreeRange) {
                if(iterateAllPolynomialsUntil(field, degree, literal, canStop)) return
            }
        }

        private fun <CoefsFieldElement: FieldElement> iterateAllPolynomialsUntil(field: Field<CoefsFieldElement>, degree: Int, literal: String = "x", canStop: (AFieldPolynomial<CoefsFieldElement>) -> Boolean): Boolean {
            val basePolynomial = OnePolynomial(field, literal).shift(degree)
            if (canStop(basePolynomial)) return true
            if (degree > 0) {
                return iterateAllPolynomialsUntil(basePolynomial, degree - 1, field.multiplicativeGroup(), canStop)
            }
            return false
        }

        private fun <CoefsFieldElement: FieldElement> iterateAllPolynomialsUntil(basePolynomial: AFieldPolynomial<CoefsFieldElement>,
                                                                                 currentDegree: Int, toAdd: Iterable<CoefsFieldElement>, canStop: (AFieldPolynomial<CoefsFieldElement>) -> Boolean): Boolean {
            if (currentDegree > 0) {
                if (iterateAllPolynomialsUntil(basePolynomial, currentDegree - 1, toAdd, canStop)) return true
            }
            for (e in toAdd) {
                val newBasePolynomial = basePolynomial.with(currentDegree, e)
                if (canStop(newBasePolynomial)) return true
                if (currentDegree > 0) {
                    if (iterateAllPolynomialsUntil(newBasePolynomial, currentDegree - 1, toAdd, canStop)) return true
                }
            }
            return false
        }

        private fun findSubExtensionDegrees(_n: Int): Set<Int> {
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
            if (dividers.isEmpty()) {
                dividers.add(1)
            }
            return dividers
        }

        fun <CoefsFieldElement: FieldElement> extendedEuclidAlgorithm(_a: AFieldPolynomial<CoefsFieldElement>,
                                                                      _b: AFieldPolynomial<CoefsFieldElement>):
                Triple<
                        AFieldPolynomial<CoefsFieldElement>,
                        AFieldPolynomial<CoefsFieldElement>,
                        AFieldPolynomial<CoefsFieldElement>> {
            if (_a.field != _b.field || _a.literal != _b.literal) {
                throw IllegalArgumentException("unable to execute euclid algorithm with polynomials on different fields: $_a, $_b")
            }
            if (_a.degree() < _b.degree()) {
                val result = extendedEuclidAlgorithm(_b, _a)
                return Triple(result.second, result.first, result.third)
            }
            var a = _a
            var b = _b
            var xa0: AFieldPolynomial<CoefsFieldElement> = OnePolynomial(a.field, a.literal)
            var xb0: AFieldPolynomial<CoefsFieldElement> = ZeroPolynomial(a.field, a.literal)
            var xa1: AFieldPolynomial<CoefsFieldElement> = ZeroPolynomial(a.field, a.literal)
            var xb1: AFieldPolynomial<CoefsFieldElement> = OnePolynomial(a.field, a.literal)
            var r = OnePolynomial(a.field, a.literal).shift(1)
            var q: AFieldPolynomial<CoefsFieldElement>
            while (r.degree() > 0) {
                r = a % b
                if (r.isZero()) {
                    return Triple(xa1, xb1, b)
                }
                q = a / b
                val newXa1 = xa0 - xa1 * q
                val newXb1 = xb0 - xb1 * q
                xa0 = xa1
                xb0 = xb1
                xa1 = newXa1
                xb1 = newXb1
                a = b
                b = r
            }
            return Triple(xa1, xb1, r)

        }

        // greatest common divisor
        fun <CoefsFieldElement: FieldElement> GCD(a: AFieldPolynomial<CoefsFieldElement>,
                                                  b: AFieldPolynomial<CoefsFieldElement>):
                AFieldPolynomial<CoefsFieldElement> {

            if (a.isZero() && !b.isZero()) {
                return b
            }
            if (b.isZero() && !a.isZero()) {
                return a
            }
            if (a.isZero() && b.isZero()) {
                return ZeroPolynomial(a.field, literal = a.literal)
            }
            return extendedEuclidAlgorithm(a, b).third
        }
    }
}