class Factorial{
    public static void main(String[] a){
	    System.out.println(new One().test());
    }
}

class One {
    public int test(){
        int[] a;
        int b;
        a = new int[5];
        a[0] = 1;
        b = a[0];
        return b;
    }
}

