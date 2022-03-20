package neilyich.ring

import neilyich.ring.element.UnitalRingElement

interface UnitalRing<Element: UnitalRingElement>: Ring<Element> {
    fun one(): Element
    fun one(times: Int): Element
}