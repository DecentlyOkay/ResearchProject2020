package tasks;

import java.util.ArrayList;

public class RNG {

	//Return an integer in the range [lower, upper]
	//Precondition: upper >= lower
	public static int randomInt(int lower, int upper) {
		return (int)(Math.random()*(upper-lower+1)+lower);
	}
	
	//Return a list of unique integers in the range [lower, upper)
	//Precondition: upper > lower, length < upper-lower+1
	public static ArrayList<Integer> randomList(int lower, int upper, int length) {
		ArrayList<Integer> numbers = new ArrayList<>();
		for(int i = lower; i < upper; i++) {
			numbers.add(i);
		}
		ArrayList<Integer> list = new ArrayList<>();
		while(length-->0) {
			list.add(numbers.remove(randomInt(0, numbers.size()-1)));
		}
		return list;
	}

}
