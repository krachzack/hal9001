package im500.main;

import robocode.AdvancedRobot;
import robocode.ScannedRobotEvent;
import robocode.util.Utils;

public class Scanner {
	private static double CORRECTION_FACTOR = 2.0;
	private AdvancedRobot robot;
	
	public Scanner(AdvancedRobot robot) {
		this.robot = robot;
	}
	
	public void scan() {
		robot.scan();
		robot.turnRadarRightRadians(Double.POSITIVE_INFINITY);
		robot.execute();
	}

	public void onScanned(ScannedRobotEvent event) {
		robot.setTurnRadarRightRadians(CORRECTION_FACTOR * Utils.normalRelativeAngle(robot.getHeadingRadians() + event.getBearingRadians() - robot.getRadarHeadingRadians()));		
	}
}
