package im500.main;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Arrays;

import robocode.AdvancedRobot;
import robocode.HitRobotEvent;
import robocode.ScannedRobotEvent;

public class HAL9001 extends AdvancedRobot {
	private static int CLOSE_QUARTERS_DISTANCE = 90;
	private Scanner scanner = new Scanner(this);
	private Tracker tracker = new Tracker(this);
	private PatternMatcher matcher = new PatternMatcher();

	private void initialize() {
		setColors(Color.BLACK, Color.RED, Color.YELLOW, Color.PINK, Color.BLACK);
		setAdjustGunForRobotTurn(true);
		setAdjustRadarForGunTurn(true);
		setAdjustRadarForRobotTurn(true);
		matcher.setRobot(this);
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
		tracker.onScannedRobot(event);
		scanner.onScanned(event);
		matcher.onScanned(event);
		
		double firepower = 0.1;
		if (event.getDistance() < CLOSE_QUARTERS_DISTANCE * 4) firepower = 1;
		if (event.getDistance() < CLOSE_QUARTERS_DISTANCE * 2) firepower = 2;
		if (event.getDistance() < CLOSE_QUARTERS_DISTANCE) firepower = 3;
		
		double bulletVelocity = 20 - 3 * firepower;
		double ticksUntilHit = event.getDistance() / bulletVelocity;
		System.out.println("Ticks: " + ticksUntilHit);
		
		double[] ememyPosFutureDumb = new double[] { tracker.getProjectedX(ticksUntilHit), tracker.getProjectedY(ticksUntilHit) };
		double[] enemyPosFutureSmart = matcher.getFuturePosition(
			(int) Math.round(ticksUntilHit),
			new double[] { tracker.getProjectedX(0), tracker.getProjectedY(0) },
			event.getHeadingRadians()
		);
		
		if(enemyPosFutureSmart != null){
			double oldTicksUntilHit = ticksUntilHit;
			
			ticksUntilHit = Math.sqrt(Math.pow(enemyPosFutureSmart[0]-getX(), 2) + Math.pow(enemyPosFutureSmart[1]-getY(), 2)) / bulletVelocity;
			
			System.out.println("prediction error: "+ (ticksUntilHit - oldTicksUntilHit));
			
			enemyPosFutureSmart = matcher.getFuturePosition(
					(int) Math.round(ticksUntilHit),
					new double[] { tracker.getProjectedX(0), tracker.getProjectedY(0) },
					event.getHeadingRadians()
				);
		}
		
		double[] enemyPosFuture = (enemyPosFutureSmart == null) ? ememyPosFutureDumb : enemyPosFutureSmart;
		
		Move.aim(this, enemyPosFuture[0], enemyPosFuture[1]);
		do {
			execute();
		} while(getGunTurnRemaining() > 1);
		
		getGraphics().setColor(Color.GREEN);
		getGraphics().drawLine((int)getX(), (int)getY(), (int)ememyPosFutureDumb[0], (int)ememyPosFutureDumb[1]);
		
		if(enemyPosFutureSmart != null) {
			getGraphics().setColor(Color.RED);
			getGraphics().drawLine((int)getX(), (int)getY(), (int)enemyPosFutureSmart[0], (int)enemyPosFutureSmart[1]);
		}
		
		/*setTurnRight(event.getBearing());
		setTurnGunRight((getHeading() - getGunHeading() + event.getBearing()) % 360);
		execute();
		
		//Maximum firepower is 3 (sadly...)
		double firepower = 0.1;
		if (event.getDistance() < CLOSE_QUARTERS_DISTANCE * 4) firepower = 1;
		if (event.getDistance() < CLOSE_QUARTERS_DISTANCE * 2) firepower = 2;
		if (event.getDistance() < CLOSE_QUARTERS_DISTANCE) firepower = 3;
		if (getEnergy() > 5) setFire(firepower);
		setAhead(event.getDistance() + 5);*/
		do {
			execute();
		} while(getGunTurnRemaining() > 1);
		
		
		if (getEnergy() > 5) setFire(firepower);
		execute();
		
		Move.towards(this, tracker.getProjectedX(0), tracker.getProjectedY(0));
		execute();
	}
	
	@Override
	public void onHitRobot(HitRobotEvent event) {
		/*setAhead(-2);
		execute();*/
	}
}
 