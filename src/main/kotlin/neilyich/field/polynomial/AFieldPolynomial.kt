package neilyich.field.polynomial

import neilyich.field.Field
import neilyich.field.element.FieldElement

fun <CoefsFieldElement: FieldElement> x(field: Field<CoefsFieldElement>, literal: String = "x"):
        AFieldPolynomial<CoefsFieldElement> = x(field, 1, literal)

fun <CoefsFieldElement: FieldElement> x(field: Field<CoefsFieldElement>, pow: Int, literal: String = "x"):
        AFieldPolynomial<CoefsFieldElement> = OnePolynomial(field, literal).shift(pow)

fun <CoefsFieldElement: FieldElement> constant(field: Field<CoefsFieldElement>, coef: CoefsFieldElement, literal: String = "x"):
        AFieldPolynomial<CoefsFieldElement> = FieldPolynomial(field, literal, coef)

abstract class AFieldPolynomial<CoefsFieldElement: FieldElement>(val field: Field<CoefsFieldElement>, val literal: String = "x"): FieldElement {
    private var string: String = ""

    abstract fun coefs(): Map<Int, CoefsFieldElement>
    abstract fun degree(): Int
    abstract operator fun get(pow: Int): CoefsFieldElement
    abstract fun with(pow: Int, e: CoefsFieldElement): AFieldPolynomial<CoefsFieldElement>
    abstract fun valueAt(e: CoefsFieldElement): CoefsFieldElement
    abstract operator fun plus(other: AFieldPolynomial<CoefsFieldElement>): AFieldPolynomial<CoefsFieldElement>
    open operator fun unaryMinus(): AFieldPolynomial<CoefsFieldElement> = this.mult(field.inverseAdd(field.one()))
    open operator fun minus(other: AFieldPolynomial<CoefsFieldElement>): AFieldPolynomial<CoefsFieldElement> = plus(-other)
    abstract operator fun times(other: AFieldPolynomial<CoefsFieldElement>): AFieldPolynomial<CoefsFieldElement>
    abstract fun divide(other: AFieldPolynomial<CoefsFieldElement>): Pair<AFieldPolynomial<CoefsFieldElement>, AFieldPolynomial<CoefsFieldElement>>
    open operator fun div(other: AFieldPolynomial<CoefsFieldElement>): AFieldPolynomial<CoefsFieldElement> = divide(other).first
    open operator fun rem(other: AFieldPolynomial<CoefsFieldElement>): AFieldPolynomial<CoefsFieldElement> = divide(other).second

    abstract fun shift(n: Int): AFieldPolynomial<CoefsFieldElement>
    abstract fun mult(e: CoefsFieldElement): AFieldPolynomial<CoefsFieldElement>

    abstract fun normalized(): AFieldPolynomial<CoefsFieldElement>
    abstract fun reverse(): AFieldPolynomial<CoefsFieldElement>

    abstract fun derivative(): AFieldPolynomial<CoefsFieldElement>

    open fun pow(n: Int, mod: AFieldPolynomial<CoefsFieldElement>? = null): AFieldPolynomial<CoefsFieldElement> {
        if (n < 0) {
            throw IllegalArgumentException("exponent must be non negative")
        }
        if (n == 0) {
            return OnePolynomial(field, literal)
        }
        if (n == 1) {
            return if (mod == null) this else this % mod
        }
        var result = this
        val binaryString = n.toString(2)
        for (c in binaryString.substring(1)) {
            result *= result
            if (c == '1') {
                result *= this
            }
            if (mod != null) {
                result %= mod
            }
        }
        return result
    }

    override fun isZero(): Boolean = degree() == 0 && this[0].isZero()
    override fun isOne(): Boolean = degree() == 0 && this[0].isOne()

    fun isNormalized(): Boolean = this[degree()].isOne()

    companion object {
        private const val START_POLYNOMIAL = "("
        private const val END_POLYNOMIAL = ")"
        private const val TERMS_DIVIDER = " + "
        private const val POW_DIVIDER = "^"

        fun <CoefsFieldElement: FieldElement> fromString(str: String, coefsField: Field<CoefsFieldElement>, literal: String = "x"): AFieldPolynomial<CoefsFieldElement> {
            var builder = StringBuilder(str.removePrefix(START_POLYNOMIAL))
            builder = StringBuilder(builder.removeSuffix(END_POLYNOMIAL))
            if (builder.length == 1 && builder.toString() == "0") {
                return ZeroPolynomial(coefsField, literal)
            }
            val coefs = builder.split(TERMS_DIVIDER).map { t ->
                val term = t.split(POW_DIVIDER)
                if (term.size > 2) {
                    throw IllegalArgumentException("unable to parse term $t")
                }
                if (term.size == 1) {
                    if (term[0].endsWith(literal)) {
                        val c = term[0].removeSuffix(literal)
                        if (c.isEmpty()) {
                            return@map 1 to coefsField.one()
                        }
                        return@map 1 to coefsField.fromString(c)
                    }
                    else {
                        return@map 0 to coefsField.fromString(term[0])
                    }
                }
                val coefStr = term[0].removeSuffix(literal)
                val coef = if (coefStr.isNotEmpty()) coefsField.fromString(coefStr) else coefsField.one()
                val pow = term[1].trim().toInt()
                return@map pow to coef
            }.toMap()
            return FieldPolynomial(coefsField, coefs, literal)
        }
    }

    final override fun toString(): String {
        if (string.isNotEmpty()) {
            return string
        }
        val builder = StringBuilder(START_POLYNOMIAL)
        for (pow in coefs().keys.sortedDescending()) {
            if (this[pow].isZero()) {
                continue
            }
            if (pow == 0 || !this[pow].isOne()) {
                builder.append(this[pow].toString())
            }
            if (pow > 0) {
                builder.append(literal)
            }
            if (pow > 1) {
                builder.append(POW_DIVIDER).append(pow)
            }
            builder.append(TERMS_DIVIDER)
        }
        if (builder.length == 1) {
            return "(0)"
        }
        string = builder.substring(0, builder.length - TERMS_DIVIDER.length) + END_POLYNOMIAL
        return string
    }

    override fun hashCode(): Int {
        return toString().hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is AFieldPolynomial<*>) return false

        if (field != other.field) return false

        return toString() == other.toString()
    }

    fun coefsList(): List<CoefsFieldElement> {
        val coefs = coefs()
        val degree = degree()
        val zero = field.zero()
        val coefsList = mutableListOf<CoefsFieldElement>()
        for (i in 0..degree) {
            coefsList.add(coefs.getOrDefault(i, zero))
        }
        return coefsList
    }

}