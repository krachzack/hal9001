package im500.main;

import javax.rmi.CORBA.Util;

import robocode.AdvancedRobot;
import robocode.ScannedRobotEvent;
import robocode.util.Utils;

public class Tracker {
	
	private AdvancedRobot r;
	private double enemyAbsoluteAngle;
	private double enemyX;
	private double enemyY;
	private long enemyTurn;
	private double enemyVelocity;
	
	public Tracker(AdvancedRobot robot) {
		this.r = robot;
	}

	public void onScannedRobot(ScannedRobotEvent e) {
		enemyAbsoluteAngle = Utils.normalAbsoluteAngle(e.getHeadingRadians() - r.getHeadingRadians());
		enemyVelocity = e.getVelocity();
		
		double selfX = r.getX();
		double selfY = r.getY();
		double toEnemyAngle = r.getRadarHeadingRadians();
		double toEnemyDistance = e.getDistance();
		
		enemyX = selfX + Math.cos(toEnemyAngle) * toEnemyDistance;
		enemyY = selfY + Math.sin(toEnemyAngle) * toEnemyDistance;
		
		enemyTurn = e.getTime();
	}
	
	public double getProjectedX() {
		long deltaTurns = r.getTime() - enemyTurn;
		return enemyX + Math.cos(enemyAbsoluteAngle) * enemyVelocity * deltaTurns;
	}
	
	public double getProjectedY() {
		long deltaTurns = r.getTime() - enemyTurn;
		return enemyY + Math.sin(enemyAbsoluteAngle) * enemyVelocity * deltaTurns;
	}
}
