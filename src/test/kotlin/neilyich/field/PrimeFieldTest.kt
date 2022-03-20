package neilyich.field

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals

class PrimeFieldTest: FieldTest() {
    @Test
    fun constructorTest() {
        listOf(2, 3, 5, 7, 3851).forEach {
            assertDoesNotThrow {
                PrimeField(it)
            }
        }
        listOf(4, 15, 32, 4203).forEach {
            assertThrows<IllegalArgumentException> {
                PrimeField(it)
            }
        }
    }

    @Test
    fun arithmeticsTest() {
        listOf(199).forEach { p ->
            val field = PrimeField(p)
            assertEquals(p, field.size())
            checkFieldAxioms(field)
        }
    }
}