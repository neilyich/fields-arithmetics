package neilyich.field.deserialization

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import neilyich.field.deserialization.factory.FieldFactory
import neilyich.field.deserialization.factory.FieldFactoryImpl
import neilyich.field.deserialization.factory.PolynomialFactory
import neilyich.field.deserialization.factory.PolynomialFactoryImpl
import neilyich.field.element.FieldElement
import neilyich.field.polynomial.AFieldPolynomial
import neilyich.field.serialization.PolynomialDescription

class PolynomialDeserializer(
    private val polynomialFactory: PolynomialFactory = PolynomialFactoryImpl(),
    private val fieldFactory: FieldFactory = FieldFactoryImpl(polynomialFactory)
): StdDeserializer<AFieldPolynomial<out FieldElement>>(AFieldPolynomial::class.java) {

    override fun deserialize(jsonParser: JsonParser?, deserializationContext: DeserializationContext?): AFieldPolynomial<out FieldElement> {
        val polynomialDescription = deserializationContext?.readValue(jsonParser, PolynomialDescription::class.java)
            ?: throw IllegalArgumentException("unable to deserialize without deserializationContext")
        val field = fieldFactory.createField(polynomialDescription.coefsField)
        return polynomialFactory.createPolynomial(polynomialDescription, field)
    }
}