package Westminster.Team5.marccarre.capabilities;

import Westminster.Team5.marccarre.State;
import robocode.AdvancedRobot;
import robocode.Rules;

import java.util.Iterator;
import java.util.Random;

public class Dodger {
    private static final Random RANDOM = new Random();

    private final AdvancedRobot robot;
    private final EnemiesTracker tracker;

    public Dodger(final AdvancedRobot robot, final EnemiesTracker tracker) {
        this.robot = robot;
        this.tracker = tracker;
    }

    /**
     * Call only after having logged the event with {@link EnemiesTracker}.
     */
    public void dodgeIfHasFired() {
        if (hasFired()) {
            dodge();
        }
    }

    private boolean hasFired() {
        final Iterator<State> stateIterator = tracker.statesForLatest().descendingIterator();
        final State last = stateIterator.next();
        final State beforeLast = stateIterator.next();
        final double changeInEnergy = beforeLast.energy() - last.energy();
        return (changeInEnergy >= Rules.MIN_BULLET_POWER) && (changeInEnergy <= Rules.MAX_BULLET_POWER);
    }

    private void dodge() {
        robot.setAhead((tracker.latest().distance() / 4 + 25) * randomDirection());
    }

    private int randomDirection() {
        return RANDOM.nextBoolean() ? 1 : -1;
    }
}
