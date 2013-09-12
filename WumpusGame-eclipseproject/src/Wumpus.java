import java.util.ArrayList;
import java.util.Random;


/**
 * this class shows another main character in game and its actions 
 * @author Can Eldem
 * @version 1.0
 * @since 
 */

public class Wumpus extends Character {

	public Wumpus(Position p) {
		super(p);

	}

	/**
	 * disturbed method use wumpus to change location or kill agent according to rules defined in game 
	 * @return none 
	 * @param none
	 */
	void Disturbed(){

		ArrayList<Position> neighbours =new ArrayList<Position>(); //get neighbours 
		Random generator = new Random();  //random number generated to determine which direction should wumpus goes
		int direction = generator.nextInt(4); //there is 4 possibilities for direction which means size of neighbours
		Position new_position_wumpus=null;  //new position of wumpus 
		neighbours=Block.findNeighbours(current); //get positions of neighbours and put them into array list 
		new_position_wumpus=neighbours.get(direction);  // choose one of neighbours and return as heading position
		while(isMoviable(new_position_wumpus)==false){  //it means there is a pit treasure or exit in new location
			direction = generator.nextInt( neighbours.size() );
			new_position_wumpus=neighbours.get(direction); //puts wumpus into that new_position
		}

		//erase wumpus from current location 
		GameBoard.blocks[current.dimension_x][current.dimension_y].wumpus_exists=false;
		neighbours=Block.findNeighbours(current);

		//aim of this loop is setting smells around wumpus's current location
		for(int i=0;i<neighbours.size();i++){
			Position set_smell=neighbours.get(i);
			GameBoard.blocks[set_smell.dimension_x][set_smell.dimension_y].smeel_exists=false;

			//if there are not any other things is empty information should be entered 
			if(GameBoard.blocks[set_smell.dimension_x][set_smell.dimension_y].breeze_exists==false &&
					GameBoard.blocks[set_smell.dimension_x][set_smell.dimension_y].glitteringness==false ){
				GameBoard.blocks[set_smell.dimension_x][set_smell.dimension_y].is_empty=true;
			}
		}//end of for loop

		neighbours=Block.findNeighbours(new_position_wumpus);

		//aim of this loop is setting smells around wumpus's new location
		for(int i=0;i<neighbours.size();i++){
			Position set_smell=neighbours.get(i);
			GameBoard.blocks[set_smell.dimension_x][set_smell.dimension_y].smeel_exists=true;
		}

		current=new_position_wumpus; //new location becomes current location for wumpus
		GameBoard.blocks[current.dimension_x][current.dimension_y].wumpus_exists=true; //set wumpus in new block 

	} // end of disturbed method


	/**
	 * checks wheather block is available for wumpus or not
	 * @return boolean , is true if position is moviable
	 * @param position
	 */
	boolean isMoviable(Position p){

		//chekcs wheather there is pit treasure or exit in new location
		if(GameBoard.blocks[p.dimension_x][p.dimension_y].pit_exists==true ||
				GameBoard.blocks[p.dimension_x][p.dimension_y].bat_exists==true ||
				GameBoard.blocks[p.dimension_x][p.dimension_y].exit_exists==true ||
				GameBoard.blocks[p.dimension_x][p.dimension_y].treasure_exists==true){
			return false;
		}
		return true;
	} //ispit end of method
}
