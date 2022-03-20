package neilyich.field.element

import neilyich.ring.element.UnitalRingElement

interface FieldElement: UnitalRingElement {
    override fun isZero(): Boolean
    override fun isOne(): Boolean
}