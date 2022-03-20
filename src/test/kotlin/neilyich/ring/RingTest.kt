package neilyich.ring

import neilyich.ring.element.RingElement
import kotlin.test.assertEquals
import kotlin.test.assertTrue

open class RingTest {
    protected fun <Element: RingElement> checkRingAxioms(ring: Ring<Element>) {
        var count = 0
        val elements = mutableSetOf<Element>()
        for (el in ring) {
            assertTrue(ring.contains(el), el.toString())
            count++
            elements.add(el)
        }
        // ring.size() unique elements
        assertEquals(ring.size(), count, "$ring")
        assertEquals(ring.size(), elements.size, "$ring")

        // exists only one 0
        assertTrue(ring.zero().isZero())
        assertEquals(1, ring.count{ it.isZero() || it == ring.zero() })

        val zero = ring.zero()
        for (a in ring) {
            // 0 + a = a + 0 = a
            assertEquals(a, ring.add(zero, a))
            assertEquals(a, ring.add(a, zero))

            // 0 * a = a * 0 = 0
            assertTrue(ring.mult(zero, a).isZero())
            assertTrue(ring.mult(a, zero).isZero())

            // -a: a - a = 0
            val minusA = ring.inverseAdd(a)
            assertTrue(ring.contains(minusA))
            assertTrue(ring.add(a, minusA).isZero())
            assertTrue(ring.add(minusA, a).isZero())

            for (b in ring) {
                assertTrue(ring.contains(ring.add(a, b)), "$a, $b  ---  $ring")
                assertTrue(ring.contains(ring.add(b, a)), "$a, $b  ---  $ring")

                assertTrue(ring.contains(ring.mult(a, b)), "$a, $b  ---  $ring")
                assertTrue(ring.contains(ring.mult(b, a)), "$a, $b  ---  $ring")

                // a + b = b + a; ab = ba
                assertEquals(ring.add(a, b), ring.add(b, a), "$a, $b  ---  $ring")
                assertEquals(ring.mult(a, b), ring.mult(b, a), "$a, $b  ---  $ring")

                for (c in ring) {
                    // a + (b + c) = (a + b) + c
                    assertEquals(ring.add(a, ring.add(b, c)), ring.add(ring.add(a, b), c), "$a, $b, $c  ---  $ring")

                    // a(bc) = (ab)c
                    assertEquals(ring.mult(a, ring.mult(b, c)), ring.mult(ring.mult(a, b), c), "$a, $b, $c  ---  $ring")

                    // (a + b) * c = ac + bc
                    assertEquals(ring.mult(ring.add(a, b), c), ring.add(ring.mult(a, c), ring.mult(b, c)), "$a, $b, $c  ---  $ring")
                }
            }
        }

    }
}