package neilyich.util.matrix

import neilyich.field.Field
import neilyich.field.element.FieldElement

fun <Element: FieldElement> identityMatrix(field: Field<Element>, n: Int): AFieldMatrix<Element> {
    return FieldMatrix(field, (0 until n).map { row ->
        (0 until n).map { col ->
            if (row == col) {
                field.one()
            } else {
                field.zero()
            }
        }
    })
}

abstract class AFieldMatrix<Element: FieldElement>(val field: Field<Element>): Cloneable {
    abstract fun width(): Int
    abstract fun height(): Int
    abstract operator fun get(row: Int, col: Int): Element
    abstract operator fun get(row: Int): Iterable<Element>
    abstract fun with(row: Int, col: Int, value: Element): AFieldMatrix<Element>
    abstract operator fun plus(other: AFieldMatrix<Element>): AFieldMatrix<Element>
    abstract operator fun times(other: AFieldMatrix<Element>): AFieldMatrix<Element>
    abstract fun mult(coef: Element): AFieldMatrix<Element>
    abstract operator fun unaryMinus(): AFieldMatrix<Element>
    abstract fun transposed(): AFieldMatrix<Element>
    abstract fun concatRight(other: AFieldMatrix<Element>): AFieldMatrix<Element>
    abstract fun concatDown(other: AFieldMatrix<Element>): AFieldMatrix<Element>
    abstract fun splitRight(col: Int): Pair<AFieldMatrix<Element>, AFieldMatrix<Element>> // [0..col-1] [col..width]
    abstract fun splitDown(row: Int): Pair<AFieldMatrix<Element>, AFieldMatrix<Element>>

    abstract fun linearOperation(destRow: Int, sourceRow: Int, coef: Element): AFieldMatrix<Element>
    abstract fun multRow(row: Int, coef: Element): AFieldMatrix<Element>
    abstract fun swapRows(row0: Int, row1: Int): AFieldMatrix<Element>
    abstract fun swapCols(col0: Int, col1: Int): AFieldMatrix<Element>

    operator fun minus(other: AFieldMatrix<Element>): AFieldMatrix<Element> = this.plus(-other)

    public abstract override fun clone(): AFieldMatrix<Element>

    fun column(col: Int): Iterable<Element> = Iterable { ColumnIterator(col) }

    open fun isZero(): Boolean {
        for (row in 0 until height()) {
            for (col in 0 until width()) {
                if (!this[row, col].isZero()) {
                    return false
                }
            }
        }
        return true
    }

    protected fun checkBounds(row: Int, col: Int) {
        checkRowBounds(row)
        checkColBounds(col)
    }
    protected fun checkRowBounds(row: Int) {
        if (row >= height()) {
            throw IndexOutOfBoundsException("row($row) >= height(${height()})")
        }
    }
    protected fun checkColBounds(col: Int) {
        if (col >= width()) {
            throw IndexOutOfBoundsException("col($col) >= width(${width()})")
        }
    }
    protected fun checkPlusBounds(other: AFieldMatrix<Element>) {
        if (other.width() != width() || other.height() != height()) {
            throw IllegalArgumentException("unable to add matrix of different dimensions")
        }
    }
    protected fun checkMultiplyBounds(other: AFieldMatrix<Element>) {
        if (width() != other.height()) {
            throw IllegalArgumentException("unable to multiply matrix of different dimensions")
        }
    }

    protected fun checkEqualRows(other: AFieldMatrix<Element>) {
        if (height() != other.height()) {
            throw IllegalArgumentException("matrix must have same height")
        }
    }

    protected fun checkEqualCols(other: AFieldMatrix<Element>) {
        if (width() != other.width()) {
            throw IllegalArgumentException("matrix must have same height")
        }
    }

    override fun toString(): String {
        val builder = StringBuilder("[\n")
        for (row in 0 until height()) {
            val rowBuilder = StringBuilder("[")
            for (col in 0 until width()) {
                rowBuilder.append(this[row, col].toString()).append(", ")
            }
            builder.append(rowBuilder.removeSuffix(", ")).append("]\n")
        }
        builder.append("]")
        return builder.toString()
    }

    private inner class ColumnIterator(private val col: Int): Iterator<Element> {
        private var row = 0

        override fun hasNext(): Boolean = row < height()

        override fun next(): Element = this@AFieldMatrix[row++, col]
    }
}