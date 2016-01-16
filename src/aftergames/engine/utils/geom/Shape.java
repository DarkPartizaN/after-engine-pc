package aftergames.engine.utils.geom;

import aftergames.engine.utils.MathUtils;

/**
 *
 * @author KiQDominaN
 */
public abstract class Shape {

    public float x, y, w, h;
    protected Point[] points;
    private int point_count;

    public abstract void reset();

    public void addPoint(float x, float y) {
        if (points == null)
            return;

        points[point_count++] = new Point(x, y);
    }

    public Point[] getPoints() {
        return points;
    }

    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void setSize(float width, float height) {
        this.w = width;
        this.h = height;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getWidth() {
        return w;
    }

    public float getHeight() {
        return h;
    }

    public float getMinX() {
        float min_x = points[0].x;
        for (Point p : points) min_x = MathUtils.min(min_x, p.x);

        return min_x;
    }

    public float getMinY() {
        float min_y = points[0].y;
        for (Point p : points) min_y = MathUtils.min(min_y, p.y);

        return min_y;
    }

    public float getMaxX() {
        float max_x = points[0].x;
        for (Point p : points) max_x = MathUtils.max(max_x, p.x);

        return max_x;
    }

    public float getMaxY() {
        float max_y = points[0].y;
        for (Point p : points) max_y = MathUtils.max(max_y, p.y);

        return max_y;
    }

    public Point getSize() {
        float minx, maxx;
        float miny, maxy;

        minx = maxx = getMinX();
        miny = maxy = getMinY();

        for (Point p : points) {
            maxx = MathUtils.max(maxx, p.x);
            maxy = MathUtils.max(maxy, p.y);
        }

        return new Point(maxx - minx, maxy - miny);
    }

    // Checks if the two polygons are intersecting.
    public static final boolean intersects(Shape a, Shape b) {
        Vector2 normal = new Vector2();

        for (int i = 0; i < a.points.length; i++) {
            int j = (i + 1) % a.points.length;

            Point p1 = a.points[i];
            Point p2 = a.points[j];

            normal.x = p2.y - p1.y;
            normal.y = p1.x - p2.x;
            normal = normal.normalize();

            float minA = 0;
            float maxA = 0;

            for (Point p : a.points) {
                float projected = normal.x * p.x + normal.y * p.y;

                if (minA == 0 || projected < minA) minA = projected;
                if (maxA == 0 || projected > maxA) maxA = projected;
            }

            float minB = 0;
            float maxB = 0;

            for (Point p : b.points) {
                float projected = normal.x * p.x + normal.y * p.y;

                if (minB == 0 || projected < minB)
                    minB = projected;
                if (maxB == 0 || projected > maxB)
                    maxB = projected;
            }

            if (maxA < minB || maxB < minA)
                return false;
        }

        return true;
    }
}
