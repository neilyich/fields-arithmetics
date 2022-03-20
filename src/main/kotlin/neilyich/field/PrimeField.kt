package neilyich.field

import neilyich.field.element.FieldElement
import neilyich.field.element.PrimeFieldElement
import neilyich.util.*

class PrimeField(private val p: Int): Field<PrimeFieldElement> {
    private val primitiveElement = PrimeFieldElement(this, NumberUtils.findPrimitiveElement(p))

    init {
        if (!NumberUtils.isPrime(p)) {
            throw IllegalArgumentException("p must be a prime number ($p)")
        }
    }

    override fun zero(): PrimeFieldElement = PrimeFieldElement(this, 0)

    override fun one(): PrimeFieldElement = PrimeFieldElement(this, 1)

    override fun element(discreteLogarithm: Int?): PrimeFieldElement {
        discreteLogarithm ?: return zero()
        return PrimeFieldElement(this, primitiveElement.value.pow(discreteLogarithm))
    }
    operator fun invoke(n: Int): PrimeFieldElement = PrimeFieldElement(this, n)

    override fun add(lhs: PrimeFieldElement, rhs: PrimeFieldElement): PrimeFieldElement {
        return PrimeFieldElement(this, lhs.value + rhs.value)
    }

    override fun mult(lhs: PrimeFieldElement, rhs: PrimeFieldElement): PrimeFieldElement {
        return PrimeFieldElement(this, lhs.value * rhs.value)
    }

    override fun inverseAdd(e: PrimeFieldElement): PrimeFieldElement {
        return PrimeFieldElement(this, -e.value)
    }

    override fun inverseMult(e: PrimeFieldElement): PrimeFieldElement {
        if (e.isZero()) {
            throw IllegalArgumentException("unable to find inverse multiplicative for 0")
        }
        if (e.isOne()) {
            return e
        }
        return PrimeFieldElement(this, e.value.modInverse(p))
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

    override fun contains(e: PrimeFieldElement): Boolean = e.value < p

    override fun primitiveElement(): PrimeFieldElement = primitiveElement

    override fun discreteLogarithm(e: PrimeFieldElement): Int? {
        return FieldUtils.calcDiscreteLogarithm(e, this)
    }

    override fun one(times: Int): PrimeFieldElement {
        if (times < 0) {
            throw IllegalArgumentException("one can be taken only non negative times")
        }
        return PrimeFieldElement(this, times)
    }

    override fun fromString(str: String): PrimeFieldElement {
        return PrimeFieldElement(this, str.trim().toInt())
    }

}