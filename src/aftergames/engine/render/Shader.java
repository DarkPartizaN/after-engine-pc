package aftergames.engine.render;

import static org.lwjgl.opengl.GL11.GL_TRUE;
import static org.lwjgl.opengl.GL20.*;

/**
 *
 * @author KiQDominaN
 */
public final class Shader {

    public int id;
    private int vId, fId;
    private String vertex_source, fragment_source;

    public Shader() {
        id = glCreateProgram();
    }

    public void setVertexShader(String shader) {
        vertex_source = shader;

        vId = glCreateShader(GL_VERTEX_SHADER);
    }

    public void setFragmentShader(String shader) {
        fragment_source = shader;

        fId = glCreateShader(GL_FRAGMENT_SHADER);
    }

    public void compile() {
        glShaderSource(vId, vertex_source);
        glShaderSource(fId, fragment_source);

        glCompileShader(vId);
        if (glGetShaderi(vId, GL_COMPILE_STATUS) != GL_TRUE)
            throw new RuntimeException(glGetShaderInfoLog(vId));

        glCompileShader(fId);
        if (glGetShaderi(fId, GL_COMPILE_STATUS) != GL_TRUE)
            throw new RuntimeException(glGetShaderInfoLog(fId));

        glAttachShader(id, vId);
        glAttachShader(id, fId);

        glLinkProgram(id);
        glValidateProgram(id);
    }

    public void setUniform(String name, int value) {
        int loc = glGetUniformLocation(id, name);
        glUniform1i(loc, value);
    }

    public void setUniform(String name, int value, int value2) {
        int loc = glGetUniformLocation(id, name);
        glUniform2i(loc, value, value2);
    }

    public void setUniform(String name, int value, int value2, int value3) {
        int loc = glGetUniformLocation(id, name);
        glUniform3i(loc, value, value2, value3);
    }

    public void setUniform(String name, int value, int value2, int value3, int value4) {
        int loc = glGetUniformLocation(id, name);
        glUniform4i(loc, value, value2, value3, value4);
    }

    public void setUniform(String name, float value) {
        int loc = glGetUniformLocation(id, name);
        glUniform1f(loc, value);
    }

    public void setUniform(String name, float value, float value2) {
        int loc = glGetUniformLocation(id, name);
        glUniform2f(loc, value, value2);
    }

    public void setUniform(String name, float value, float value2, float value3) {
        int loc = glGetUniformLocation(id, name);
        glUniform3f(loc, value, value2, value3);
    }

    public void setUniform(String name, float value, float value2, float value3, float value4) {
        int loc = glGetUniformLocation(id, name);
        glUniform4f(loc, value, value2, value3, value4);
    }

    public void setUniform(String name, boolean value) {
        int loc = glGetUniformLocation(id, name);
        glUniform1i(loc, (value) ? 1 : 0);
    }

    public void setUniform(String name, Color value) {
        int loc = glGetUniformLocation(id, name);
        glUniform4f(loc, value.r, value.g, value.b, value.a);
    }
}