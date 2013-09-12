import java.util.Scanner;


public class Game {

	public static void main(String[] args) throws InterruptedException {

		int steps=0; //number of steps
		GameBoard gb=new GameBoard();  //create an game board
		AI artificial_agent=new AI(gb.agent,gb.wumpus);
		steps=0;
		
		boolean ai_switch=true; //if you want to play game by yourself switch ai off 

		if(ai_switch==true){
			//game loop
			while(artificial_agent.game_over==false){
				Thread.sleep(500);
				artificial_agent.logic();
				steps++;
			}//main loop of game
			System.out.println("number of steps:"+steps);
		}

		//this is for self playing without ai
		else {

			boolean game_over=false;
			gb=new GameBoard();  //create an game board
			gb.displayGameBoard();  //initializing of game
			Position position;
			String way;
			String desicion;
			String direction_shoot;
			Scanner input_location=new Scanner(System.in); //  Scanner.
			Scanner input_shoot_desicion=new Scanner(System.in); //  a Scanner.
			Scanner input_shoot_direction=new Scanner(System.in); //  a Scanner

			while(game_over==false){

				GameBoard.blocks[gb.agent.current.dimension_x][gb.agent.current.dimension_y].writeBlockMessage();
				if(GameBoard.blocks[gb.agent.current.dimension_x][gb.agent.current.dimension_y].smeel_exists==true){

					System.out.println("Do you want to shoot?(Y/N):");
					desicion=input_shoot_desicion.next(); // Get what the user types.

					if(desicion.equals("Y")){
						System.out.println("which direction:?");
						direction_shoot=input_shoot_direction.next(); // Get what the user types.
						gb.agent.Shoot(direction_shoot);
						gb.wumpus.Disturbed();  

						if(gb.agent.current==gb.wumpus.current){
							game_over=true;
							System.out.println("you died...");
						}

						else{
							System.out.println("wumpus went to another location...");
							gb.displayGameBoard();
						}
					}//shooting yes 
				}
				System.out.println("way please?(NORTH/SOUTH/LEFT/RIGHT):");
				way=input_location.next(); // Get what the user types.
				game_over=gb.agent.agentMove(way);
				gb.displayGameBoard();
			}//main loop of game non ai game

		}//end of else 

	}//end of main

}//end of game class
