package neilyich.util.matrix

import neilyich.field.Field
import neilyich.field.element.FieldElement

class VFieldVector<Element: FieldElement>(
    field: Field<Element>,
    private val content: List<Element>
): AFieldMatrix<Element>(field) {

    override fun width(): Int = 1

    override fun height(): Int = content.size

    override fun get(row: Int, col: Int): Element {
        checkBounds(row, col)
        return content[row]
    }

    override fun get(row: Int): Iterable<Element> {
        checkRowBounds(row)
        return listOf(content[row])
    }

    override fun with(row: Int, col: Int, value: Element): VFieldVector<Element> {
        checkBounds(row, col)
        val newContent = content.toMutableList()
        newContent[row] = value
        return VFieldVector(field, newContent.toList())
    }

    override fun plus(other: AFieldMatrix<Element>): VFieldVector<Element> {
        checkPlusBounds(other)
        return VFieldVector(field, content.mapIndexed { i, el ->
            field.add(el, other[i, 0])
        })
    }

    override fun times(other: AFieldMatrix<Element>): AFieldMatrix<Element> {
        checkMultiplyBounds(other)
        return FieldMatrix(field, content.map { listOf(it) }) * other
    }

    override fun unaryMinus(): VFieldVector<Element> {
        return VFieldVector(field, content.map { field.inverseAdd(it) })
    }

    override fun clone(): VFieldVector<Element> = VFieldVector(field, content.toList())

    override fun mult(coef: Element): VFieldVector<Element> {
        return VFieldVector(field, content.map { field.mult(it, coef) })
    }

    override fun transposed(): HFieldVector<Element> = HFieldVector(field, content)

    override fun concatRight(other: AFieldMatrix<Element>): AFieldMatrix<Element> {
        checkEqualRows(other)
        val newContent = (0 until height()).map { this[it].toMutableList() }
        for (row in 0 until other.height()) {
            for (col in 0 until other.width()) {
                newContent[row].add(other[row, col])
            }
        }
        return FieldMatrix(field, newContent)

    }

    override fun concatDown(other: AFieldMatrix<Element>): VFieldVector<Element> {
        checkEqualCols(other)
        val newContent = content + other.column(0)
        return VFieldVector(field, newContent)
    }

    override fun splitRight(col: Int): Pair<AFieldMatrix<Element>, AFieldMatrix<Element>> {
        throw UnsupportedOperationException("unable to split vertical vector vertically")
    }

    override fun splitDown(row: Int): Pair<VFieldVector<Element>, VFieldVector<Element>> {
        checkRowBounds(row)
        val upContent = content.subList(0, row)
        val downContent = content.subList(row, height())
        return VFieldVector(field, upContent) to VFieldVector(field, downContent)
    }

    override fun swapRows(row0: Int, row1: Int): VFieldVector<Element> {
        checkRowBounds(row0)
        checkRowBounds(row1)
        val swappedContent = content.toMutableList()
        swappedContent[row0] = swappedContent[row1].also { swappedContent[row1] = swappedContent[row0] }
        return VFieldVector(field, swappedContent.toList())
    }

    override fun swapCols(col0: Int, col1: Int): VFieldVector<Element> {
        checkColBounds(col0)
        checkColBounds(col1)
        return this
    }

    override fun linearOperation(destRow: Int, sourceRow: Int, coef: Element): AFieldMatrix<Element> {
        checkRowBounds(destRow)
        checkRowBounds(sourceRow)
        val sourceCoef = content[sourceRow]
        return VFieldVector(field, content.mapIndexed { r, el ->
            if (r == destRow) {
                field.add(el, field.mult(sourceCoef, coef))
            } else {
                el
            }
        })
    }

    override fun multRow(row: Int, coef: Element): AFieldMatrix<Element> {
        checkRowBounds(row)
        return VFieldVector(field, content.mapIndexed { r, el ->
            if (r == row) {
                field.mult(el, coef)
            } else {
                el
            }
        })
    }

    fun toList(): List<Element> = content
}