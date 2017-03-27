package Westminster.Team5.marccarre.bots;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import robocode.AdvancedRobot;
import robocode.Bullet;
import robocode.BulletHitBulletEvent;
import robocode.Condition;
import robocode.HitByBulletEvent;
import robocode.HitRobotEvent;
import robocode.ScannedRobotEvent;
import robocode.WinEvent;
import robocode.util.Utils;

/**
 * This robot is based on BasicGFSurfer by Voidious, PEZ, and Bayen.
 * <p/>
 * It is a combination of the movement from BasicSurfer and the gun from
 * GFTargetingBot. See:
 * http://robowiki.net?BasicSurfer
 * http://robowiki.net?GFTargetingBot
 */
public class ArchEnemy extends AdvancedRobot {

	private static final double BULLET_POWER = 1.8;
	private static double lateralDirection;
	private static double lastEnemyVelocity;
	private static WSMovement movement;
	private long lastScanTime = 0;

	public ArchEnemy() {
		movement = new WSMovement(this);
	}

	public void run() {
		setColors(Color.BLACK, Color.BLACK, Color.BLACK, Color.RED, Color.RED);
		lateralDirection = 1;
		lastEnemyVelocity = 0;

		setAdjustGunForRobotTurn(true);
		setAdjustRadarForGunTurn(true);

		while (true) {
			// basic mini-radar code
			if (Math.abs(getRadarTurnRemaining()) < 0.000001 && getOthers() > 0) {
				setTurnRadarRightRadians(Double.POSITIVE_INFINITY);
			}

			if (lastScanTime + 1 < getTime()) {
				//No scans, forcing surfing
				movement.myLocation = new Point2D.Double(getX(), getY());
				movement.updateWaves();
				movement.doSurfing();
			}
			execute();
		}
	}

	public void onScannedRobot(ScannedRobotEvent event) {
		lastScanTime = getTime();
		double enemyAbsoluteBearing = getHeadingRadians()
				+ event.getBearingRadians();
		double enemyDistance = event.getDistance();
		double enemyVelocity = event.getVelocity();
		if (enemyVelocity != 0) {
			lateralDirection = GFTUtils
					.sign(enemyVelocity
							* Math.sin(event.getHeadingRadians()
							- enemyAbsoluteBearing));
		}
		GFTWave wave = new GFTWave(this);
		wave.gunLocation = new Point2D.Double(getX(), getY());
		GFTWave.targetLocation = GFTUtils.project(wave.gunLocation,
				enemyAbsoluteBearing, enemyDistance);
		wave.lateralDirection = lateralDirection;
		wave.bulletPower = BULLET_POWER;
		wave.setSegmentations(enemyDistance, enemyVelocity, lastEnemyVelocity);
		lastEnemyVelocity = enemyVelocity;
		wave.bearing = enemyAbsoluteBearing;
		setTurnGunRightRadians(Utils.normalRelativeAngle(enemyAbsoluteBearing
				- getGunHeadingRadians() + wave.mostVisitedBearingOffset()));
		if (getEnergy() >= BULLET_POWER) {
			setFire(wave.bulletPower);
			addCustomEvent(wave);
		}
		movement.onScannedRobot(event);
		setTurnRadarRightRadians(Utils.normalRelativeAngle(enemyAbsoluteBearing
				- getRadarHeadingRadians()) * 2);
	}

	/**
	 * onHitRobot: Move away a bit.
	 */
	public void onHitRobot(HitRobotEvent event) {
		// If he's in front of us, set back up a bit.
		if (event.getBearing() > -90 && event.getBearing() < 90) {
			setBack(100);
		} // else he's in back of us, so set ahead a bit.
		else {
			setAhead(100);
		}
	}

	public void onHitByBullet(HitByBulletEvent event) {
		movement.updateWaves(event.getBullet());
	}

	public void onBulletHitBullet(BulletHitBulletEvent event) {
		movement.updateWaves(event.getBullet());
	}

	/**
	 * onWin: Do a victory dance
	 */
	public void onWin(WinEvent e) {
		setTurnRadarRightRadians(Double.POSITIVE_INFINITY);
		setTurnGunLeftRadians(Double.POSITIVE_INFINITY);
		for (int i = 0; i < 50; i++) {
			turnRight(35);
			turnLeft(35);
		}
	}

}

class GFTWave extends Condition {
	public static Point2D.Double targetLocation;

	public double bulletPower;
	public Point2D.Double gunLocation;
	public double bearing;
	public double lateralDirection;

	private static final double MAX_DISTANCE = 1000;
	private static final int DISTANCE_INDEXES = 5;
	private static final int VELOCITY_INDEXES = 5;
	private static final int BINS = 25;
	private static final int MIDDLE_BIN = (BINS - 1) / 2;
	private static final double MAX_ESCAPE_ANGLE = 0.7;
	private static final double BIN_WIDTH = MAX_ESCAPE_ANGLE
			/ (double) MIDDLE_BIN;

	private static int[][][][] statBuffers = new int[DISTANCE_INDEXES][VELOCITY_INDEXES][VELOCITY_INDEXES][BINS];

	private int[] buffer;
	private AdvancedRobot robot;
	private double distanceTraveled;

	GFTWave(AdvancedRobot robot) {
		this.robot = robot;
	}

	public boolean test() {
		advance();
		if (hasArrived()) {
			buffer[currentBin()]++;
			robot.removeCustomEvent(this);
		}
		return false;
	}

	double mostVisitedBearingOffset() {
		return (lateralDirection * BIN_WIDTH) * (mostVisitedBin() - MIDDLE_BIN);
	}

	void setSegmentations(double distance, double velocity, double lastVelocity) {
		int distanceIndex = (int) (distance / (MAX_DISTANCE / DISTANCE_INDEXES));
		int velocityIndex = (int) Math.abs(velocity / 2);
		int lastVelocityIndex = (int) Math.abs(lastVelocity / 2);
		buffer = statBuffers[distanceIndex][velocityIndex][lastVelocityIndex];
	}

	private void advance() {
		distanceTraveled += GFTUtils.bulletVelocity(bulletPower);
	}

	private boolean hasArrived() {
		return distanceTraveled > gunLocation.distance(targetLocation) - 18;
	}

	private int currentBin() {
		int bin = (int) Math
				.round(((Utils.normalRelativeAngle(GFTUtils.absoluteBearing(
						gunLocation, targetLocation) - bearing)) / (lateralDirection * BIN_WIDTH))
						+ MIDDLE_BIN);
		return GFTUtils.minMax(bin, 0, BINS - 1);
	}

	private int mostVisitedBin() {
		int mostVisited = MIDDLE_BIN;
		for (int i = 0; i < BINS; i++) {
			if (buffer[i] > buffer[mostVisited]) {
				mostVisited = i;
			}
		}
		return mostVisited;
	}
}

class GFTUtils {
	public static double bulletVelocity(double power) {
		return 20 - 3 * power;
	}

	public static Point2D.Double project(Point2D.Double sourceLocation,
			double angle, double length) {
		return new Point2D.Double(sourceLocation.x + Math.sin(angle) * length,
				sourceLocation.y + Math.cos(angle) * length);
	}

	public static double absoluteBearing(Point2D.Double source,
			Point2D.Double target) {
		return Math.atan2(target.x - source.x, target.y - source.y);
	}

	public static int sign(double v) {
		return v < 0 ? -1 : 1;
	}

	public static int minMax(int v, int min, int max) {
		return Math.max(min, Math.min(max, v));
	}

	public static double limit(double min, double value, double max) {
		return Math.max(min, Math.min(value, max));
	}

	public static double maxEscapeAngle(double velocity) {
		return Math.asin(8.0 / velocity);
	}
}

class WSMovement {
	private static final double BATTLE_FIELD_WIDTH = 800;
	private static final double BATTLE_FIELD_HEIGHT = 600;
	private static final double WALL_MARGIN = 18;
	private static final double WALL_STICK = 160;
	private static final int BINS = 47;
	private static final double LESS_THAN_HALF_PI = 1.25;    // Math.PI/2 would be
	// perpendicular movement, less will keep us moving away slightly

	private static double oppEnergy = 100.0;

	private static double surfStats[] = new double[BINS];

	private AdvancedRobot robot;

	private List<EnemyWave> enemyWaves = new ArrayList<>();
	private List<Integer> surfDirections = new ArrayList<>();
	private List<Double> surfAbsBearings = new ArrayList<>();

	/**
	 * This is a rectangle that represents an 800x600 battle field, used for a
	 * simple, iterative WallSmoothing method (by PEZ). If you're not familiar
	 * with WallSmoothing, the wall stick indicates the amount of space we try
	 * to always have on either end of the tank (extending straight out the
	 * front or back) before touching a wall.
	 */
	private static Rectangle2D.Double fieldRectangle = new Rectangle2D.Double(
			WALL_MARGIN, WALL_MARGIN, BATTLE_FIELD_WIDTH - WALL_MARGIN * 2,
			BATTLE_FIELD_HEIGHT - WALL_MARGIN * 2);

	public Point2D.Double myLocation; // our bot's location
	public Point2D.Double enemyLocation; // enemy bot's location

	WSMovement(AdvancedRobot robot) {
		this.robot = robot;
	}

	public void onScannedRobot(ScannedRobotEvent event) {
		myLocation = new Point2D.Double(robot.getX(), robot.getY());

		double lateralVelocity = robot.getVelocity()
				* Math.sin(event.getBearingRadians());
		double absBearing = event.getBearingRadians()
				+ robot.getHeadingRadians();

		robot.setTurnRadarRightRadians(Utils.normalRelativeAngle(absBearing
				- robot.getRadarHeadingRadians()) * 2);

		surfDirections.add(0, new Integer((lateralVelocity >= 0) ? 1 : -1));
		surfAbsBearings.add(0, new Double(absBearing + Math.PI));

		double bulletPower = oppEnergy - event.getEnergy();
		if (bulletPower < 3.01 && bulletPower > 0.09
				&& surfDirections.size() > 2) {
			EnemyWave enemyWave = new EnemyWave();
			enemyWave.fireTime = robot.getTime() - 1;
			enemyWave.bulletVelocity = GFTUtils.bulletVelocity(bulletPower);
			enemyWave.distanceTraveled = GFTUtils.bulletVelocity(bulletPower);
			enemyWave.direction = surfDirections.get(2).intValue();
			enemyWave.directAngle = surfAbsBearings.get(2).doubleValue();
			enemyWave.fireLocation = (Point2D.Double) enemyLocation.clone(); // last
			// tick
			enemyWaves.add(enemyWave);
		}

		oppEnergy = event.getEnergy();

		// update after EnemyWave detection, because that needs the previous
		// enemy location as the source of the wave
		enemyLocation = GFTUtils.project(myLocation, absBearing,
				event.getDistance());

		updateWaves();
		doSurfing();
	}

	public void updateWaves() {
		for (int x = 0; x < enemyWaves.size(); x++) {
			EnemyWave enemyWave = (EnemyWave) enemyWaves.get(x);

			enemyWave.distanceTraveled = (robot.getTime() - enemyWave.fireTime)
					* enemyWave.bulletVelocity;
			if (enemyWave.distanceTraveled > myLocation.distance(enemyWave.fireLocation) + 50) {
				enemyWaves.remove(x);
				x--;
			}
		}
	}

	public void updateWaves(Bullet bullet) {
		// If the _enemyWaves collection is empty, we must have missed the
		// detection of this wave somehow.
		if (!enemyWaves.isEmpty()) {
			Point2D.Double hitBulletLocation = new Point2D.Double(
					bullet.getX(), bullet.getY());
			EnemyWave hitWave = null;

			// look through the EnemyWaves, and find one that could've hit us.
			for (int x = 0; x < enemyWaves.size(); x++) {
				EnemyWave ew = (EnemyWave) enemyWaves.get(x);

				if (Math.abs(ew.distanceTraveled
						- myLocation.distance(ew.fireLocation)) < 50
						&& Math.abs(GFTUtils.bulletVelocity(bullet.getPower())
						- ew.bulletVelocity) < 0.001) {
					hitWave = ew;
					break;
				}
			}

			if (hitWave != null) {
				logHit(hitWave, hitBulletLocation);
				// We can remove this wave now.
				enemyWaves.remove(enemyWaves.lastIndexOf(hitWave));
			}
		}
	}

	public void doSurfing() {
		//TODO:  In choosing the wave to surf, adjust it to surf the one that will hit you first, instead of the closest one. 
		EnemyWave surfWave = getClosestSurfableWave();

		if (surfWave == null) {
			return;
		}

		double dangerLeft = checkDanger(surfWave, -1);
		double dangerRight = checkDanger(surfWave, 1);

		double goAngle = GFTUtils.absoluteBearing(surfWave.fireLocation,
				myLocation);
		if (dangerLeft < dangerRight) {
			goAngle = wallSmoothing(myLocation, goAngle - (LESS_THAN_HALF_PI), -1);
		} else {
			goAngle = wallSmoothing(myLocation, goAngle + (LESS_THAN_HALF_PI), 1);
		}

		setBackAsFront(robot, goAngle);
	}

	private EnemyWave getNextWave() {
		EnemyWave wave = null;
		for (int i = 0; i < enemyWaves.size(); i++) {
			EnemyWave ew = (EnemyWave) enemyWaves.get(i);
		}
		return wave;
	}

	private EnemyWave getClosestSurfableWave() {
		double closestDistance = 50000; // I use some very big number here
		EnemyWave surfWave = null;

		for (int x = 0; x < enemyWaves.size(); x++) {
			EnemyWave ew = (EnemyWave) enemyWaves.get(x);
			double distance = myLocation.distance(ew.fireLocation)
					- ew.distanceTraveled;

			if (distance > ew.bulletVelocity && distance < closestDistance) {
				surfWave = ew;
				closestDistance = distance;
			}
		}

		return surfWave;
	}

	private double checkDanger(EnemyWave surfWave, int direction) {
		int index = getFactorIndex(surfWave,
				predictPosition(surfWave, direction));

		return surfStats[index];
	}

	private static void setBackAsFront(AdvancedRobot robot, double goAngle) {
		double angle = Utils.normalRelativeAngle(goAngle
				- robot.getHeadingRadians());
		if (Math.abs(angle) > (Math.PI / 2)) {
			if (angle < 0) {
				robot.setTurnRightRadians(Math.PI + angle);
			} else {
				robot.setTurnLeftRadians(Math.PI - angle);
			}
			robot.setBack(100);
		} else {
			if (angle < 0) {
				robot.setTurnLeftRadians(-1 * angle);
			} else {
				robot.setTurnRightRadians(angle);
			}
			robot.setAhead(100);
		}
	}

	private double wallSmoothing(Point2D.Double botLocation, double angle,
			int orientation) {
		while (!fieldRectangle.contains(GFTUtils.project(botLocation, angle,
				WALL_STICK))) {
			angle += orientation * 0.05;
		}
		return angle;
	}

	private Point2D.Double predictPosition(EnemyWave surfWave, int direction) {
		Point2D.Double predictedPosition = (Point2D.Double) myLocation.clone();
		double predictedVelocity = robot.getVelocity();
		double predictedHeading = robot.getHeadingRadians();
		double maxTurning, moveAngle, moveDir;

		int counter = 0; // number of ticks in the future
		boolean intercepted = false;

		while (!intercepted && counter < 500) { // the rest of these code
			// comments are rozu's
			moveAngle = wallSmoothing(
					predictedPosition,
					GFTUtils.absoluteBearing(surfWave.fireLocation,
							predictedPosition) + (direction * (Math.PI / 2)),
					direction)
					- predictedHeading;
			moveDir = 1;

			if (Math.cos(moveAngle) < 0) {
				moveAngle += Math.PI;
				moveDir = -1;
			}

			moveAngle = Utils.normalRelativeAngle(moveAngle);

			// maxTurning is built in like this, you can't turn more then this
			// in one tick
			maxTurning = Math.PI / 720d
					* (40d - 3d * Math.abs(predictedVelocity));
			predictedHeading = Utils.normalRelativeAngle(predictedHeading
					+ GFTUtils.limit(-maxTurning, moveAngle, maxTurning));

			// If predictedVelocity and moveDir have
			// different signs you want to break down
			// otherwise you want to accelerate (look at the factor "2")
			predictedVelocity += (predictedVelocity * moveDir < 0 ? 2 * moveDir
					: moveDir);
			predictedVelocity = GFTUtils.limit(-8, predictedVelocity, 8);

			// calculate the new predicted position
			predictedPosition = GFTUtils.project(predictedPosition,
					predictedHeading, predictedVelocity);

			counter++;

			if (predictedPosition.distance(surfWave.fireLocation) < surfWave.distanceTraveled
					+ (counter * surfWave.bulletVelocity)
					+ surfWave.bulletVelocity) {
				intercepted = true;
			}
		}

		return predictedPosition;
	}

	// Given the EnemyWave that the bullet was on, and the point where we
	// were hit, calculate the index into our stat array for that factor.
	private static int getFactorIndex(EnemyWave ew, Point2D.Double targetLocation) {
		double offsetAngle = (GFTUtils.absoluteBearing(ew.fireLocation,
				targetLocation) - ew.directAngle);
		double factor = Utils.normalRelativeAngle(offsetAngle)
				/ GFTUtils.maxEscapeAngle(ew.bulletVelocity) * ew.direction;

		return (int) GFTUtils.limit(0, (factor * ((BINS - 1) / 2))
				+ ((BINS - 1) / 2), BINS - 1);
	}

	// Given the EnemyWave that the bullet was on, and the point where we
	// were hit, update our stat array to reflect the danger in that area.
	private void logHit(EnemyWave ew, Point2D.Double targetLocation) {
		int index = getFactorIndex(ew, targetLocation);

		for (int x = 0; x < BINS; x++) {
			// for the spot bin that we were hit on, add 1;
			// for the bins next to it, add 1 / 2;
			// the next one, add 1 / 5; and so on...
			surfStats[x] += 1.0 / (Math.pow(index - x, 2) + 1);
		}
	}
}

class EnemyWave {
	public Point2D.Double fireLocation;
	public long fireTime;
	public double bulletVelocity;
	public double directAngle;
	public double distanceTraveled;
	public int direction;
}
