package neilyich.field.serialization.description.factory

import neilyich.field.Field
import neilyich.field.element.FieldElement
import neilyich.field.serialization.FieldDescription

internal interface FieldDescriptionFactory {
    fun createFieldDescription(field: Field<out FieldElement>): FieldDescription
}