package neilyich.field.serialization.description.factory

import neilyich.field.Field
import neilyich.field.PolynomialField
import neilyich.field.element.FieldElement
import neilyich.field.serialization.FieldDescription
import neilyich.field.serialization.FieldType

internal class FieldDescriptionFactoryImpl(private val polynomialDescriptionFactory: PolynomialDescriptionFactory) : FieldDescriptionFactory {
    override fun createFieldDescription(field: Field<out FieldElement>): FieldDescription {
        val innerField = field.innerField()
            ?: return FieldDescription(FieldType.PRIME, field.characteristics(), null, null)
        return FieldDescription(FieldType.POLYNOMIAL, null, createFieldDescription(innerField), polynomialDescriptionFactory.createPolynomialDescription((field as PolynomialField<out FieldElement>).mod))
    }
}