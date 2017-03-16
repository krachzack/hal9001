package im500.main;

import robocode.Robot;

public class HAL9001 extends Robot {
	@Override
	public void run() {
		while (true) {
			turnGunLeft(5.0);
			fire(0);
		}
	}
}
