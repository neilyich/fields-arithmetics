package neilyich.util.matrix

import neilyich.field.Field
import neilyich.field.element.FieldElement

class FieldMatrix<Element: FieldElement>(
    field: Field<Element>,
    private val content: List<List<Element>>
): AFieldMatrix<Element>(field), Cloneable {
    private val width: Int
    private val height: Int

    init {
        val h = content.size
        val w = if (content.isEmpty()) {
            0
        } else {
            content[0].size
        }
        for (row in content) {
            if (row.size != w) {
                throw IllegalArgumentException("rows of matrix must have same length")
            }
        }
        height = h
        width = w
    }

    override fun width(): Int = width

    override fun height(): Int = height

    override fun get(row: Int, col: Int): Element {
        checkBounds(row, col)
        return content[row][col]
    }

    override fun with(row: Int, col: Int, value: Element): AFieldMatrix<Element> {
        checkBounds(row, col)
        val newContent = content.toMutableList()
        val newRow = newContent[row].toMutableList()
        newRow[col] = value
        newContent[row] = newRow.toList()
        return FieldMatrix(field, newContent.toList())
    }

    override fun plus(other: AFieldMatrix<Element>): AFieldMatrix<Element> {
        checkPlusBounds(other)
        val newContent = content.mapIndexed { i, row ->
            row.mapIndexed { j, el ->
                field.add(el, other[i, j])
            }
        }
        return FieldMatrix(field, newContent)
    }

    override fun times(other: AFieldMatrix<Element>): AFieldMatrix<Element> {
        checkMultiplyBounds(other)
        val newContent = mutableListOf<List<Element>>()
        for (row in 0 until height) {
            val newRow = mutableListOf<Element>()
            for (col in 0 until other.width()) {
                var current = field.zero()
                for (i in 0 until width) {
                    current = field.add(current, field.mult(this[row, i], other[i, col]))
                }
                newRow.add(current)
            }
            newContent.add(newRow.toList())
        }
        return FieldMatrix(field, newContent.toList())
    }

    override fun unaryMinus(): AFieldMatrix<Element> {
        return FieldMatrix(field,
            content.map { row ->
                row.map { el ->
                    field.inverseAdd(el)
                }
            }
        )
    }

    override fun clone(): FieldMatrix<Element> {
        return FieldMatrix(field, content.map { it.toList() })
    }

    override fun get(row: Int): Iterable<Element> {
        checkRowBounds(row)
        return content[row]
    }

    override fun mult(coef: Element): AFieldMatrix<Element> {
        return FieldMatrix(field, content.map { row -> row.map { field.mult(it, coef) } })
    }

    override fun transposed(): AFieldMatrix<Element> {
        val newContent = (0 until width).map { col ->
            (0 until height).map { row ->
                this[row, col]
            }
        }
        return FieldMatrix(field, newContent)
    }

    override fun concatRight(other: AFieldMatrix<Element>): AFieldMatrix<Element> {
        checkEqualRows(other)
        val newContent = content.mapIndexed { i, row -> row + other[i] }
        return FieldMatrix(field, newContent)
    }

    override fun concatDown(other: AFieldMatrix<Element>): AFieldMatrix<Element> {
        checkEqualCols(other)
        val newContent = content + (0 until other.height()).map { other[it].toList() }
        return FieldMatrix(field, newContent)
    }

    override fun splitRight(col: Int): Pair<AFieldMatrix<Element>, AFieldMatrix<Element>> {
        checkColBounds(col)
        val rContent = content.map { it.subList(0, col) }
        val lContent = content.map { it.subList(col, width()) }
        return FieldMatrix(field, rContent) to FieldMatrix(field, lContent)
    }

    override fun splitDown(row: Int): Pair<AFieldMatrix<Element>, AFieldMatrix<Element>> {
        checkRowBounds(row)
        val upContent = content.subList(0, row)
        val downContent = content.subList(row, height())
        return FieldMatrix(field, upContent) to FieldMatrix(field, downContent)
    }

    override fun swapRows(row0: Int, row1: Int): AFieldMatrix<Element> {
        checkRowBounds(row0)
        checkRowBounds(row1)
        val swappedContent = content.toMutableList()
        swappedContent[row0] = swappedContent[row1].also { swappedContent[row1] = swappedContent[row0] }
        return FieldMatrix(field, swappedContent.toList())
    }

    override fun swapCols(col0: Int, col1: Int): AFieldMatrix<Element> {
        checkColBounds(col0)
        checkColBounds(col1)
        val swappedContent = content.map { it.toMutableList() }
        swappedContent.forEach { row -> row[col0] = row[col1].also { row[col1] = row[col0] } }
        return FieldMatrix(field, swappedContent)
    }

    override fun linearOperation(destRow: Int, sourceRow: Int, coef: Element): AFieldMatrix<Element> {
        checkRowBounds(destRow)
        checkRowBounds(sourceRow)
        val source = this[sourceRow].toList()
        return FieldMatrix(field,
            content.mapIndexed { r, rowContent ->
                if (r == destRow) {
                    rowContent.mapIndexed { col, el ->
                        field.add(el, field.mult(source[col], coef))
                    }
                } else {
                    rowContent
                }
            }
        )
    }

    override fun multRow(row: Int, coef: Element): AFieldMatrix<Element> {
        checkRowBounds(row)
        return FieldMatrix(field,
            content.mapIndexed { r, rowContent ->
                if (r == row) {
                    rowContent.map { el ->
                        field.mult(el, coef)
                    }
                } else {
                    rowContent
                }
            }
        )
    }
}