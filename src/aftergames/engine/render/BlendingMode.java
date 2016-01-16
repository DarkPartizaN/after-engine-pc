package aftergames.engine.render;

import static org.lwjgl.opengl.GL11.*;

/**
 *
 * @author KiQDominaN
 */
public final class BlendingMode {

    public static final BlendingMode BLEND_ADDITIVE = new BlendingMode(GL_ONE, GL_ONE);
    public static final BlendingMode BLEND_ALPHA = new BlendingMode(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
    public static final BlendingMode BLEND_MULTIPLY = new BlendingMode(GL_DST_COLOR, GL_ZERO);
    public static final BlendingMode BLEND_SRC2DST = new BlendingMode(GL_SRC_COLOR, GL_ONE);
    public static final BlendingMode BLEND_ADDMUL = new BlendingMode(GL_ONE_MINUS_DST_COLOR, GL_ONE);
    public static final BlendingMode BLEND_ADDALPHA = new BlendingMode(GL_SRC_ALPHA, GL_ONE);
    public static final BlendingMode BLEND_DETAIL = new BlendingMode(GL_DST_COLOR, GL_SRC_COLOR);

    public int src = GL_ONE;
    public int dst = GL_ONE;

    public BlendingMode() {
        //GL_ONE, GL_ONE
    }

    public BlendingMode(int src, int dst) {
        this.src = src;
        this.dst = dst;
    }

}