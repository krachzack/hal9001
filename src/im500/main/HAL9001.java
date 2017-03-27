package im500.main;

import java.awt.Color;

import robocode.AdvancedRobot;
import robocode.HitRobotEvent;
import robocode.HitWallEvent;
import robocode.Robot;
import robocode.ScannedRobotEvent;

public class HAL9001 extends AdvancedRobot {
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
		
		if (event.getDistance() < 45) setFire(getEnergy() * 0.2);
		setAhead(event.getDistance() + 5);
		execute();
	}
	
	@Override
	public void onHitRobot(HitRobotEvent event) {
		setAhead(-1);
		setFire(getEnergy() * 0.5);
		execute();
	}
}
 