package neilyich.field.serialization

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.ser.std.StdSerializer
import neilyich.field.Field
import neilyich.field.element.FieldElement
import neilyich.field.serialization.description.factory.*

class FieldSerializer(
    private val fieldElementDescriptionFactory: FieldElementDescriptionFactory = FieldElementDescriptionFactoryImpl(),
    private val polynomialDescriptionFactory: PolynomialDescriptionFactory = PolynomialDescriptionFactoryImpl(fieldElementDescriptionFactory),
    private val fieldDescriptionFactory: FieldDescriptionFactory = FieldDescriptionFactoryImpl(fieldElementDescriptionFactory, polynomialDescriptionFactory)
): StdSerializer<Field<out FieldElement>>(Field::class.java) {

    override fun serialize(field: Field<out FieldElement>?, jsonGenerator: JsonGenerator?, serializerProvider: SerializerProvider?) {
        serializerProvider?.defaultSerializeValue(field?.let { fieldDescriptionFactory.createFieldDescription(it) }, jsonGenerator)
    }
}