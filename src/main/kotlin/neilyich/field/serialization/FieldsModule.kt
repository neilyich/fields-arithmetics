package neilyich.field.serialization

import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.databind.ser.std.StdSerializer
import neilyich.field.Field
import neilyich.field.deserialization.FieldDeserializer
import neilyich.field.deserialization.PolynomialDeserializer
import neilyich.field.element.FieldElement
import neilyich.field.polynomial.AFieldPolynomial

class FieldsModule(
    fieldSerializer: StdSerializer<Field<out FieldElement>> = FieldSerializer(),
    fieldDeserializer: StdDeserializer<Field<out FieldElement>> = FieldDeserializer(),
    polynomialSerializer: StdSerializer<AFieldPolynomial<out FieldElement>> = PolynomialSerializer(),
    polynomialDeserializer: StdDeserializer<AFieldPolynomial<out FieldElement>> = PolynomialDeserializer(),
    ): SimpleModule() {
    init {
        addSerializer(Field::class.java, fieldSerializer)
        addDeserializer(Field::class.java, fieldDeserializer)
        addSerializer(AFieldPolynomial::class.java, polynomialSerializer)
        addDeserializer(AFieldPolynomial::class.java, polynomialDeserializer)
    }
}