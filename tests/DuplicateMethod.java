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

    public int bar(int theVar, int anotherVar) {
        return theVar + anotherVar;
    }
    public int bar(int theVar) {
        return theVar;
    }
}

class A extends Shared { }

class B extends A {
    public int foo() {
        return theVar;
    }
}

class C extends A {
    public int foo() {
        return theVar;
    }
}
