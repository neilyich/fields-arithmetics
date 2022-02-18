package neilyich.field.serialization.description.factory

import neilyich.field.element.FieldElement
import neilyich.field.serialization.PolynomialRingDescription
import neilyich.ring.PolynomialRing

class PolynomialRingDescriptionFactoryImpl(
    private val polynomialDescriptionFactory: PolynomialDescriptionFactory = PolynomialDescriptionFactoryImpl(),
    private val fieldDescriptionFactory: FieldDescriptionFactory = FieldDescriptionFactoryImpl(polynomialDescriptionFactory)
): PolynomialRingDescriptionFactory {

    override fun createPolynomialRingDescription(ring: PolynomialRing<out FieldElement>): PolynomialRingDescription {
        val fieldDescription = fieldDescriptionFactory.createFieldDescription(ring.mod.field)
        return PolynomialRingDescription(polynomialDescriptionFactory.createPolynomialDescription(ring.mod, fieldDescription), ring.toString())
    }
}