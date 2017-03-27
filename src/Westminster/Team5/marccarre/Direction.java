package Westminster.Team5.marccarre;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public enum Direction {
    STRAIGHT(0), LEFT(1), RIGHT(2);

    private static final Map<Integer, Direction> DIRECTIONS = new HashMap<Integer, Direction>() {{
        put(0, STRAIGHT);
        put(1, LEFT);
        put(2, RIGHT);
    }};

    private final int direction;

    Direction(final int direction) {
        this.direction = direction;
    }

    public int toInt() {
        return direction;
    }

    public static Direction randomize(final Random random) {
        return DIRECTIONS.get(random.nextInt(DIRECTIONS.size()));
    }
}
