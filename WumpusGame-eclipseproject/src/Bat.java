import java.util.Random;

/**
 * this class is created for Bat character in game which transports agent into empty location
 * @author Can Eldem
 * @version 1.0
 * @since 
 */

public class Bat extends Character {

	public Bat(){
		super();
	}

	public Bat(Position p) {
		super(p);
		 
	}

	/**
	 * Teleports agent into another empty location.
	 * @param  
	 * @return
	 */
	Position teleport (Block[][] blocks){

		Position new_position=new Position();

		Random generator = new Random();

		int randomIndexX = generator.nextInt( Setup.board_size_x );
		int randomIndexY = generator.nextInt( Setup.board_size_y );

		//agent should not be teleported into same location again 
		if(checkBlock(blocks[randomIndexX][randomIndexY])==true){
       
			randomIndexX = generator.nextInt( Setup.board_size_x );
			randomIndexY = generator.nextInt( Setup.board_size_y );
		    //try to find a location while it is not empty
			while(checkBlock(blocks[randomIndexX][randomIndexY])==true){
				randomIndexX = generator.nextInt( Setup.board_size_x );
				randomIndexY = generator.nextInt( Setup.board_size_y );
			}//while
		}//if
		new_position.dimension_x=randomIndexX;
		new_position.dimension_y=randomIndexY;
		return new_position;
	}
	/**
	 * Checks future block wheather it is empty or not.
	 * @param  
	 * @return
	 */
	boolean checkBlock(Block checked_block){

		// if one of them are in block method return as true to warn other method
		if(checked_block.wumpus_exists==true || checked_block.bat_exists==true || checked_block.exit_exists==true
				||checked_block.treasure_exists==true || checked_block.pit_exists==true || checked_block.is_empty==false){
  
			return true ;
		}
		else 
			return false;
	}
}
