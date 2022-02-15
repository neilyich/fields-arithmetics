package neilyich.field.serialization

internal data class FieldDescription(val type: FieldType, val mod: Int?, val innerField: FieldDescription?, val polynomialMod: PolynomialDescription?)
