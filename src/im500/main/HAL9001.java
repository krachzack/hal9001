package im500.main;

import java.awt.Color;

import robocode.AdvancedRobot;
import robocode.HitRobotEvent;
import robocode.ScannedRobotEvent;

public class HAL9001 extends AdvancedRobot {
	private static int CLOSE_QUARTERS_DISTANCE = 65;
	private Scanner scanner = new Scanner(this);

	private void initialize() {
		setColors(Color.BLACK, Color.RED, Color.YELLOW, Color.PINK, Color.BLACK);
		setAdjustGunForRobotTurn(true);
		setAdjustRadarForGunTurn(true);
		setAdjustRadarForRobotTurn(true);
	}
	
	@Override
	public void run() {
		initialize();
		while (true) {
			scanner.scan();
		}
	}
	
	@Override
	public void onScannedRobot(ScannedRobotEvent event) {
		scanner.onScanned(event);
		
		setTurnRight(event.getBearing());
		setTurnGunRight((getHeading() - getGunHeading() + event.getBearing()) % 360);
		execute();
		
		//Maximum firepower is 3 (sadly...)
		double firepower = 0.1;
		if (event.getDistance() < CLOSE_QUARTERS_DISTANCE * 4) firepower = 1;
		if (event.getDistance() < CLOSE_QUARTERS_DISTANCE * 2) firepower = 2;
		if (event.getDistance() < CLOSE_QUARTERS_DISTANCE) firepower = 3;
		if (getEnergy() > 5) setFire(firepower);
		setAhead(event.getDistance() + 5);
		execute();
	}
	
	@Override
	public void onHitRobot(HitRobotEvent event) {
		setAhead(-1);
		execute();
	}
}
 