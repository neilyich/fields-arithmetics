package neilyich.field

import neilyich.field.element.FieldElement
import neilyich.ring.UnitalRing

abstract class Field<Element: FieldElement>: UnitalRing<Element>() {
    abstract override fun zero(): Element
    abstract override fun one(): Element

    abstract override fun add(lhs: Element, rhs: Element): Element
    abstract override fun mult(lhs: Element, rhs: Element): Element

    abstract override fun inverseAdd(e: Element): Element

    abstract override fun size(): Int

    override fun iterator(): Iterator<Element> = FieldIterator()

    open fun primitiveElement(): Element = element(1)

    // element(null) = 0, element(0) = 1, ...
    abstract fun element(discreteLogarithm: Int?): Element
    fun div(lhs: Element, rhs: Element): Element = this.mult(lhs, this.inverseMult(rhs))

    abstract fun inverseMult(e: Element): Element

    abstract fun characteristics(): Int
    abstract fun extensionDegree(): Int

    abstract fun innerField(): Field<out FieldElement>?

    private val multiplicativeGroup: Iterable<Element> by lazy { createMultiplicativeGroup() }

    fun multiplicativeGroup(): Iterable<Element> = multiplicativeGroup

    protected open fun createMultiplicativeGroup(): Iterable<Element> = Iterable { FieldIterator().apply { next() } }

    override fun createElements(): Iterable<Element> = listOf(zero()) + multiplicativeGroup()

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

    private inner class FieldIterator: Iterator<Element> {
        private var currentElement = this@Field.zero()
        private var hasNext = true

        override fun hasNext(): Boolean = hasNext

        override fun next(): Element {
            if (currentElement.discreteLogarithm() == this@Field.size() - 2) {
                hasNext = false
                return currentElement
            }
            val result = currentElement
            currentElement = this@Field.element((currentElement.discreteLogarithm() ?: -1) + 1)
            return result
        }

    }
}