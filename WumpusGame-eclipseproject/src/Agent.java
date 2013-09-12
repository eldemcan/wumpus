import java.util.ArrayList;
import java.util.Random;


/**
 * this class show main character in game and its actions 
 * @author Can Eldem
 * @version 1.0
 * @since 
 */

public class Agent extends Character{

	boolean got_treasure=false; // shows wheather agent have treasure or not
	boolean teleported=false;   //if agent face with bat and teleported to another location this field becomes true

	public Agent(Position p) {
		super(p);

	}

	/**
	 * moves agent to given direction 
	 * @param  direction , NORTH ,SOUTH,LEFT ,RIGHT
	 * @return if agent have not died in new location it returns false 
	 */ 

	boolean agentMove(String direction){

		Position new_position=null;
		boolean game_over=false;
		Bat b;
		GameBoard.blocks[current.dimension_x][current.dimension_y].agent_exists=false;

		if(direction.equals("NORTH")){
			System.out.println("Agent moves to NORTH...");
			new_position=moveNorth();
			
		}//end of north method
		
		if(direction.equals("SOUTH")){
			System.out.println("Agent moves to SOUTH...");
			new_position=moveSouth();
		}
		
		if(direction.equals("LEFT")){
			System.out.println("Agent moves to LEFT...");
			new_position=moveLeft();
		}
		
		if(direction.equals("RIGHT")){
			System.out.println("Agent moves to RIGHT...");
			new_position=moveRight();
		}
			
			//display related information about current block
			//current block can hold information about nieghbour blocks
			
			GameBoard.blocks[new_position.dimension_x][new_position.dimension_y].writeBlockMessage();  

			   //game over if you encounter with wumpus
			if(	GameBoard.blocks[new_position.dimension_x][new_position.dimension_y].wumpus_exists==true){
				game_over=true;
			}
			
			//game over if you fall into pit
			if(	GameBoard.blocks[new_position.dimension_x][new_position.dimension_y].pit_exists==true){
				game_over=true;
			}
 
			if(	GameBoard.blocks[new_position.dimension_x][new_position.dimension_y].treasure_exists==true){
				got_treasure=true;  //agent gets the treasure 
				System.out.println("Agent got the treasure...");
				
				//erase treasure and its light from map 
				
				GameBoard.blocks[new_position.dimension_x][new_position.dimension_y].treasure_exists=false;
				
			if(GameBoard.blocks[new_position.dimension_x][new_position.dimension_y].breeze_exists==false &&
						   GameBoard.blocks[new_position.dimension_x][new_position.dimension_y].smeel_exists==false){
				
				GameBoard.blocks[new_position.dimension_x][new_position.dimension_y].is_empty=true;
				
			}
								
				ArrayList<Position> neighbours =new ArrayList<Position>();
				
				neighbours=Block.findNeighbours(new_position);
				//start change glitter information for neighbours
				for(int i=0;i<neighbours.size();i++){
					
					Position set=neighbours.get(i);					
					GameBoard.blocks[set.dimension_x][set.dimension_y].glitteringness=false;
					//if is there anything left set block information as empty
					if(GameBoard.blocks[set.dimension_x][set.dimension_y].breeze_exists==false &&
					   GameBoard.blocks[set.dimension_x][set.dimension_y].smeel_exists==false){
					   GameBoard.blocks[set.dimension_x][set.dimension_y].is_empty=true;
		                     
					}//end of if
				}//end of for loop
				
			} //end of if for treasure
			
			if(	got_treasure==true && GameBoard.blocks[new_position.dimension_x][new_position.dimension_y].exit_exists==true){
				game_over=true;  //agent gets the treasure and reaches exit 
			}

		 	if(	GameBoard.blocks[new_position.dimension_x][new_position.dimension_y].bat_exists==true){

				b=new Bat();
				GameBoard.blocks[current.dimension_x][current.dimension_y].agent_exists=false;
				new_position=b.teleport(GameBoard.blocks);
				current=new_position;
				GameBoard.blocks[current.dimension_x][current.dimension_y].agent_exists=true; //changed information for moved block
				GameBoard.blocks[current.dimension_x][current.dimension_y].writeBlockMessage(); //display information about new teleported block  
                teleported=true;
			}
		 	
		 	current=new_position;  //assgin new position to agent
		 	GameBoard.blocks[current.dimension_x][current.dimension_y].agent_exists=true; //changed information for moved block
				 
		return game_over;
	}

	/**
	 * shoots in given direction
	 * @param  direction , NORTH ,SOUTH,LEFT ,RIGHT
	 * @return true if wumpus is dead
	 */ 
	boolean Shoot(String direction){

		boolean is_wumpus_dead=false;  
		ArrayList<Position> neighbours =new ArrayList<Position>();
		neighbours=Block.findNeighbours(current); //neigbours are inserted north,south,west,east respectively
		Position p;

		if(direction.equalsIgnoreCase("NORTH")){

			p=neighbours.get(0);  //get north location

			if(GameBoard.blocks[p.dimension_x][p.dimension_y].wumpus_exists==true){ //wumpus in this location and dies
				is_wumpus_dead=true;
			}
		}
		else if(direction.equalsIgnoreCase("SOUTH")){
			p=neighbours.get(1); //get south location
			if(GameBoard.blocks[p.dimension_x][p.dimension_y].wumpus_exists==true){ //wumpus in this location and dies
				is_wumpus_dead=true;
			}

		}

		else if(direction.equalsIgnoreCase("EAST")){
			p=neighbours.get(3);  //get east location 

			if(GameBoard.blocks[p.dimension_x][p.dimension_y].wumpus_exists==true){ //wumpus in this location and dies
				is_wumpus_dead=true;
			}

		}

		else { //west

			p=neighbours.get(2); //get west location

			if(GameBoard.blocks[p.dimension_x][p.dimension_y].wumpus_exists==true){ //wumpus in this location and dies
				is_wumpus_dead=true;
			}

		} 

		return is_wumpus_dead;

	} // end of Shoot method
	
}
