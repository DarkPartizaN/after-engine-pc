package aftergames.engine.render;

/**
 *
 * @author KiQDominaN
 */
public abstract class Renderer {

    public abstract void init();
    public abstract void preRender();
    public abstract void render();
    public abstract void postRender();
}