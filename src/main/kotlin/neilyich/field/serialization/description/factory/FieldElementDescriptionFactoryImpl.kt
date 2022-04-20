package neilyich.field.serialization.description.factory

import neilyich.field.element.FieldElement
import neilyich.field.serialization.FieldElementDescription

class FieldElementDescriptionFactoryImpl : FieldElementDescriptionFactory {
    override fun createFieldElementDescription(element: FieldElement): FieldElementDescription {
        return FieldElementDescription(element.toString())
    }
}