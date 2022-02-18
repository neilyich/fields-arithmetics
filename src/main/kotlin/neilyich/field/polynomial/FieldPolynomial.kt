package neilyich.field.polynomial

import neilyich.field.Field
import neilyich.field.element.FieldElement

class FieldPolynomial<CoefsFieldElement: FieldElement>(field: Field<CoefsFieldElement>, private val coefs: Map<Int, CoefsFieldElement> = mapOf(), literal: String = "x"): AFieldPolynomial<CoefsFieldElement>(field, literal), Cloneable {
    private val degree: Int = coefs.keys.filter{!coefs[it]!!.isZero()}.maxOrNull() ?: 0

    init {
        if ((coefs.keys.minOrNull() ?: 0) < 0) {
            throw IllegalArgumentException("polynomial coefs must be non negative")
        }
    }

    constructor(field: Field<CoefsFieldElement>, vararg coefsList: CoefsFieldElement): this(field, coefsList.mapIndexed{ i, c -> (coefsList.size - 1 - i) to c }.toMap()) {
    }

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
        var result: AFieldPolynomial<CoefsFieldElement> = FieldPolynomial(field, literal = literal)
        for ((pow, e) in other.coefs()) {
            result += shift(pow).mult(e)
        }
        return result
    }

    override fun divide(other: AFieldPolynomial<CoefsFieldElement>): Pair<AFieldPolynomial<CoefsFieldElement>, AFieldPolynomial<CoefsFieldElement>> {
        if (other.isZero()) {
            throw IllegalArgumentException("divider must not be 0")
        }
        if (other.degree() > degree()) {
            return FieldPolynomial(field, literal = literal) to this
        }
        val firstDividerCoef = other[other.degree()]
        val quotientCoefs = mutableMapOf<Int, CoefsFieldElement>()
        var r: AFieldPolynomial<CoefsFieldElement> = clone()
        for(i in degree() downTo other.degree()) {
            val firstCoef = r[i]
            val resultCoef = field.div(firstCoef, firstDividerCoef)
            val currentQuotient = other.shift(i - other.degree()).mult(resultCoef)
            r -= currentQuotient
            quotientCoefs[i - other.degree()] = resultCoef
        }
        return FieldPolynomial(field, quotientCoefs.toMap(), literal) to r
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
}