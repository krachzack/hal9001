package Westminster.Team5.marccarre.capabilities.radar;

import robocode.AdvancedRobot;

public class OscillatingRadar implements Radar {
	private final AdvancedRobot robot;
	private int scanDirection = 1;

	public OscillatingRadar(final AdvancedRobot robot) {
		this.robot = robot;
	}

	public void scan() {
		robot.setTurnRadarRight(360 * scanDirection);
		scanDirection *= -1; // Change direction.
	}
}
