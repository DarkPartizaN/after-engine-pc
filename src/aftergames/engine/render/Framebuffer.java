package aftergames.engine.render;

import static org.lwjgl.opengl.EXTFramebufferObject.*;
import static org.lwjgl.opengl.GL11.*;

/**
 *
 * @author KiQDominaN
 */
public final class Framebuffer implements RenderTarget {

    public int id;
    private Texture tex;

    public Framebuffer(int width, int height) {
        tex = new Texture(width, height);

        glBindTexture(GL_TEXTURE_2D, tex.id);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, tex.width, tex.height, 0, GL_RGBA, GL_UNSIGNED_BYTE, 0);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);

        glBindTexture(GL_TEXTURE_2D, 0);

        id = glGenFramebuffersEXT();
        glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, id);

        glFramebufferTexture2DEXT(GL_FRAMEBUFFER_EXT, GL_COLOR_ATTACHMENT0_EXT, GL_TEXTURE_2D, tex.id, 0);

        glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, 0);
    }

    public Texture getTexture() {
        return tex;
    }

    public void clear() {
        glClearColor(1f, 1f, 1f, 1f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);
    }

    public void clear(Color c) {
        glClearColor(c.r, c.g, c.b, c.a);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);
    }

    public void bind() {
        glBindTexture(GL_TEXTURE_2D, 0);
        glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, id);
    }

    public void unbind() {
        glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, 0);
    }

    public void draw() {
        glEnable(GL_TEXTURE_2D);
        glBindTexture(GL_TEXTURE_2D, tex.id);

        glBegin(GL_QUADS);
        {
            glTexCoord2f(0, 1);
            glVertex2f(0, 0);
            glTexCoord2f(1, 1);
            glVertex2f(tex.width, 0);
            glTexCoord2f(1, 0);
            glVertex2f(tex.width, tex.height);
            glTexCoord2f(0, 0);
            glVertex2f(0, tex.height);
        }
        glEnd();

        glDisable(GL_TEXTURE_2D);
    }

    public Framebuffer copy() {
        Framebuffer tmp = new Framebuffer(tex.width, tex.height);

        tmp.bind();
        draw();
        tmp.unbind();

        return tmp;
    }

    public void blend(int src, int dst) {
        glEnable(GL_TEXTURE_2D);
        glBindTexture(GL_TEXTURE_2D, tex.id);

        glEnable(GL_BLEND);
        glBlendFunc(src, dst);

        glBegin(GL_QUADS);
        {
            glTexCoord2f(0, 1);
            glVertex2f(0, 0);
            glTexCoord2f(1, 1);
            glVertex2f(tex.width, 0);
            glTexCoord2f(1, 0);
            glVertex2f(tex.width, tex.height);
            glTexCoord2f(0, 0);
            glVertex2f(0, tex.height);
        }
        glEnd();

        glDisable(GL_BLEND);
        glDisable(GL_TEXTURE_2D);
    }

}
