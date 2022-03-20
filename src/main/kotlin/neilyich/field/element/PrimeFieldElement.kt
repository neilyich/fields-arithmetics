package neilyich.field.element

import neilyich.field.Field
import neilyich.util.NumberUtils

class PrimeFieldElement(field: Field<*>, value: Int) : FieldElement {
    val value = NumberUtils.mod(value, field.characteristics())

    override fun isZero(): Boolean = value == 0

    override fun isOne(): Boolean = value == 1

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