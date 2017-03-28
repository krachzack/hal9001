package im500.main;

import robocode.AdvancedRobot;
import robocode.Robot;
import robocode.util.Utils;

public final class Move {

	public static void towards(AdvancedRobot r, double targetX, double targetY) {
		face(r, targetX, targetY);
		
		double deltaX = targetX - r.getX();
		double deltaY = targetY - r.getY();
		double deltaDistance = Math.sqrt(deltaX*deltaX + deltaY*deltaY);
		
		r.setAhead(deltaDistance);
	}
	
	public static void face(AdvancedRobot r, double targetX, double targetY) {
		double deltaX = targetX - r.getX();
		double deltaY = targetY - r.getY();
		double targetAngle = 0.5*Math.PI - Math.atan2(deltaY, deltaX);
		face(r, targetAngle);
	}
	
	public static void face(AdvancedRobot r, double targetAngleRadians) {
		double bearing =  Utils.normalRelativeAngle(targetAngleRadians - r.getHeadingRadians());
		r.setTurnRightRadians(bearing);
	}
	
}
