class BubbleSort {
	public static void main(String[] a) {
		System.out.println((new BBS()).Start(10));
	}
}

class BBS {
	int[] number;
	int size;
	int Print() {
		int j;
		j = 0;
		while (j < size) {
			System.out.println(number[j]);
			j = j + 1;
		}
		return 0;
	}
}

