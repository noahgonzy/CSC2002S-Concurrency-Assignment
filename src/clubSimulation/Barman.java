package clubSimulation;
public class Barman extends Thread{

    private PeopleLocation myLocation;
    private int movingSpeed;
    public static ClubGrid club;
    private int ID = -1;
    private int movedir = 1;
    private GridBlock currentBlock;

    Barman(PeopleLocation loc){
        myLocation = loc;
		movingSpeed = 1200; 
        myLocation.setLocation(club.getBarmanStart());
    }

    /* 
    private void checkPause() {
		if(ClubSimulation.paused.get()){
			System.out.println("Thread " + this.ID + " is globally paused");
		}
		while(ClubSimulation.paused.get()){
		}
    }
    */

    private void movebarman(){
        currentBlock = club.movebarman(currentBlock, myLocation);

    }
    
    public void run(){
        while(true){
            //checkPause();
            int x_mv = myLocation.getX() + 1;
            try{
                sleep(movingSpeed/5);
                movebarman();
                System.out.println(currentBlock.getX());
                System.out.println("Barman Trying to Move");
            }
            catch(InterruptedException e){
                e.printStackTrace();
            }  	
        }
    }
}