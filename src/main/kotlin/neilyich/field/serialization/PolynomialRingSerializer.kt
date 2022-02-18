package neilyich.field.serialization

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.ser.std.StdSerializer
import neilyich.field.element.FieldElement
import neilyich.field.serialization.description.factory.PolynomialRingDescriptionFactory
import neilyich.field.serialization.description.factory.PolynomialRingDescriptionFactoryImpl
import neilyich.ring.PolynomialRing

class PolynomialRingSerializer(
    private val polynomialRingDescriptionFactory: PolynomialRingDescriptionFactory = PolynomialRingDescriptionFactoryImpl()
): StdSerializer<PolynomialRing<out FieldElement>>(PolynomialRing::class.java) {
    override fun serialize(ring: PolynomialRing<out FieldElement>?, jsonGenerator: JsonGenerator?, serializerProvider: SerializerProvider?) {
        serializerProvider?.defaultSerializeValue(ring?.let { polynomialRingDescriptionFactory.createPolynomialRingDescription(it) }, jsonGenerator)
    }
}