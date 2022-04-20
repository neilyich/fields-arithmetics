package neilyich.field.serialization.description.factory

import neilyich.field.Field
import neilyich.field.PolynomialField
import neilyich.field.element.FieldElement
import neilyich.field.serialization.FieldDescription

class FieldDescriptionFactoryImpl(
    private val fieldElementDescriptionFactory: FieldElementDescriptionFactory = FieldElementDescriptionFactoryImpl(),
    private val polynomialDescriptionFactory: PolynomialDescriptionFactory = PolynomialDescriptionFactoryImpl(fieldElementDescriptionFactory)
) : FieldDescriptionFactory {

    override fun createFieldDescription(field: Field<out FieldElement>): FieldDescription {
        val innerField = field.innerField()
            ?: return FieldDescription(field.characteristics(), null, field.toString())
        return FieldDescription(null, polynomialDescriptionFactory.createPolynomialDescription((field as PolynomialField<out FieldElement>).mod, createFieldDescription(innerField)), field.toString())
    }
}