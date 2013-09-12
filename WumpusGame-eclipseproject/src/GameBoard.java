import java.util.ArrayList;
import java.util.Random;


/**
 * this class is created for creation of gameboard
 * @author  Can Eldem
 * @version  1.0
 * @since
 */


public class GameBoard {

	/**
	 * @uml.property  name="blocks"
	 * @uml.associationEnd  multiplicity="(0 -1)"
	 */
	public static Block[][] blocks=new Block[Setup.board_size_x][Setup.board_size_y];   //gameboard composed with blocks,tiles 
	/**
	 * @uml.property  name="wumpus"
	 * @uml.associationEnd  
	 */
	Wumpus wumpus;     //main character   has a relationship
	/**
	 * @uml.property  name="agent"
	 * @uml.associationEnd  
	 */
	Agent agent; //main character has a relationship 
	/**
	 * @uml.property  name="bat"
	 * @uml.associationEnd  
	 */
	Bat bat; //bat character has a relationship

	/**
	 * Creates an empty gameboard size will be determined according to values in setup class
	 * @return none
	 * @param none
	 */
	public GameBoard( ) {
		super();
		for(int i=0;i<Setup.board_size_x;i++){
			for(int j=0;j<Setup.board_size_y;j++){
				blocks[i][j]=new Block();
			}

		}

		//putting characters on game board
		InitializeWumpus();
		InitializeExit();
		InitializeTreasure();
		InitializeAgent();
		InitializeBats();
		InitializePits();

	}// end of game board method

	 /**
	 * initializes wumpus into gameboard
	 * @return none
	 * @param none
	 */
	public void InitializeWumpus(){

		Position p=new Position();
		ArrayList<Position>neighbours;
		Random generator = new Random();
        //to determine wumpus position program creates random numbers
		int randomIndexX = generator.nextInt( Setup.board_size_x );
		int randomIndexY = generator.nextInt( Setup.board_size_y );
		p=new Position(randomIndexX,randomIndexY);
		//fill realted block information
		blocks[randomIndexX][randomIndexY].wumpus_exists=true;
		wumpus=new Wumpus(p); //set object to that position as well 
		blocks[randomIndexX][randomIndexY].is_empty=false; //wumpus location set as not empty
		neighbours=Block.findNeighbours(p);

		//neighbour coordinations obtaied and smell will be set for AI to detect
		for(int i=0;i<neighbours.size();i++){
			Position to_be_set;
			to_be_set=neighbours.get(i);
			blocks[to_be_set.dimension_x][to_be_set.dimension_y].smeel_exists=true;
			blocks[to_be_set.dimension_x][to_be_set.dimension_y].is_empty=false;
		}
	}//end of initialize wumpus 

	     /**
		 * initializes exit into board
		 * @return none
		 * @param none
		 */
	public void InitializeExit(){

		Random generator = new Random();
		int randomIndexX = generator.nextInt( Setup.board_size_x );
		int randomIndexY = generator.nextInt( Setup.board_size_y );

		   //if there is a wumpus get another coordination
		   //software does not check for other elements in board because they have not been initialized
		if(blocks[randomIndexX][randomIndexY].wumpus_exists==true){

			randomIndexX = generator.nextInt( Setup.board_size_x );
			randomIndexY = generator.nextInt( Setup.board_size_y );

			//checks wheather there is wumpus or not 
			while(blocks[randomIndexX][randomIndexY].wumpus_exists==true){
				randomIndexX = generator.nextInt( Setup.board_size_x );
				randomIndexY = generator.nextInt( Setup.board_size_y );
			}

			blocks[randomIndexX][randomIndexY].exit_exists=true;
			blocks[randomIndexX][randomIndexY].is_empty=false;
		}//end of if block

		else { //if there is no wumpus at the moment add exit

			blocks[randomIndexX][randomIndexY].exit_exists=true;
			blocks[randomIndexX][randomIndexY].is_empty=false;
		}

	} //end of initializ exit method 

	 /**
	 * initializes treasure into board
	 * @return none
	 * @param none
	 */
	public void InitializeTreasure(){

		Position p=new Position();
		ArrayList<Position>neighbours;	
		//get random positions
		Random generator = new Random();
		int randomIndexX = generator.nextInt( Setup.board_size_x );
		int randomIndexY = generator.nextInt( Setup.board_size_y );

		//check wheather there is wumpus or exit in that block 
		if(blocks[randomIndexX][randomIndexY].wumpus_exists==true || blocks[randomIndexX][randomIndexY].exit_exists==true ){

			randomIndexX = generator.nextInt( Setup.board_size_x );
			randomIndexY = generator.nextInt( Setup.board_size_y );

			//checks wheather there is wumpus or not 
			while(blocks[randomIndexX][randomIndexY].wumpus_exists==true || blocks[randomIndexX][randomIndexY].exit_exists==true  ){

				randomIndexX = generator.nextInt( Setup.board_size_x );
				randomIndexY = generator.nextInt( Setup.board_size_y );

			}

			p.dimension_x=randomIndexX;
			p.dimension_y=randomIndexY;
			
			blocks[randomIndexX][randomIndexY].treasure_exists=true; // put treasure 
			blocks[randomIndexX][randomIndexY].is_empty=false; 

			neighbours=Block.findNeighbours(p); //find neighbours to set glitteringess

			//glitteringess is set around treasure
			for(int i=0;i<neighbours.size();i++){
				Position to_be_set;
				to_be_set=neighbours.get(i);
				blocks[to_be_set.dimension_x][to_be_set.dimension_y].glitteringness=true;
				blocks[to_be_set.dimension_x][to_be_set.dimension_y].is_empty=false;
			}
		}//end of if block

		else { //if there is no wumpus and exit in that block add treasure 

			p.dimension_x=randomIndexX;
			p.dimension_y=randomIndexY;

			blocks[randomIndexX][randomIndexY].treasure_exists=true; //put treasure 
			blocks[randomIndexX][randomIndexY].is_empty=false;

			neighbours=Block.findNeighbours(p);

			//glitteringess is set around treasure
			for(int i=0;i<neighbours.size();i++){
				Position to_be_set; //negihbour blocks should be informed about main block
				to_be_set=neighbours.get(i);
				blocks[to_be_set.dimension_x][to_be_set.dimension_y].glitteringness=true;
				blocks[to_be_set.dimension_x][to_be_set.dimension_y].is_empty=false;
			}
		}//end of else block

	}//end of initial treasure class 


	     /**
		 * initializes agent into board
		 * @return none
		 * @param none
		 */
	public void InitializeAgent(){

		Random generator = new Random();
		int randomIndexX = generator.nextInt( Setup.board_size_x );
		int randomIndexY = generator.nextInt( Setup.board_size_y );

		//checks wheather this block is avaiable to put agent or not 
		if(blocks[randomIndexX][randomIndexY].wumpus_exists==true || blocks[randomIndexX][randomIndexY].treasure_exists==true || 

				blocks[randomIndexX][randomIndexY].exit_exists==true || blocks[randomIndexX][randomIndexY].is_empty==true ){

			randomIndexX = generator.nextInt( Setup.board_size_x );
			randomIndexY = generator.nextInt( Setup.board_size_y );

			//checks wheather there is wumpus or not 
			while(blocks[randomIndexX][randomIndexY].wumpus_exists==true || blocks[randomIndexX][randomIndexY].treasure_exists==true || 

					blocks[randomIndexX][randomIndexY].exit_exists==true || blocks[randomIndexX][randomIndexY].is_empty==true){

				randomIndexX = generator.nextInt( Setup.board_size_x );
				randomIndexY = generator.nextInt( Setup.board_size_y );

			}//end of while

			//set the agent
			blocks[randomIndexX][randomIndexY].agent_exists=true;
			agent=new Agent(new Position(randomIndexX,randomIndexY)); //create agent object since initialization is completed

		}//end of if block

		else { //if there is no wumpus at the moment add exit

			blocks[randomIndexX][randomIndexY].agent_exists=true;
			agent=new Agent(new Position(randomIndexX,randomIndexY)); //create agent object since initialization is completed

		}

	} //end of initalize agent method 


	         /**
			 * initializes bats into board
			 * @return none
			 * @param none
			 */
	public void InitializeBats( ){

		for(int j=0;j<Setup.bats;j++){   // process need to repeated for multiple bats

			Random generator = new Random();
			int randomIndexX = generator.nextInt( Setup.board_size_x );
			int randomIndexY = generator.nextInt( Setup.board_size_y );

			  //if there are other objects but bats into another empty location
			if(blocks[randomIndexX][randomIndexY].wumpus_exists==true || blocks[randomIndexX][randomIndexY].treasure_exists==true || 
					blocks[randomIndexX][randomIndexY].exit_exists==true || blocks[randomIndexX][randomIndexY].agent_exists==true ||
					blocks[randomIndexX][randomIndexY].bat_exists==true){

				randomIndexX = generator.nextInt( Setup.board_size_x );
				randomIndexY = generator.nextInt( Setup.board_size_y );

				//checks wheather this bloks is available to put bat or not 
				while(blocks[randomIndexX][randomIndexY].wumpus_exists==true || blocks[randomIndexX][randomIndexY].exit_exists==true ||
						blocks[randomIndexX][randomIndexY].agent_exists==true || blocks[randomIndexX][randomIndexY].treasure_exists==true 
						|| blocks[randomIndexX][randomIndexY].bat_exists==true){

					randomIndexX = generator.nextInt( Setup.board_size_x );
					randomIndexY = generator.nextInt( Setup.board_size_y );

				}//end of while

				blocks[randomIndexX][randomIndexY].bat_exists=true; // put treasure 
				blocks[randomIndexX][randomIndexY].is_empty=false;

			}//end of if block

			else { //if there is no wumpus and exit in that block add treasure 

				blocks[randomIndexX][randomIndexY].bat_exists=true; //put bat in that block
				blocks[randomIndexX][randomIndexY].is_empty=false;

			}//end of else block

		} //end of for, start same process for another bat

	}//end of initial bats class 


	           /**
			 * initializes pits into board
			 * @return none
			 * @param none
			 */
	public void InitializePits( ){   //used for initialazing pits
		for(int j=0;j<Setup.pits;j++){   // process need to repeated for multiple bats

			Position p=new Position();
			ArrayList<Position>neighbours;		
			Random generator = new Random();
			int randomIndexX = generator.nextInt( Setup.board_size_x );
			int randomIndexY = generator.nextInt( Setup.board_size_y );

			//checks wheather this bloks is available to put bat or not 
			if(blocks[randomIndexX][randomIndexY].wumpus_exists==true || blocks[randomIndexX][randomIndexY].treasure_exists==true || 
					blocks[randomIndexX][randomIndexY].exit_exists==true || blocks[randomIndexX][randomIndexY].agent_exists==true ||
					blocks[randomIndexX][randomIndexY].bat_exists==true || 
					blocks[randomIndexX][randomIndexY].pit_exists==true ){

				randomIndexX = generator.nextInt( Setup.board_size_x );
				randomIndexY = generator.nextInt( Setup.board_size_y );
				//get new position untill block gets available
				while(blocks[randomIndexX][randomIndexY].wumpus_exists==true || blocks[randomIndexX][randomIndexY].exit_exists==true ||
						blocks[randomIndexX][randomIndexY].agent_exists==true || blocks[randomIndexX][randomIndexY].treasure_exists==true 
						|| blocks[randomIndexX][randomIndexY].bat_exists==true ||
						blocks[randomIndexX][randomIndexY].breeze_exists==true){

					randomIndexX = generator.nextInt( Setup.board_size_x );
					randomIndexY = generator.nextInt( Setup.board_size_y );

				} //end of while block 

				p.dimension_x=randomIndexX;
				p.dimension_y=randomIndexY;

				blocks[randomIndexX][randomIndexY].pit_exists=true;// put treasure
				blocks[randomIndexX][randomIndexY].is_empty=false; //set not empty for current location of pit

				neighbours=Block.findNeighbours(p);

				//neighbour coordinations obtained and smell will be set for AI to detect
				for(int i=0;i<neighbours.size();i++){
					Position to_be_set;
					to_be_set=neighbours.get(i);
					blocks[to_be_set.dimension_x][to_be_set.dimension_y].breeze_exists=true;
					blocks[to_be_set.dimension_x][to_be_set.dimension_y].is_empty=false;
				}//for

			}//end of if block 

			else { //if there is no pits and  any other thins in that block 

				p.dimension_x=randomIndexX;
				p.dimension_y=randomIndexY;

				blocks[randomIndexX][randomIndexY].pit_exists=true; //put treasure 
				blocks[randomIndexX][randomIndexY].is_empty=false;
				neighbours=Block.findNeighbours(p);

				//neighbour coordinations obtained and smell will be set for AI to detect
				for(int i=0;i<neighbours.size();i++){
					Position to_be_set; //negihbour blocks should be informed about main block
					to_be_set=neighbours.get(i);
					blocks[to_be_set.dimension_x][to_be_set.dimension_y].breeze_exists=true;
					blocks[to_be_set.dimension_x][to_be_set.dimension_y].is_empty=false;
				}
			}//end of else block

		}//end of for block 

	}//end of initialize pits class 

    /**
	 * displays game board
	 * @return none
	 * @param none
	 */
	public static void displayGameBoard(){  

		System.out.println("Game board:");

		for(int i=0;i<Setup.board_size_x;i++){
			   for(int j=0;j<Setup.board_size_y;j++){
				          
				if(blocks[i][j].wumpus_exists==true){
					System.out.print(" W ");					
				}

				else if(blocks[i][j].bat_exists==true){
					System.out.print(" B ");	
				}

				else if(blocks[i][j].pit_exists==true){
					System.out.print(" P ");	
				}

				else if(blocks[i][j].treasure_exists==true){
					System.out.print(" T ");	

				}

				else if(blocks[i][j].exit_exists==true){
					System.out.print(" E ");	

				}

				else if(blocks[i][j].agent_exists==true){
					System.out.print(" A ");	
				}

				else {  //means its empty
					System.out.print(" * ");
				}

			}//end of for block

			System.out.println(""); //get another line

		} //end of for block

		System.out.println("");

	}//end of display function 

}
