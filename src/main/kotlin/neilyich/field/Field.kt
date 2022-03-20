package neilyich.field

import neilyich.field.element.FieldElement
import neilyich.ring.UnitalRing

interface Field<Element: FieldElement>: UnitalRing<Element> {
    override fun zero(): Element
    override fun one(): Element
    override fun one(times: Int): Element

    override fun add(lhs: Element, rhs: Element): Element
    override fun mult(lhs: Element, rhs: Element): Element

    override fun inverseAdd(e: Element): Element

    override fun size(): Int

    override fun iterator(): Iterator<Element> = FieldIterator(this)
    fun multiplicativeGroup(): Iterable<Element> = Iterable { FieldIterator(this).apply { next() } }

    fun primitiveElement(): Element

    // element(null) = 0, element(0) = 1, ...
    fun element(discreteLogarithm: Int?): Element
    fun discreteLogarithm(e: Element): Int?
    fun div(lhs: Element, rhs: Element): Element = this.mult(lhs, this.inverseMult(rhs))

    fun inverseMult(e: Element): Element

    fun characteristics(): Int
    fun extensionDegree(): Int

    fun innerField(): Field<out FieldElement>?

    override fun fromString(str: String): Element

}