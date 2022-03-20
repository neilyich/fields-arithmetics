package neilyich.util.matrix

import neilyich.field.element.FieldElement

class MatrixUtils {
    companion object {

        data class GaussAlgorithmResult<Element: FieldElement>(
            val matrix: AFieldMatrix<Element>,
            val basisColumnsNumbers: List<Int>
        )

        private fun <Element: FieldElement> gaussAlgorithm(matrix: AFieldMatrix<Element>): GaussAlgorithmResult<Element> {
            val w = matrix.width()
            val h = matrix.height()
            val field = matrix.field
            var curCol = 0
            var curRow = 0
            var curMatr = matrix.clone()
            val firstElements = mutableListOf<Int>()
            while (curCol < w) {
                val nonZero = findNonZeroElement(curMatr, curCol, curRow)
                if (nonZero == null) {
                    curCol++
                    continue
                }
                firstElements.add(curCol)
                val coef = field.inverseMult(nonZero.second)
                println("($curCol) swap $curRow and ${nonZero.first}, coef=$coef:")
                curMatr = curMatr.swapRows(curRow, nonZero.first).multRow(curRow, coef)
                println(curMatr)
                for (row in curRow + 1 until h) {
                    if (!curMatr[row, curCol].isZero()) {
                        println("[$row] += [$curRow] * ${field.inverseAdd(curMatr[row, curCol])}")
                        curMatr = curMatr.linearOperation(row, curRow, field.inverseAdd(curMatr[row, curCol]))
                    }
                }
                curRow++
                curCol++
                println(curMatr)
                println()
            }
            val rank = firstElements.size
            curMatr = curMatr.splitDown(rank).first
            for (row in rank - 1 downTo 1) {
                val column = firstElements[row]
                for (r in 0 until row) {
                    if (!curMatr[r, column].isZero()) {
                        println("[$r] += [$row] * ${field.inverseAdd(curMatr[r, column])}")
                        curMatr = curMatr.linearOperation(r, row, field.inverseAdd(curMatr[r, column]))
                    }
                }
                println(curMatr)
            }
            return GaussAlgorithmResult(curMatr, firstElements)
        }

        private fun <Element: FieldElement> findNonZeroElement(matrix: AFieldMatrix<Element>, col: Int, startRow: Int): Pair<Int, Element>? {
            for (row in startRow until matrix.height()) {
                if (!matrix[row, col].isZero()) {
                    return row to matrix[row, col]
                }
            }
            return null
        }

        fun <Element: FieldElement> solve(matrix: AFieldMatrix<Element>): List<VFieldVector<Element>> {
            val field = matrix.field
            val w = matrix.width()
            val solution = mutableListOf<VFieldVector<Element>>()
            val gaussResult = gaussAlgorithm(matrix)
            val basisColumns = gaussResult.basisColumnsNumbers.toHashSet()
            for (freeCol in 0 until w) {
                if (basisColumns.contains(freeCol)) {
                    continue
                }
                val s = (0 until w).map {
                    if (it == freeCol) {
                        field.one()
                    } else {
                        field.zero()
                    }
                }.toMutableList()

                for (row in 0 until gaussResult.matrix.height()) {
                    val column = gaussResult.basisColumnsNumbers[row]
                    val coef = gaussResult.matrix[row, freeCol]
                    s[column] = field.inverseAdd(coef)
                }
                solution.add(VFieldVector(field, s))
            }
            return solution
        }
    }
}