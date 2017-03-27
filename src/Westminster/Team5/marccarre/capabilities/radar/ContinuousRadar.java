package Westminster.Team5.marccarre.capabilities.radar;

import robocode.AdvancedRobot;

public class ContinuousRadar implements Radar {
	private final AdvancedRobot robot;

	public ContinuousRadar(final AdvancedRobot robot) {
		this.robot = robot;
	}

	public void scan() {
		robot.setTurnRadarRight(360);
	}
}
