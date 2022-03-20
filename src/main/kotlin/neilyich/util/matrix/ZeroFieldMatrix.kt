package neilyich.util.matrix

import neilyich.field.Field
import neilyich.field.element.FieldElement

class ZeroFieldMatrix<Element: FieldElement>(
    field: Field<Element>,
    private val height: Int,
    private val width: Int
): AFieldMatrix<Element>(field) {

    private val zero = field.zero()

    override fun width(): Int = width

    override fun height(): Int = height

    override fun isZero(): Boolean = true

    override fun get(row: Int, col: Int): Element {
        checkBounds(row, col)
        return zero
    }

    override fun get(row: Int): Iterable<Element> {
        checkRowBounds(row)
        return (1..width).map { zero }.toList()
    }

    override fun with(row: Int, col: Int, value: Element): AFieldMatrix<Element> {
        return FieldMatrix(field, (0 until height).map { r ->
            if (row == r) {
                (0 until width).map { c ->
                    if (col == c) value else zero
                }
            }
            else {
                (0 until width).map {
                    zero
                }
            }
        })
    }

    override fun plus(other: AFieldMatrix<Element>): AFieldMatrix<Element> {
        checkPlusBounds(other)
        return other.clone()
    }

    override fun times(other: AFieldMatrix<Element>): AFieldMatrix<Element> {
        checkMultiplyBounds(other)
        return this
    }

    override fun unaryMinus(): AFieldMatrix<Element> = this

    override fun clone(): AFieldMatrix<Element> = this

    override fun mult(coef: Element): AFieldMatrix<Element> = this

    override fun transposed(): ZeroFieldMatrix<Element> = ZeroFieldMatrix(field, width, height)

    override fun concatRight(other: AFieldMatrix<Element>): AFieldMatrix<Element> {
        checkEqualRows(other)
        if (other is ZeroFieldMatrix) {
            return ZeroFieldMatrix(field, height, width + other.width())
        }
        val newContent = (0 until height).map { (0 until width).map { zero }.toMutableList() }
        for (row in 0 until other.height()) {
            for (col in 0 until other.width()) {
                newContent[row].add(other[row, col])
            }
        }
        return FieldMatrix(field, newContent)
    }

    override fun concatDown(other: AFieldMatrix<Element>): AFieldMatrix<Element> {
        checkEqualCols(other)
        if (other is ZeroFieldMatrix) {
            return ZeroFieldMatrix(field, height + other.height(), width)
        }
        val newContent = (0 until height + other.height()).map { row ->
            (0 until width).map {  col ->
                if (row < height) {
                    zero
                } else {
                    other[row - height, col]
                }
            }
        }
        return FieldMatrix(field, newContent)
    }

    override fun splitRight(col: Int): Pair<ZeroFieldMatrix<Element>, ZeroFieldMatrix<Element>> {
        checkColBounds(col)
        return ZeroFieldMatrix(field, height, col) to ZeroFieldMatrix(field, height, width - col)
    }

    override fun splitDown(row: Int): Pair<AFieldMatrix<Element>, AFieldMatrix<Element>> {
        checkRowBounds(row)
        return ZeroFieldMatrix(field, row, width) to ZeroFieldMatrix(field, height - row, width)
    }

    override fun swapRows(row0: Int, row1: Int): AFieldMatrix<Element> = this

    override fun swapCols(col0: Int, col1: Int): AFieldMatrix<Element> = this

    override fun linearOperation(destRow: Int, sourceRow: Int, coef: Element): AFieldMatrix<Element> = this

    override fun multRow(row: Int, coef: Element): AFieldMatrix<Element> = this

}