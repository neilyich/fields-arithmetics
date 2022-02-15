package neilyich.field.element

import neilyich.field.Field

class PrimeFieldElement(field: Field<out PrimeFieldElement>, value: Int) : FieldElement(field) {
    val value: Int

    init {
        val p = field.characteristics()
        if (value < 0) {
            this.value = value % p + p
        }
        else {
            this.value = value % p
        }
    }

    override fun isZero(): Boolean = value == 0

    override fun isOne(): Boolean = value == 1

    override fun number(): Int = value

    override fun toString(): String {
        return value.toString()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is PrimeFieldElement) return false

        if (value != other.value) return false

        return true
    }

    override fun hashCode(): Int {
        return value
    }

}