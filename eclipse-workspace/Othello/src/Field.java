import java.awt.Point;
import java.util.Scanner;
import java.util.ArrayList;

/*
 * Author: Ethan Fisher
 * Date: 3/27/2020
 * 
 * This class controls everything within the field
 */

public class Field {
	
	// used to get input from the user
	private Scanner input;
	
	/* ansi colors for outputs from 
	 * https://stackoverflow.com/questions/5762491/how-to-print-color-in-console-using-system-out-println 
	 * Start */
	public static final String ANSI_RESET = "\u001B[0m";
	public static final String ANSI_BLACK = "\u001B[30m";
	public static final String ANSI_WHITE = "\u001B[37m";
	/* End */
	
	// variables used for every location of the field
	public static final int EMPTY = 0;
	public static final int PLAYER_ONE = 1;
	public static final int PLAYER_TWO = 2;
	
	// difficulties for the computer
	public static final int EASY = 0;
	public static final int HARD = 1;
	
	// controls each position on the field
	private int[][] field;
	
	// current user
	private int currentUser;

	public Field() {
		
		// initialize scanner
		input = new Scanner(System.in);
		
		// initialize array
		field = new int[8][8];
		
		// make every spot empty
		for (int i = 0; i < 8; i++)
			for (int j = 0; j < 8; j++)
				field[i][j] = EMPTY;
		
		// in othello, these are the players' starting positions
		field[3][3] = field[4][4] = PLAYER_ONE;
		field[3][4] = field[4][3] = PLAYER_TWO;
		
		// initialize current user
		currentUser = PLAYER_ONE;
	}
	
	public void play() {
		
		// checks if the player is playing with a computer
		boolean usingComputer = Othello.getYesOrNo("Are you playing with a computer?");
		
		// if they are, find the difficulty
		int difficulty = 0;
		
		if (usingComputer) {
			
			// used to get input from the user with a limited number of valid inputs
			String strDifficulty = Othello.getInput("What is the difficulty of the computer?"
					+ " (Easy or Hard) ", new String[] { "easy", "hard" });
			
			// evaluate input from user
			if (strDifficulty.equals("easy"))
				difficulty = EASY;
			else
				difficulty = HARD;
		}
		
		do {
			
			// tell the user the points they're allowed to use
			ArrayList<Point> legalPoints = getLegalPoints();
			
			// check if the current user has no spots to choose
			if (legalPoints.size() == 0) {
				
				// find the user and other player
				String user, otherPlayer;
				if (currentUser == PLAYER_ONE) {
					user = "one";
					otherPlayer = usingComputer ? "The computer" : "Player two";
				} else {
					user = usingComputer ? "The computer" : "Player two";
					otherPlayer = "one";
				}
				
				// tell the user that the other player has won
				System.out.println(user + " does not have any legal points to player,\n"
						+ "therefore " + otherPlayer + " has won.");
				break;
			}
			
			// get a point from the user
			Point p;
			
			// if the computer is being used and it's player two's turn
			if (usingComputer && currentUser == PLAYER_TWO) 
				
				// get the computer's choice and tell the user
				p = getComputerChoice(legalPoints, difficulty);
			else
				// otherwise it's a real person
				p = getPointFromUser(legalPoints);
			
			// input point into field
			field[p.x][p.y] = currentUser;
			
			// flip the pieces from the inputed point and print the field
			ArrayList<Point> flippedPoints = getFlippedPoints(p);
			flipPieces(flippedPoints);
			printField();
			
			// switch user
			currentUser = currentUser == PLAYER_ONE ? PLAYER_TWO : PLAYER_ONE;
			
			// loops while the current player hasn't won
		} while (!checkWin());
		
		// end game
		System.out.println("Thank you for playing Othello!");
	}
	
	public void printField() {
		
		/* prints the field out, with blue for player 1 and
         * red for player 2 */
		
		// number the field (adding spaces to line up with the y-axis)
		System.out.println("  1 2 3 4 5 6 7 8");
		
		for (int i = 0; i < 8; i++)  {
			
			// number the field and add a space
			System.out.print((i + 1) + " ");
			
			for (int j = 0; j < 8; j++) {
				
				// prints different things based on who selected the piece of the field
				switch(field[j][i]) {
					case PLAYER_ONE:
						System.out.print(ANSI_BLACK + "O " + ANSI_RESET);
						break;
					case PLAYER_TWO:
						System.out.print(ANSI_WHITE + "O " + ANSI_RESET);
						break;
					default:
						System.out.print(ANSI_BLACK + "_ " + ANSI_RESET);
				}
			}
			// print a new line
			System.out.println();
		}
	}
	
	private ArrayList<Point> getLegalPoints() {
		
		// contains every legal point the user can choose
		ArrayList<Point> legalPoints = new ArrayList<>();
		
		for (int i = 0; i < 8; i++)
			for (int j = 0; j < 8; j++) {
				
				// check if the current point is legal, and add it if it is
				Point p = new Point(i, j);
				if (isLegal(p))
					legalPoints.add(p);
			}
		
		return legalPoints;
	}
	
	private Point getPointFromUser(ArrayList<Point> legalPoints) {
		// gets a legal point from the user
		
		System.out.print("Player " + (currentUser == PLAYER_ONE ? "one" : "two") + ", pick a point"
				+ " on the field to put your piece in the form X-Y. (Ex: 1-3) ");
		
		while(true) {
			
			// get answer in the form of two strings, holding x and y respectively
			String[] stringAnswer = input.nextLine().split("-");
			
			// integer values for points
			int x, y;
			
			// make sure only x and y were inputed
			if (stringAnswer.length == 2) {
				try {
					x = Integer.parseInt(stringAnswer[0]);
					y = Integer.parseInt(stringAnswer[1]);
				} catch(NumberFormatException e) {
					// if the input isn't an integer, continue
					System.out.print("Invalid. Try again. ");
					continue;
				}
				
				// subtract 1 because they pick 1-8 and the array is from 0-7
				Point p = new Point(x - 1, y - 1);
				
				// make sure the point they chose is legal
				if (legalPoints.contains(p))
					return p;
				else
					System.out.print("Invalid. Try again. ");
				
			} else
				System.out.print("Invalid. Try again. ");
		}
	}
	
	private boolean isLegal(Point p) {
		
		// make sure the point is within the boundaries 1-8 inclusive
		if (p.x < 0 || p.y < 0 || p.x > 7 || p.y > 7)
			return false;
		
		// check if the piece is taken
		if (field[p.x][p.y] != EMPTY)
			return false;
		
		// check if the piece is adjacent to an opponent's
		boolean adjacent = false;
		int otherPlayer = currentUser == PLAYER_ONE ? PLAYER_TWO : PLAYER_ONE;
		
		// use math.max and math.min the make sure no OutOfBoundsExceptions occur
		for (int x = Math.max(p.x - 1, 0); x <= Math.min(p.x + 1, 7); x++) 
			for (int y = Math.max(p.y - 1, 0); y <= Math.min(p.y + 1, 7); y++) {
				
				// don't test the current position
				if (x == p.x && y == p.y)
					continue;
				
				if (field[x][y] == otherPlayer) {
					adjacent = true;
					
					// add to x to break from the first loop, then break from the second
					x += 3;
					break;
				}
			}
		if (!adjacent)
			return false;
		
		// check if there is a piece across from the selected one
		// and the space in between isn't empty
		for (int x = 0; x < 8; x++) 
			for (int y = 0; y < 8; y++) {
				
				// only check diagonals or straight lines
				int xDist = Math.abs(p.x - x);
				int yDist = Math.abs(p.y - y);
				
				// both p and the other point must be the user's pieces
				if (field[x][y] == currentUser) {
					if (xDist == yDist && xDist > 1) {
						
						// meaning the point is diagonal from the given point
						// and far enough away so there is space for a point in between
						if (checkDiagonalPointsBetween(p, new Point(x, y))) 
							return true;
						
					} else if (xDist > 1 && yDist == 0) {
						
						// meaning it's a horizontal line from the given point
						if (checkHorizontalPointsBetween(p, new Point(x, y)))
							return true;
						
					} else if (yDist > 1 && xDist == 0) {
						
						// meaning it's a vertical line from the given point
						if (checkVerticalPointsBetween(p, new Point(x, y))) 
							return true;
						
					} else
						// otherwise the point isn't diagonal or straight across
						continue;
				}
			}
			
		// if none of the check points between return true, it isn't legal
		return false;
	}
	
	private boolean checkDiagonalPointsBetween(Point p, Point p2) {
		// make sure none of the points are empty or the current user's
		
		// find the larger and smaller x's and the smaller y
		int x1, x2, y;
		if (p.x < p2.x) {
			x1 = p.x;
			x2 = p2.x;
		} else {
			x1 = p2.x;
			x2 = p.x;
		}
		
		if (p.y < p2.y)
			y = p.y;
		else 
			y = p2.y;
		
		// x2 - x1 is the distance between the points, so it could be y1 - y2 also
		for (int i = 1; i < x2 - x1; i++) {
			
			// make sure the pieces in between are the opponent's
			if (field[x1 + i][y + i] == EMPTY || field[x1 + i][y + i] == currentUser)
				return false;
		}
		
		return true;
	}
	
	private boolean checkVerticalPointsBetween(Point p, Point p2) {
		// make sure none of the points are empty or the current user's
		
		Point smaller, larger;
		if (p.y < p2.y) {
			smaller = p;
			larger = p2;
		} else {
			smaller = p2;
			larger = p;
		}
		
		for (int i = smaller.y + 1; i < larger.y; i++) {
			
			// make sure the pieces in between are the opponent's
			if (field[smaller.x][i] == EMPTY || field[smaller.x][i] == currentUser)
				return false;
		}	
		
		return true;
	}
	
	private boolean checkHorizontalPointsBetween(Point p, Point p2) {
		// make sure none of the points are empty or the current user's
		
		Point smaller, larger;
		if (p.x < p2.x) {
			smaller = p;
			larger = p2;
		} else {
			smaller = p2;
			larger = p;
		}
		
		for (int i = smaller.x + 1; i < larger.x; i++) {
			
			// make sure the pieces in between are the opponent's
			if (field[i][smaller.y] == EMPTY || field[i][smaller.y] == currentUser)
				return false;
		}	
		
		return true;
	}
	
	private Point getComputerChoice(ArrayList<Point> inputs, int difficulty) {
		// used with the computer and gives the point to flip,
		// given legal points and the computer's difficulty
		
		// stores the computer's point
		Point p;
		
		try {
			
			// wait a little bit so the computer doesn't instantly answer
			Thread.sleep(1500);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// keeps track of the number of points flipped for each input
		int[] numFlipped = new int[inputs.size()];
		
		for (int i = 0; i < numFlipped.length; i++) {
			
			// get the size of the array of flipped points
			numFlipped[i] = getFlippedPoints(inputs.get(i)).size();
		}
		
		if (difficulty == HARD) {
			
			// the index of the highest number of flipped points,
			// and the highest number itself
			int index = 0, highest = 0;
			
			for (int i = 0; i < numFlipped.length; i++) {
				
				// if the number of flipped points is higher, that index is now the highest
				if (numFlipped[i] > highest) {
					highest = numFlipped[i];
					index = i;
				}
			}
			
			p = inputs.get(index);
		} else {
			
			// otherwise just picks a random point
			int randNum = (int) (Math.random() * inputs.size());
			p = inputs.get(randNum);
		}
		
		// tell user
		System.out.println("The computer has chosen (" + (p.x + 1) + ", " + (p.y + 1) + ")");
		
		// wait again to show board
		try {
			Thread.sleep(1000);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return p;
	}
	
	private ArrayList<Point> getFlippedPoints(Point lastPoint) {
		// outputs the points that would be flipped with a given input
		
		// keeps track of the flipped points
		ArrayList<Point> flipped = new ArrayList<>();
		
		// stores which directions have found points
		final int TOP = 0, TOP_RIGHT = 1, RIGHT = 2, BOTTOM_RIGHT = 3,
				BOTTOM = 4, BOTTOM_LEFT = 5, LEFT = 6, TOP_LEFT = 7;
		boolean[] pointsFound = new boolean[8];
		for (int i = 0; i < 8; i++)
			pointsFound[i] = false;
		
		// goes in all directions to find if any pieces should be flipped
		for (int i = 1; i < 8; i++) {
			
			// top point
			if (lastPoint.y - i >= 0 && !pointsFound[TOP]) {
				
				// if there's an empty space, no spots can be flipped
				if (field[lastPoint.x][lastPoint.y - i] == EMPTY)
					pointsFound[TOP] = true;
				
				else if (field[lastPoint.x][lastPoint.y - i] == currentUser) {
					
					// then add the flipped points to the array list
					for (int j = 1; j < i; j++) 
						flipped.add(new Point(lastPoint.x, lastPoint.y - j));
					
					// remember that it was found
					pointsFound[TOP] = true;
					
				}
			}
			
			// top right point
			if (lastPoint.y - i >= 0 && lastPoint.x + i < 8 && !pointsFound[TOP_RIGHT]) {
				
				// if there's an empty space, no spots can be flipped
				if (field[lastPoint.x + i][lastPoint.y - i] == EMPTY)
					pointsFound[TOP_RIGHT] = true;
				
				else if (field[lastPoint.x + i][lastPoint.y - i] == currentUser) {
					
					// then flip all pieces in between this point and lastPoint
					for (int j = 1; j < i; j++) {
						
						// make sure each point is within the bounds 0-7
						if (lastPoint.y - j >= 0 && lastPoint.x + j < 8) 
							flipped.add(new Point(lastPoint.x + j, lastPoint.y - j));
						// otherwise the loop is ended
						else
							break;
					}
					
					// remember that it was found
					pointsFound[TOP_RIGHT] = true;
					
				}
			}
			
			// right point
			if (lastPoint.x + i < 8 && !pointsFound[RIGHT]) {
				
				// if there's an empty space, no spots can be flipped
				if (field[lastPoint.x + i][lastPoint.y] == EMPTY) 
					pointsFound[RIGHT] = true;
				
				else if (field[lastPoint.x + i][lastPoint.y] == currentUser) {

					// then add the flipped points to the array list
					for (int j = 1; j < i; j++) 
						flipped.add(new Point(lastPoint.x + j, lastPoint.y));
					
					// remember that it was found
					pointsFound[RIGHT] = true;
					
				}
			}
			
			// bottom right point
			if (lastPoint.y + i < 8 && lastPoint.x + i < 8 && !pointsFound[BOTTOM_RIGHT]) {
				
				// if there's an empty space, no spots can be flipped
				if (field[lastPoint.x + i][lastPoint.y + i] == EMPTY)
					pointsFound[BOTTOM_RIGHT] = true;
				
				else if (field[lastPoint.x + i][lastPoint.y + i] == currentUser) {
					
					// then flip all pieces in between this point and lastPoint
					for (int j = 1; j < i; j++) {
						
						// make sure each point is within the bounds 0-7
						if (lastPoint.y + j < 8 && lastPoint.x + j < 8) 
							flipped.add(new Point(lastPoint.x + j, lastPoint.y + j));
						// otherwise the loop is ended
						else
							break;
					}
					
					// remember that it was found
					pointsFound[BOTTOM_RIGHT] = true;
					
				}
			}
			
			// bottom point
			if (lastPoint.y + i < 7 && !pointsFound[BOTTOM]) {
				
				// if there's an empty space, no spots can be flipped
				if (field[lastPoint.x][lastPoint.y + i] == EMPTY)
					pointsFound[BOTTOM] = true;
				
				else if (field[lastPoint.x][lastPoint.y + i] == currentUser) {

					// then add the flipped points to the array list
					for (int j = 1; j < i; j++) 
						flipped.add(new Point(lastPoint.x, lastPoint.y + j));
					
					// remember that it was found
					pointsFound[BOTTOM] = true;
					
				}
			}
			
			// bottom left point
			if (lastPoint.y + i < 8 && lastPoint.x - i >= 0 && !pointsFound[BOTTOM_LEFT]) {
				
				// if there's an empty space, no spots can be flipped
				if (field[lastPoint.x - i][lastPoint.y + i] == EMPTY)
					pointsFound[BOTTOM_LEFT] = true;
				
				else if (field[lastPoint.x - i][lastPoint.y + i] == currentUser) {
					
					// then flip all pieces in between this point and lastPoint
					for (int j = 1; j < i; j++) {
						
						// make sure each point is within the bounds 0-7
						if (lastPoint.y + j < 8 && lastPoint.x - j >= 0) 
							flipped.add(new Point(lastPoint.x - j, lastPoint.y + j));
						// otherwise the loop is ended
						else
							break;
					}
					
					// remember that it was found
					pointsFound[BOTTOM_LEFT] = true;
					
				}
			}
			
			// left point
			if (lastPoint.x - i >= 0 && !pointsFound[LEFT]) {
				
				// if there's an empty space, no spots can be flipped
				if (field[lastPoint.x - i][lastPoint.y] == EMPTY)
					pointsFound[LEFT] = true;

				else if (field[lastPoint.x - i][lastPoint.y] == currentUser) {

					// then add the flipped points to the array list
					for (int j = 1; j < i; j++) 
						flipped.add(new Point(lastPoint.x - j, lastPoint.y));
					
					// remember that it was found
					pointsFound[LEFT] = true;
					
				}
			}
			
			// top left point
			if (lastPoint.y - i >= 0 && lastPoint.x - i >= 0 && !pointsFound[TOP_LEFT]) {
				
				// if there's an empty space, no spots can be flipped
				if (field[lastPoint.x - i][lastPoint.y - i] == EMPTY)
					pointsFound[TOP_LEFT] = true;
				
				else if (field[lastPoint.x - i][lastPoint.y - i] == currentUser) {
					
					// then flip all pieces in between this point and lastPoint
					for (int j = 1; j < i; j++) {
						
						// make sure each point is within the bounds 0-7
						if (lastPoint.y - j >= 0 && lastPoint.x - j >= 0) 
							flipped.add(new Point(lastPoint.x - j, lastPoint.y - j));
						// otherwise the loop is ended
						else
							break;
					}
					
					// remember that it was found
					pointsFound[TOP_LEFT] = true;
					
				}
			}
		}
		
		return flipped;
	}
	
	private void flipPieces(ArrayList<Point> points) {
		// flips all of the inputed points
		for (int i = 0; i < points.size(); i++) {
			Point p = points.get(i);
			field[p.x][p.y] = currentUser;
		}
	}
	
	private boolean checkWin() {
		
		// numbers to keep track of who has more
		int one = 0, two = 0;
		
		for (int i = 0; i < 8; i++) 
			for (int j = 0; j < 8; j++) {
				
				// make sure no spaces are empty
				if (field[i][j] == EMPTY)
					return false;
				
				// add to the number each player has
				else if (field[i][j] == PLAYER_ONE)
					one++;
				else
					two++;
			}
		
		// evaluate numbers
		if (one != two) {
			
			// find which user has won (in string form)
			String user = one > two ? "one" : "two";
			System.out.println("Player " + user + " has won.");
			
		} else {
			
			// otherwise it was a tie
			System.out.println("There has been a tie");
		}
		
		return true;
	}

}
