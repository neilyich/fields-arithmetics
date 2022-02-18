package neilyich.ring

import neilyich.ring.element.UnitalRingElement
import kotlin.test.assertEquals
import kotlin.test.assertTrue

open class UnitalRingTest: RingTest() {
    protected fun <Element: UnitalRingElement> checkUnitalRingAxioms(ring: UnitalRing<Element>) {
        checkRingAxioms(ring)

        // exists only one 1
        assertTrue(ring.one().isOne())
        assertEquals(1, ring.count{ it.isOne() || it == ring.one() })

        val one = ring.one()
        for (a in ring) {
            // 1 * a = a * 1 = a
            assertEquals(a, ring.mult(one, a))
            assertEquals(a, ring.mult(a, one))
        }
    }
}