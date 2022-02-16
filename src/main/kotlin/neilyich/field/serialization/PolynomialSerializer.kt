package neilyich.field.serialization

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.ser.std.StdSerializer
import neilyich.field.element.FieldElement
import neilyich.field.polynomial.AFieldPolynomial
import neilyich.field.serialization.description.factory.FieldDescriptionFactory
import neilyich.field.serialization.description.factory.FieldDescriptionFactoryImpl
import neilyich.field.serialization.description.factory.PolynomialDescriptionFactory
import neilyich.field.serialization.description.factory.PolynomialDescriptionFactoryImpl

class PolynomialSerializer(private val polynomialDescriptionFactory: PolynomialDescriptionFactory = PolynomialDescriptionFactoryImpl(),
                           private val fieldDescriptionFactory: FieldDescriptionFactory = FieldDescriptionFactoryImpl(polynomialDescriptionFactory)):
    StdSerializer<AFieldPolynomial<out FieldElement>>(AFieldPolynomial::class.java) {

    override fun serialize(polynomial: AFieldPolynomial<out FieldElement>?, jsonGenerator: JsonGenerator?, serializerProvider: SerializerProvider?) {
        if (polynomial == null) {
            jsonGenerator?.writeNull()
            return
        }
        val fieldDescription = fieldDescriptionFactory.createFieldDescription(polynomial.field)
        val polynomialDescription = polynomialDescriptionFactory.createPolynomialDescription(polynomial, fieldDescription)
        serializerProvider?.defaultSerializeValue(polynomialDescription, jsonGenerator)
    }
}