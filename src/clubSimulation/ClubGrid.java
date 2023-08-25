//M. M. Kuttel 2023 mkuttel@gmail.com
//Grid for the club

package clubSimulation;
import java.util.concurrent.BrokenBarrierException;


//This class represents the club as a grid of GridBlocks
public class ClubGrid {
	private GridBlock [][] Blocks;
	private final int x;
	private final int y;
	public  final int bar_y;
	
	private GridBlock exit;
	private GridBlock entrance; //hard coded entrance
	private GridBlock barmanstart; //hard coded barman starting block
	private final static int minX =5;//minimum x dimension
	private final static int minY =5;//minimum y dimension
	
	private PeopleCounter counter; //counter of all people in the club on this grid
	
	ClubGrid(int x, int y, int [] exitBlocks,PeopleCounter c) throws InterruptedException {
		if (x<minX) x=minX; //minimum x
		if (y<minY) y=minY; //minimum x
		this.x=x;
		this.y=y;
		this.bar_y=y-3;
		Blocks = new GridBlock[x][y];
		this.initGrid(exitBlocks);
		entrance=Blocks[getMaxX()/2][0];
		counter=c;
		barmanstart = Blocks[getMaxX()/2][getMaxY()-2]; //sets barman starting position
	}
	
	//initialise the grid, creating all the GridBlocks
	private  void initGrid(int []exitBlocks) throws InterruptedException {
		for (int i=0;i<x;i++) {
			for (int j=0;j<y;j++) {
				boolean exit_block=false;
				boolean bar=false;
				boolean dance_block=false;
				if ((i==exitBlocks[0])&&(j==exitBlocks[1])) {exit_block=true;}
				else if (j>=(y-3)) bar=true; 
				else if ((i>x/2) && (j>3) &&(j< (y-5))) dance_block=true;
				//bar is hardcoded two rows before  the end of the club
				Blocks[i][j]=new GridBlock(i,j,exit_block,bar,dance_block);
				if (exit_block) {this.exit = Blocks[i][j];}
			}
		}
	}
	
	public  int getMaxX() {
		return x;
	}
	
	public int getMaxY() {
		return y;
	}

	//returns barman staritng block for barman to position themself
	public GridBlock getBarmanStart() {
		return barmanstart;
	}

	public GridBlock whereEntrance() { 
		return entrance;
	}

	public synchronized boolean inGrid(int i, int j) {
		if ((i>=x) || (j>=y) ||(i<0) || (j<0)) 
			return false;
		return true;
	}
	
	public synchronized boolean inPatronArea(int i, int j) {
		if ((i>=x) || (j>bar_y) ||(i<0) || (j<0)) 
			return false;
		return true;
	}
	
	public GridBlock enterClub(PeopleLocation myLocation) throws InterruptedException  {
		counter.personArrived(); //add to counter of people waiting 
		//checks if club entrance block is occupied or if club is over capacity 
		//synchronized to entrance so that only one thread can try enter the club at a timexw
		synchronized(entrance){
			while(entrance.occupied() || counter.overCapacity()){
				System.out.println("Thread tried to enter club but club is either full or entrance is occupied");
				entrance.wait();
			}
		}
		entrance.get(myLocation.getID());
		counter.personEntered(); //add to counter
		myLocation.setLocation(entrance);
		myLocation.setInRoom(true);
		return entrance;
	}
	
	//might be changed
	public boolean clubFull(){
		return(counter.overCapacity());
	}

	//tells the barman which direction to move in, and will alternate to tell the barman to move left and right
	//can be alterd later if the barman needs to move in directiosn other than left and right
	static int barmandir = 1;

	public synchronized GridBlock movebarman(GridBlock currentBlock, PeopleLocation myLocation){
		int c_x = currentBlock.getX();
		int c_y = currentBlock.getY();
		int new_x;

		//if barmandir = 1, move right, if barmandir = -1, move left
		if(barmandir == 1){
			new_x = c_x+1;
		}
		else{
			new_x = c_x-1;
		};

		//if the new x is not in the grid, then move the barman the other way, and change the barmandir variable to tell him which way to move
		if(!inGrid(new_x,c_y)){
			if(barmandir == 1){
				barmandir = -1;
				new_x = c_x - 1;
			}
			else{
				barmandir = 1;
				new_x = c_x+1;
			}
		}
		
		//set his new block to the new position
		GridBlock newBlock = Blocks[new_x][c_y];
		myLocation.setLocation(newBlock);
		return newBlock;
	}

	//give drinks to the user at the block above where the barman is
	public void giveDrinks(){
		GridBlock todrink = Blocks[Barman.currentBlock.getX()][Barman.currentBlock.getY()-1];
		synchronized(todrink){
			todrink.notifyAll();
		}
	}
	
	public synchronized GridBlock move(GridBlock currentBlock,int step_x, int step_y,PeopleLocation myLocation) throws InterruptedException {  //try to move in 

		int c_x= currentBlock.getX();
		int c_y= currentBlock.getY();
		
		int new_x = c_x+step_x; //new block x coordinates
		int new_y = c_y+step_y; // new block y  coordinates
		
		//restrict i an j to grid
		if (!inPatronArea(new_x,new_y)) {
			//Invalid move to outside  - ignore
			return currentBlock;
		}

		if ((new_x==currentBlock.getX())&&(new_y==currentBlock.getY())) //not actually moving
			return currentBlock;
		 
		GridBlock newBlock = Blocks[new_x][new_y];
		
		if (!newBlock.get(myLocation.getID())) return currentBlock; //stay where you are

		currentBlock.release(); //must release current block

		//notify users waiting at the entrance if a thread moves off the entrance that they can come into the club
		synchronized(entrance){
			if((currentBlock.getX() == entrance.getX()) && (currentBlock.getY() == entrance.getY())){
				entrance.notify();
			}
		}
		myLocation.setLocation(newBlock);
		
		return newBlock;
	} 

	public void leaveClub(GridBlock currentBlock,PeopleLocation myLocation)   {
		currentBlock.release();
		counter.personLeft(); //add to counter
		myLocation.setInRoom(false);
		//notifies threads that a thread has left and they may now be able to enter
		synchronized(entrance){
			entrance.notify();
		}
			
	}

	public GridBlock getExit() {
		return exit;
	}

	public GridBlock whichBlock(int xPos, int yPos) {
		if (inGrid(xPos,yPos)) {
			return Blocks[xPos][yPos];
		}
		System.out.println("block " + xPos + " " +yPos + "  not found");
		return null;
	}
	
	public void setExit(GridBlock exit) {
		this.exit = exit;
	}

	public int getBar_y() {
		return bar_y;
	}
}