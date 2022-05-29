class Factorial{
    public static void main(String[] a){
	    System.out.println(new Fac().sumOf(3,4, 5));
    }
}

class Fac {

    public int sumOf(int x, int y, int z) {
        int a;
        int b;
        int c;
        a = x;
        b = y;
        c = z;
        return a + b + c;
    }

}
