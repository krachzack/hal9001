package im500.main;

import java.awt.Color;

import robocode.AdvancedRobot;
import robocode.ScannedRobotEvent;

public class HAL9001 extends AdvancedRobot {
	private Scanner scanner = new Scanner(this);

	private void initialize() {
		setColors(Color.BLACK, Color.RED, Color.YELLOW, Color.BLACK, Color.BLACK);
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
		
		if (event.getDistance() < 60) setFire(10.0);
		setAhead(event.getDistance() + 5);
		execute();
	}
}
 