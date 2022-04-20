package neilyich.field.serialization.description.factory

import neilyich.field.element.FieldElement
import neilyich.field.serialization.PolynomialRingDescription
import neilyich.ring.PolynomialRing

class PolynomialRingDescriptionFactoryImpl(
    private val fieldElementDescriptionFactory: FieldElementDescriptionFactory = FieldElementDescriptionFactoryImpl(),
    private val polynomialDescriptionFactory: PolynomialDescriptionFactory = PolynomialDescriptionFactoryImpl(fieldElementDescriptionFactory),
    private val fieldDescriptionFactory: FieldDescriptionFactory = FieldDescriptionFactoryImpl(fieldElementDescriptionFactory, polynomialDescriptionFactory)
): PolynomialRingDescriptionFactory {

    override fun createPolynomialRingDescription(ring: PolynomialRing<out FieldElement>): PolynomialRingDescription {
        val fieldDescription = fieldDescriptionFactory.createFieldDescription(ring.mod.field)
        return PolynomialRingDescription(polynomialDescriptionFactory.createPolynomialDescription(ring.mod, fieldDescription), ring.toString())
    }
}