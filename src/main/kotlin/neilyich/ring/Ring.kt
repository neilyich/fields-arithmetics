package neilyich.ring

import neilyich.ring.element.RingElement

abstract class Ring<Element: RingElement>: Iterable<Element> {
    abstract fun zero(): Element

    private val elements: Iterable<Element> by lazy { createElements() }
    fun elements(): Iterable<Element> = elements

    protected abstract fun createElements(): Iterable<Element>

    open fun contains(e: Element): Boolean = elements().contains(e)

    abstract fun add(lhs: Element, rhs: Element): Element
    abstract fun mult(lhs: Element, rhs: Element): Element

    fun sub(lhs: Element, rhs: Element): Element = this.add(lhs, this.inverseAdd(rhs))
    abstract fun inverseAdd(e: Element): Element

    abstract fun size(): Int

    override fun iterator(): Iterator<Element> = RingIterator()

    private inner class RingIterator: Iterator<Element> {
        private val elementsIterator = this@Ring.elements().iterator()

        override fun hasNext(): Boolean = elementsIterator.hasNext()

        override fun next(): Element = elementsIterator.next()

    }
}