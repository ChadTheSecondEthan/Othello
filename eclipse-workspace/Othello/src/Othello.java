import java.util.Scanner;

/*
 * Author: Ethan Fisher
 * Date: 3/27/2020
 * 
 * This class starts the game by initializing the field
 */

public class Othello {

	public static void main(String[] args) {
		
		// initialize field
		Field field = new Field();
		
		System.out.println("Welcome to Othello!\n\nHere is the starting field. "
				+ "Black 0's represents player one's pieces,\n"
				+ "and White 0's represent player two's pieces.\n");
		
		// show instructions
		showInstructions();
		
		// print the starting field
		field.printField();
		
		// run the game
		field.play();
	}
	
	private static void showInstructions() {
		System.out.println("Your goal is to end the game the with the most\n"
				+ "pieces colored your color. When it is your turn, you choose a position\n"
				+ "adjacent to an opponent's piece that is also across from one of your pieces,\n"
				+ "which will flip all of the pieces in between to be your color.\n");
	}
	
	public static String getInput(String prompt, String[] params) {
		
		// keeps the input
		String s;
		
		// Scanner for input
		Scanner input = new Scanner(System.in);
		
		while(true) {
			System.out.print(prompt);
			
			// keep things lower case
			do s = input.nextLine().toLowerCase();
			while(s.equals(""));
			
			// the input must be one specified in params
			for (String str : params) 
				if (s.equals(str.toLowerCase()))
					return s;
			
			// otherwise it's invalid
			System.out.print("Invalid. Try again. ");
		}
	}
	
	public static boolean getYesOrNo(String prompt) {
		
		// keeps the input
		String s;
		
		// Scanner for input
		Scanner input = new Scanner(System.in);
		
		while(true) {
			System.out.print(prompt + "(y or n) ");
			
			// keep things lower case
			do s = input.nextLine().toLowerCase();
			while(s.equals(""));
			
			// return only if the answer is yes or no
			if (s.equals("yes") || s.equals("y"))
				return true;
			else if (s.equals("no") || s.equals("n"))
				return false;
			
			// otherwise it's invalid
			System.out.print("Invalid. Try again. ");
		}
	}

}
