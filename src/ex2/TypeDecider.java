package ex2;

import ast.*;

public class TypeDecider {
	public static String llvmType(String t) {
		switch (t) {
		case "bool":
		case "boolean":
			return "i1";
		case "int":
			return "i32";
		case "int[]":
			return "i32*";
		}
		return "i8*";
	}

	public static String javaType(AstType t) {
		if (t instanceof BoolAstType)
			return "boolean";
		else if (t instanceof IntAstType)
			return "int";
		else if (t instanceof IntArrayAstType)
			return "int[]";
		else if (t instanceof RefType) {
			return ((RefType) t).id();
		}
		throw new RuntimeException("invalid type");
	}

	public static String llvmType(AstType t) {
		if (t instanceof BoolAstType)
			return "i1";
		else if (t instanceof IntAstType)
			return "i32";
		else if (t instanceof IntArrayAstType)
			return "i32*";
		else
			return "i8*";
	}

	public static int llvmSize(AstType t) {
		if (t instanceof BoolAstType)
			return 1;
		else if (t instanceof IntAstType)
			return 4;
		else if (t instanceof IntArrayAstType)
			return 8;
		else
			return 8;
	}
}
