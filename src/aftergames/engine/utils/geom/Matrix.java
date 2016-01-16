package aftergames.engine.utils.geom;

import aftergames.engine.utils.BufferUtils;
import aftergames.engine.utils.MathUtils;

import java.nio.FloatBuffer;

/**
 *
 * @author KiQDominaN
 */
public final class Matrix {

    public float[] m = new float[16];
    private FloatBuffer buff = BufferUtils.create_float_buffer(16);
    private static Matrix mat_tmp = new Matrix();

    public Matrix() {
        reset();
    }

    public void set(float[] m) {
        System.arraycopy(m, 0, this.m, 0, m.length);
    }

    public void set(Matrix m) {
        set(m.m);
    }

    public float[] get() {
        return m;
    }

    public FloatBuffer gl_mat() {
        buff.clear();
        buff.put(m);
        buff.flip();

        return buff;
    }

    public void reset() {
        m[0] = 1;
        m[1] = 0;
        m[2] = 0;
        m[3] = 0;

        m[4] = 0;
        m[5] = 1;
        m[6] = 0;
        m[7] = 0;

        m[8] = 0;
        m[9] = 0;
        m[10] = 1;
        m[11] = 0;

        m[12] = 0;
        m[13] = 0;
        m[14] = 0;
        m[15] = 1;
    }

    public Matrix get_translation() {
        return translate(m[12], m[13], m[14]);
    }

    public Matrix get_rotation() {
        mat_tmp.m[0] = m[0];
        mat_tmp.m[1] = m[1];
        mat_tmp.m[4] = m[4];
        mat_tmp.m[5] = m[5];

        return mat_tmp;
    }

    public static Matrix translate(float x, float y) {
        mat_tmp.reset();

        mat_tmp.m[12] = x;
        mat_tmp.m[13] = y;
        mat_tmp.m[14] = 0;

        return mat_tmp;
    }

    public static Matrix translate(float x, float y, float z) {
        mat_tmp.reset();

        mat_tmp.m[12] = x;
        mat_tmp.m[13] = y;
        mat_tmp.m[14] = z;

        return mat_tmp;
    }

    public static Matrix rotate(float angle) {
        mat_tmp.reset();

        float sin = MathUtils.sin(angle);
        float cos = MathUtils.cos(angle);

        mat_tmp.m[0] = cos;
        mat_tmp.m[1] = -sin;
        mat_tmp.m[4] = sin;
        mat_tmp.m[5] = cos;

        return mat_tmp;
    }

    public static Matrix rotate(float angle, int x, int y, int z) {
        mat_tmp.reset();

        float sin = MathUtils.sin(angle);
        float cos = MathUtils.cos(angle);

        if (x == 1) {
            mat_tmp.m[5] = cos;
            mat_tmp.m[6] = -sin;
            mat_tmp.m[9] = sin;
            mat_tmp.m[10] = cos;
        }
        if (y == 1) {
            mat_tmp.m[0] = cos;
            mat_tmp.m[2] = sin;
            mat_tmp.m[8] = -sin;
            mat_tmp.m[10] = cos;
        }
        if (z == 1) {
            mat_tmp.m[0] = cos;
            mat_tmp.m[1] = -sin;
            mat_tmp.m[4] = sin;
            mat_tmp.m[5] = cos;
        }

        return mat_tmp;
    }

    public static Matrix scale(float x, float y) {
        mat_tmp.reset();

        mat_tmp.m[0] = x;
        mat_tmp.m[5] = y;

        return mat_tmp;
    }

    public Matrix invert() {
        int i, j, k;
        int size = 4;
        float[] mass = new float[16];
        float[] tmp = new float[16];

        System.arraycopy(m, 0, mass, 0, m.length);

        for (i = 0; i < size; i++) {
            for (j = 0; j < size; j++)
                tmp[i + j * size] = 0;
            tmp[i + i * size] = 1;
        }

        float a, b;
        for (i = 0; i < size; i++) {
            a = mass[i + i * size];
            for (j = i + 1; j < size; j++) {
                b = mass[j + i * size];
                for (k = 0; k < size; k++) {
                    mass[j + k * size] = mass[i + k * size] * b - mass[j + k * size] * a;
                    tmp[j + k * size] = tmp[i + k * size] * b - tmp[j + k * size] * a;
                }
            }
        }

        float sum;
        for (i = 0; i < size; i++) {
            for (j = size - 1; j >= 0; j--) {
                sum = 0;
                for (k = size - 1; k > j; k--)
                    sum += mass[j + k * size] * tmp[k + i * size];

                if (mass[j + j * size] == 0) return null;

                tmp[j + i * size] = (tmp[j + i * size] - sum) / mass[j + j * size];
            }
        }

        mat_tmp.set(tmp);

        return mat_tmp;
    }

    private static float[] f = new float[16];

    public static Matrix mul(Matrix m1, Matrix m2) {
        f[0] = m1.m[0] * m2.m[0] + m1.m[1] * m2.m[4] + m1.m[2] * m2.m[8] + m1.m[3] * m2.m[12];
        f[1] = m1.m[0] * m2.m[1] + m1.m[1] * m2.m[5] + m1.m[2] * m2.m[9] + m1.m[3] * m2.m[13];
        f[2] = m1.m[0] * m2.m[2] + m1.m[1] * m2.m[6] + m1.m[2] * m2.m[10] + m1.m[3] * m2.m[14];
        f[3] = m1.m[0] * m2.m[3] + m1.m[1] * m2.m[7] + m1.m[2] * m2.m[11] + m1.m[3] * m2.m[15];

        f[4] = m1.m[4] * m2.m[0] + m1.m[5] * m2.m[4] + m1.m[6] * m2.m[8] + m1.m[7] * m2.m[12];
        f[5] = m1.m[4] * m2.m[1] + m1.m[5] * m2.m[5] + m1.m[6] * m2.m[9] + m1.m[7] * m2.m[13];
        f[6] = m1.m[4] * m2.m[2] + m1.m[5] * m2.m[6] + m1.m[6] * m2.m[10] + m1.m[7] * m2.m[14];
        f[7] = m1.m[4] * m2.m[3] + m1.m[5] * m2.m[7] + m1.m[6] * m2.m[11] + m1.m[7] * m2.m[15];

        f[8] = m1.m[8] * m2.m[0] + m1.m[9] * m2.m[4] + m1.m[10] * m2.m[8] + m1.m[11] * m2.m[12];
        f[9] = m1.m[8] * m2.m[1] + m1.m[9] * m2.m[5] + m1.m[10] * m2.m[9] + m1.m[11] * m2.m[13];
        f[10] = m1.m[8] * m2.m[2] + m1.m[9] * m2.m[6] + m1.m[10] * m2.m[10] + m1.m[11] * m2.m[14];
        f[11] = m1.m[8] * m2.m[3] + m1.m[9] * m2.m[7] + m1.m[10] * m2.m[11] + m1.m[11] * m2.m[15];

        f[12] = m1.m[12] * m2.m[0] + m1.m[13] * m2.m[4] + m1.m[14] * m2.m[8] + m1.m[15] * m2.m[12];
        f[13] = m1.m[12] * m2.m[1] + m1.m[13] * m2.m[5] + m1.m[14] * m2.m[9] + m1.m[15] * m2.m[13];
        f[14] = m1.m[12] * m2.m[2] + m1.m[13] * m2.m[6] + m1.m[14] * m2.m[10] + m1.m[15] * m2.m[14];
        f[15] = m1.m[12] * m2.m[3] + m1.m[13] * m2.m[7] + m1.m[14] * m2.m[11] + m1.m[15] * m2.m[15];

        mat_tmp.set(f);

        return mat_tmp;
    }

    public void transform(Shape s) {
        transform(s.getPoints());
    }

    public void transform(Point p) {
        float x = p.x;
        float y = p.y;

        p.x = x * m[0] + y * m[1] + m[12];
        p.y = x * m[4] + y * m[5] + m[13];
    }

    public void transform(Point[] points) {
        float x, y;

        for (Point p : points) {
            x = p.x;
            y = p.y;

            p.x = x * m[0] + y * m[1] + m[12];
            p.y = x * m[4] + y * m[5] + m[13];
        }
    }

    public void transform(Vector2 v) {
        float x, y;

        x = v.x;
        y = v.y;

        v.x = x * m[0] + y * m[1] + m[12];
        v.y = x * m[4] + y * m[5] + m[13];
    }

    public void set(float f, int column, int row) {
        m[column * 4 + row] = f;
    }

    public float get(int column, int row) {
        return m[column * 4 + row];
    }
}
