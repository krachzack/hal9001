package im500.main;

import java.awt.Color;

import robocode.AdvancedRobot;
import robocode.HitWallEvent;
import robocode.Robot;
import robocode.ScannedRobotEvent;
import robocode.util.Utils;

public class HAL9001 extends AdvancedRobot {
	
	double nextOrientation = 0;
	long lastTurn = System.currentTimeMillis();
	
	@Override
	public void run() {
		
		setAllColors(Color.CYAN);
		
		while (true) {
			setTurnRadarRight(Double.POSITIVE_INFINITY);
			execute();
		}
	}
	
	@Override
	public void onScannedRobot(ScannedRobotEvent event) {
		setTurnRight(event.getBearing());
		execute();
		setFire(10.0);
		setAhead(event.getDistance() + 5);
		execute();
		scan();
	}

}
 