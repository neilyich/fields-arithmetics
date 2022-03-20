package neilyich.field.deserialization.factory

import neilyich.field.Field
import neilyich.field.CachingPolynomialField
import neilyich.field.PrimeField
import neilyich.field.element.FieldElement
import neilyich.field.serialization.FieldDescription

class FieldFactoryImpl(
    private val polynomialFactory: PolynomialFactory = PolynomialFactoryImpl()
): FieldFactory {

    override fun createField(fieldDescription: FieldDescription): Field<out FieldElement> {
        return createFieldRecursively(fieldDescription)
    }

    private fun createFieldRecursively(fieldDescription: FieldDescription): Field<out FieldElement> {
        if (fieldDescription.mod != null) {
            return PrimeField(fieldDescription.mod)
        }
        val polynomialCoefsField = createFieldRecursively(fieldDescription.polynomialMod!!.coefsField)
        val polynomial = polynomialFactory.createPolynomial(fieldDescription.polynomialMod, polynomialCoefsField)
        return CachingPolynomialField(polynomial)
    }
}