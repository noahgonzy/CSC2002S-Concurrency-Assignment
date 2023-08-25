package clubSimulation;
public class Barman extends Thread{

    private PeopleLocation myLocation;
    private int movingSpeed;
    public static ClubGrid club;
    private int ID = -1;
    private int movedir = 1;
    public static GridBlock currentBlock;

    Barman(PeopleLocation loc){
        myLocation = loc;
		movingSpeed = 1200; 
        myLocation.setLocation(club.getBarmanStart());
        currentBlock = club.getBarmanStart();
    }

    private void checkPause() {
		synchronized(ClubSimulation.paused){
			try{
				while(ClubSimulation.paused.get()){
                    System.out.println("Thread " + this.ID + " (Barman) is now paused");
					ClubSimulation.paused.wait();
				}
			}
			catch(InterruptedException e){
				e.printStackTrace();
			}
		}
    }

    private void moveBarman(){
        currentBlock = club.movebarman(currentBlock, myLocation);
    }

    private void giveDrinks(){
        club.giveDrinks();
    }
    
    public void run(){
        while(true){
            checkPause();
            int x_mv = myLocation.getX() + 1;
            try{
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