class Factorial{
    public static void main(String[] a){
	    System.out.println(new Fac().sumOf(5,6));
    }
}

class Fac {

    public int sumOf(int x, int y){
		int a;
        int b;
        a = x;
        b = a + y;
        return b;
    }

}
