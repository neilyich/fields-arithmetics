package neilyich.field.serialization.deserialization.factory

import neilyich.field.Field
import neilyich.field.element.FieldElement
import neilyich.field.serialization.FieldDescription

internal interface FieldFactory {
    fun createField(fieldDescription: FieldDescription): Field<out FieldElement>
}