package Westminster.Team5.marccarre.capabilities.radar;

import robocode.AdvancedRobot;

import java.util.Random;

public class RandomlyOscillatingRadar implements Radar {
	private static final Random RANDOM = new Random();
	private final AdvancedRobot robot;
	private int scanDirection = 1;

	public RandomlyOscillatingRadar(final AdvancedRobot robot) {
		this.robot = robot;
	}

	public void scan() {
		robot.setTurnRadarRight(360 * scanDirection);
		scanDirection *= RANDOM.nextBoolean() ? 1 : -1; // Randomly change direction.
	}
}
