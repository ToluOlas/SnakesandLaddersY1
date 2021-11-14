import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

public class Snakes_and_Ladders {
	
	public static int[] p1 = {1,1,0,0};//player format: {player number,current tile,stick count,biscuit count}
	public static int[] p2 = {2,1,0,0};
	public static int[] p3 = {3,1,0,0};
	public static int[] p4 = {4,1,0,0};
	public static int[] finishedPlayers = {0,0,0}; //array storing the players that complete the game, also stores their completion order
	public static String[] playerNames = {"","","",""}; //array storing the names of all players
	public static int[][] SnL = {{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0}};//format: {'tile for Snake/Ladder activation','tile player is sent upon activation'}
	public static int[] SnB;//public array for sticks and biscuits, will end up with two of each (spaces 0 and 1 being sticks, spaces 2 and 3 being biscuits.
	public static int turnCounter = 1;//the number of turns that have passed within the game
	
	public static void main(String args[]) //the main method that starts the game sequence
	{
		int playercount = 0;//the variable for the number of players
		System.out.println("How many players are are playing? Choose from 2 to 4 players.");
		playercount = IntVerify();//playercount is retrieved
		Scanner myReader = new Scanner(System.in);
		for (int i=0;i<playercount;++i)//every player gets to give themselves a name
		{
			System.out.println("Player "+(i+1)+", please enter your name:");
			playerNames[i]=myReader.nextLine();
		}
		System.out.println("Generating Board...");
		GenerateBoard();//snakes, ladders, sticks and biscuits are randomly put onto the board
		System.out.println("Game starting...");
		System.out.println("_____________________________________________________________");
		System.out.println("");
		System.out.println("                          Turn "+turnCounter);
		TurnSequence(p1,playercount,playerNames[0]);//game starts with player 1's turn	
	}
	
	public static int IntVerify()//the method that checks and verifies the player count hcosen
	{
		Scanner myReader = new Scanner(System.in);
		if(myReader.hasNextInt())//checks if the input is an integer
		{
			int x = myReader.nextInt();
			if (x==2||x==3||x==4)//the valid numbers for playercount
			{
				return x;//only return command in the method that ends the recursion loop, player count is accepted
			}
			else
			{
				System.out.println("Please enter a number from 2 to 4");
				return IntVerify();//reprompted to enter a player count number if the input isn't an integer in the valid number range
			}
		}
		else 
		{
			System.out.println("Input invalid, please enter an integer from 2 to 4");
			return IntVerify();//reprompted to enter a player count number if the input isn't an integer
		}
		
	}
	
	public static void GenerateBoard()//the method is used to create the boards objects randomly
	{
		int[] distinctValues = ThreadLocalRandom.current().ints(2, 95).distinct().limit(8).toArray(); //chooses random values for snake heads and the bottom of ladders. creates a 1D array containing randomly generated integers in the range of 2-95, distinct() makes it so that every number is unique, and limit(8) stops when 8 numbers are generated
		Random rand = new Random();
		for (int i=0;i<SnL.length;++i) //for loop used to create the tail of snakes and the top ladders. Ensures the random values are still values on the board (1-100)
		{
			int rndLength = rand.nextInt(15) + 1; //snakes and ladders can be from 1 to 15 spaces long
			if (i<SnL.length/2) //snakes are the first half of the array (spaces 0, 1, 2 and 3)
			{
				while (distinctValues[i]-rndLength<1) //since it's a tail, it will be below the head. The program checks the validity of that tails position
				{
					rndLength = rand.nextInt(15) + 1; //a different snake length will be randomly chosen if the previous resulting tail goes off the board (>1)
				}
				SnL[i][0]=distinctValues[i];//snake head is put into the first position of the 2D array
				SnL[i][1]=distinctValues[i]-rndLength;//snake tail is put into the second position of the 2D array
				//System.out.println("Snake "+(i+1)+"="+SnL[i][0]+","+SnL[i][1]);
			}
			else //ladders are the second half of the array (spaces 4, 5, 6 and 7)
			{
				while (distinctValues[i]+rndLength>=100) //if the generated ladder top goes over 100..
				{
					rndLength = rand.nextInt(15) + 1; //..the program will try a different random value
				}
				SnL[i][0]=distinctValues[i];// both the ladder top and bottom are put into the snakes and ladders array
				SnL[i][1]=distinctValues[i]+rndLength;
				//System.out.println("Ladder "+(i-3)+"="+SnL[i][0]+","+SnL[i][1]);
			}
		}
		SnB = ThreadLocalRandom.current().ints(2, 75).distinct().limit(4).toArray();//chooses 4 distinct random values, in the range of 2 to 75, and puts them in an array
	}
	
	
	public static int DiceRoll(String playerName)//the method that rolls the dice from the players
	{
		Random rand = new Random();
		int x = rand.nextInt(6) + 1; //a random number from 1 to 6 is chosen
		System.out.println(playerName+" rolled a "+x+"!");//the number is displayed to the players
		return x;
	}
	
	public static void NextTurn(int prevPlayer, int totalPlayers)//the method called at the end of a Turn Sequence to choose the next players turn
	{
		if (finishedPlayers[totalPlayers-2]>0) //the game ends when there is only one person that hasn't reached the end of the board, so the program checks if the last availible space has been filled
		{
			EndGame();//game ending sequence
		}
		else
		{
			int nextPlayer = prevPlayer +1;//the player sequence is incremented
			if (nextPlayer>totalPlayers)//the valid player range is 1 to the number representing the total players
			{
				turnCounter = turnCounter+1;
				System.out.println("__________________________________________");
				System.out.println("");
				System.out.println("          Turn "+turnCounter);
				TurnSequence(p1,totalPlayers, playerNames[0]);//program loops back to player 1s turn 
			}
			else
			{
				switch (nextPlayer) {
				case 2:
					TurnSequence(p2,totalPlayers, playerNames[1]);//player 2's turn
					break;
				case 3:
					TurnSequence(p3,totalPlayers, playerNames[2]);//player 3's turn
					break;
				case 4:
					TurnSequence(p4,totalPlayers, playerNames[3]);//player 4's turn
					break;
				}
			}
		}
	}
	
	public static void TurnSequence(int[] currentPlayer, int totalPlayers, String name)//the method that handles a players turn. It calls all appropriate methods and handles any values that may need to change within a turn
	{
		if (currentPlayer[1]==100)//player is skipped if they have already completed the board
		{
			NextTurn(currentPlayer[0],totalPlayers);
		}
		else
		{
			System.out.println(name+", it's your turn!");
			System.out.println("Type 'S' to see your stats, otherwise press ENTER to roll the dice.");
			Scanner myReader = new Scanner(System.in);
			String enter=myReader.nextLine();//player can type S at this point if they want to see their current tile and other information
			if (enter.equals("S") || enter.equals("s"))
			{
				SeeStats(currentPlayer,name);
			}
			int roll = DiceRoll(name);//dice roll value is retrieved
			if (currentPlayer[1]+roll>100)//players must move exactly onto tile 100 to win
			{
				System.out.println("...but overshoots the end");
				System.out.println("You need to roll a "+(100-currentPlayer[1])+", or less to be able to move");//no movement occurs
			}
			else if (currentPlayer[1]+roll==100)
			{
				System.out.println(name+" has reached the end of the board!");
				currentPlayer[1]=100;//player completes the board, their tile is updated so they will no longer be asked to take their turn
				boolean playerRanked = false;//variable that ensures the player isn't added multiple times to the finished players array
				for (int i=0;i<finishedPlayers.length;++i)//for loop ensures players are added to 'finishedPlayers' in order of board completion 
				{
					if (finishedPlayers[i]== 0 && playerRanked == false)//if the space in the array isn't already occupied by a player, and the current player hasn't already been added to the array
					{
						finishedPlayers[i]=currentPlayer[0];//player is put into their completion position
						playerRanked = true;//set to true so they're not added again
					}
				}
			}
			else//the most common result of a roll, the player simply moves up the board
			{
				System.out.println(name+" moved from space "+currentPlayer[1]+" to space "+(currentPlayer[1]+roll));
				currentPlayer[1]=currentPlayer[1]+roll;//players tile position is updated
			}
			tileCheck(currentPlayer,name);//ran to check if the player has stepped on an action tile
			System.out.println("Press ENTER to end your turn.");
			enter=myReader.nextLine();
			if (roll==6)
			{
				System.out.println("Extra turn granted due to rolling a 6");
				System.out.println("");
				TurnSequence(currentPlayer,totalPlayers,name);//player is called to have another turn
			}
			else
			{
				NextTurn(currentPlayer[0],totalPlayers);//current players number is used to determine the next player to have a turn
			}		
		}
	}
	
	public static void EndGame()//the method that displays the player rankings
	{
		System.out.println("This games winner is... "+playerNames[finishedPlayers[0]-1]+"!!");
		if (finishedPlayers[1]>0)
		{
			System.out.println("Second place: "+playerNames[finishedPlayers[1]-1]);
		}
		if (finishedPlayers[2]>0)//only array spaces that have players in them will be displayed
		{
			System.out.println("Third place: "+playerNames[finishedPlayers[2]-1]);
		}
	}
	
	public static void tileCheck(int[] currentPlayer, String name)//the method that will check a players current position, and determine what happens as a result
	{
		for (int i=0;i<SnB.length;++i)//for loop for checking sticks and biscuits
		{
			if (SnB[i]==currentPlayer[1]&&i<SnB.length/2)//integers in the first half of the array are sticks
			{
				System.out.println(name+" has found a big stick! It can be used to ward off snakes.");
				currentPlayer[2]=currentPlayer[2]+1;//stick added to the players count
				SnB[i]=0; //stick is removed from the board
			}
			else if (SnB[i]==currentPlayer[1]&&i>(SnB.length/2)-1)//integers in the second half of the array are biscuits
			{
				System.out.println(name+" has found a biscuit! It can be used for the next snake encounter.");
				currentPlayer[3]=currentPlayer[3]+1;//biscuits added to the players count
				SnB[i]=0; //biscuit is removed from the board
			}
				
		}
		
		for (int i=0;i<SnL.length;++i)//for loop for checking snakes and ladders
		{
			if (SnL[i][0]==currentPlayer[1]&&i<SnL.length/2)//integer pairs in the first half of the array are snakes
			{
				System.out.println(name+" has encountered a snake!!");
				if (currentPlayer[2]>0)//stick count is checked first
				{
					currentPlayer[2]=currentPlayer[2]-1;//a stick is removed from the players possession
					System.out.println(name+" used a big stick!");
					if (SnL[i][0]<90)//a snake will not move if the resulting move would make them go onto or over tile 100
					{
						SnL[i][0]=SnL[i][0]+10;//snake head moves up 10 spaces
						SnL[i][1]=SnL[i][1]+10;//snake tail moves up 10 spaces
						System.out.println("The snake slithered up to tile "+SnL[i][0]);
					}
				}
				else if (currentPlayer[3]>0)//biscuit count is checked if the player has no sticks
				{
					currentPlayer[2]=currentPlayer[3]-1;//a biscuit is removed from the players possession 
					System.out.println(name+" used a biscuit!");
				    System.out.println("The snake let you pass... this time");
				}
				else//if the player has neither a stick or biscuit
				{
					System.out.println(name+" slithered down from tile "+SnL[i][0]+" to "+SnL[i][1]+"...");
					currentPlayer[1]=SnL[i][1];//player moves down to the snakes tail
				}
			}
			
			else if (SnL[i][0]==currentPlayer[1]&&i>(SnL.length/2)-1)//integer pairs in the second half of the array are ladders
			{
				System.out.println("You found a ladder!");
				System.out.println(name+" climbed from tile "+SnL[i][0]+" to "+SnL[i][1]);
				currentPlayer[1]=SnL[i][1];//player moves up to the top of the ladder
			}
		}
	}
	
	public static void SeeStats(int[] playerScore,String playerName)//the method that will display player information (current tile, number of sticks, number of biscuits)
	{
		System.out.println("Player "+playerScore[0]+" - "+playerName+"'s Stats:");
		System.out.println("   Current Tile: "+playerScore[1]);
		System.out.println("   Stick Count: "+playerScore[2]);
		System.out.println("   Biscuit Count: "+playerScore[3]);
		System.out.println("Press ENTER to roll the dice");
		Scanner myReader = new Scanner(System.in);
		String enter=myReader.nextLine();//player must press enter to continue their turn
	}
	

}

