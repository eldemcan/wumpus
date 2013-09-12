import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.Stack;

/**
 * Ai to play the wumpus game 
 * @author  Can Eldem
 * @version  1.0
 * @since
 */

public class AI {

	/**
	 * @uml.property  name="ai_map"
	 * @uml.associationEnd  multiplicity="(0 -1)"
	 */
	public AiBlock[][] ai_map=new AiBlock[Setup.board_size_x][Setup.board_size_y]; //map for ai to hold information
	Stack<String> path=new Stack<String>(); ;//hold previous positions when require to return 
	ArrayList<Position> posibble_location_marking; //possible pit positions
	/**
	 * @uml.property  name="detected_exit"
	 * @uml.associationEnd  
	 */
	Position detected_exit;  //exit location 
	boolean game_over=false; //shows ai wheather stop or not
	boolean wumpus_dead=false; //shows wheather wumpus is dead or not
	boolean find_exit_method=false; //it means find exit method have not called yet
	/**
	 * @uml.property  name="agent"
	 * @uml.associationEnd  
	 */
	Agent agent;//gets agent object
	/**
	 * @uml.property  name="wumpus"
	 * @uml.associationEnd  
	 */
	Wumpus wumpus; //gets wumpus object

	AI(Agent agent,Wumpus wumpus){

		this.agent=agent;
		this.wumpus=wumpus;

	}

	void logic( ){

		System.out.println();
		System.out.println("my current location is:"+agent.current.dimension_x+","+agent.current.dimension_y);
		GameBoard.displayGameBoard(); //display game board
		ArrayList<Position> current_neighbours; 
		Block current_block=GameBoard.blocks[agent.current.dimension_x][agent.current.dimension_y]; //gets info about current block 
		game_over=current_block.writeBlockMessage(); //write current block information on screen


		// if agent have not died 
		if(game_over==false){
			MarkMap();  //mark the ai map with current information

			if(current_block.is_empty==true){
				aiMove(); //returns to moved direction
			}
			//if agent teleported into new location previous move should be deleted
			if(agent.teleported==true){
				path=new Stack();
				agent.teleported=false;
			}
			//agent encountered with data, now it should decide wheather there is enough 
			//data or not .if not turn back and try another location , if there is act according to data
			if((current_block.breeze_exists==true || current_block.smeel_exists==true || current_block.glitteringness==true) && find_exit_method==false){

				for(int i=0;i<4;i++){ //number is 4 because agent can have 4 neighbours 
					current_neighbours=Block.findNeighbours(agent.current);//get neighbour coordinations 
					//to check wheather there are available information about nighbours or not 

					Position p=current_neighbours.get(i);

					//if that neighbour have not been discovered yet  
					//directions put position array list accordingly
					//each iteration also shows information about direction
					if(ai_map[p.dimension_x][p.dimension_y]==null){

						if(i==0){
							game_over=agent.agentMove("NORTH");
							MarkMap(); //mark the ai map with current information
							path.push("NORTH");
						}
						else if(i==1){
							game_over=agent.agentMove("SOUTH");
							MarkMap(); //mark the ai map with current information
							path.push("SOUTH");
						}
						else if(i==2){
							game_over=agent.agentMove("LEFT");
							MarkMap(); //mark the ai map with current information
							path.push("LEFT");
						}
						else if(i==3){
							game_over=agent.agentMove("RIGHT");
							MarkMap(); //mark the ai map with current information
							path.push("RIGHT");
						}

					}//end of if

					//*******************************************************************************
					// this logical operations evaluates agent actions according to marked map information

					else if((ai_map[p.dimension_x][p.dimension_y].been_here==false &&
							ai_map[p.dimension_x][p.dimension_y].wumpus_exists==true &&
							ai_map[p.dimension_x][p.dimension_y].pit_exists==false &&
							ai_map[p.dimension_x][p.dimension_y].treasure_exists==false) ||

							(ai_map[p.dimension_x][p.dimension_y].been_here==false &&
							ai_map[p.dimension_x][p.dimension_y].wumpus_exists==true &&
							ai_map[p.dimension_x][p.dimension_y].pit_exists==true &&
							ai_map[p.dimension_x][p.dimension_y].treasure_exists==false)){

						int wumpus_meter=wumpusMeter(); //get the risk
						if(wumpus_meter>1){  //wumpus meter shows possible wumpus numbers around agent if 
							System.out.println("Evaluated... need to turn back.");
							back();
						}

						//if wumpus meter shows one .it means ai is able too detect wumpus position
						else if(wumpus_meter==1){ //AI knows about wumpus location 

							if(i==0)
								wumpus_dead=agent.Shoot("NORTH");
							else if(i==1)
								wumpus_dead=agent.Shoot("SOUTH");
							else if(i==2)
								wumpus_dead=agent.Shoot("LEFT");
							else if(i==3)
								wumpus_dead=agent.Shoot("RIGHT");

							//if you miss by any chance wumpus will be disturbed
							if(wumpus_dead==false){
								wumpus.Disturbed();
							}

						}//end of wumpus meter 1

					}//end of if

					
					//if pits are detected turn back
					else if((ai_map[p.dimension_x][p.dimension_y].been_here==false &&
							ai_map[p.dimension_x][p.dimension_y].wumpus_exists==false &&
							ai_map[p.dimension_x][p.dimension_y].pit_exists==true &&
							ai_map[p.dimension_x][p.dimension_y].treasure_exists==false)){
						
                        int pit_meter=pitMeter();
						if(pit_meter>1){ //there are more pits than guessed 
							System.out.println("Evaluated... need to turn back.");
							back();
						}

						//means agent knows where pit is so take another location except from pit
						else if(pit_meter==1){

							if(i==0){
								game_over=agent.agentMove("SOUTH");
								MarkMap(); //mark the ai map with current information
								path.push("SOUTH");
							}
							else if(i==1){
							    game_over=agent.agentMove("NORTH");
								MarkMap(); //mark the ai map with current information
								path.push("NORTH");
							}
							else if(i==2){
								game_over=agent.agentMove("RIGHT");
								MarkMap(); //mark the ai map with current information
								path.push("RIGHT");
							}
							else if(i==3){
								game_over=agent.agentMove("LEFT");
								MarkMap(); //mark the ai map with current information
								path.push("LEFT");
							}
						}//end of pit meter== 1
					}//end of else if

					//if there is a possible treasure around AI no any other danger
					//AI should move towards instead of going back and search for other possibilities 
					else if((ai_map[p.dimension_x][p.dimension_y].been_here==false &&
							ai_map[p.dimension_x][p.dimension_y].wumpus_exists==false &&
							ai_map[p.dimension_x][p.dimension_y].pit_exists==false &&
							ai_map[p.dimension_x][p.dimension_y].treasure_exists==true)){
						
                         int treasure_meter=treasureMeter();
						if(treasure_meter>0){  

							if(i==0){
								game_over=agent.agentMove("NORTH");
								MarkMap(); //mark the ai map with current information
								path.push("NORTH");
							}
							else if(i==1){
								game_over=agent.agentMove("SOUTH");
								MarkMap(); //mark the ai map with current information
								path.push("SOUTH");
							}
							else if(i==2){
								game_over=agent.agentMove("LEFT");
								MarkMap(); //mark the ai map with current information
								path.push("LEFT");
							}
							else if(i==3){
								game_over=agent.agentMove("RIGHT");
								MarkMap(); //mark the ai map with current information
								path.push("RIGHT");
							}
						}//treasure meter 0

					}//end of else if

					//if there are pits and treasure agent will move according to knowledge that it have 
					else if((ai_map[p.dimension_x][p.dimension_y].been_here==false &&
							ai_map[p.dimension_x][p.dimension_y].wumpus_exists==false &&
							ai_map[p.dimension_x][p.dimension_y].pit_exists==true &&
							ai_map[p.dimension_x][p.dimension_y].treasure_exists==true)){

						 int pit_meter=pitMeter();
						 int treasure_meter=treasureMeter();
						if(treasure_meter>1 &&  pit_meter>1){ //ai could not detect  where treasure and pit
							System.out.println("Evaluated... need to turn back.");
							back(); //turn back
						}
						//means agent knows about pit so it can take risk to move to take treasure 
						else if(treasure_meter>1 && pit_meter==1){

							if(i==0 && ai_map[p.dimension_x][p.dimension_y].pit_exists==false){
								game_over=agent.agentMove("NORTH");
								MarkMap(); //mark the ai map with current information
								path.push("NORTH");
							}
							else if(i==1 && ai_map[p.dimension_x][p.dimension_y].pit_exists==false){
								game_over=agent.agentMove("SOUTH");
								MarkMap(); //mark the ai map with current information
								path.push("SOUTH");
							}
							else if(i==2 && ai_map[p.dimension_x][p.dimension_y].pit_exists==false){
								game_over=agent.agentMove("LEFT");
								MarkMap(); //mark the ai map with current information
								path.push("LEFT");
							}
							else if(i==3 && ai_map[p.dimension_x][p.dimension_y].pit_exists==false){
								game_over=agent.agentMove("RIGHT");
								MarkMap(); //mark the ai map with current information
								path.push("RIGHT");
							}
						}//end of else if

						else if(treasure_meter==1){  //ai knows where treasure is 

							if(i==0){
								game_over=agent.agentMove("NORTH");
								MarkMap(); //mark the ai map with current information
								path.push("NORTH");
							}
							else if(i==1){
								game_over=agent.agentMove("SOUTH");
								MarkMap(); //mark the ai map with current information
								path.push("SOUTH");
							}
							else if(i==2){
								game_over=agent.agentMove("LEFT");
								MarkMap(); //mark the ai map with current information
								path.push("LEFT");
							}
							else if(i==3){
								game_over=agent.agentMove("RIGHT");
								MarkMap(); //mark the ai map with current information
								path.push("RIGHT");
							}
						}// treasureMeter 1
					}//end of else if

					//there is a treasure and wumpus around agent
					else if((ai_map[p.dimension_x][p.dimension_y].been_here==false &&
							ai_map[p.dimension_x][p.dimension_y].wumpus_exists==true &&
							ai_map[p.dimension_x][p.dimension_y].pit_exists==true &&
							ai_map[p.dimension_x][p.dimension_y].treasure_exists==true)
							||
							(ai_map[p.dimension_x][p.dimension_y].been_here==false &&
							ai_map[p.dimension_x][p.dimension_y].wumpus_exists==true &&
							ai_map[p.dimension_x][p.dimension_y].pit_exists==false &&
							ai_map[p.dimension_x][p.dimension_y].treasure_exists==true) ){

						 int wumpus_meter=wumpusMeter(); //get the risk
					 	 int treasure_meter=treasureMeter();
					 	 
						if(treasure_meter>1 && wumpus_meter>1){  //if agent detect any of them returns back
							System.out.println("Evaluated... need to turn back.");
							back();
						}

						if(treasure_meter==1){ //AI knows about treasure 

							if(i==0){
								game_over=agent.agentMove("NORTH");
								MarkMap(); //mark the ai map with current information
								path.push("NORTH");
							}
							else if(i==1){
								game_over=agent.agentMove("SOUTH");
								MarkMap(); //mark the ai map with current information
								path.push("SOUTH");
							}
							else if(i==2){
								game_over=agent.agentMove("LEFT");
								MarkMap(); //mark the ai map with current information
								path.push("LEFT");
							}
							else if(i==3)
								game_over=agent.agentMove("RIGHT");
							    MarkMap(); //mark the ai map with current information
							    path.push("RIGHT");
						}

						if(wumpus_meter==1){ //AI knows about wumpus location 

							if(i==0)
								wumpus_dead=agent.Shoot("NORTH");
							else if(i==1)
								wumpus_dead=agent.Shoot("SOUTH");
							else if(i==2)
								wumpus_dead=agent.Shoot("LEFT");
							else if(i==3)
								wumpus_dead=agent.Shoot("RIGHT");

							//if you miss by any chance wumpus will be disturbed
							if(wumpus_dead==false){
								wumpus.Disturbed();
							}
						}
					}//end of else if
				}//for

			}//	breeze_exists==true || smeel_exists==true || glitteringness==true

			//if agent gots treasure and saved exit location 
			if(agent.got_treasure==true &&  detected_exit!=null){  
				findExit();
				find_exit_method=true; //find exit method called 
			}

			//if agent got treasure and exit has been reached it means game over 
			if(current_block.exit_exists==true && agent.got_treasure==true){
				game_over=true;
				System.out.println("AI manage to finish game");
			}

			//if agent got treasure and does not know where exit is  
			else if(current_block.exit_exists==true && agent.got_treasure==false){
				aiMove(); //returns to moved direction
			}

		}//else

		displayAiMap(); //display the map which ai marked

	}//end of logic class 


	/**
	 *this function basically calculates cost for each direction regarding destination(exit)
	 *point and moves agent accordingly  
	 *@param  
	 * @return
	 */
	void findExit(){

		Agent temp_agent=null;
		Position current;
		int north_cost=0;  
		int south_cost=0;
		int west_cost=0;
		int east_cost=0;
		Block block_win=GameBoard.blocks[agent.current.dimension_x][agent.current.dimension_y]; 
		
	 
		//cost calculations will be done by counting 
		//in order to count ai uses move function and increase the variable 
		//if destination and current point are same cost calculation is complate 
		temp_agent=new Agent(agent.current);
		current=temp_agent.current;
		while(current.dimension_y!=detected_exit.dimension_y){

			current=temp_agent.moveLeft();
			temp_agent.current=current;
			 west_cost++;
		}

		temp_agent=new Agent(agent.current);
		current=temp_agent.current;
		while(current.dimension_y!=detected_exit.dimension_y){

			current=temp_agent.moveRight();
			temp_agent.current=current;
			east_cost++;
		}

		temp_agent=new Agent(agent.current);
		current=temp_agent.current;
		while(current.dimension_x!=detected_exit.dimension_x){

			current=temp_agent.moveNorth();
			temp_agent.current=current;
			north_cost++;
		}

		temp_agent=new Agent(agent.current);
		current=temp_agent.current;
		while(current.dimension_x!=detected_exit.dimension_x){

			current=temp_agent.moveSouth();
			temp_agent.current=current;
			south_cost++;
		}

		//display cost information to user
		System.out.println("exit location is:"+detected_exit.dimension_x+","+detected_exit.dimension_y);
		System.out.println("my current location is:"+agent.current.dimension_x+","+agent.current.dimension_y);
		System.out.println("north cost:"+north_cost);
		System.out.println("south cost:"+south_cost);
		System.out.println("west cost:"+west_cost);
		System.out.println("east cost:"+east_cost);

		
		//going south has more cost than going nort , go to north but if you are not same dimension with destination
		if((north_cost<south_cost) && north_cost!=0 ){

			if(ai_map[agent.moveNorth().dimension_x][agent.moveNorth().dimension_y].been_here==true)
				 game_over=agent.agentMove("NORTH");
			//if north way is stuck ai needs to go another empty place to finish the game
			else if(ai_map[agent.moveLeft().dimension_x][agent.moveLeft().dimension_y].been_here==true)
				game_over=agent.agentMove("LEFT");
			else if(ai_map[agent.moveRight().dimension_x][agent.moveRight().dimension_y].been_here==true)
				game_over=agent.agentMove("RIGHT");
			else if(ai_map[agent.moveSouth().dimension_x][agent.moveSouth().dimension_y].been_here==true)
				game_over=agent.agentMove("SOUTH");
				
		}

		//going north has more cost than going south , go to north but if you are not same dimension with destination
		else if((north_cost>south_cost) && (south_cost!=0)) {

			if(ai_map[agent.moveSouth().dimension_x][agent.moveSouth().dimension_y].been_here==true)
				game_over=agent.agentMove("SOUTH");
			//if south way is stuck ai needs to go another empty place to finish the game
			else if(ai_map[agent.moveLeft().dimension_x][agent.moveLeft().dimension_y].been_here==true)
				game_over=agent.agentMove("LEFT");
			else if(ai_map[agent.moveRight().dimension_x][agent.moveRight().dimension_y].been_here==true)
				game_over=agent.agentMove("RIGHT");
			else if(ai_map[agent.moveNorth().dimension_x][agent.moveNorth().dimension_y].been_here==true)
				game_over=agent.agentMove("NORTH");
                
		}
		//going east has more cost than going west , go to north but if you are not same dimension with destination
		if(east_cost<west_cost && (east_cost!=0) ){
			if(ai_map[agent.moveRight().dimension_x][agent.moveRight().dimension_y].been_here==true)
				game_over=agent.agentMove("RIGHT");
			//if west way is stuck ai needs to go another empty place to finish the game
			else if(ai_map[agent.moveLeft().dimension_x][agent.moveLeft().dimension_y].been_here==true)
				game_over=agent.agentMove("LEFT");
			else if(ai_map[agent.moveSouth().dimension_x][agent.moveSouth().dimension_y].been_here==true)
				game_over=agent.agentMove("SOUTH");
			else if(ai_map[agent.moveNorth().dimension_x][agent.moveNorth().dimension_y].been_here==true)
				game_over=agent.agentMove("NORTH");

		}
		//going west has more cost than going east , go to north but if you are not same dimension with destination
		else if(east_cost>west_cost &&(west_cost!=0)){
			if(ai_map[agent.moveLeft().dimension_x][agent.moveLeft().dimension_y].been_here==true)
				game_over=agent.agentMove("LEFT");
			//if east way is stuck ai needs to go another empty place to finish the game
			else if(ai_map[agent.moveRight().dimension_x][agent.moveRight().dimension_y].been_here==true)
				game_over=agent.agentMove("RIGHT");
			else if(ai_map[agent.moveSouth().dimension_x][agent.moveSouth().dimension_y].been_here==true)
				game_over=agent.agentMove("SOUTH");
			else if(ai_map[agent.moveNorth().dimension_x][agent.moveNorth().dimension_y].been_here==true)
				game_over=agent.agentMove("NORTH");
		}
		

	}// end of find exit method


	/**
	 *checks neihbour information of ai and returns how much possible treasure around agent
	 *point and moves agent accordingly  
	 *@param  
	 * @return possible treasure counter
	 */

	int treasureMeter(){

		ArrayList<Position> neighbours; 
		neighbours=Block.findNeighbours(agent.current);
		int treasure_possibility_counter=0;

		for(int i=0;i<neighbours.size();i++){

			if(ai_map[neighbours.get(i).dimension_x][neighbours.get(i).dimension_y]!=null &&
					ai_map[neighbours.get(i).dimension_x][neighbours.get(i).dimension_y].treasure_exists==true){
				treasure_possibility_counter++;
			}//if

		}//for loop

		System.out.println("treasure meter is:"+treasure_possibility_counter);
		return treasure_possibility_counter;


	}//end of treasureMeter


	/**
	 *checks neihbour information of ai and returns how much possible pit around agent
	 *point and moves agent accordingly  
	 *@param  
	 * @return possible treasure counter
	 */	
	int pitMeter(){

		ArrayList<Position> neighbours; 
		neighbours=Block.findNeighbours(agent.current);//agent neighbours
		int pit_possibility_counter=0;

		for(int i=0;i<neighbours.size();i++){

			if(ai_map[neighbours.get(i).dimension_x][neighbours.get(i).dimension_y]!=null &&
					ai_map[neighbours.get(i).dimension_x][neighbours.get(i).dimension_y].pit_exists==true){
				pit_possibility_counter++;
			}//if

		}//for loop

		System.out.println("pit meter is:"+pit_possibility_counter);
		return pit_possibility_counter;


	}//end of pitMeter

	/**
	 *checks neihbour information of ai and returns how much possible wumpus(detected) around agent
	 *point and moves agent accordingly  
	 *@param  
	 * @return possible wumpus counter
	 */
	int wumpusMeter(){

		ArrayList<Position> neighbours; 
		neighbours=Block.findNeighbours(agent.current);
		int wumpus_possibility_counter=0;

		for(int i=0;i<neighbours.size();i++){

			if(ai_map[neighbours.get(i).dimension_x][neighbours.get(i).dimension_y]!=null &&
					ai_map[neighbours.get(i).dimension_x][neighbours.get(i).dimension_y].wumpus_exists==true){
				wumpus_possibility_counter++;
			}//if

		}//for loop

		System.out.println("wumpus meter is:"+wumpus_possibility_counter);
		return wumpus_possibility_counter;

	}//end of wumpus meter 

	/**
	 *go back to wumpus previous position if it is not transported by bat 
	 *   
	 *@param  none
	 * @return none
	 */
	void back( ){

		String return_path="empty";

		//this means game just started or agent teleported to another location by bat 
		if(path.isEmpty()){
			aiMove(); //returns to moved direction
			System.out.println("last move:"+return_path);
		}
		else{
			return_path=path.pop();
			System.out.println("last move:"+return_path);
			//if you move to north to return back you need to move to south
			if(return_path.equals("NORTH"))			
				game_over=agent.agentMove("SOUTH");

			else if(return_path.equals("SOUTH"))
				game_over=agent.agentMove("NORTH");

			else if(return_path.equals("LEFT"))
				game_over=agent.agentMove("RIGHT");

			else if(return_path.equals("RIGHT"))
				game_over=agent.agentMove("LEFT");
		}
	}//end of back method


	/**
	 *this method is used in markmap method to eleminate positons that agent been to in 
	 *(elimination is withing current position's neighbours) 
	 *@param  neihbours or current position
	 * @return neighbours which agent have not been to 
	 */
	ArrayList<Position> filterPossibilities(ArrayList<Position> neighbours){
       
		for(int i=0;i<neighbours.size();i++){
			Position p=neighbours.get(i);
			//neigbours exposed in ai map
			if(ai_map[p.dimension_x][p.dimension_y]!=null && ai_map[p.dimension_x][p.dimension_y].been_here==true){
				 neighbours.remove(p);
			}//!=null if
		}//for
	 
		return neighbours;
	}//end of filter possibilities

	/**
	 *change agents position  
	 * 
	 *@param  none
	 *@return none 
	 */

	void aiMove( ){

		String[] words = {"NORTH", "SOUTH", "RIGHT", "LEFT" };   // paths 
		boolean break_flag=false;
		int direction=-1;

		ArrayList<Position>neighbours=Block.findNeighbours(agent.current); //find neighbours of current position

		for(int i=0;i<neighbours.size();i++){

			//if positions are null and haven2t been there before 
			if((ai_map[neighbours.get(i).dimension_x][neighbours.get(i).dimension_y]==null && break_flag==false)
					|| ((ai_map[neighbours.get(i).dimension_x][neighbours.get(i).dimension_y].been_here==false)
							&& break_flag==false )){

				//if north direction is empty of have not been to  go there
				if(i==0){
					game_over=agent.agentMove("NORTH");
					path.push("NORTH");
					MarkMap(); //mark the ai map with current information
					break_flag=true;
				}

				//if south direction is empty of have not been to  go there
				else if(i==1){
					game_over=agent.agentMove("SOUTH");
					path.push("SOUTH");
					MarkMap( ); //mark the ai map with current information
					break_flag=true;
				}

				//if left direction is empty of have not been to  go there
				else if(i==2){
					game_over=agent.agentMove("LEFT");
					path.push("LEFT");
					MarkMap( ); //mark the ai map with current information
					break_flag=true;
				}

				//if right direction is empty of have not been to  go there
				else if(i==3){
					game_over=agent.agentMove("RIGHT");
					path.push("RIGHT");
					MarkMap( ); //mark the ai map with current information
					break_flag=true;
				}
			}
		}//end of for 

		// in case of agent stucks it needs to move in order to finish game 
		if(break_flag==false){
			Random generator = new Random();
			direction = generator.nextInt(4); 
			game_over=agent.agentMove(words[direction]); //moves agent randomly 
			path.push(words[direction]);
		}

	}//end of random move method

	/**
	 *marks and updates ai blocks according to current position info and past information about visited blocks   
	 * 
	 *@param  none
	 *@return none 
	 */
	void MarkMap( ){  

		//get the agent's current information block 
		Block current_block=GameBoard.blocks[agent.current.dimension_x][agent.current.dimension_y];
		//create ai block and mark as been here
		ai_map[agent.current.dimension_x][agent.current.dimension_y]=new AiBlock(GameBoard.blocks[agent.current.dimension_x][agent.current.dimension_y],true);

		//there are some possibilities according to percepts that agent fells 
		//if it is first time for percept ai agent opens a ai block and fill the information 
		//if it is filled before agent updates information in the light of current block information 

		if(current_block.breeze_exists==true && current_block.smeel_exists==false && current_block.glitteringness==false){

			posibble_location_marking=Block.findNeighbours(agent.current); //all neighbours are possible pits in this case
			posibble_location_marking=filterPossibilities(posibble_location_marking);

			for(int i=0;i<posibble_location_marking.size();i++){

				if(ai_map[posibble_location_marking.get(i).dimension_x][posibble_location_marking.get(i).dimension_y]==null){

					ai_map[posibble_location_marking.get(i).dimension_x][posibble_location_marking.get(i).dimension_y]=new AiBlock();
					ai_map[posibble_location_marking.get(i).dimension_x][posibble_location_marking.get(i).dimension_y].pit_exists=true;
					ai_map[posibble_location_marking.get(i).dimension_x][posibble_location_marking.get(i).dimension_y].treasure_exists=false;
					ai_map[posibble_location_marking.get(i).dimension_x][posibble_location_marking.get(i).dimension_y].is_empty=false;
					ai_map[posibble_location_marking.get(i).dimension_x][posibble_location_marking.get(i).dimension_y].wumpus_exists=false;

				}

				//means block AI looking if forecasted  need to be adjust with light of current block information
				else{

					Block forecasted_block=ai_map[posibble_location_marking.get(i).dimension_x][posibble_location_marking.get(i).dimension_y];

					if(forecasted_block.wumpus_exists==true){
						ai_map[posibble_location_marking.get(i).dimension_x][posibble_location_marking.get(i).dimension_y].wumpus_exists=false;
					}

					if(forecasted_block.treasure_exists==true){
						ai_map[posibble_location_marking.get(i).dimension_x][posibble_location_marking.get(i).dimension_y].treasure_exists=false;
					}

					if(forecasted_block.pit_exists==false){
						ai_map[posibble_location_marking.get(i).dimension_x][posibble_location_marking.get(i).dimension_y].pit_exists=true;
					}
				}

			}//for

		}//breeze


		else if(current_block.breeze_exists==false && current_block.smeel_exists==true && current_block.glitteringness==false){

			posibble_location_marking=Block.findNeighbours(agent.current); //all neighbours are possible pits in this case
			posibble_location_marking=filterPossibilities(posibble_location_marking);

			for(int i=0;i<posibble_location_marking.size();i++){

				if(ai_map[posibble_location_marking.get(i).dimension_x][posibble_location_marking.get(i).dimension_y]==null){

					ai_map[posibble_location_marking.get(i).dimension_x][posibble_location_marking.get(i).dimension_y]=new AiBlock();
					ai_map[posibble_location_marking.get(i).dimension_x][posibble_location_marking.get(i).dimension_y].pit_exists=false;
					ai_map[posibble_location_marking.get(i).dimension_x][posibble_location_marking.get(i).dimension_y].treasure_exists=false;
					ai_map[posibble_location_marking.get(i).dimension_x][posibble_location_marking.get(i).dimension_y].is_empty=false;
					ai_map[posibble_location_marking.get(i).dimension_x][posibble_location_marking.get(i).dimension_y].wumpus_exists=true;
				}
				//means block AI looking if forecasted  need to be adjust with light of current block information
				else{

					Block forecasted_block=ai_map[posibble_location_marking.get(i).dimension_x][posibble_location_marking.get(i).dimension_y];

					if(forecasted_block.wumpus_exists==false){
						ai_map[posibble_location_marking.get(i).dimension_x][posibble_location_marking.get(i).dimension_y].wumpus_exists=true;
					}

					if(forecasted_block.treasure_exists==true){
						ai_map[posibble_location_marking.get(i).dimension_x][posibble_location_marking.get(i).dimension_y].treasure_exists=false;
					}

					if(forecasted_block.pit_exists==true){
						ai_map[posibble_location_marking.get(i).dimension_x][posibble_location_marking.get(i).dimension_y].pit_exists=false;
					}

				}//else

			}

		}//smell


		else if(current_block.breeze_exists==true && current_block.smeel_exists==true && current_block.glitteringness==false){

			posibble_location_marking=Block.findNeighbours(agent.current); //all neighbours are possible pits in this case
			posibble_location_marking=filterPossibilities(posibble_location_marking);

			for(int i=0;i<posibble_location_marking.size();i++){

				if(ai_map[posibble_location_marking.get(i).dimension_x][posibble_location_marking.get(i).dimension_y]==null){

					ai_map[posibble_location_marking.get(i).dimension_x][posibble_location_marking.get(i).dimension_y]=new AiBlock();
					ai_map[posibble_location_marking.get(i).dimension_x][posibble_location_marking.get(i).dimension_y].pit_exists=true;
					ai_map[posibble_location_marking.get(i).dimension_x][posibble_location_marking.get(i).dimension_y].treasure_exists=false;
					ai_map[posibble_location_marking.get(i).dimension_x][posibble_location_marking.get(i).dimension_y].is_empty=false;
					ai_map[posibble_location_marking.get(i).dimension_x][posibble_location_marking.get(i).dimension_y].wumpus_exists=true;
				}

				//means block AI looking if forecasted  need to be adjust with light of current block information
				else{

					Block forecasted_block=ai_map[posibble_location_marking.get(i).dimension_x][posibble_location_marking.get(i).dimension_y];

					if(forecasted_block.wumpus_exists==false){
						ai_map[posibble_location_marking.get(i).dimension_x][posibble_location_marking.get(i).dimension_y].wumpus_exists=true;
					}

					if(forecasted_block.treasure_exists==true){
						ai_map[posibble_location_marking.get(i).dimension_x][posibble_location_marking.get(i).dimension_y].treasure_exists=false;
					}

					if(forecasted_block.pit_exists==false){
						ai_map[posibble_location_marking.get(i).dimension_x][posibble_location_marking.get(i).dimension_y].pit_exists=true;
					}

				}//else
			}

		}//breeze

		else if(current_block.breeze_exists==true && current_block.smeel_exists==false && current_block.glitteringness==true){

			posibble_location_marking=Block.findNeighbours(agent.current); //all neighbours are possible pits in this case
			posibble_location_marking=filterPossibilities(posibble_location_marking);

			for(int i=0;i<posibble_location_marking.size();i++){

				if(ai_map[posibble_location_marking.get(i).dimension_x][posibble_location_marking.get(i).dimension_y]==null){

					ai_map[posibble_location_marking.get(i).dimension_x][posibble_location_marking.get(i).dimension_y]=new AiBlock();
					ai_map[posibble_location_marking.get(i).dimension_x][posibble_location_marking.get(i).dimension_y].pit_exists=true;
					ai_map[posibble_location_marking.get(i).dimension_x][posibble_location_marking.get(i).dimension_y].treasure_exists=true;
					ai_map[posibble_location_marking.get(i).dimension_x][posibble_location_marking.get(i).dimension_y].is_empty=false;
					ai_map[posibble_location_marking.get(i).dimension_x][posibble_location_marking.get(i).dimension_y].wumpus_exists=false;
				}
				//means block AI looking if forecasted  need to be adjust with light of current block information
				else{

					Block forecasted_block=ai_map[posibble_location_marking.get(i).dimension_x][posibble_location_marking.get(i).dimension_y];

					if(forecasted_block.wumpus_exists==true){
						ai_map[posibble_location_marking.get(i).dimension_x][posibble_location_marking.get(i).dimension_y].wumpus_exists=false;
					}

					if(forecasted_block.treasure_exists==false && agent.got_treasure==false){
						ai_map[posibble_location_marking.get(i).dimension_x][posibble_location_marking.get(i).dimension_y].treasure_exists=true;
					}

					if(forecasted_block.pit_exists==false){
						ai_map[posibble_location_marking.get(i).dimension_x][posibble_location_marking.get(i).dimension_y].pit_exists=true;
					}

				}//else
			}

		}//breeze

		else if(current_block.breeze_exists==false && current_block.smeel_exists==true && current_block.glitteringness==true){

			posibble_location_marking=Block.findNeighbours(agent.current); //all neighbours are possible pits in this case
			posibble_location_marking=filterPossibilities(posibble_location_marking);

			for(int i=0;i<posibble_location_marking.size();i++){

				if(ai_map[posibble_location_marking.get(i).dimension_x][posibble_location_marking.get(i).dimension_y]==null){
					ai_map[posibble_location_marking.get(i).dimension_x][posibble_location_marking.get(i).dimension_y]=new AiBlock();
					ai_map[posibble_location_marking.get(i).dimension_x][posibble_location_marking.get(i).dimension_y].pit_exists=false;
					ai_map[posibble_location_marking.get(i).dimension_x][posibble_location_marking.get(i).dimension_y].treasure_exists=true;
					ai_map[posibble_location_marking.get(i).dimension_x][posibble_location_marking.get(i).dimension_y].is_empty=false;
					ai_map[posibble_location_marking.get(i).dimension_x][posibble_location_marking.get(i).dimension_y].wumpus_exists=true;
				}
				//means block AI looking if forecasted  need to be adjust with light of current block information
				else{

					Block forecasted_block=ai_map[posibble_location_marking.get(i).dimension_x][posibble_location_marking.get(i).dimension_y];
					if(forecasted_block.wumpus_exists==false){
						ai_map[posibble_location_marking.get(i).dimension_x][posibble_location_marking.get(i).dimension_y].wumpus_exists=true;
					}
					if(forecasted_block.treasure_exists==false && agent.got_treasure==false){
						ai_map[posibble_location_marking.get(i).dimension_x][posibble_location_marking.get(i).dimension_y].treasure_exists=true;
					}
					if(forecasted_block.pit_exists==true){
						ai_map[posibble_location_marking.get(i).dimension_x][posibble_location_marking.get(i).dimension_y].pit_exists=false;
					}

				}//else
			}
		}

		else if(current_block.breeze_exists==false && current_block.smeel_exists==false && current_block.glitteringness==true){

			posibble_location_marking=Block.findNeighbours(agent.current); //all neighbours are possible pits in this case
			posibble_location_marking=filterPossibilities(posibble_location_marking);

			for(int i=0;i<posibble_location_marking.size();i++){

				if(ai_map[posibble_location_marking.get(i).dimension_x][posibble_location_marking.get(i).dimension_y]==null){
					Block block_guessed=ai_map[posibble_location_marking.get(i).dimension_x][posibble_location_marking.get(i).dimension_y];

					ai_map[posibble_location_marking.get(i).dimension_x][posibble_location_marking.get(i).dimension_y]=new AiBlock();
					ai_map[posibble_location_marking.get(i).dimension_x][posibble_location_marking.get(i).dimension_y].pit_exists=false;
					ai_map[posibble_location_marking.get(i).dimension_x][posibble_location_marking.get(i).dimension_y].treasure_exists=true;
					ai_map[posibble_location_marking.get(i).dimension_x][posibble_location_marking.get(i).dimension_y].is_empty=false;
					ai_map[posibble_location_marking.get(i).dimension_x][posibble_location_marking.get(i).dimension_y].wumpus_exists=false;
				}
				//means block AI looking if forecasted  need to be adjust with light of current block information
				else{

					Block forecasted_block=ai_map[posibble_location_marking.get(i).dimension_x][posibble_location_marking.get(i).dimension_y];

					if(forecasted_block.wumpus_exists==true){
						ai_map[posibble_location_marking.get(i).dimension_x][posibble_location_marking.get(i).dimension_y].wumpus_exists=false;
					}
					if(forecasted_block.treasure_exists==false && agent.got_treasure==false){
						ai_map[posibble_location_marking.get(i).dimension_x][posibble_location_marking.get(i).dimension_y].treasure_exists=true;
					}
					if(forecasted_block.pit_exists==true){
						ai_map[posibble_location_marking.get(i).dimension_x][posibble_location_marking.get(i).dimension_y].pit_exists=false;
					}
				}//else
			}
		}

		else if(current_block.breeze_exists==false && current_block.smeel_exists==false && current_block.glitteringness==false){

			posibble_location_marking=Block.findNeighbours(agent.current); //all neighbours are possible pits in this case
			posibble_location_marking=filterPossibilities(posibble_location_marking);

			for(int i=0;i<posibble_location_marking.size();i++){

				if(ai_map[posibble_location_marking.get(i).dimension_x][posibble_location_marking.get(i).dimension_y]==null){

					ai_map[posibble_location_marking.get(i).dimension_x][posibble_location_marking.get(i).dimension_y]=new AiBlock();
					ai_map[posibble_location_marking.get(i).dimension_x][posibble_location_marking.get(i).dimension_y].pit_exists=false;
					ai_map[posibble_location_marking.get(i).dimension_x][posibble_location_marking.get(i).dimension_y].treasure_exists=false;
					ai_map[posibble_location_marking.get(i).dimension_x][posibble_location_marking.get(i).dimension_y].is_empty=true;
					ai_map[posibble_location_marking.get(i).dimension_x][posibble_location_marking.get(i).dimension_y].wumpus_exists=false;

				}

				//means block AI looking if forecasted  need to be adjust with light of current block information
				else{

					Block forecasted_block=ai_map[posibble_location_marking.get(i).dimension_x][posibble_location_marking.get(i).dimension_y];
					if(forecasted_block.wumpus_exists==true){
						ai_map[posibble_location_marking.get(i).dimension_x][posibble_location_marking.get(i).dimension_y].wumpus_exists=false;
					}
					if(forecasted_block.treasure_exists==true){
						ai_map[posibble_location_marking.get(i).dimension_x][posibble_location_marking.get(i).dimension_y].treasure_exists=false;
					}
					if(forecasted_block.pit_exists==true){
						ai_map[posibble_location_marking.get(i).dimension_x][posibble_location_marking.get(i).dimension_y].pit_exists=false;
					}
					if(forecasted_block.is_empty==false){
						ai_map[posibble_location_marking.get(i).dimension_x][posibble_location_marking.get(i).dimension_y].is_empty=true;
					}
				}
			}//for
		}//else if 


		else if(current_block.breeze_exists==true && current_block.smeel_exists==true && current_block.glitteringness==true){

			posibble_location_marking=Block.findNeighbours(agent.current); //all neighbours are possible pits in this case
			posibble_location_marking=filterPossibilities(posibble_location_marking);

			for(int i=0;i<posibble_location_marking.size();i++){

				if(ai_map[posibble_location_marking.get(i).dimension_x][posibble_location_marking.get(i).dimension_y]==null){

					ai_map[posibble_location_marking.get(i).dimension_x][posibble_location_marking.get(i).dimension_y]=new AiBlock();
					ai_map[posibble_location_marking.get(i).dimension_x][posibble_location_marking.get(i).dimension_y].pit_exists=true;
					ai_map[posibble_location_marking.get(i).dimension_x][posibble_location_marking.get(i).dimension_y].treasure_exists=true;
					ai_map[posibble_location_marking.get(i).dimension_x][posibble_location_marking.get(i).dimension_y].is_empty=false;
					ai_map[posibble_location_marking.get(i).dimension_x][posibble_location_marking.get(i).dimension_y].wumpus_exists=true;

				}

				//means block AI looking if forecasted  need to be adjust with light of current block information
				else{

					Block forecasted_block=ai_map[posibble_location_marking.get(i).dimension_x][posibble_location_marking.get(i).dimension_y];

					if(forecasted_block.wumpus_exists==false){
						ai_map[posibble_location_marking.get(i).dimension_x][posibble_location_marking.get(i).dimension_y].wumpus_exists=true;
					}

					if(forecasted_block.treasure_exists==false && agent.got_treasure==false){
						ai_map[posibble_location_marking.get(i).dimension_x][posibble_location_marking.get(i).dimension_y].treasure_exists=true;
					}

					if(forecasted_block.pit_exists==false){
						ai_map[posibble_location_marking.get(i).dimension_x][posibble_location_marking.get(i).dimension_y].pit_exists=true;
					}

					if(forecasted_block.is_empty==true){
						ai_map[posibble_location_marking.get(i).dimension_x][posibble_location_marking.get(i).dimension_y].is_empty=false;
					}
				}
			}//for

		}//else if ends 

		//if agent face with exit block it records exit position and change block information
		 if(current_block.exit_exists==true){

			ai_map[agent.current.dimension_x][agent.current.dimension_y].wumpus_exists=false;
			ai_map[agent.current.dimension_x][agent.current.dimension_y].is_empty=false;
			ai_map[agent.current.dimension_x][agent.current.dimension_y].bat_exists=false;
			ai_map[agent.current.dimension_x][agent.current.dimension_y].pit_exists=false;
			ai_map[agent.current.dimension_x][agent.current.dimension_y].treasure_exists=false;
			ai_map[agent.current.dimension_x][agent.current.dimension_y].exit_exists=true;
			ai_map[agent.current.dimension_x][agent.current.dimension_y].smeel_exists=false;
			ai_map[agent.current.dimension_x][agent.current.dimension_y].breeze_exists=false;
			ai_map[agent.current.dimension_x][agent.current.dimension_y].glitteringness=false;
			detected_exit=new Position(agent.current.dimension_x, agent.current.dimension_y);  //save exit position
			System.out.println("exit location is saved:"+detected_exit.dimension_y+","+detected_exit.dimension_y);
			System.out.println("got treasure:"+agent.got_treasure);
		}//if	


	}//end of mark map method

	/**
	 *displays map information according to ai block , in a way it displays the information of what ai have  
	 * null = n  , wumpus w, pit p , treasure t,exit q,empty e
	 *@param  none
	 *@return none 
	 */
	void displayAiMap(){

		System.out.println();
		System.out.println("********AI board**********");


		for(int i=0;i<Setup.board_size_x;i++){
			for(int j=0;j<Setup.board_size_y;j++){


				if(ai_map[i][j]==null){
					System.out.print(" N ");
				}

				else if(ai_map[i][j].been_here==true){
					System.out.print(" X ");					
				}

				else if(ai_map[i][j].wumpus_exists==true){
					System.out.print(" W ");					
				}

				else if(ai_map[i][j].pit_exists==true){
					System.out.print(" P ");	
				}
				else if(ai_map[i][j].treasure_exists==true){
					System.out.print(" T ");	
				}
				else if(ai_map[i][j].exit_exists==true){
					System.out.print(" Q ");	
				}
				else if(ai_map[i][j].is_empty==true)
					System.out.print(" E ");
			}
			System.out.println("");
		}
	}
}//end of AI class