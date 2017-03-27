package Westminster.Team5.marccarre;

import java.awt.geom.Point2D;

public class Calculations {
    public static final double TOLERANCE_ON_DOUBLES = 0.0000001;

    private Calculations() {
        // Pure utility class, do NOT instantiate.
    }

    /**
     * Calculate bullet's velocity.
     * See also: http://robowiki.net/wiki/Robocode/FAQ#Game_physics
     *
     * @param power power of the bullet for which we want to calculate velocity.
     * @return bullet's velocity.
     */
    public static double bulletVelocity(final double power) {
        return 20 - 3 * power;
    }

    /**
     * Calculates absolute angle/bearing between reference and target point, assuming North is 0°.
     *
     * @param x1 the X coordinate of the reference point
     * @param y1 the Y coordinate of the reference point
     * @param x2 the X coordinate of the target point
     * @param y2 the Y coordinate of the target point
     * @return corresponding absolute angle/bearing.
     */
    public static double absoluteBearing(final double x1, final double y1, final double x2, final double y2) {
        final double dx = x2 - x1;
        final double dy = y2 - y1;

        // Given North is 0°, point #2 is...
        if (dx == 0) {
            if (dy > 0) { // ... North of point #1.
                return 0;
            } else if (dy < 0) { // ... South of point #1
                return 180;
            } else { // point #1 == point #2.
                return 0;
            }
        } else if (dy == 0) {
            if (dx > 0) { // ... East of point #1.
                return 90;
            } else if (dx < 0) { // ... West of point #1.
                return 270;
            }
        }

        final double hypotenuse = Point2D.distance(x1, y1, x2, y2);
        final double angle = Math.toDegrees(Math.asin(dx / hypotenuse)); // WARNING: angle can be < 0 if dx < 0.

        // Given North is 0°, point #2 is...
        if ((dx > 0) && (dy > 0)) { // ... North-East of point #1.
            return angle;
        } else if ((dx > 0) && (dy < 0)) { // ... South-East of point #1.
            return 180 - angle;
        } else if ((dx < 0) && (dy < 0)) { // ... South-West of point #1.
            return 180 - angle; // dx < 0 => angle < 0, so to get 180 + angle, we subtract angle.
        } else if ((dx < 0) && (dy > 0)) { // ... North-West of point #1.
            return 360 + angle; // dx < 0 => angle < 0, so to get 360 - angle, we subtract angle.
        } else {
            throw new IllegalStateException();
        }
    }

    /**
     * Non-normalized bearings could be greater than 180 or less than -180, leading to non-efficient moves.
     * This utility function helps to normalize them.
     *
     * @param angle non-normalized bearing.
     * @return corresponding normalized bearing.
     */
    public static double normalizeBearing(double angle) {
        while (angle > 180) {
            angle -= 360;
        }
        while (angle < -180) {
            angle += 360;
        }
        return angle;
    }
}
