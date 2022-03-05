package neilyich.field.element

import neilyich.field.Field
import neilyich.field.polynomial.AFieldPolynomial

class CachingPolynomialFieldElement<CoefsFieldElement: FieldElement>(
    field: Field<CachingPolynomialFieldElement<CoefsFieldElement>>,
    polynomial: AFieldPolynomial<CoefsFieldElement>,
    discreteLogarithm: Int?
) : PolynomialFieldElement<CachingPolynomialFieldElement<CoefsFieldElement>, CoefsFieldElement>(field, polynomial, discreteLogarithm)