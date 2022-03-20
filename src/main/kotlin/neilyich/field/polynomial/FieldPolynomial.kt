package neilyich.field.polynomial

import neilyich.field.Field
import neilyich.field.element.FieldElement
import kotlin.math.max

class FieldPolynomial<CoefsFieldElement: FieldElement>(field: Field<CoefsFieldElement>, coefs: Map<Int, CoefsFieldElement> = mapOf(), literal: String = "x"): AFieldPolynomial<CoefsFieldElement>(field, literal), Cloneable {
    private val coefs: Map<Int, CoefsFieldElement> = coefs.filterValues { !it.isZero() }
    private val degree: Int = coefs.keys.filter{!coefs[it]!!.isZero()}.maxOrNull() ?: 0

    init {
        if ((coefs.keys.filter{!coefs[it]!!.isZero()}.minOrNull() ?: 0) < 0) {
            throw IllegalArgumentException("polynomial coefs must be non negative: $coefs")
        }
    }

    constructor(field: Field<CoefsFieldElement>, literal: String, vararg coefsList: CoefsFieldElement):
            this(field, coefsList.mapIndexed{ i, c -> (coefsList.size - 1 - i) to c }.toMap(), literal)

    constructor(field: Field<CoefsFieldElement>, literal: String, vararg coefsPairs: Pair<Int, CoefsFieldElement>):
            this(field, coefsPairs.toMap(), literal)

    constructor(field: Field<CoefsFieldElement>, vararg coefsList: CoefsFieldElement):
            this(field, coefsList.mapIndexed{ i, c -> (coefsList.size - 1 - i) to c }.toMap())

    constructor(field: Field<CoefsFieldElement>, vararg coefsPairs: Pair<Int, CoefsFieldElement>):
            this(field, coefsPairs.toMap())

    constructor(field: Field<CoefsFieldElement>, coefs: List<CoefsFieldElement>, literal: String):
            this(field, coefs.mapIndexed{ i, c -> i to c}.toMap(), literal)

    override fun degree(): Int = degree
    override fun coefs(): Map<Int, CoefsFieldElement> = coefs

    override fun get(pow: Int): CoefsFieldElement = coefs[pow] ?: field.zero()

    override fun with(pow: Int, e: CoefsFieldElement): AFieldPolynomial<CoefsFieldElement> {
        val newCoefs = coefs.toMutableMap()
        newCoefs[pow] = e
        return FieldPolynomial(field, newCoefs.toMap(), literal)
    }

    override fun valueAt(e: CoefsFieldElement): CoefsFieldElement {
        var currentValue = field.zero()
        for (pow in degree() downTo 0) {
            currentValue = field.add(this[pow], field.mult(currentValue, e))
        }
        return currentValue
    }

    override fun plus(other: AFieldPolynomial<CoefsFieldElement>): AFieldPolynomial<CoefsFieldElement> {
        val newCoefs = coefs.toMutableMap()
        for((pow, e) in other.coefs()) {
            val newCoef = field.add(this[pow], e)
            if (newCoef.isZero()) {
                newCoefs.remove(pow)
            }
            else {
                newCoefs[pow] = newCoef
            }
        }
        return FieldPolynomial(field, newCoefs.toMap(), literal)
    }

    override fun times(other: AFieldPolynomial<CoefsFieldElement>): AFieldPolynomial<CoefsFieldElement> {
        return timesRecursive(this, other, null)
    }

    companion object {
        private const val maxDegreeOfSimpleMultiplication = 6
        private const val maxDegreeOfSimpleDivision = 6

        private fun <CoefsFieldElement: FieldElement> simpleMult(a: AFieldPolynomial<CoefsFieldElement>,
                                                                 b: AFieldPolynomial<CoefsFieldElement>,
                                                                 xPowMod: Int?): AFieldPolynomial<CoefsFieldElement> {
            var result: AFieldPolynomial<CoefsFieldElement> = FieldPolynomial(a.field, literal = a.literal)
            for ((pow, e) in b.coefs()) {
                result += a.shift(pow).mult(e)
            }
            if (xPowMod == null ) return result
            val coefs = result.coefs().filter { (pow, _) -> pow < xPowMod }
            return FieldPolynomial(a.field, coefs, a.literal)
        }

        private fun <CoefsFieldElement: FieldElement> splitPolynomial(pol: AFieldPolynomial<CoefsFieldElement>, borderDegree: Int):
                Pair<AFieldPolynomial<CoefsFieldElement>, AFieldPolynomial<CoefsFieldElement>> {

            val leftCoefs = mutableMapOf<Int, CoefsFieldElement>()
            val rightCoefs = mutableMapOf<Int, CoefsFieldElement>()
            for ((pow, coef) in pol.coefs()) {
                if (pow > borderDegree) {
                    leftCoefs[pow - borderDegree - 1] = coef
                } else {
                    rightCoefs[pow] = coef
                }
            }
            return FieldPolynomial(pol.field, leftCoefs, pol.literal) to FieldPolynomial(pol.field, rightCoefs, pol.literal)
        }

        private fun <CoefsFieldElement: FieldElement> shift(a: AFieldPolynomial<CoefsFieldElement>, n: Int, xPowMod: Int?): AFieldPolynomial<CoefsFieldElement> {
            val maxDegree = (xPowMod ?: (a.degree() + n + 1)) - 1
            val newCoefs = mutableMapOf<Int, CoefsFieldElement>()
            for ((pow, coef) in a.coefs()) {
                val newDegree = pow + n
                if (newDegree <= maxDegree) {
                    newCoefs[newDegree] = coef
                }
            }
            return FieldPolynomial(a.field, newCoefs.toMap(), a.literal)
        }

        private fun <CoefsFieldElement: FieldElement> timesRecursive(a: AFieldPolynomial<CoefsFieldElement>, b: AFieldPolynomial<CoefsFieldElement>, xPowMod: Int?):
                AFieldPolynomial<CoefsFieldElement> {

            if (a.degree() <= maxDegreeOfSimpleMultiplication || b.degree() <= maxDegreeOfSimpleMultiplication) {
                return simpleMult(a, b, xPowMod)
            }
            val n = max(a.degree(), b.degree())
            val splitDegree = n / 2
            val shiftN = (splitDegree + 1) * 2
            val (a1, a2) = splitPolynomial(a, splitDegree)
            val (b1, b2) = splitPolynomial(b, splitDegree)
            val a1b1 = timesRecursive(a1, b1, xPowMod)
            val a2b2 = timesRecursive(a2, b2, xPowMod)
            return shift(a1b1, shiftN, xPowMod) + a2b2 + shift(timesRecursive(a1 + a2, b1 + b2, xPowMod) - a1b1 - a2b2, splitDegree + 1, xPowMod)
        }

        private fun <CoefsFieldElement: FieldElement> simpleDivide(a: AFieldPolynomial<CoefsFieldElement>, b: AFieldPolynomial<CoefsFieldElement>):
                Pair<AFieldPolynomial<CoefsFieldElement>, AFieldPolynomial<CoefsFieldElement>> {

            if (b.isZero()) {
                throw IllegalArgumentException("divider must not be 0")
            }
            if (b.degree() > a.degree()) {
                return ZeroPolynomial(a.field, a.literal) to a
            }
            val firstDividerCoef = b[b.degree()]
            val quotientCoefs = mutableMapOf<Int, CoefsFieldElement>()
            var r: AFieldPolynomial<CoefsFieldElement> = a
            for(i in a.degree() downTo b.degree()) {
                val firstCoef = r[i]
                val resultCoef = a.field.div(firstCoef, firstDividerCoef)
                val currentQuotient = b.shift(i - b.degree()).mult(resultCoef)
                r -= currentQuotient
                quotientCoefs[i - b.degree()] = resultCoef
            }
            return FieldPolynomial(a.field, quotientCoefs.toMap(), a.literal) to r
        }

        private fun <CoefsFieldElement: FieldElement> findInversePolynomial(a: AFieldPolynomial<CoefsFieldElement>, xPowMod: Int): AFieldPolynomial<CoefsFieldElement> {
            if (a[0].isZero()) {
                throw IllegalArgumentException("polynomial $a is not coprime with ${a.literal}^$xPowMod")
            }
            var currentInverse: AFieldPolynomial<CoefsFieldElement> = FieldPolynomial(a.field, a.literal, a.field.inverseMult(a[0]))
            var currentXPowMod = 1
            while (currentXPowMod < xPowMod) {
                currentXPowMod = currentXPowMod shl 1
                currentInverse = currentInverse.mult(a.field.add(a.field.one(), a.field.one())) -
                        timesRecursive(timesRecursive(a, currentInverse, currentXPowMod), currentInverse, currentXPowMod)
            }
            return currentInverse
        }

        private fun <CoefsFieldElement: FieldElement> divide(a: AFieldPolynomial<CoefsFieldElement>, b: AFieldPolynomial<CoefsFieldElement>):
                AFieldPolynomial<CoefsFieldElement> {
            if (a.degree() <= maxDegreeOfSimpleDivision || b.degree() <= maxDegreeOfSimpleDivision) {
                return simpleDivide(a, b).first
            }
            if (b.isZero()) {
                throw IllegalArgumentException("divider must not be 0")
            }
            if (b.degree() > a.degree()) {
                return ZeroPolynomial(b.field, b.literal)
            }
            if (a[0].isZero() || b[0].isZero()) {
                val k = getLowestCoefDegree(a)
                val l = getLowestCoefDegree(b)
                return if (k >= l) {
                    val shiftA = a.shift(-l)
                    val shiftB = b.shift(-l)
                    val shiftAPlusOne = shiftA + OnePolynomial(a.field, a.literal)
                    divide(shiftAPlusOne, shiftB)
                } else {
                    val q = divide(a.shift(-k), b.shift(-l))
                    val dif = l - k
                    val qCoefs = mutableMapOf<Int, CoefsFieldElement>()
                    for ((pow, coef) in q.coefs()) {
                        val newPow = pow - dif
                        if (newPow >= 0) {
                            qCoefs[newPow] = coef
                        }
                    }
                    FieldPolynomial(a.field, qCoefs, a.literal)
                }
            }
            val na = a.reverse()
            val nb = b.reverse()
            val xPowMod = a.degree() - b.degree() + 1
            val notNb = findInversePolynomial(nb, xPowMod)
            val nq = timesRecursive(na, notNb, xPowMod)
            val qDegree = a.degree() - b.degree()
            val qCoefs = mutableMapOf<Int, CoefsFieldElement>()
            for (pow in 0..qDegree) {
                qCoefs[pow] = nq[qDegree - pow]
            }
            return FieldPolynomial(nq.field, qCoefs, nq.literal)
        }

        private fun <CoefsFieldElement: FieldElement> getLowestCoefDegree(a: AFieldPolynomial<CoefsFieldElement>): Int {
            for (i in 0 until a.degree()) {
                if (!a[i].isZero()) {
                    return i
                }
            }
            return a.degree()
        }
    }

    override fun div(other: AFieldPolynomial<CoefsFieldElement>): AFieldPolynomial<CoefsFieldElement> = divide(this, other)

    override fun rem(other: AFieldPolynomial<CoefsFieldElement>): AFieldPolynomial<CoefsFieldElement> = this - other * (this / other)

    override fun divide(other: AFieldPolynomial<CoefsFieldElement>): Pair<AFieldPolynomial<CoefsFieldElement>, AFieldPolynomial<CoefsFieldElement>> {
        if (degree <= maxDegreeOfSimpleDivision || other.degree() <= maxDegreeOfSimpleDivision) {
            return simpleDivide(this, other)
        }
        val q = this / other
        val r = this - other * q
        return q to r
    }

    override fun clone(): AFieldPolynomial<CoefsFieldElement> {
        return FieldPolynomial(field, coefs.toMap(), literal)
    }

    override fun shift(n: Int): AFieldPolynomial<CoefsFieldElement> {
        val newCoefs = coefs.mapKeys { it.key + n }
        return FieldPolynomial(field, newCoefs.toMap(), literal)
    }

    override fun mult(e: CoefsFieldElement): AFieldPolynomial<CoefsFieldElement> {
        val newCoefs = coefs.mapValues { field.mult(e, it.value) }
        return FieldPolynomial(field, newCoefs.toMap(), literal)
    }

    override fun normalized(): AFieldPolynomial<CoefsFieldElement> {
        if (isZero() || isOne() || this[degree].isOne()) {
            return this
        }
        val coef = field.inverseMult(this[degree])
        return mult(coef)
    }

    override fun reverse(): AFieldPolynomial<CoefsFieldElement> {
        val newCoefs = coefs.mapKeys { degree - it.key }
        return FieldPolynomial(field, newCoefs.toMap(), literal)
    }

    override fun derivative(): AFieldPolynomial<CoefsFieldElement> {
        val newCoefs = mutableMapOf<Int, CoefsFieldElement>()
        for ((pow, coef) in coefs) {
            if (pow > 0) {
                newCoefs[pow - 1] = field.mult(coef, field.one(pow))
            }
        }
        return FieldPolynomial(field, newCoefs, literal)
    }
}