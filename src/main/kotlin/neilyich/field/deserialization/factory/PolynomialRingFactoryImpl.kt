package neilyich.field.deserialization.factory

import neilyich.field.element.FieldElement
import neilyich.field.serialization.PolynomialRingDescription
import neilyich.ring.PolynomialRing

class PolynomialRingFactoryImpl(
    private val polynomialFactory: PolynomialFactory = PolynomialFactoryImpl(),
    private val fieldFactory: FieldFactory = FieldFactoryImpl(polynomialFactory)
) : PolynomialRingFactory {

    override fun createPolynomialRing(polynomialRingDescription: PolynomialRingDescription): PolynomialRing<out FieldElement> {
        val fieldDescription = polynomialRingDescription.polynomialMod.coefsField
        val coefsField = fieldFactory.createField(fieldDescription)
        val polynomial = polynomialFactory.createPolynomial(polynomialRingDescription.polynomialMod, coefsField)
        return PolynomialRing(polynomial)
    }
}