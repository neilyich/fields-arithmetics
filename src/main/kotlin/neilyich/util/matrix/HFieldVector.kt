package neilyich.util.matrix

import neilyich.field.Field
import neilyich.field.element.FieldElement

class HFieldVector<Element: FieldElement>(
    field: Field<Element>,
    private val content: List<Element>
): AFieldMatrix<Element>(field) {

    override fun width(): Int = content.size

    override fun height(): Int = 1

    override fun get(row: Int, col: Int): Element {
        checkBounds(row, col)
        return content[col]
    }

    override fun get(row: Int): Iterable<Element> {
        checkRowBounds(row)
        return content
    }

    override fun with(row: Int, col: Int, value: Element): HFieldVector<Element> {
        checkBounds(row, col)
        val newContent = content.toMutableList()
        newContent[col] = value
        return HFieldVector(field, newContent.toList())
    }

    override fun plus(other: AFieldMatrix<Element>): HFieldVector<Element> {
        checkPlusBounds(other)
        return HFieldVector(field, content.mapIndexed { col, el ->
            field.add(el, other[0, col])
        })
    }

    override fun times(other: AFieldMatrix<Element>): AFieldMatrix<Element> {
        checkMultiplyBounds(other)
        return FieldMatrix(field, listOf(content)) * other
    }

    override fun unaryMinus(): HFieldVector<Element> {
        return HFieldVector(field, content.map { field.inverseAdd(it) })
    }

    override fun clone(): HFieldVector<Element> = HFieldVector(field, content.toList())

    override fun mult(coef: Element): HFieldVector<Element> {
        return HFieldVector(field, content.map { field.mult(it, coef) })
    }

    override fun transposed(): VFieldVector<Element> = VFieldVector(field, content)

    override fun concatRight(other: AFieldMatrix<Element>): HFieldVector<Element> {
        checkEqualRows(other)
        val newContent = content + other[0]
        return HFieldVector(field, newContent)
    }

    override fun concatDown(other: AFieldMatrix<Element>): AFieldMatrix<Element> {
        checkEqualCols(other)
        val newContent = (0 until 1 + other.height()).map { row ->
            (0 until width()).map { col ->
                if (row == 0) {
                    this[0, col]
                } else {
                    other[row - 1, col]
                }
            }
        }
        return FieldMatrix(field, newContent)
    }

    override fun splitRight(col: Int): Pair<HFieldVector<Element>, HFieldVector<Element>> {
        checkColBounds(col)
        val rContent = content.subList(0, col)
        val lContent = content.subList(col, width())
        return HFieldVector(field, rContent) to HFieldVector(field, lContent)
    }

    override fun splitDown(row: Int): Pair<AFieldMatrix<Element>, AFieldMatrix<Element>> {
        throw UnsupportedOperationException("unable to split horizontal vector horizontally")
    }

    override fun swapRows(row0: Int, row1: Int): HFieldVector<Element> {
        checkRowBounds(row0)
        checkRowBounds(row1)
        return this
    }

    override fun swapCols(col0: Int, col1: Int): HFieldVector<Element> {
        checkColBounds(col0)
        checkColBounds(col1)
        val swappedContent = content.toMutableList()
        swappedContent[col0] = swappedContent[col1].also { swappedContent[col1] = swappedContent[col0] }
        return HFieldVector(field, swappedContent.toList())
    }

    override fun linearOperation(destRow: Int, sourceRow: Int, coef: Element): AFieldMatrix<Element> {
        checkRowBounds(destRow)
        checkRowBounds(sourceRow)
        return HFieldVector(field, content.map { field.add(it, field.mult(it, coef)) })
    }

    override fun multRow(row: Int, coef: Element): AFieldMatrix<Element> {
        checkRowBounds(row)
        return mult(coef)
    }

    fun toList(): List<Element> = content
}