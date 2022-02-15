package neilyich.field

import neilyich.field.element.FieldElement

abstract class Field<Element: FieldElement> {
    abstract fun zero(): Element
    abstract fun one(): Element
    abstract fun element(n: Int): Element

    abstract fun add(lhs: Element, rhs: Element): Element
    abstract fun mult(lhs: Element, rhs: Element): Element

    fun sub(lhs: Element, rhs: Element): Element = this.add(lhs, this.inverseAdd(rhs))
    fun div(lhs: Element, rhs: Element): Element = this.mult(lhs, this.inverseMult(rhs))

    abstract fun inverseAdd(e: Element): Element
    abstract fun inverseMult(e: Element): Element

    abstract fun characteristics(): Int
    abstract fun extensionDegree(): Int
    abstract fun size(): Int

    abstract fun innerField(): Field<out FieldElement>?

    fun multiplicativeGroup(): Set<Element> = (1 until size()).map { element(it) }.toSet()

    protected fun checkSameField(lhs: Element, rhs: Element) {
        if (lhs.field != rhs.field || this != lhs.field || this != rhs.field) {
            throw IllegalArgumentException("elements must be from equal fields")
        }
    }

    protected fun checkSameField(e: Element) {
        if (e.field != this) {
            throw IllegalArgumentException("elements must be from equal fields")
        }
    }
}