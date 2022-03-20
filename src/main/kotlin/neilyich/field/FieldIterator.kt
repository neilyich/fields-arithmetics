package neilyich.field

import neilyich.field.element.FieldElement

class FieldIterator<Element: FieldElement>(private val field: Field<Element>): Iterator<Element> {
    private val primitiveElement = field.primitiveElement()
    private var currentElement = field.one()
    private var currentElementsCounter = 0

    override fun hasNext(): Boolean = currentElementsCounter < field.size()

    override fun next(): Element {
        if (currentElementsCounter == 0) {
            currentElementsCounter++
            return field.zero()
        }
        if (currentElementsCounter == 1) {
            currentElementsCounter++
            return currentElement
        }
        currentElementsCounter++
        currentElement = field.mult(currentElement, primitiveElement)
        return currentElement
    }

}
