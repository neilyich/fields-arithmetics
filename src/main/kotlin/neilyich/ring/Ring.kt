package neilyich.ring

import neilyich.ring.element.RingElement

interface Ring<Element: RingElement>: Iterable<Element> {
    fun zero(): Element

    fun contains(e: Element): Boolean

    fun add(lhs: Element, rhs: Element): Element
    fun mult(lhs: Element, rhs: Element): Element

    fun sub(lhs: Element, rhs: Element): Element = this.add(lhs, this.inverseAdd(rhs))
    fun inverseAdd(e: Element): Element

    fun size(): Int

    override fun iterator(): Iterator<Element>

    fun fromString(str: String): Element
}