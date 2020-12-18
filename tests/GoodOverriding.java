class Main {
	public static void main(String[] args) {
		System.out.println(1);
	}
}

class Shared {
	int theVar;

	A override(A a1, A a2) {
		return a2;
	}

	int foo() {
		return theVar;
	}

}

class A extends Shared {
}

class B extends A {
	A override(A a1, A a2) {
		return a2;
	}

	int foo() {
		return theVar;
	}

}

class C extends A {
	int foo() {
		return theVar;
	}

}

