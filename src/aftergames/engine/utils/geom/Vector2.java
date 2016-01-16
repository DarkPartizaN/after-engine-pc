package aftergames.engine.utils.geom;

import aftergames.engine.utils.MathUtils;
import aftergames.engine.utils.MathUtils;

/**
 *
 * @author KiQDominaN
 */
public class Vector2 extends Point {

    public static final Vector2 ZERO_VECTOR = new Vector2();

    public Vector2() {
        x = y = 0;
    }

    public Vector2(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void reset() {
        x = y = 0;
    }

    public float length() {
        return MathUtils.sqrt((x * x) + (y * y));
    }

    public Vector2 normalize() {
        return new Vector2(x, y).div(length());
    }

    public Vector2 add(Vector2 v) {
        return new Vector2(x + v.x, y + v.y);
    }

    public Vector2 sub(Vector2 v) {
        return new Vector2(x - v.x, y - v.y);
    }

    public Vector2 mul(float f) {
        return new Vector2(x * f, y * f);
    }

    public Vector2 div(float f) {
        return new Vector2(x / f, y / f);
    }

    public float dot(Vector2 v) {
        return x * v.x + y * v.y;
    }

    public Vector2 invert() {
        return new Vector2(-x, -y);
    }

}