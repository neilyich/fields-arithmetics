package neilyich.field.polynomial

import neilyich.field.Field
import neilyich.field.element.FieldElement
import neilyich.ring.element.UnitalRingElement

fun <CoefsFieldElement: FieldElement> x(field: Field<CoefsFieldElement>, literal: String = "x"):
        AFieldPolynomial<CoefsFieldElement> = x(field, 1, literal)

fun <CoefsFieldElement: FieldElement> x(field: Field<CoefsFieldElement>, pow: Int, literal: String = "x"):
        AFieldPolynomial<CoefsFieldElement> = OnePolynomial(field, literal).shift(pow)


abstract class AFieldPolynomial<CoefsFieldElement: FieldElement>(val field: Field<CoefsFieldElement>, val literal: String = "x"): UnitalRingElement {
    private var string: String = ""

    abstract fun coefs(): Map<Int, CoefsFieldElement>
    abstract fun degree(): Int
    abstract operator fun get(pow: Int): CoefsFieldElement
    abstract fun with(pow: Int, e: CoefsFieldElement): AFieldPolynomial<CoefsFieldElement>
    abstract fun valueAt(e: CoefsFieldElement): CoefsFieldElement
    abstract operator fun plus(other: AFieldPolynomial<CoefsFieldElement>): AFieldPolynomial<CoefsFieldElement>
    operator fun unaryMinus(): AFieldPolynomial<CoefsFieldElement> = this.mult(field.inverseAdd(field.one()))
    operator fun minus(other: AFieldPolynomial<CoefsFieldElement>): AFieldPolynomial<CoefsFieldElement> = plus(-other)
    abstract operator fun times(other: AFieldPolynomial<CoefsFieldElement>): AFieldPolynomial<CoefsFieldElement>
    abstract fun divide(other: AFieldPolynomial<CoefsFieldElement>): Pair<AFieldPolynomial<CoefsFieldElement>, AFieldPolynomial<CoefsFieldElement>>
    open operator fun div(other: AFieldPolynomial<CoefsFieldElement>): AFieldPolynomial<CoefsFieldElement> = divide(other).first
    open operator fun rem(other: AFieldPolynomial<CoefsFieldElement>): AFieldPolynomial<CoefsFieldElement> = divide(other).second

    abstract fun shift(n: Int): AFieldPolynomial<CoefsFieldElement>
    abstract fun mult(e: CoefsFieldElement): AFieldPolynomial<CoefsFieldElement>

    abstract fun normalized(): AFieldPolynomial<CoefsFieldElement>
    abstract fun reverse(): AFieldPolynomial<CoefsFieldElement>

    fun pow(n: Int, mod: AFieldPolynomial<CoefsFieldElement>? = null): AFieldPolynomial<CoefsFieldElement> {
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

    final override fun toString(): String {
        if (string.isNotEmpty()) {
            return string
        }
        val builder = StringBuilder()
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
                builder.append("^").append(pow)
            }
            builder.append(" + ")
        }
        if (builder.isEmpty()) {
            return "0"
        }
        string = builder.substring(0, builder.length - 3)
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

}