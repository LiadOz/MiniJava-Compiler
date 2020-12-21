class Main {
	public static void main(String[] a) {
	    System.out.println(new Simple().bar());
	}
}

class Simple {
	public int bar() {
	    int x;
	    System.out.println(10 + x * (x + 7));
	    return 0;
	}
}