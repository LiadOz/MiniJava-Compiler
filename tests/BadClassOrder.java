class Main {
    public static void main(String[] args) {
        System.out.println(1);
    }
}

class Shared {
    int theVar;

    public int foo() {
        return theVar;
    }
}

class B extends A {
    public int foo() {
        return theVar;
    }
}

class A extends Shared { }

class C extends A {
    public int foo() {
        return theVar;
    }
}
