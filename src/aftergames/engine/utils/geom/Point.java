package aftergames.engine.utils.geom;

/**
 *
 * @author KiQDominaN
 */
public class Point {

    public float x, y;

    public Point() {
        x = y = 0;
    }

    public Point(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public Point(Point point) {
        this(point.x, point.y);
    }
}
