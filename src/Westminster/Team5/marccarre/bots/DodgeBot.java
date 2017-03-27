package Westminster.Team5.marccarre.bots;

import Westminster.Team5.marccarre.capabilities.Dodger;
import Westminster.Team5.marccarre.capabilities.EnemiesTracker;
import robocode.AdvancedRobot;
import robocode.ScannedRobotEvent;

public class DodgeBot extends AdvancedRobot {

    private static final int MOVE_CLOSER_TO_ENEMY = 30; // 0 = neutral, + = move closer, - = move away.

    private final EnemiesTracker enemiesTracker = new EnemiesTracker();
    private final Dodger dodger = new Dodger(this, enemiesTracker);

    public void run() {
        setTurnGunRight(99999);
    }

    public void onScannedRobot(ScannedRobotEvent e) {
        setTurnRight(e.getBearing() + 90 - MOVE_CLOSER_TO_ENEMY);

        enemiesTracker.log(e);
        dodger.dodgeIfHasFired();

        setTurnGunRight(99999);
    }
}
