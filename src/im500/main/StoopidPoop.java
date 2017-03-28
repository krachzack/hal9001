package im500.main;

import java.awt.Color;

import robocode.AdvancedRobot;
import robocode.Robot;
import robocode.ScannedRobotEvent;

public class StoopidPoop extends AdvancedRobot {
	boolean found = false;
	
	@Override
	public void run() {
		setRadarColor(Color.PINK);
		setBodyColor(Color.PINK);
		setBulletColor(Color.PINK);
		setGunColor(Color.PINK);
		setScanColor(Color.PINK);
		
		while (true) {
			ahead(5);
			
			if(!found){
				turnRadarLeft(50);
				scan();			
			}else{
				fire(1);
			}
		}
	}
	
	@Override
	public void onScannedRobot(ScannedRobotEvent event) {
		found = true;
		turnGunRight((getHeading() + event.getBearing()) % 360 - getGunHeading());
		super.onScannedRobot(event);
	}
}
