class Main {
	public static void main(String[] a) {
	    System.out.println(new Simple().bar());
	}
}

class Simple {
	public int bar() {
	int x;
	int y;    
        x = 10;
        if (x < 2)
	        y = 5;
	else
	        System.out.println(1);
        return y;
	}
}
