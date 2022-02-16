package neilyich.field.serialization

data class PolynomialDescription(val coefsField: FieldDescription, val coefs: List<PolynomialCoefDescription>, val literal: String)