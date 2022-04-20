package neilyich.util

import neilyich.field.Field
import neilyich.field.element.FieldElement
import neilyich.field.polynomial.*
import neilyich.util.NumberUtils.Companion.findSubExtensionDegrees
import neilyich.util.matrix.AFieldMatrix
import neilyich.util.matrix.MatrixUtils
import neilyich.util.matrix.VFieldVector
import neilyich.util.matrix.ZeroFieldMatrix

class FieldPolynomialUtils {
    companion object {
        fun <CoefsFieldElement: FieldElement> isPrime(polynomial: AFieldPolynomial<CoefsFieldElement>): Boolean {
            if (polynomial.degree() == 1) {
                return true
            }
            val q = polynomial.field.size()
            val x = OnePolynomial(polynomial.field, polynomial.literal).shift(1)
            if (polynomial.normalized() != GCD(x.pow(q.pow(polynomial.degree()), polynomial) - x, polynomial).normalized()) {
                return false
            }
            val dividers = findSubExtensionDegrees(polynomial.degree())
            for (d in dividers) {
                if (GCD(x.pow(q.pow(d), polynomial) - x, polynomial).degree() != 0) {
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
                val dividers = findSubExtensionDegrees(polynomial.field.size().pow(polynomial.degree()) - 1)
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
            val dividers = findSubExtensionDegrees(polynomial.field.size().pow(polynomial.degree()) - 1)
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
            return primitivePolynomial ?: throw IllegalArgumentException("no primitive normalizedPolynomials of degree=$degree, field=$field")
        }

        private fun <CoefsFieldElement: FieldElement> isPrimitive(polynomial: AFieldPolynomial<CoefsFieldElement>): Boolean {
            if (!isPrime(polynomial)) {
                return false
            }
            val x: AFieldPolynomial<CoefsFieldElement> = if (polynomial.degree() == 1) {
                if (polynomial[0].isZero()) {
                    return true
                }
                constant(polynomial.field, polynomial.field.inverseAdd(polynomial[0]), polynomial.literal)
            } else {
                OnePolynomial(polynomial.field).shift(1)
            }
            val dividers = findSubExtensionDegrees(polynomial.field.size().pow(polynomial.degree()) - 1)
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
            return extendedEuclidAlgorithm(a, b).third.normalized()
        }

        private fun <CoefsFieldElement: FieldElement> squareFreePolynomialFactorization(polynomial: AFieldPolynomial<CoefsFieldElement>, logger: (Any) -> Unit = {}): Set<AFieldPolynomial<CoefsFieldElement>> {
            logger("making factorization of square free polynomial f(x) = $polynomial")
            if (polynomial.degree() == 1) {
                logger("deg(f) = 1 => f(x) is only divided by itself")
                return setOf(polynomial)
            }
            val n = polynomial.degree()
            val field = polynomial.field
            val q = field.size()
            val literal = polynomial.literal
            var remaindersMatrix: AFieldMatrix<CoefsFieldElement> = ZeroFieldMatrix(field, n, 1)
            for (i in 1 until n) {
                val xPowIQMinusXPowI = x(field, i * q, literal) - x(field, i, literal)
                logger("((x)^${i * q} - (x)^$i) % f(x) = ${xPowIQMinusXPowI % polynomial}")
                remaindersMatrix = remaindersMatrix.concatRight(VFieldVector(field, (xPowIQMinusXPowI % polynomial).coefsList().reversed().startPad(n, field.zero())))
            }
            logger("Remainders matrix:\n$remaindersMatrix")
            val dividers = mutableSetOf(polynomial)
            val basis = MatrixUtils.solve(remaindersMatrix, logger)
            logger("f(x) = $polynomial must have ${basis.size} dividers")
            if (basis.size == 1) {
                logger("$polynomial is prime")
                return setOf(polynomial)
            }
            for (i in basis.indices) {
                logger("e$i(x) = ${FieldPolynomial(field, basis[i].toList(), literal)}")
            }
            for (i in 1 until basis.size) {
                logger("current dividers set: $dividers")
                val e = FieldPolynomial(field, basis[i].toList(), literal)
                val dividersTmp = dividers.toMutableSet()
                while (dividersTmp.isNotEmpty()) {
                    var f = dividersTmp.iterator().next()
                    dividersTmp.remove(f)
                    logger("calculating (F(x), e$i(x) - a) = ($f, $e - a)")
                    for (a in field) {
                        val d = GCD(f, e - constant(field, a, literal))
                        logger("a = $a, (F(x), e$i(x) - a) = ($f, ${e - constant(field, a, literal)}) = $d")
                        if (d.degree() == 0) {
                            continue
                        }
                        if (d.degree() == f.degree()) {
                            break
                        }
                        logger("remove F(x) = $f = $d * ${f / d} from dividers")
                        logger("add $d and ${f / d} to dividers")
                        dividers.remove(f)
                        dividers.add(d)
                        dividers.add(f / d)
                        if (dividers.size == basis.size) {
                            logger("found required ${basis.size} dividers")
                            return if (polynomial.isNormalized())
                                dividers.map {
                                    it.normalized()
                                }.toSet()
                            else dividers
                        }
                        logger("F(x) = F(x) / $d = $f / $d = ${f / d}")
                        f /= d
                    }
                }
            }
            throw RuntimeException("Unable to make factorization of polynomial $polynomial")
        }

        fun <CoefsFieldElement: FieldElement> polynomialFactorization(polynomial: AFieldPolynomial<CoefsFieldElement>, logger: (Any) -> Unit = {}): Map<AFieldPolynomial<CoefsFieldElement>, Int> {
            logger("making factorization of f(x) = $polynomial")
            val derivative = polynomial.derivative()
            logger("derivative: f'(x) = $derivative")
            val dividersMultiplicityMap = mutableMapOf<AFieldPolynomial<CoefsFieldElement>, Int>()
            if (derivative.isZero()) {
                if (polynomial.degree() == 0) {
                    logger("f(x) = $polynomial is constant and is only divided by itself")
                    return mapOf(polynomial to 1)
                }
                val newGCoefs = polynomial.coefs().mapKeys { it.key / polynomial.field.size() }
                val newG = FieldPolynomial(polynomial.field, newGCoefs, polynomial.literal)
                logger("f'(x) = 0 => making factorization of f(x)^(1/${polynomial.field.size()}) = $newG")
                val gFactorization = polynomialFactorization(newG, logger)
                for ((fi, ni) in gFactorization) {
                    dividersMultiplicityMap[fi] = polynomial.field.size() * ni
                }
                return dividersMultiplicityMap
            }
            val gcd = GCD(polynomial, derivative)
            logger("(f(x), f'(x)) = ($polynomial, $derivative) = $gcd")
            val squareFreePolynomial = polynomial / gcd
            logger("making factorization of square free polynomial: f(x) / (f(x), f'(x)) = $polynomial / $gcd = $squareFreePolynomial")
            val dividers = squareFreePolynomialFactorization(squareFreePolynomial, logger)
            logger("dividers of $squareFreePolynomial: $dividers")
            for (d in dividers) {
                var nj = 1
                var fnj = d.pow(nj + 1)
                while ((polynomial % fnj).isZero()) {
                    logger("$d^${nj + 1} = $fnj | $polynomial")
                    fnj *= d
                    nj++
                }
                logger("$d^${nj + 1} = $fnj !| $polynomial")
                dividersMultiplicityMap[d] = nj
            }
            var fini: AFieldPolynomial<CoefsFieldElement> = OnePolynomial(polynomial.field, polynomial.literal)
            for ((fi, ni) in dividersMultiplicityMap) {
                fini *= fi.pow(ni)
            }
            val factorizationDescription = factorizationDescription(dividersMultiplicityMap)
            logger("current factorization: f(x) = $polynomial = g(x) * $factorizationDescription = g(x) * $fini")
            val g = polynomial / fini
            logger("g(x) = $polynomial / $fini = $g")
            if (g.degree() == 0) {
                logger("g(x) = $g is constant => factorization completed: f(x) = $polynomial = $factorizationDescription")
                return dividersMultiplicityMap
            }
            val newGCoefs = g.coefs().mapKeys { it.key / polynomial.field.size() }
            val newG = FieldPolynomial(polynomial.field, newGCoefs, polynomial.literal)
            logger("g(x) = $g is not constant => making factorization of g(x)^(1/${polynomial.field.size()}) = $newG")
            val gFactorization = polynomialFactorization(newG, logger)
            logger("g(x)^(1/${polynomial.field.size()}) = $newG = ${factorizationDescription(gFactorization)}")
            for ((fi, ni) in gFactorization) {
                dividersMultiplicityMap[fi] = polynomial.field.size() * ni
            }
            logger("factorization completed: f(x) = g(x) * $fini = $polynomial = ${factorizationDescription(dividersMultiplicityMap)}")
            return dividersMultiplicityMap
        }

        fun <CoefsFieldElement: FieldElement> factorizationDescription(factorization: Map<AFieldPolynomial<CoefsFieldElement>, Int>): String {
            val builder = StringBuilder()
            for (pol in factorization.keys.sortedBy { it.degree() }) {
                builder.append(pol.toString())
                val pow = factorization[pol]!!
                if(pow > 1) {
                    builder.append("^").append(pow)
                }
            }
            return builder.toString()
        }
    }
}