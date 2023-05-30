class Basic {
    public static void main (String[] args) {
        System.out.println(new YoMama().potato());
    }
}

class YoMama {
    int field;
    public int potato() {
        YoMama a;
        YoMama b;
        a = new YoMama();
        b = new YoMama();
        a = a.yummy(a);
        return 0;


    }

    public YoMama yummy(YoMama y) {
        field = y.get();
        return y;
    }

    public int get() {
        return field;
    }

}


// heap space for k: 0x4056d0
// potato: possibly 0x401256 YAAAAS
// meow: 0x4012bf
// YoMama$$: 0x40120e - (this)


// 
// RBP:  daf8
// k  -0x8(rbp)  value: 0x4056d0
// a
// RSP: dae0 (-1)