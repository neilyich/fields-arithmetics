package neilyich.field.deserialization.factory

import neilyich.field.Field
import neilyich.field.element.FieldElement
import neilyich.field.serialization.FieldDescription

interface FieldFactory {
    fun createField(fieldDescription: FieldDescription): Field<out FieldElement>
}