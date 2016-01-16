package aftergames.engine.render;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL20.*;

import aftergames.engine.EngineRuntime;
import aftergames.engine.ui.UIFont;
import aftergames.engine.utils.geom.Matrix;
import aftergames.engine.utils.geom.Point;
import aftergames.engine.utils.geom.Rect;
import aftergames.engine.utils.geom.Shape;
import aftergames.engine.world.Camera;

/**
 *
 * @author KiQDominaN
 */
public final class Renderer {

    //Viewport
    public static Rect viewport;
    //Matrices
    private static Matrix mat_proj, mat_view;
    //Render targets
    private static RenderTarget current_target;
    //Blending mode
    private static BlendingMode current_blending;
    //Shader
    public static Shader current_shader;
    //GL modes
    public static final int R_TRI = GL_TRIANGLES, R_TRI_FAN = GL_TRIANGLE_FAN, R_QUAD = GL_QUADS, R_POLY = GL_POLYGON;
    //Debug
    public static boolean r_debug = false, r_diffuse = true, r_lightning = true, r_shadows = true, r_grayscale = false;

    static {
        mat_proj = new Matrix();
        mat_view = new Matrix();

        // Setup GL
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        glEnable(GL_SCISSOR_TEST);
    }

    //
    //PROJECTION OPERATIONS
    //
    public static void setOrtho(int width, int height) {
        float l = -(width / 2f);
        float r = -l;
        float b = -(height / 2f);
        float t = -b;
        float zn = -1;
        float zf = 1;

        float[] m = new float[16];

        m[0] = 2f / (r - l);
        m[1] = 0;
        m[2] = 0;
        m[3] = 0;

        m[4] = 0;
        m[5] = 2f / (b - t);
        m[6] = 0;
        m[7] = 0;

        m[8] = 0;
        m[9] = 0;
        m[10] = 2f / (zf - zn);
        m[11] = 0;

        m[12] = (l + r) / (l - r) + (l * m[0]);
        m[13] = (t + b) / (b - t) + (b * m[5]);
        m[14] = (zf + zn) / (zn - zf);
        m[15] = 1;

        //Projection matrix
        glMatrixMode(GL_PROJECTION);
        mat_proj.set(m);
        glLoadMatrixf(mat_proj.gl_mat());

        //Modelview matrix (reset in ortho mode)
        glMatrixMode(GL_MODELVIEW);
        mat_view.reset();
        glLoadIdentity();
    }

    public static void setCamera(Camera camera) {
        setOrtho(EngineRuntime.screen_width, EngineRuntime.screen_height);

        mat_view = Matrix.translate(-camera.getWorldX(), -camera.getWorldY());
        glLoadMatrixf(mat_view.gl_mat());
    }

    public static Matrix getProjection() {
        return mat_proj;
    }

    public static Matrix getModelView() {
        return mat_view;
    }

    //
    //COMMON OPERATIONS
    //
    public static void begin(int mode) {
        glBegin(mode);
    }

    public static void end() {
        glEnd();
    }

    public static void clear() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);
    }

    public static void setColor(float r, float g, float b, float a) {
        glColor4f(r, g, b, a);
    }

    public static void setColor(Color color) {
        glColor4f(color.r, color.g, color.b, color.a);
    }

    public static void setTarget(RenderTarget target) {
        if (current_target != null) current_target.unbind();
        if (target != null) target.bind();

        current_target = target;
    }

    public static void setShader(Shader shader) {
        if (shader == null) {
            glUseProgram(0);
            return;
        }

        glUseProgram(shader.id);
        current_shader = shader;
    }

    public static void setBlending(BlendingMode mode) {
        if (mode == null) {
            glDisable(GL_BLEND);

            current_blending = null;
            return;
        }

        if (current_blending == null) {
            glEnable(GL_BLEND);
            glBlendFunc(mode.src, mode.dst);

            current_blending = mode;
            return;
        }

        if (current_blending.src != mode.src || current_blending.dst != mode.dst) {
            glBlendFunc(mode.src, mode.dst);

            current_blending = mode;
        }
    }

    public static void setTexture(Texture texture) {
        if (texture == null) {
            glBindTexture(GL_TEXTURE_2D, 0);
            glDisable(GL_TEXTURE_2D);
            return;
        }

        glEnable(GL_TEXTURE_2D);

        glBindTexture(GL_TEXTURE_2D, texture.id);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, texture.wrap_mode);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, texture.wrap_mode);
    }

    public static void setMultiTexture(int pos, Texture texture) {
        if (texture == null) {
            glActiveTexture(GL_TEXTURE0 + pos);
            glDisable(GL_TEXTURE_2D);
            return;
        }

        glActiveTexture(GL_TEXTURE0 + pos);
        glEnable(GL_TEXTURE_2D);
        glBindTexture(GL_TEXTURE_2D, texture.id);
    }

    public static void setTexCoords(float u, float v) {
        glTexCoord2f(u, v);
    }

    public static void setMultiTexCoords(int tex, float u, float v) {
        glMultiTexCoord2f(tex, u, v);
    }

    public static void setVertexCoords(float x, float y) {
        glVertex2f(x, y);
    }

    public static void setVertexCoords(float x, float y, float z) {
        glVertex3f(x, y, z);
    }

    public static void setVertexCoords(Point p) {
        glVertex2f(p.x, p.y);
    }

    public static void clipRect(int x, int y, int w, int h) {
        glScissor(x, EngineRuntime.screen_height - y - h, w, h);
    }

    public static void resetClip() {
        clipRect(0, 0, EngineRuntime.screen_width, EngineRuntime.screen_height);
    }

    //
    //COLOR DRAWING
    //
    public static void drawPoint(int x, int y, Color color) {
        setTexture(null);
        setBlending(BlendingMode.BLEND_ALPHA);
        setColor(color);

        glBegin(GL_POINTS);
        {
            glVertex2f(x + 0.5f, y + 0.5f);
        }
        glEnd();

        setColor(Color.white);
        setBlending(null);
    }

    public static void drawPoint(Point p, Color color) {
        drawPoint((int) p.x, (int) p.y, color);
    }

    public static void drawPoint(Point p, float size, Color color) {
        glPointSize(size);
        drawPoint(p, color);
        glPointSize(1f);
    }

    public static void drawStraight(int x1, int y1, int x2, int y2, Color color) {
        drawLine(x1, y1, x1 + x2, y1 + y2, color);
    }

    public static void drawLine(int x1, int y1, int x2, int y2, Color color) {
        setTexture(null);
        setBlending(BlendingMode.BLEND_ALPHA);
        setColor(color);

        glBegin(GL_LINES);
        {
            glVertex2f(x1 + 0.5f, y1 + 0.5f);
            glVertex2f(x2, y2);
        }
        glEnd();

        setColor(Color.white);
        setBlending(null);
    }

    public static void drawLine(Point a, Point b, Color color) {
        drawLine((int) a.x, (int) a.y, (int) b.x, (int) b.y, color);
    }

    public static void drawRect(Point a, Point b, Point c, Point d, Color color) {
        setTexture(null);
        setBlending(BlendingMode.BLEND_ALPHA);
        setColor(color);

        glBegin(GL_LINE_LOOP);
        {
            glVertex2f((int) a.x, (int) a.y);
            glVertex2f((int) b.x, (int) b.y);
            glVertex2f((int) c.x, (int) c.y);
            glVertex2f((int) d.x, (int) d.y);
        }
        glEnd();

        setColor(Color.white);
        setBlending(null);
    }

    public static void drawRect(int x, int y, int w, int h, Color color) {
        setTexture(null);
        setBlending(BlendingMode.BLEND_ALPHA);
        setColor(color);

        glBegin(GL_LINE_LOOP);
        {
            glVertex2f(x + 0.5f, y + 0.5f);
            glVertex2f(x + w, y + 0.5f);
            glVertex2f(x + w, y + h);
            glVertex2f(x + 0.5f, y + h);
        }
        glEnd();

        setColor(Color.white);
        setBlending(null);
    }

    public static void fillRect(int x, int y, int w, int h, Color color) {
        setTexture(null);
        setBlending(BlendingMode.BLEND_ALPHA);
        setColor(color);

        glBegin(GL_POLYGON);
        {
            glVertex2i(x, y);
            glVertex2i(x + w, y);
            glVertex2i(x + w, y + h);
            glVertex2i(x, y + h);
        }
        glEnd();

        setColor(Color.white);
        setBlending(null);
    }

    public static void drawShape(Shape shape, Color color) {
        setTexture(null);
        setBlending(BlendingMode.BLEND_ALPHA);
        setColor(color);

        glBegin(GL_LINE_LOOP);
        {
            for (Point p : shape.getPoints()) glVertex2f(p.x, p.y);
        }
        glEnd();

        setColor(Color.white);
        setBlending(null);
    }

    //
    //TEXTURE DRAWING
    //
    public static void drawImage(Texture tex, int x, int y, int w, int h) {
        setTexture(tex);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

        setBlending(BlendingMode.BLEND_ALPHA);

        glBegin(GL_QUADS);
        {
            glTexCoord2f(0, 0);
            glVertex2f(x, y);
            glTexCoord2f(1, 0);
            glVertex2f(x + w, y);
            glTexCoord2f(1, 1);
            glVertex2f(x + w, y + h);
            glTexCoord2f(0, 1);
            glVertex2f(x, y + h);
        }
        glEnd();

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        setTexture(null);
        setBlending(null);
    }

    public static void drawImage(Texture tex, int x, int y) {
        drawImage(tex, x, y, tex.width, tex.height);
    }

    //
    //FONT DRAWING
    //
    public static void drawString(String s, int x, int y, Color color) {
        drawString(UIFont.getDefault(), s, x, y, color);
    }

    public static void drawString(UIFont font, String s, int x, int y, Color color) {
        if (s == null || s.length() <= 0) return;

        glEnable(GL_TEXTURE_2D);
        glBindTexture(GL_TEXTURE_2D, font.getFontTexture().id);
        glTexEnvi(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_MODULATE);

        setBlending(BlendingMode.BLEND_ALPHA);
        setColor(color);

        glBegin(GL_QUADS);
        {
            for (char c : s.toCharArray()) {
                if (c == '\n') y += font.getHeight();

                float width = font.charWidth(c);
                float height = font.getHeight();
                float u = 1f / font.getFontImageWidth() * font.getCharX(c);
                float v = 1f / font.getFontImageHeight() * font.getCharY(c);
                float u2 = u + 1f / font.getFontImageWidth() * width;
                float v2 = v + 1f / font.getFontImageHeight() * height;

                glTexCoord2f(u, v);
                glVertex2f(x, y);

                glTexCoord2f(u2, v);
                glVertex2f(x + width, y);

                glTexCoord2f(u2, v2);
                glVertex2f(x + width, y + height);

                glTexCoord2f(u, v2);
                glVertex2f(x, y + height);

                x += width;
            }
        }
        glEnd();

        glDisable(GL_TEXTURE_2D);
        setColor(Color.white);
        setBlending(null);
    }
}
