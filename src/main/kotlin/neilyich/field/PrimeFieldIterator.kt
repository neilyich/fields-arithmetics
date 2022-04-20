package neilyich.field

import neilyich.field.element.PrimeFieldElement

class PrimeFieldIterator(private val field: Field<PrimeFieldElement>): Iterator<PrimeFieldElement> {
    private val size = field.size()
    private var currentValue = 0

    override fun hasNext(): Boolean {
        return currentValue <= size - 1
    }

    override fun next(): PrimeFieldElement {
        return PrimeFieldElement(field, currentValue++)
    }
}

fun Field<PrimeFieldElement>.sorted(): Iterable<PrimeFieldElement> {
    return Iterable { PrimeFieldIterator(this) }
}

fun Field<PrimeFieldElement>.sortedMultiplicativeGroup(): Iterable<PrimeFieldElement> {
    return Iterable { sorted().iterator().apply { next() } }
}
