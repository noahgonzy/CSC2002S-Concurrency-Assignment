package clubSimulation;
public class Barman extends Thread{
    private PeopleLocation myLocation; //stores barman location data
    private int movingSpeed; //stores moving speed of barman
    public static ClubGrid club; //sets information of clubgrid
    private int ID = -1; //sets Thread ID to -1
    private int movedir = 1; //sets default movedir to 1 (right)
    public static GridBlock currentBlock; //stores currnt location of barman

    Barman(PeopleLocation loc){
        myLocation = loc;
		movingSpeed = 1200; //can be changed for different barman speed
        myLocation.setLocation(club.getBarmanStart());
        currentBlock = club.getBarmanStart();
    }

    private void checkPause() {
		synchronized(ClubSimulation.paused){
		//Checks the atomic boolean "paused" from club simulation
			try{
				while(ClubSimulation.paused.get()){
                    //tells user that the thread is paused and then waits to be unpaused with the rest of the simulation
                    System.out.println("Thread " + this.ID + " (Barman) is now paused");
					ClubSimulation.paused.wait();
				}
			}
			//catching the interrupted exception
			catch(InterruptedException e){
				e.printStackTrace();
			}
		}
    }

    //moves barman to next block (either left or right depending on his past movements or if he tries to move out of club area)
    private void moveBarman(){
        currentBlock = club.movebarman(currentBlock, myLocation);
    }

    //gives drinks to notified patrons
    private void giveDrinks(){
        club.giveDrinks();
    }

    private void startSim(){
        try{
			//waiting for the countdownlatch starter to start the simulation
			ClubSimulation.starter.await();
		}
		catch (InterruptedException e){
			e.printStackTrace();
		}	
    }
    
    public void run(){
        startSim();
        while(true){
            //checks if the simualtion is paused
            checkPause();
            int x_mv = myLocation.getX() + 1;
            try{
                //sleeps for a specified time, moves and then gives drinks notifying patrons above him
                sleep(movingSpeed/2);
                moveBarman();
                giveDrinks();
            }
            catch(InterruptedException e){
                e.printStackTrace();
            }  	
        }
    }
}