package neilyich.ring

import neilyich.ring.element.UnitalRingElement

abstract class UnitalRing<Element: UnitalRingElement>: Ring<Element>() {
    abstract fun one(): Element
}