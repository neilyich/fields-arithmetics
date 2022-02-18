package neilyich.field

import neilyich.field.element.FieldElement
import neilyich.util.NumberUtils
import neilyich.field.element.PrimeFieldElement

class PrimeField(p: Int): Field<PrimeFieldElement>() {
    private val p: Int

    init {
        if (!NumberUtils.isPrime(p)) {
            throw IllegalArgumentException("p must be a prime number ($p)")
        }
        this.p = p
    }

    override fun zero(): PrimeFieldElement = PrimeFieldElement(this, 0)

    override fun one(): PrimeFieldElement = PrimeFieldElement(this, 1)

    override fun element(discreteLogarithm: Int?): PrimeFieldElement = this(discreteLogarithm?.let { it + 1 } ?: 0)
    operator fun invoke(n: Int): PrimeFieldElement = PrimeFieldElement(this, n)

    override fun add(lhs: PrimeFieldElement, rhs: PrimeFieldElement): PrimeFieldElement {
        checkSameField(lhs, rhs)
        return PrimeFieldElement(this, lhs.value + rhs.value)
    }

    override fun mult(lhs: PrimeFieldElement, rhs: PrimeFieldElement): PrimeFieldElement {
        checkSameField(lhs, rhs)
        return PrimeFieldElement(this, lhs.value * rhs.value)
    }

    override fun inverseAdd(e: PrimeFieldElement): PrimeFieldElement {
        checkSameField(e)
        return PrimeFieldElement(this, -e.value)
    }

    override fun inverseMult(e: PrimeFieldElement): PrimeFieldElement {
        checkSameField(e)
        if (e.isZero()) {
            throw IllegalArgumentException("unable to find inverse multiplicative for 0")
        }
        if (e.isOne()) {
            return e
        }
        val (_, inverse, _) = NumberUtils.extendedEuclidAlgorithm(p, e.value)
        return PrimeFieldElement(this, inverse)
    }

    override fun characteristics(): Int = p
    override fun extensionDegree(): Int = 1
    override fun size(): Int = p

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PrimeField

        return p == other.p
    }

    override fun hashCode(): Int {
        return p
    }

    override fun toString(): String {
        return "Z$p"
    }

    override fun innerField(): Field<out FieldElement>? = null

}