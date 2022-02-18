package neilyich.field.deserialization

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import neilyich.field.deserialization.factory.PolynomialRingFactory
import neilyich.field.deserialization.factory.PolynomialRingFactoryImpl
import neilyich.field.element.FieldElement
import neilyich.field.serialization.PolynomialRingDescription
import neilyich.ring.PolynomialRing

class PolynomialRingDeserializer(
    private val polynomialRingFactory: PolynomialRingFactory = PolynomialRingFactoryImpl()
): StdDeserializer<PolynomialRing<out FieldElement>>(PolynomialRing::class.java) {

    override fun deserialize(jsonParser: JsonParser?, deserializationContext: DeserializationContext?): PolynomialRing<out FieldElement> {
        val ringDescription = deserializationContext?.readValue(jsonParser, PolynomialRingDescription::class.java)
            ?: throw IllegalArgumentException("unable to deserialize without deserializationContext")
        return polynomialRingFactory.createPolynomialRing(ringDescription)
    }
}