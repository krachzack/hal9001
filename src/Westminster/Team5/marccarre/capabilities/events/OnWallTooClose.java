package Westminster.Team5.marccarre.capabilities.events;

import robocode.AdvancedRobot;
import robocode.Condition;

public class OnWallTooClose extends Condition {
    public static final String EVENT_NAME = OnWallTooClose.class.getSimpleName();

    private final AdvancedRobot robot;
    private final double wallMargin;

    public OnWallTooClose(final AdvancedRobot robot, final double wallMargin) {
        super(EVENT_NAME);
        this.robot = robot;
        this.wallMargin = wallMargin;
    }

    @Override
    public boolean test() {
        return (leftWallTooClose() || rightWallTooClose() || topWallTooClose() || bottomWallTooClose());
    }

    private boolean leftWallTooClose() {
        return (robot.getX() <= wallMargin);
    }

    private boolean rightWallTooClose() {
        return (robot.getX() >= robot.getBattleFieldWidth() - wallMargin);
    }

    private boolean topWallTooClose() {
        return (robot.getY() >= robot.getBattleFieldHeight() - wallMargin);
    }

    private boolean bottomWallTooClose() {
        return (robot.getY() <= wallMargin);
    }
}
