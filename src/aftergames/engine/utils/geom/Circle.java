package aftergames.engine.utils.geom;

import aftergames.engine.utils.MathUtils;
import aftergames.engine.utils.MathUtils;

/**
 *
 * @author KiQDominaN
 */
public class Circle extends Shape {

    private final int radius;
    private final float delta;

    public Circle(float centerx, float centery, int radius, int precize) {
        this.x = centerx;
        this.y = centery;
        this.radius = radius;
        delta = 360f / precize;

        points = new Point[precize];

        double angle = 0;
        for (int i = 0; i < precize; i++) {
            points[i] = new Point(x + radius * MathUtils.cos(angle), y - radius * MathUtils.sin(angle));

            angle += delta;
        }
    }

    public void reset() {
        double angle = 0;
        for (Point p : points) {
            p.x = x + radius * MathUtils.cos(angle);
            p.y = y - radius * MathUtils.sin(angle);

            angle += delta;
        }
    }
}
