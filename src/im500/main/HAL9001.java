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
		tracker.onScannedRobot(event);
		scanner.onScanned(event);
		
		Move.towards(this, tracker.getProjectedX(0), tracker.getProjectedY(0));
	
		
		double firepower = 0.1;
		if (event.getDistance() < CLOSE_QUARTERS_DISTANCE * 4) firepower = 1;
		if (event.getDistance() < CLOSE_QUARTERS_DISTANCE * 2) firepower = 2;
		if (event.getDistance() < CLOSE_QUARTERS_DISTANCE) firepower = 3;
		if (getEnergy() > 5) setFire(firepower);
		
		
		System.out.println(firepower);
		
		
		double bulletVelocity = 20 - 3 * firepower;
		double ticksUntilHit = event.getDistance() / bulletVelocity;
		System.out.println("Ticks: " + ticksUntilHit);
				
		double overExtapolation = 1.0;
		Move.aim(this, tracker.getProjectedX(ticksUntilHit*overExtapolation), tracker.getProjectedY(ticksUntilHit*overExtapolation));
		getGraphics().setColor(Color.GREEN);
		getGraphics().drawLine((int)getX(), (int)getY(), (int)tracker.getProjectedX(0), (int)tracker.getProjectedY(0));
		getGraphics().setColor(Color.RED);
		getGraphics().drawLine((int)getX(), (int)getY(), (int)tracker.getProjectedX(ticksUntilHit*overExtapolation), (int)tracker.getProjectedY(ticksUntilHit*overExtapolation));
		
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
		execute();
	}
	
	@Override
	public void onHitRobot(HitRobotEvent event) {
		/*setAhead(-2);
		execute();*/
	}
}
 