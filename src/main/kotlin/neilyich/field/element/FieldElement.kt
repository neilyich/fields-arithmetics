package neilyich.field.element

import neilyich.field.Field

abstract class FieldElement(val field: Field<out FieldElement>) {
    abstract fun isZero(): Boolean
    abstract fun isOne(): Boolean
    abstract fun number(): Int
}