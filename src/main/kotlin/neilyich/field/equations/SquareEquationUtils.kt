package neilyich.field.equations

import neilyich.field.element.FieldElement
import neilyich.field.element.PrimeFieldElement
import neilyich.field.polynomial.AFieldPolynomial
import neilyich.field.polynomial.FieldPolynomial
import neilyich.field.polynomial.OnePolynomial
import neilyich.field.polynomial.x
import neilyich.field.sortedMultiplicativeGroup
import neilyich.util.NumberUtils
import neilyich.util.pow

class SquareEquationUtils {
    companion object {
        fun solve(equation: SquareEquation<PrimeFieldElement>, logger: (Any) -> Unit = {}): Pair<PrimeFieldElement, PrimeFieldElement>? {
            logger("Solving equation: $equation")
            val field = equation.field
            val d = discriminant(equation)
            logger("discriminant=$d")
            val jacobi = NumberUtils.jacobiSymbol(d.value, field.size())
            logger("(${d.value} / ${field.size()}) = $jacobi")
            if (jacobi != 1) {
                logger("equation ($equation) does not have solution")
                return null
            }
            logger("looking for t in $field such as (x^2 - tx + $d) is prime")
            var mod: AFieldPolynomial<PrimeFieldElement>? = null
            for (t in field.sortedMultiplicativeGroup()) {
                val testD = discriminant(SquareEquation(field, field.one(), field.inverseAdd(t), d))
                val j = NumberUtils.jacobiSymbol(testD.value, field.size())
                logger("t=$t: discriminant=$testD, (${testD.value} / ${field.size()}) = $j")
                if (j == -1) {
                    mod = FieldPolynomial(field, 2 to field.one(), 1 to field.inverseAdd(t), 0 to d)
                    break
                }
            }
            if (mod == null) {
                throw RuntimeException("unable to find prime square polynomial")
            }
            logger("found prime polynomial: $mod")
            logger("sqrt($d)=x^${(field.size() + 1) / 2}")
            val rootDPol = powLog(x(field), (field.size() + 1) / 2, mod, logger)
            logger("x^${(field.size() + 1) / 2}=$rootDPol")
            if (rootDPol.degree() > 0) {
                throw RuntimeException("root polynomial must be constant")
            }
            val rootD = rootDPol[0]
            logger("sqrt($d)=+-$rootD")
            if (field.mult(rootD, rootD) != d) {
                logger("ERROR: $rootD^2 != d")
            }
            val minusB = field.inverseAdd(equation.linearCoef)
            val twoAInverse = field.inverseMult(field.mult(field.one(2), equation.squareCoef))
            val x1 = field.mult(field.add(minusB, rootD), twoAInverse)
            val x2 = field.mult(field.sub(minusB, rootD), twoAInverse)
            logger("x1 = (-${equation.linearCoef} + $rootD) / (2 * ${equation.squareCoef}) = $x1")
            logger("x2 = (-${equation.linearCoef} - $rootD) / (2 * ${equation.squareCoef}) = $x2")
            return x1 to x2
        }
        
        private fun discriminant(equation: SquareEquation<PrimeFieldElement>): PrimeFieldElement {
            val f = equation.field
            val b2 = f.mult(equation.linearCoef, equation.linearCoef)
            val ac = f.mult(equation.squareCoef, equation.freeCoef)
            return f.sub(b2, f.mult(f.one(4), ac))
        }

        private fun <Coef: FieldElement> powLog(pol: AFieldPolynomial<Coef>, pow: Int, mod: AFieldPolynomial<Coef>, logger: (Any) -> Unit): AFieldPolynomial<Coef> {
            logger("calculating $pol^$pow mod$mod")
            val binaryString = pow.toString(2)
            val terms = mutableListOf<Int>()
            val two = 2
            val oldestPow2 = two.pow(binaryString.length - 1)
            for (i in binaryString.indices) {
                val c = binaryString[i]
                if (c == '0') {
                    continue
                }
                terms.add(two.pow(binaryString.length - i - 1))
            }
            logger("$pow = ${terms.joinToString(separator = " + ")}")
            logger("$pol^$pow = ${terms.joinToString(separator = ""){"$pol^$it"}}")
            val powMap = mutableMapOf(0 to OnePolynomial(pol.field, pol.literal), 1 to pol)
            var currentPow = 2
            while (currentPow <= oldestPow2) {
                val tmp = powMap[currentPow / 2]
                if (tmp == null) {
                    logger("something went wrong, but $pol^$pow mod$mod = ${pol.pow(pow, mod)}")
                    return pol.pow(pow, mod)
                }
                logger("$pol^$currentPow = $tmp^2 = ${(tmp * tmp) % mod}")
                powMap[currentPow] = (tmp * tmp) % mod
                currentPow *= 2
            }
            logger("$pol^$pow = ${terms.joinToString(separator = ""){"$pol^$it"}} = ${terms.joinToString(separator = ""){"${powMap[it]}"}}")
            var currentResult: AFieldPolynomial<Coef> = OnePolynomial(pol.field, pol.literal)
            for (i in terms.indices) {
                currentResult *= powMap[terms[i]]!!
                currentResult %= mod
                if (i < terms.size - 1) {
                    logger("$pol^$pow = $currentResult * ${terms.subList(i + 1, terms.size).joinToString(separator = ""){"${powMap[it]}"}}")
                } else {
                    logger("$pol^$pow = $currentResult")
                }
            }
            if (currentResult != pol.pow(pow, mod)) {
                logger("ERROR: $pol^$pow = ${pol.pow(pow, mod)} (not $currentResult)")
                return pol.pow(pow, mod)
            }
            return currentResult
        }
    }
}
