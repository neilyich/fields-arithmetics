package neilyich.field

import com.fasterxml.jackson.databind.ObjectWriter
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import neilyich.field.element.CachingPolynomialFieldElement
import neilyich.field.element.FieldElement
import neilyich.field.element.PrimeFieldElement
import neilyich.field.polynomial.AFieldPolynomial
import neilyich.field.polynomial.FieldPolynomial
import neilyich.field.serialization.FieldsModule
import neilyich.ring.PolynomialRing
import neilyich.util.FieldUtils
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class SerializationTest {
    companion object {
        private val objectMapper = jacksonObjectMapper()
        private val writer: ObjectWriter

        init {
            objectMapper.registerModule(FieldsModule())
            writer = objectMapper.writerWithDefaultPrettyPrinter()
        }
    }

    @Test
    fun fieldSerializationTest() {
        testSerializationAndDeserialization(PrimeField(31))
        testSerializationAndDeserialization(FieldUtils.field(11, 3))

        val f0 = FieldUtils.primeField(5)
        testSerializationAndDeserialization(f0)
        val f1 = FieldUtils.extend(f0, 4)
        testSerializationAndDeserialization(f1)
        val f2 = FieldUtils.extend(f1, 2)
        testSerializationAndDeserialization(f2)
    }

    private fun testSerializationAndDeserialization(field: Field<out FieldElement>) {
        testSerializationAndDeserialization(field, Field::class.java)
    }
    
    @Test
    fun polynomialSerializationTest() {
        val f = PrimeField(17)
        listOf(
            FieldPolynomial(f, f(1), f(0), f(11)),
            FieldPolynomial(f, f(1), f(14), f(2)),
            FieldPolynomial(f, f(1), f(6), f(8), f(2), f(10)),
        ).forEach { 
            testSerializationAndDeserialization(it)
        }
        val g = FieldUtils.extend(f, 2) as CachingPolynomialField<CachingPolynomialFieldElement<PrimeFieldElement>>
        listOf(
            FieldPolynomial(g, g.element(1), g.element(100), g.element(11)),
            FieldPolynomial(g, g.element(1), g.element(14), g.element(26)),
            FieldPolynomial(g, g.element(150), g.element(65), g.element(8), g.element(72), g.element(144)),
        ).forEach {
            testSerializationAndDeserialization(it)
        }

    }
    
    private fun testSerializationAndDeserialization(polynomial: AFieldPolynomial<out FieldElement>) {
        testSerializationAndDeserialization(polynomial, AFieldPolynomial::class.java)
    }

    @Test
    fun ringSerializationTest() {
        val f = PrimeField(17)
        listOf(
            FieldPolynomial(f, f(1), f(0), f(11)),
            FieldPolynomial(f, f(1), f(14), f(2)),
            FieldPolynomial(f, f(1), f(6), f(8), f(2), f(10)),
        ).forEach {
            testSerializationAndDeserialization(PolynomialRing(it))
        }
        val g = FieldUtils.extend(f, 2) as CachingPolynomialField<CachingPolynomialFieldElement<PrimeFieldElement>>
        listOf(
            FieldPolynomial(g, g.element(1), g.element(100), g.element(11)),
            FieldPolynomial(g, g.element(1), g.element(14), g.element(26)),
            FieldPolynomial(g, g.element(150), g.element(65), g.element(8), g.element(72), g.element(144)),
        ).forEach {
            testSerializationAndDeserialization(PolynomialRing(it))
        }
    }

    private fun testSerializationAndDeserialization(ring: PolynomialRing<out FieldElement>) {
        testSerializationAndDeserialization(ring, PolynomialRing::class.java)
    }

    private fun <T> testSerializationAndDeserialization(value: Any, clazz: Class<in T>) {
        val s = writer.writeValueAsString(value)
        val deserialized = objectMapper.readValue(s, clazz)
        assertEquals(value, deserialized, "'$value' != '$deserialized'")
    }
}