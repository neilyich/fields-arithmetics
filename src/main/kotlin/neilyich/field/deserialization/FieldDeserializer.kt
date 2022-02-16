package neilyich.field.deserialization

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import neilyich.field.Field
import neilyich.field.deserialization.factory.FieldFactory
import neilyich.field.deserialization.factory.FieldFactoryImpl
import neilyich.field.deserialization.factory.PolynomialFactoryImpl
import neilyich.field.element.FieldElement
import neilyich.field.serialization.FieldDescription

class FieldDeserializer(private val fieldFactory: FieldFactory = FieldFactoryImpl(PolynomialFactoryImpl())):
    StdDeserializer<Field<out FieldElement>>(Field::class.java) {

    override fun deserialize(jsonParser: JsonParser?, deserializationContext: DeserializationContext?): Field<out FieldElement> {
        val fieldDescription = deserializationContext?.readValue(jsonParser, FieldDescription::class.java)
            ?: throw IllegalArgumentException("unable to deserialize without deserializationContext")
        return fieldFactory.createField(fieldDescription)
    }
}