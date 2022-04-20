package neilyich.field.serialization.description.factory

import neilyich.field.element.FieldElement
import neilyich.field.serialization.FieldElementDescription

interface FieldElementDescriptionFactory {
    fun createFieldElementDescription(element: FieldElement): FieldElementDescription
}