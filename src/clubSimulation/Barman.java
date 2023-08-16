package clubSimulation;
public class Barman extends Thread{

    private PeopleLocation myLocation;
    private int movingSpeed;
    public static ClubGrid club;
    private int ID = -1;

    Barman(PeopleLocation loc){
        myLocation = loc;
		movingSpeed=1200; 
        myLocation.setLocation(club.getBarmanStart());
        System.out.println("Barman in club as position: " + myLocation.getX() + " " + myLocation.getY());
	}
}