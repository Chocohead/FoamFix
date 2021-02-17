package pl.asie.foamfix.blob;

import java.lang.reflect.Field;

import org.objectweb.asm.Type;

enum FieldType {
	BOOLEAN,
	BYTE,
	CHAR,
	SHORT,
	INT,
	LONG,
	FLOAT,
	DOUBLE,
	OBJECT;

	public static FieldType of(Field field) {
		Type fieldType = Type.getType(field.getType());

		switch (fieldType.getSort()) {
		case Type.BOOLEAN:
			return BOOLEAN;

		case Type.CHAR:
			return CHAR;

		case Type.BYTE:
			return BYTE;

		case Type.SHORT:
			return SHORT;

		case Type.INT:
			return INT;

		case Type.FLOAT:
			return FLOAT;

		case Type.LONG:
			return LONG;

		case Type.DOUBLE:
			return DOUBLE;

		case Type.ARRAY:
		case Type.OBJECT:
			return OBJECT;

		case Type.VOID:
		case Type.METHOD:
		default:
			throw new IllegalStateException("Unexpected field type: " + fieldType + " for " + field.getDeclaringClass() + '#' + field.getName());
		}
	}

	public static FieldType of(Class<?> type) {
		Type fieldType = Type.getType(type);

		switch (fieldType.getSort()) {
		case Type.BOOLEAN:
			return BOOLEAN;

		case Type.CHAR:
			return CHAR;

		case Type.BYTE:
			return BYTE;

		case Type.SHORT:
			return SHORT;

		case Type.INT:
			return INT;

		case Type.FLOAT:
			return FLOAT;

		case Type.LONG:
			return LONG;

		case Type.DOUBLE:
			return DOUBLE;

		case Type.ARRAY:
		case Type.OBJECT:
			return OBJECT;

		case Type.VOID:
		case Type.METHOD:
		default:
			throw new IllegalArgumentException("Invalid field type: " + type + " (yielding " + fieldType + ')');
		}
	}
}