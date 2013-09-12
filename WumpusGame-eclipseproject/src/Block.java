import java.util.ArrayList;


/**
 * this class is created for holding information about blocks,tile on gameboard
 * @author Can Eldem
 * @version 1.0
 * @since 
 */
public class Block {

	boolean is_empty=true;        //by default all blocks condisidered as empty
	boolean wumpus_exists=false; //checks there is wumpus in block or not
	boolean bat_exists=false;     //checks there is bat in block or not
	boolean pit_exists=false;      //checks there is pit in block or not
	boolean treasure_exists=false;   //checks there is treasure in block or not
	boolean exit_exists=false;       //checks there is exit in block or not
	boolean smeel_exists=false;       //checks there is smeel in block or not
	boolean breeze_exists=false;      //checks there is breeze in block or not
	boolean glitteringness=false;     //checks there is light for treasure in block or not
	boolean agent_exists=false;            //locate agents position


	//empty constructor
	Block(){

	}

	public Block(Block b) {
		super();
		this.is_empty = b.is_empty;
		this.wumpus_exists = b.wumpus_exists;
		this.bat_exists = b.bat_exists;
		this.pit_exists = b.pit_exists;
		this.treasure_exists = b.treasure_exists;
		this.exit_exists = b.exit_exists;
		this.smeel_exists = b.smeel_exists;
		this.breeze_exists = b.breeze_exists;
		this.glitteringness =b.glitteringness;
		this.agent_exists = b.agent_exists;
	}

	/**
     * Find neighbour positions according to current positions
     * @return neighbour positions 
     * @param position ,generally current position of agent
     */
	public static ArrayList<Position> findNeighbours(Position current){

		ArrayList<Position> neighbours =new ArrayList<Position>();
		Position north_neighbour=null;
		Position south_neighbour=null;
		Position east_neighbour=null;
		Position west_neighbour=null;

		Character ch=new Character(current); //using characters class methods for discover neighbours

		west_neighbour=ch.moveLeft();
		east_neighbour=ch.moveRight();
		north_neighbour=ch.moveNorth();
		south_neighbour=ch.moveSouth();

		//adding neighbours to list and pass to required place
		neighbours.add(north_neighbour);
		neighbours.add(south_neighbour);
		neighbours.add(west_neighbour);
		neighbours.add(east_neighbour);
		return neighbours;
	}

	 /**
    * writes realted message about current block
    * @return returns false if agent dies  
    * @param none
    */
	boolean writeBlockMessage(){
		boolean game_over=false;

		if(this.smeel_exists==true){
			System.out.println("smell detected...");
		}
		if(this.breeze_exists==true){
			System.out.println("It is cold...");
		}
		if(this.bat_exists==true){
			System.out.println("Encountered with bat teleporting to another location...");
		}
		if(this.exit_exists){
			System.out.println("Exit founded...");
			game_over=false;
		}
		if(this.glitteringness==true){
			System.out.println("room is glittering...");
		}
		if(this.is_empty==true){
			System.out.println("there is nothing in this room...");
		}
		if(this.pit_exists==true){
			System.out.println("falling into pit...");
			game_over=true;
		}
		if(this.wumpus_exists==true){
			System.out.println("encountered with wumpus agent dies...");
			game_over=true;
		}

		return game_over;
	} //end of messsage method
}
