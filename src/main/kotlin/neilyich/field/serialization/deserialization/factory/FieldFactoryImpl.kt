package neilyich.field.serialization.deserialization.factory

import neilyich.field.Field
import neilyich.field.PolynomialField
import neilyich.field.PrimeField
import neilyich.field.element.FieldElement
import neilyich.field.serialization.FieldDescription
import neilyich.field.serialization.FieldType

internal class FieldFactoryImpl(private val polynomialFactory: PolynomialFactory): FieldFactory {
    companion object {
        private const val literals = "xyzabcdefghijklmnopqrstuvw"
    }

    override fun createField(fieldDescription: FieldDescription): Field<out FieldElement> {
        return createFieldRecursively(fieldDescription).first
    }

    private fun createFieldRecursively(fieldDescription: FieldDescription): Pair<Field<out FieldElement>, Int> {
        if (fieldDescription.type == FieldType.PRIME) {
            return PrimeField(fieldDescription.mod!!) to 0
        }
        val (polynomialCoefsField, literalNumber) = createFieldRecursively(fieldDescription.innerField!!)
        val polynomial = polynomialFactory.createPolynomial(fieldDescription.polynomialMod!!, polynomialCoefsField, literals[literalNumber].toString())
        return PolynomialField(polynomial) to literalNumber + 1
    }
}