import java.util.Random;

/**
 * this class is created for indicate general properties of characters in game .
 * @author  Can Eldem
 * @version  1.0
 * @since
 */

public class Character {

	/**
	 * @uml.property  name="current"
	 * @uml.associationEnd  
	 */
	Position current; //every character should have an position

	public Character(){
		super();
	}

	public Character(Position p) {
		super();
		current = p;
	}


	/**
	 * moves character to north location
	 * @param  takes the current position with position object
	 * @return new position
	 */      
	Position moveNorth( ){

		Position new_position=new Position( );

		if(current.dimension_x==0){ //wheather it is in the edge or not

			new_position.dimension_x=(Setup.board_size_x-1);

		}

		else{
			new_position.dimension_x=(current.dimension_x-1);
 		}
		    new_position.dimension_y=current.dimension_y;

		return new_position;
	}

	/**
	 * moves character to south location
	 * @param  takes the current position with position object
	 * @return new position 
	 */   

	Position moveSouth( ){

		Position new_position=new Position( );

		if(current.dimension_x==Setup.board_size_x-1){ //wheather it is in the edge or not

			new_position.dimension_x=(current.dimension_x)-(Setup.board_size_x-1);

		}

		else{
			new_position.dimension_x=(current.dimension_x+1);

		}
    		new_position.dimension_y=current.dimension_y;

		return new_position;
	}//end of move south method

	/**
	 * moves character to Left location
	 * @param  takes the current position with position object
	 * @return new position 
	 */ 
	Position moveLeft( ){

		Position new_position=new Position( );

		if(current.dimension_y==0){ //wheather it is in the edge or not

			new_position.dimension_y=(Setup.board_size_y-1);

		}
		else{
			new_position.dimension_y=(current.dimension_y-1);

		}
		new_position.dimension_x=current.dimension_x;

		return new_position;
	}//end of move left

	/**
	 * moves character to right location
	 * @param  takes the current position with position object
	 * @return new position 
	 */ 

	Position moveRight( ){

		Position new_position=new Position( );

		if(current.dimension_y==Setup.board_size_y-1){ //wheather it is in the edge or not

			new_position.dimension_y=(current.dimension_y)-(Setup.board_size_y-1);
		}
		else{
			new_position.dimension_y=(current.dimension_y+1);

		}
		new_position.dimension_x=current.dimension_x;

		return new_position;
	} //end of move right

}
