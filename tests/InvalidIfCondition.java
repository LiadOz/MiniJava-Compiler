class Main {
	public static void main(String[] a) {
		System.out.println((new Simple()).bar());
	}
}

class Simple {
	int bar() {
		if (2)
			System.out.println(0);
		else
			System.out.println(1);
		return 0;
	}

}
