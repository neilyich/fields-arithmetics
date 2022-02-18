package neilyich.field.element

import neilyich.field.Field
import neilyich.ring.element.UnitalRingElement

abstract class FieldElement(val field: Field<out FieldElement>): UnitalRingElement {
    abstract override fun isZero(): Boolean
    abstract override fun isOne(): Boolean
    abstract fun discreteLogarithm(): Int?
}