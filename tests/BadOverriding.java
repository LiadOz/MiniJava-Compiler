class Main {
    public static void main(String[] args) {
        System.out.println(1);
    }
}

class Shared {
    int theVar;

    public A override(A a1, A a2) {
        return a2;
    }
    public int foo() {
        return theVar;
    }
}

class A extends Shared { }

class B extends A {
    public B override(B a1, A a2) {
        return a2;
    }
    public int foo() {
        return theVar;
    }
}

class C extends A {
    public int foo() {
        return theVar;
    }
}
