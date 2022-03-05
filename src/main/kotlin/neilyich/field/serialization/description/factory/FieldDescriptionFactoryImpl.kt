package neilyich.field.serialization.description.factory

import neilyich.field.Field
import neilyich.field.PolynomialField
import neilyich.field.element.FieldElement
import neilyich.field.element.PolynomialFieldElement
import neilyich.field.serialization.FieldDescription
import neilyich.field.serialization.FieldType

class FieldDescriptionFactoryImpl(
    private val polynomialDescriptionFactory: PolynomialDescriptionFactory = PolynomialDescriptionFactoryImpl()
) : FieldDescriptionFactory {

    override fun createFieldDescription(field: Field<out FieldElement>): FieldDescription {
        val innerField = field.innerField()
            ?: return FieldDescription(FieldType.PRIME, field.characteristics(), null, field.toString())
        return FieldDescription(FieldType.POLYNOMIAL, null, polynomialDescriptionFactory.createPolynomialDescription((field as PolynomialField<out FieldElement, out PolynomialFieldElement<out FieldElement, out FieldElement>>).mod, createFieldDescription(innerField)), field.toString())
    }
}