class Basic {
    public static void main (String[] args) {
        System.out.println(new YoMama().potato());
    }
}

class YoMama {
    int oogabooga;
    public int potato() {
        YoMama k;
        int a;
        oogabooga = 2;
        System.out.println(this.aa());
        k = new YoMama();
        System.out.println(k.aa());
        System.out.println(k.aa());
        System.out.println(this.aa());
        System.out.println(new YoMama().aa());
        return 5;   
    }

    public int aa() {
        return oogabooga;
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