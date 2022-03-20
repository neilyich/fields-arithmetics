package neilyich.field

import neilyich.field.element.FieldElement
import neilyich.ring.UnitalRingTest
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

open class FieldTest: UnitalRingTest() {
    protected fun <Element: FieldElement> checkFieldAxioms(field: Field<Element>) {
        checkUnitalRingAxioms(field)

        var expectedDiscreteLog: Int? = null
        for (a in field) {
            val actualDiscreteLog = field.discreteLogarithm(a)
            assertEquals(expectedDiscreteLog, actualDiscreteLog)
            expectedDiscreteLog = (expectedDiscreteLog ?: -1) + 1
        }

        assertEquals(field.element(null), field.zero())
        assertEquals(field.element(0), field.one())

        // F* = F \ {0}
        assertEquals(field.size() - 1, field.multiplicativeGroup().count())
        field.multiplicativeGroup().forEach {
            assertFalse(it.isZero())
        }

        for (a in field.multiplicativeGroup()) {
            // a != 0 => a^-1: a * a^-1 = 1
            val invA = field.inverseMult(a)
            assertTrue(field.mult(a, invA).isOne(), "$a, $invA, $field")
            assertTrue(field.mult(invA, a).isOne())
        }
    }
}