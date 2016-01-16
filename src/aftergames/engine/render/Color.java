package aftergames.engine.render;

/**
 *
 * @author KiQDominaN
 */
public final class Color {

    public static final Color transparent = new Color(0, 0, 0, 0);
    public static final Color black = new Color(0, 0, 0, 1);
    public static final Color white = new Color(1, 1, 1, 1);
    public static final Color gray = new Color(0.5f, 0.5f, 0.5f, 1f);
    public static final Color dark_gray = new Color(0.2f, 0.2f, 0.2f, 1f);
    public static final Color red = new Color(1, 0, 0, 1);
    public static final Color green = new Color(0, 1, 0, 1);
    public static final Color blue = new Color(0, 0, 1, 1);
    public static final Color cyan = new Color(0, 0.5f, 1, 1);
    public static final Color yellow = new Color(1, 1, 0, 1);
    public static final Color orange = new Color(1, 0.5f, 0, 1);
    public static final Color violet = new Color(1, 0, 1, 1);
    public static final Color brown = new Color(0.5f, 0.25f, 0, 1);

    public float r, g, b, a;

    public Color(float r, float g, float b, float a) {
        this.r = Math.min(1, r);
        this.g = Math.min(1, g);
        this.b = Math.min(1, b);
        this.a = Math.min(1, a);
    }

    public Color(int rgba) {
        r = Math.min(1, (0xff & (rgba >> 16)) / 255f);
        g = Math.min(1, (0xff & (rgba >> 8)) / 255f);
        b = Math.min(1, (0xff & rgba) / 255f);
        a = Math.min(1, (0xff & (rgba >> 24)) / 255f);
    }

    public void set(int rgba) {
        r = Math.min(1, (0xff & (rgba >> 16)) / 255f);
        g = Math.min(1, (0xff & (rgba >> 8)) / 255f);
        b = Math.min(1, (0xff & rgba) / 255f);
        a = Math.min(1, (0xff & (rgba >> 24)) / 255f);
    }

    public int get() {
        return ((int) (r * 255) << 24 | (int) (g * 255) << 16 | (int) (b * 255) << 8 | (int) a * 255);
    }

    public Color mix(Color c) {
        return mix(c, 0.5f);
    }

    public Color mix(Color c, float percents) {
        return new Color(r * (1f - percents) + c.r * percents, g * (1f - percents) + c.g * percents, b * (1f - percents) + c.b * percents, a * (1f - percents) + c.a * percents);
    }

    public java.awt.Color getAWTColor() {
        return new java.awt.Color(r, g, b);
    }

    public Color invert() {
        return new Color(1f - r, 1f - g, 1f - b, 1f - a);
    }

}
