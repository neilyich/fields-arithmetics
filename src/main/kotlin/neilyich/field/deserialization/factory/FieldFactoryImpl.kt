package neilyich.field.deserialization.factory

import neilyich.field.Field
import neilyich.field.PolynomialField
import neilyich.field.PrimeField
import neilyich.field.element.FieldElement
import neilyich.field.serialization.FieldDescription
import neilyich.field.serialization.FieldType

class FieldFactoryImpl(
    private val polynomialFactory: PolynomialFactory = PolynomialFactoryImpl()
): FieldFactory {

    override fun createField(fieldDescription: FieldDescription): Field<out FieldElement> {
        return createFieldRecursively(fieldDescription)
    }

    private fun createFieldRecursively(fieldDescription: FieldDescription): Field<out FieldElement> {
        if (fieldDescription.type == FieldType.PRIME) {
            return PrimeField(fieldDescription.mod!!)
        }
        val polynomialCoefsField = createFieldRecursively(fieldDescription.polynomialMod!!.coefsField)
        val polynomial = polynomialFactory.createPolynomial(fieldDescription.polynomialMod, polynomialCoefsField)
        return PolynomialField(polynomial)
    }
}