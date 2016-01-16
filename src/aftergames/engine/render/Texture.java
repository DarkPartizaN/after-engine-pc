package aftergames.engine.render;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;

import aftergames.engine.utils.geom.Rect;

/**
 *
 * @author KiQDominaN
 */
public final class Texture {

    public static int CLAMP = GL_CLAMP_TO_EDGE, REPEAT = GL_REPEAT;
    public int id;
    public int width, height;
    public int[] raw;
    public int wrap_mode;

    public Texture() {
        id = glGenTextures();
    }

    public Texture(int width, int height) {
        id = glGenTextures();

        this.width = width;
        this.height = height;

        glBindTexture(GL_TEXTURE_2D, id); //Bind texture ID

        //Setup wrap mode
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, CLAMP);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, CLAMP);

        //Setup texture scaling filtering
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

        //Send texel data to OpenGL
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, (java.nio.ByteBuffer) null);
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    public void setWrapMode(int mode) {
        wrap_mode = mode;
    }

    public Rect getRegion(float x, float y, float x2, float y2) {
        Rect tmp = new Rect(x, y, x2, y2);

        return tmp;
    }
}