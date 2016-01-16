package aftergames.engine.utils.geom;

/**
 *
 * @author KiQDominaN
 */
public class Rect extends Shape {

    public Rect() {
        points = new Point[4];
    }

    public Rect(float x, float y, float w, float h) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;

        points = new Point[4];

        addPoint(x, y);
        addPoint(x + w, y);
        addPoint(x + w, y + h);
        addPoint(x, y + h);
    }

    public void reset() {
        points[0].x = x;
        points[0].y = y;

        points[1].x = x + w;
        points[1].y = y;

        points[2].x = x + w;
        points[2].y = y + h;

        points[3].x = x;
        points[3].y = y + h;
    }
}
