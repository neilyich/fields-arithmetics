package neilyich.field.serialization

data class FieldDescription(val type: FieldType, val mod: Int?, val polynomialMod: PolynomialDescription?, val readableDescription: String)
