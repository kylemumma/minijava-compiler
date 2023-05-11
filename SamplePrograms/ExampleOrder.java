class ExampleOrder {

    public static void main (String[] args) {
        System.out.println(new c1().f());
    }
}

class c1 {
    public int f() {
        int a;
        int b;
        int c;
        {
            a = 2;
            a = 3;
            a = 4;
        }
        return 5;
    }
    public int middle() {
        return 55;
    }
    public int last() {
        return 555;
    }
}

class c2 {}
