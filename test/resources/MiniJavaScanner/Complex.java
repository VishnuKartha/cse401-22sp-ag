public int search(){
    int i = 0;
    int[] arr = new int[10];
    while(i < arr.length){
        if(arr[i] < 0){
            i++;
        }else{
            i--;
        }
    }
    return i;
}