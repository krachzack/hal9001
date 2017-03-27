package Westminster.Team5;

import Westminster.Team5.marccarre.Calculations;
import Westminster.Team5.marccarre.State;
import Westminster.Team5.marccarre.capabilities.Dodger;
import Westminster.Team5.marccarre.capabilities.EnemiesTracker;
import Westminster.Team5.marccarre.capabilities.events.OnWallTooClose;
import Westminster.Team5.marccarre.capabilities.radar.ContinuousRadar;
import Westminster.Team5.marccarre.capabilities.radar.Radar;
import robocode.AdvancedRobot;
import robocode.CustomEvent;
import robocode.HitWallEvent;
import robocode.RobotDeathEvent;
import robocode.ScannedRobotEvent;
import robocode.WinEvent;

import java.awt.*;

import static robocode.Rules.MAX_BULLET_POWER;

public class MarcCarre extends AdvancedRobot {

    private static final int MOVE_CLOSER_TO_ENEMY = 30; // 0 = neutral, + = move closer, - = move away.

    private final EnemiesTracker enemiesTracker = new EnemiesTracker();
    private final Dodger dodger = new Dodger(this, enemiesTracker);
    private final Radar radar = new ContinuousRadar(this);

    private State targettedEnemy;

    public void onScannedRobot(final ScannedRobotEvent e) {
        final State state = enemiesTracker.log(e);
        if ((targettedEnemy == null) || (targettedEnemy.name().equals(e.getName())) || (state.equals(enemiesTracker.closest()))) {
            targettedEnemy = state;

            setTurnRight(targettedEnemy.bearing() + 90 - MOVE_CLOSER_TO_ENEMY);
            setTurnGunTowardsTarget(targettedEnemy.bearing());
        }

        dodger.dodgeIfHasFired();
    }

    public void onHitWall(final HitWallEvent e) {
        setBack(100);
        setTurnRight(e.getBearing() + 90);
        execute();
    }

    public void onRobotDeath(final RobotDeathEvent e) {
        if ((targettedEnemy != null) && (targettedEnemy.name().equals(e.getName()))) {
            targettedEnemy = null;
        }
    }

    public void onWin(final WinEvent e) {
        dance();
    }

    public void onCustomEvent(final CustomEvent e) {
        if (e.getCondition().getName().equals(OnWallTooClose.EVENT_NAME)) {
            setBack(100);
        }
    }

    public void run() {
        setColors(Color.BLACK, Color.CYAN, Color.BLACK, Color.CYAN, Color.CYAN);

        // Independent radar movement:
        setAdjustRadarForGunTurn(true);
        setAdjustRadarForRobotTurn(true);
        // Independent gun movement:
        setAdjustGunForRobotTurn(true);

        addCustomEvent(new OnWallTooClose(this, 60));
        turnGunLeft(90); // Set gun at 90° to better dodge.

        while (true) {
            radar.scan();
            move();
            fire();
            execute(); // Execute queued-up actions.
        }
    }

    private void move() {
        if (targettedEnemy == null) {
            return;
        }

        // Move a little closer...
        if (targettedEnemy.distance() > 200) {
            setAhead(targettedEnemy.distance() / 2);
        }
        // ...but not too close.
        if (targettedEnemy.distance() < 100) {
            setBack(targettedEnemy.distance());
        }
    }

    private void fire() {
        if (canFire()) {
            setFire(Math.min(400 / targettedEnemy.distance(), MAX_BULLET_POWER));
        }
    }

    private boolean canFire() {
        return (targettedEnemy != null) && (getGunHeat() == 0) && isAlmostFacingEnemy();
    }

    private boolean isAlmostFacingEnemy() {
        return Math.abs(getGunTurnRemaining()) < 10;
    }

    private void dance() {
        final int angle = 30;
        for (int i = 0; i < 5; ++i) {
            setTurnLeft(angle);
            setTurnGunRight(angle);
            setTurnRadarLeft(angle);

            setTurnRight(angle);
            setTurnGunLeft(angle);
            setTurnRadarRight(angle);
        }
        execute();
    }

    private void setTurnRadarTowardsTarget(final double bearing) {
        setTurnRadarRight(Calculations.normalizeBearing(getHeading() - getRadarHeading() + bearing));
    }

    private void setTurnGunTowardsTarget(final double bearing) {
        setTurnGunRight(Calculations.normalizeBearing(getHeading() - getGunHeading() + bearing));
    }
}
