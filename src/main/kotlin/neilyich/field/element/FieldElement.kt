package neilyich.field.element

import neilyich.field.Field

abstract class FieldElement(val field: Field<*>) {
    abstract fun isZero(): Boolean
    abstract fun isOne(): Boolean
}